/*
 * Copyright (C) 2016-2019 Arnaud 'Bluexin' Solé
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

@file:Suppress("SuspiciousVarProperty")

package com.tencao.saoui.screens.util

import com.tencao.saomclib.GLCore
import com.tencao.saomclib.utils.math.Vec2d
import com.tencao.saoui.api.elements.CategoryButton
import com.tencao.saoui.api.elements.IconLabelElement
import com.tencao.saoui.api.items.IItemFilter
import com.tencao.saoui.api.screens.IIcon
import com.tencao.saoui.events.EventCore.mc
import com.tencao.saoui.screens.CoreGUI
import com.tencao.saoui.screens.MouseButton
import com.tencao.saoui.util.IconCore
import com.tencao.saoui.util.localize
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.container.ClickType
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.PlayerContainer
import net.minecraft.inventory.container.Slot
import net.minecraft.item.*
import net.minecraft.util.Direction
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.registries.ForgeRegistries
import java.util.*

fun CategoryButton.itemList(inventory: Container, filter: IItemFilter) {
    inventory.slots.forEach { slot ->
        +ItemStackElement(slot, filter.getValidSlots(), Vec2d.ZERO, filter.getValidSlots().contains(slot), filter)
    }
    +object : IconLabelElement(icon = IconCore.NONE, label = "gui.empty".localize()) {
        private var mark = false

        override var valid: Boolean
            get() {
                if (mark) return false
                mark = true
                val r = this@itemList.validElementsSequence.none()
                mark = false
                return r
            }
            set(_) {}

        override var disabled: Boolean
            get() = true
            set(_) {}
    }
}

class ItemStackElement(private val slot: Slot, val equipSlots: Set<Slot>, pos: Vec2d, override var highlighted: Boolean, private val filter: (iss: ItemStack) -> Boolean) :
    IconLabelElement(icon = slot.item.toIcon(), pos = pos) {

    init {
        onClick { _, button ->
            if (button == MouseButton.LEFT) {
                if (tlParent is CoreGUI<*>) {
                    (tlParent as CoreGUI<*>).openGui(PopupItem(label.string, itemStack.itemDesc(), if (mc.options.advancedItemTooltips) ForgeRegistries.ITEMS.getKey(itemStack.item).toString() else "")) += {
                        when (it) {
                            PopupItem.Result.EQUIP -> handleEquip()
                            PopupItem.Result.DROP -> handleDrop()
                            else -> {}
                        }
                        highlighted = false
                    }
                }
            }
            true
        }
        // itemStack.getTooltip(mc.player, if (mc.gameSettings.advancedItemTooltips) ITooltipFlag.TooltipFlags.ADVANCED else ITooltipFlag.TooltipFlags.NORMAL)
    }

    private fun handleEquip() {
        if (equipSlots.size > 1) {
            (tlParent as CoreGUI<*>).openGui(PopupSlotSelection(label.string, listOf("Select a slot"), "", equipSlots.filter { it != slot }.toSet())) += {
                if (it != -1) {
                    swapItems(equipSlots.first { slot -> slot.slotIndex == it })
                }
            }
        } else if (equipSlots.first() == slot) {
            var stack = itemStack
            mc.player!!.containerMenu.slots
                .filter { it.slotIndex !in IntRange(0, 4) && it.mayPlace(stack) && it.hasItem() && it.item.count != it.item.maxStackSize && PlayerContainer.canItemQuickReplace(it, stack, true) }
                .any slotCheck@{
                    stack = moveItems(it)
                    stack.isEmpty
                }
            if (!stack.isEmpty) {
                mc.player!!.containerMenu.slots
                    .filter { it.slotIndex !in IntRange(0, 4) && !it.hasItem() && it.mayPlace(stack) }
                    .any slotCheck@{
                        stack = moveItems(it)
                        stack.isEmpty
                    }
            }
            if (!stack.isEmpty) {
                throwItem()
            }
        } else {
            swapItems(equipSlots.first())
        }

        mc.player!!.containerMenu.broadcastChanges()
        (controllingParent as? CategoryButton)?.reInit()
    }

    private fun handleDrop() {
        (tlParent as CoreGUI<*>).openGui(PopupYesNo(label.string, "Are you sure you want to discard this item?", "")) += {
            if (it == PopupYesNo.Result.YES) {
                throwItem()
            }
        }
    }

    /**
     * Will pickup or place item, and return
     * the currently held item
     */
    fun moveItems(slot: Slot): ItemStack {
        return mc.gameMode?.handleInventoryMouseClick(mc.player!!.containerMenu.containerId, slot.slotIndex, 0, ClickType.PICKUP, mc.player!!) ?: ItemStack.EMPTY
    }

    private fun swapItems(otherSlot: Slot) {
        mc.gameMode?.handleInventoryMouseClick(mc.player!!.containerMenu.containerId, otherSlot.slotIndex, 0, ClickType.PICKUP, mc.player!!)
        mc.gameMode?.handleInventoryMouseClick(mc.player!!.containerMenu.containerId, slotID, 0, ClickType.PICKUP, mc.player!!)
        mc.gameMode?.handleInventoryMouseClick(mc.player!!.containerMenu.containerId, otherSlot.slotIndex, 0, ClickType.PICKUP, mc.player!!)
    }

    fun throwItem() {
        mc.gameMode?.handleInventoryMouseClick(mc.player!!.containerMenu.containerId, slotID, 0, ClickType.THROW, mc.player!!)
    }

    /**
     * TODO Finish this
     * Compares two items together
     * Types:   0 - Armor
     *          1 - Weapons
     *          2 - Tools
     *          3 - Food
     *          4 - Misc
     */
    fun compare(other: ItemStack, type: Int): List<String> {
        val stringBuilder = mutableListOf<String>()
        when (type) {
            0 -> {
                val desc = itemStack.itemDesc()
                other.itemDesc().forEachIndexed { index, s ->
                    if (index <= 3) {
                        stringBuilder.add("$s -> ${desc[index]}")
                    }
                }
            }
            else -> {
            }
        }
        return stringBuilder
    }

    private val itemStack
        get() = with(slot.item) {
            return@with if (filter(this)) this
            else ItemStack.EMPTY
        }

    private val slotID
        get() = slot.slotIndex

    override var valid: Boolean
        get() = !itemStack.isEmpty
        set(_) {}

    override var label: ITextComponent = StringTextComponent("")
        get() = if (!itemStack.isEmpty) {
            if (itemStack.count > 1) itemStack.getTooltipLines(mc.player, ITooltipFlag.TooltipFlags.NORMAL)[0].copy().append(" x${itemStack.count}")
            else itemStack.getTooltipLines(mc.player, ITooltipFlag.TooltipFlags.NORMAL)[0]
        } else StringTextComponent("gui.empty".localize())
}

