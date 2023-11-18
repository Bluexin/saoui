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

import com.tencao.saoui.GLCore
import com.tencao.saoui.Vector2d
import com.tencao.saoui.api.elements.CategoryButton
import com.tencao.saoui.api.elements.IconLabelElement
import com.tencao.saoui.api.items.IItemFilter
import com.tencao.saoui.api.screens.IIcon
import com.tencao.saoui.screens.CoreGUI
import com.tencao.saoui.screens.MouseButton
import com.tencao.saoui.util.*
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.Container
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.block.Block
import org.joml.Vector2d

fun CategoryButton.itemList(inventory: InventoryMenu, filter: IItemFilter) {
    inventory.slots.forEach { slot ->
        +ItemStackElement(slot, filter.getValidSlots(), Vector2d(0, 0), filter.getValidSlots().contains(slot), filter)
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

class ItemStackElement(private val slot: Slot, val equipSlots: Set<Slot>, pos: Vector2d, override var highlighted: Boolean, private val filter: (iss: ItemStack) -> Boolean) :
    IconLabelElement(icon = slot.item.toIcon(), pos = pos) {

    init {
        onClick { _, button ->
            if (button == MouseButton.LEFT) {
                if (tlParent is CoreGUI<*>) {
                    (tlParent as CoreGUI<*>).openGui(PopupItem(label.string, itemStack.descriptionId, if (Client.minecraft.options.advancedItemTooltips) itemStack.item.toString() else "")) += {
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
        // itemStack.getTooltip(Client.player, if (mc.gameSettings.advancedItemTooltips) ITooltipFlag.TooltipFlags.ADVANCED else TooltipFlag.Default.NORMAL)
    }

    private fun handleEquip() {
        if (equipSlots.size > 1) {
            (tlParent as CoreGUI<*>).openGui(PopupSlotSelection(label.string, listOf("Select a slot"), "", equipSlots.filter { it != slot }.toSet())) += {
                if (it != -1) {
                    swapItems(equipSlots.first { slot -> slot.index == it })
                }
            }
        } else if (equipSlots.first() == slot) {
            var stack = itemStack
            Client.player!!.containerMenu.slots
                .filter { it.index !in IntRange(0, 4) && it.mayPlace(stack) && it.hasItem() && it.item.count != it.item.maxStackSize && InventoryMenu.canItemQuickReplace(it, stack, true) }
                .any slotCheck@{
                    stack = moveItems(it)
                    stack.isEmpty
                }
            if (!stack.isEmpty) {
                Client.player!!.containerMenu.slots
                    .filter { it.index !in IntRange(0, 4) && !it.hasItem() && it.mayPlace(stack) }
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

        Client.player!!.containerMenu.broadcastChanges()
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
        return Client.minecraft.gameMode?.handleInventoryMouseClick(Client.player!!.containerMenu.containerId, slot.index, 0, ClickType.PICKUP, Client.player!!) ?: ItemStack.EMPTY
    }

    private fun swapItems(otherSlot: Slot) {
        Client.minecraft.gameMode?.handleInventoryMouseClick(Client.player!!.containerMenu.containerId, otherSlot.index, 0, ClickType.PICKUP, Client.player!!)
        Client.minecraft.gameMode?.handleInventoryMouseClick(Client.player!!.containerMenu.containerId, slotID, 0, ClickType.PICKUP, Client.player!!)
        Client.minecraft.gameMode?.handleInventoryMouseClick(Client.player!!.containerMenu.containerId, otherSlot.index, 0, ClickType.PICKUP, Client.player!!)
    }

    fun throwItem() {
        Client.minecraft.gameMode?.handleInventoryMouseClick(Client.player!!.containerMenu.containerId, slotID, 0, ClickType.THROW, Client.player!!)
    }

    private val itemStack
        get() = with(slot.item) {
            return@with if (filter(this)) this
            else ItemStack.EMPTY
        }

    private val slotID
        get() = slot.index

    override var valid: Boolean
        get() = !itemStack.isEmpty
        set(_) {}

    override var label: Component = "".toTextComponent()
        get() = if (!itemStack.isEmpty) {
            if (itemStack.count > 1) itemStack.getTooltipLines(Client.player, TooltipFlag.Default.NORMAL)[0].copy().append(" x${itemStack.count}")
            else itemStack.getTooltipLines(Client.player, TooltipFlag.Default.NORMAL)[0]
        } else ("gui.empty".translate())
}

class ItemIcon(private val itemStack: () -> ItemStack) : IIcon {
    private val itemRenderer by lazy { Minecraft.getInstance().itemRenderer }

    override fun glDraw(x: Int, y: Int, z: Float) {
        val f = itemStack().popTime.toFloat()/* - partialTicks*/
        GLCore.disableStandardItemLighting()

        itemRenderer.blitOffset += z

        if (f > 0.0f) {
            GLCore.pushMatrix()
            val f1 = 1.0f + f / 5.0f
            GLCore.translate((x + 8).toFloat(), (y + 12).toFloat(), z)
            GLCore.scale(1.0f / f1, (f1 + 1.0f) / 2.0f, z.plus(1))
            GLCore.translate((-(x + 8)).toFloat(), (-(y + 12)).toFloat(), z)
        }

        GLCore.enableStandardItemLighting()
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

fun Container.asSequence(): Sequence<ItemStack> {
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

fun Container.asNumberedSequence(): Sequence<Pair<ItemStack, Int>> {
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

inline fun Container.forEach(body: (ItemStack) -> Unit) {
    (0 until containerSize).forEach { body(this[it]) }
}

operator fun Container.get(index: Int): ItemStack = getItem(index)