class ItemIcon(private val itemStack: () -> ItemStack) : IIcon {
    private val itemRenderer by lazy { Minecraft.getInstance().itemRenderer }

    override fun glDraw(x: Int, y: Int, z: Float) {
        val f = itemStack().popTime.toFloat()/* - partialTicks*/
        RenderHelper.turnOff()

        itemRenderer.blitOffset += z

        if (f > 0.0f) {
            GLCore.pushMatrix()
            val f1 = 1.0f + f / 5.0f
            GLCore.translate((x + 8).toFloat(), (y + 12).toFloat(), z)
            GLCore.scale(1.0f / f1, (f1 + 1.0f) / 2.0f, z.plus(1))
            GLCore.translate((-(x + 8)).toFloat(), (-(y + 12)).toFloat(), z)
        }

        RenderHelper.turnBackOn()
        itemRenderer.renderAndDecorateItem(itemStack(), x, y)
        GLCore.depth(false)

        itemRenderer.blitOffset -= z

        if (f > 0.0f) GLCore.popMatrix()

//        itemRenderer.renderItemOverlays(fontRenderer, itemStack, x, y)
    }
}

fun Item.toIcon(): ItemIcon = ItemIcon { ItemStack(this) }
fun Block.toIcon(): ItemIcon = ItemIcon { ItemStack(this) }
fun ItemStack.toIcon(): ItemIcon = ItemIcon { this }

fun ItemStack.itemDesc(): List<String> {
    val stringBuilder = mutableListOf<String>()
    val desc = getTooltipLines(mc.player, ITooltipFlag.TooltipFlags.NORMAL).map { it.string }.toMutableList()
    desc.removeAt(0)
    if (item is ToolItem) {
        if (toolTypes.isNotEmpty()) {
            toolTypes.forEachIndexed { index, s ->
                stringBuilder.add("itemDesc.type".localize().plus("itemDesc.tool".localize()))
                if (toolTypes.size > 1) {
                    if (index == 0) {
                        stringBuilder.add(
                            "itemDesc.toolClasses".localize(
                                s.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                                item.getHarvestLevel(this, s, mc.player, null)
                            )
                        )
                    } else {
                        stringBuilder.add(
                            "itemDesc.toolClassSpace".localize(
                                s.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                                item.getHarvestLevel(this, s, mc.player, null)
                            )
                        )
                    }
                } else {
                    stringBuilder.add(
                        "itemDesc.toolClass".localize(
                            s.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                            item.getHarvestLevel(this, s, mc.player, null)
                        )
                    )
                }
            }
        }

        if (isEnchantable) {
            stringBuilder.add("itemDesc.enchantability".localize((item as ToolItem).enchantmentValue))
        } else {
            stringBuilder.add(
                "itemDesc.enchantable".localize(
                    isEnchantable.toString()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                )
            )
        }
        stringBuilder.add(
            "itemDesc.repairable".localize(
                isRepairable.toString()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            )
        )
    } else if (toolTypes.isNotEmpty()) {
        toolTypes.forEachIndexed { index, s ->
            stringBuilder.add("itemDesc.type".localize("itemDesc.tool".localize()))
            if (toolTypes.size > 1) {
                if (index == 0) {
                    stringBuilder.add(
                        "itemDesc.toolClasses".localize(
                            s.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                            item.getHarvestLevel(this, s, mc.player, null)
                        )
                    )
                } else {
                    stringBuilder.add(
                        "itemDesc.toolClassSpace".localize(
                            s.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                            item.getHarvestLevel(this, s, mc.player, null)
                        )
                    )
                }
            } else {
                stringBuilder.add(
                    "itemDesc.toolClass".localize(
                        s.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                        item.getHarvestLevel(this, s, mc.player, null)
                    )
                )
            }
            stringBuilder.add(
                "itemDesc.enchantable".localize(
                    isEnchantable.toString()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                )
            )
        }

        // TODO Item tool
    } else if (item is HoeItem) {
        stringBuilder.add("itemDesc.type".localize("itemDesc.tool".localize()))
        stringBuilder.add("itemDesc.toolClass".localize("itemDesc.hoe".localize(), 0))
    } else if (item is SwordItem) {
        stringBuilder.add("itemDesc.type".localize("itemDesc.sword".localize()))
        if (isEnchantable) {
            stringBuilder.add("itemDesc.enchantability".localize((item as SwordItem).enchantmentValue))
        } else {
            stringBuilder.add(
                "itemDesc.enchantable".localize(
                    isEnchantable.toString()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                )
            )
        }
    } else if (item is BlockItem) {
        // TODO Make it so it's not super dependant on player, aka allow fake player
        val itemBlock = item as BlockItem
        val block = itemBlock.block
        val blockItemUseContext = itemBlock.updatePlacementContext(BlockItemUseContext(ItemUseContext(mc.player!!, Hand.MAIN_HAND, BlockRayTraceResult(mc.player!!.position(), Direction.DOWN, mc.player!!.blockPosition(), true))))
        val state = (item as BlockItem).block.getStateForPlacement(blockItemUseContext!!)
        if (state != null) {
            if (block.hasTileEntity(state)) {
                stringBuilder.add("itemDesc.type".localize("itemDesc.tileEntity".localize()))
            } else {
                stringBuilder.add("itemDesc.type".localize("itemDesc.block".localize()))
            }
            stringBuilder.add(
                "itemDesc.hardness".localize(state.getDestroySpeed(mc.player!!.level, mc.player!!.blockPosition()))
            )
            stringBuilder.add(
                "itemDesc.solid".localize(
                    state.material.isSolid.toString()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                )
            )
            if (!state.requiresCorrectToolForDrops()) {
                stringBuilder.add("itemDesc.toolRequired.false".localize())
            } else {
                stringBuilder.add("itemDesc.toolRequired.true".localize())
                stringBuilder.add(
                    "itemDesc.mostEffective".localize(
                        mc.player!!.inventory.asSequence().filter { it.isCorrectToolForDrops(state) }
                            .maxByOrNull { it.getDestroySpeed(state) }?.displayName
                            ?: "itemDesc.none".localize()
                    )
                )
            }
        }

        // TODO Block Desc
    } else if (isEdible) {
        stringBuilder.add("itemDesc.type".localize("itemDesc.food".localize()))
        stringBuilder.add("itemDesc.foodValue".localize(item.foodProperties!!.nutrition))
        stringBuilder.add("itemDesc.saturationValue".localize(item.foodProperties!!.saturationModifier))
        // TODO Food Desc
    } else if (item is EnchantedBookItem) {
        // TODO Enchant Desc
    } else if (item is WrittenBookItem) {
        // TODO Book Desk
    } else if (item is ArmorItem) {
        stringBuilder.add("itemDesc.type".localize("itemDesc.armor".localize()))
        val equipSlot = (item as ArmorItem).getEquipmentSlot(this) ?: (item as ArmorItem).slot
        stringBuilder.add(
            "itemDesc.slot".localize(
                equipSlot.getName()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            )
        )
        if (isEnchantable) {
            stringBuilder.add("itemDesc.enchantability".localize((item as ArmorItem).enchantmentValue))
        } else {
            stringBuilder.add(
                "itemDesc.enchantable".localize(
                    isEnchantable.toString()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                )
            )
        }
        if (isRepairable) {
            stringBuilder.add("itemDesc.repairItem".localize((item as ArmorItem).material.repairIngredient.items.map { it.displayName }))
        } else {
            stringBuilder.add(
                "itemDesc.repairable".localize(
                    isRepairable.toString()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                )
            )
        }
        stringBuilder.add("itemDesc.toughness".localize ((item as ArmorItem).material.toughness))
    } else if (hasTag()) {
        // TODO Potion Desc
        tag?.get("Potion")?.let {
            val potion = ForgeRegistries.POTION_TYPES.getValue(ResourceLocation(it.toString()))
        }
    } else {
        stringBuilder.add("itemDesc.type".localize("itemDesc.material".localize()))
    }

    stringBuilder.addAll(desc)

    return stringBuilder
}

fun IInventory.asSequence(): Sequence<ItemStack> {
    return Sequence {
        object : Iterator<ItemStack> {
            private var index = 0
            private val size get() = this@asSequence.containerSize - 1

            override fun hasNext() = index < size

            override fun next(): ItemStack {
                if (!hasNext()) throw IndexOutOfBoundsException("index: $index, size: $size")
                return this@asSequence[index++]
            }
        }
    }
}

fun IInventory.asNumberedSequence(): Sequence<Pair<ItemStack, Int>> {
    return Sequence {
        object : Iterator<Pair<ItemStack, Int>> {
            private var index = 0
            private val size get() = this@asNumberedSequence.containerSize - 1

            override fun hasNext() = index < size

            override fun next(): Pair<ItemStack, Int> {
                if (!hasNext()) throw IndexOutOfBoundsException("index: $index, size: $size")
                return this@asNumberedSequence[index] to index++
            }
        }
    }
}

inline fun IInventory.forEach(body: (ItemStack) -> Unit) {
    (0 until containerSize).forEach { body(this[it]) }
}

operator fun IInventory.get(index: Int): ItemStack = getItem(index)
