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

package com.saomc.saoui.neo.screens

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.elements.neo.NeoCategoryButton
import com.saomc.saoui.api.elements.neo.NeoIconLabelElement
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.events.EventCore.mc
import com.saomc.saoui.util.IconCore
import com.teamwizardry.librarianlib.features.kotlin.get
import com.teamwizardry.librarianlib.features.kotlin.isNotEmpty
import com.teamwizardry.librarianlib.features.kotlin.toolClasses
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.inventory.IInventory
import net.minecraft.item.*
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.ForgeRegistries


@NeoGuiDsl
fun NeoCategoryButton.itemList(inventory: IInventory, filter: (iss: ItemStack) -> Boolean, vararg equippedRange: IntRange = arrayOf(-1..-1)) {
    (0 until inventory.sizeInventory).forEach {
        +ItemStackElement(inventory, it, Vec2d.ZERO, equippedRange.any { r -> it in r }, filter)
    }
    +object : NeoIconLabelElement(icon = IconCore.NONE, label = I18n.format("gui.empty")) {
        private var mark = false

        override val valid: Boolean
            get() {
                if (mark) return false
                mark = true
                val r = !this@itemList.validElementsSequence.any()
                mark = false
                return r
            }

        override var disabled: Boolean
            get() = true
            set(_) {}
    }
}

class ItemStackElement(private val inventoryIn: IInventory, private val slot: Int, pos: Vec2d, override var selected: Boolean, private val filter: (iss: ItemStack) -> Boolean) :
        NeoIconLabelElement(icon = ItemIcon { inventoryIn.getStackInSlot(slot) }, pos = pos) {

    init {
        onClick { _, button ->
            if (button == MouseButton.LEFT)
                (tlParent as? NeoGui<*>)?.
                    openGui(PopupYesNo(label, itemStack.itemDesc(), if (mc.gameSettings.advancedItemTooltips) ForgeRegistries.ITEMS.getKey(itemStack.item).toString() else ""))
            true
        }
        //itemStack.getTooltip(mc.player, if (mc.gameSettings.advancedItemTooltips) ITooltipFlag.TooltipFlags.ADVANCED else ITooltipFlag.TooltipFlags.NORMAL)
    }

    private val itemStack
        get() = with(inventoryIn.getStackInSlot(slot)) {
            return@with if (filter(this)) this
            else ItemStack.EMPTY
        }

    override val valid: Boolean
        get() = itemStack.isNotEmpty

    override val label: String
        get() = if (itemStack.isNotEmpty) {
            if (itemStack.count > 1) I18n.format("saoui.formatItems", itemStack.getTooltip(mc.player, ITooltipFlag.TooltipFlags.NORMAL)[0], itemStack.count)
            else I18n.format("saoui.formatItem", itemStack.getTooltip(mc.player, ITooltipFlag.TooltipFlags.NORMAL)[0])
        } else I18n.format("gui.empty")
}

class ItemIcon(private val itemStack: () -> ItemStack) : IIcon {
    private val itemRenderer by lazy { Minecraft.getMinecraft().renderItem }

    override fun glDraw(x: Int, y: Int) {
        val f = itemStack().animationsToGo.toFloat()/* - partialTicks*/

        if (f > 0.0f) {
            GLCore.pushMatrix()
            val f1 = 1.0f + f / 5.0f
            GLCore.translate((x + 8).toFloat(), (y + 12).toFloat(), 0.0f)
            GLCore.scale(1.0f / f1, (f1 + 1.0f) / 2.0f, 1.0f)
            GLCore.translate((-(x + 8)).toFloat(), (-(y + 12)).toFloat(), 0.0f)
        }

        RenderHelper.enableGUIStandardItemLighting()
        itemRenderer.renderItemAndEffectIntoGUI(itemStack(), x, y)
        GLCore.depth(false)

        if (f > 0.0f) GLCore.popMatrix()

//        itemRenderer.renderItemOverlays(fontRenderer, itemStack, x, y)
    }
}

fun ItemStack.itemDesc(): List<String> {
    val stringBuilder = mutableListOf<String>()
    val desc = getTooltip(mc.player, ITooltipFlag.TooltipFlags.NORMAL)
    desc.removeAt(0)
    if (item is ItemTool){
        if (toolClasses.isNotEmpty()) {
            toolClasses.forEachIndexed { index, s ->
                stringBuilder.add(I18n.format("itemDesc.type", I18n.format("itemDesc.tool")))
                if (toolClasses.size > 1) {
                    if (index == 0)
                        stringBuilder.add(I18n.format("itemDesc.toolClasses", s.capitalize(), item.getHarvestLevel(this, s, mc.player, null)))
                    else
                        stringBuilder.add(I18n.format("itemDesc.toolClassSpace", s.capitalize(), item.getHarvestLevel(this, s, mc.player, null)))
                } else
                    stringBuilder.add(I18n.format("itemDesc.toolClass", s.capitalize(), item.getHarvestLevel(this, s, mc.player, null)))
            }
        }

        stringBuilder.add(I18n.format("itemDesc.enchantable", isItemEnchantable.toString().capitalize()))
        stringBuilder.add(I18n.format("itemDesc.enchantability", (item as ItemTool).itemEnchantability))
        stringBuilder.add(I18n.format("itemDesc.repairable",  (item as ItemTool).isRepairable.toString().capitalize()))
    }

    else if (toolClasses.isNotEmpty()) {
        toolClasses.forEachIndexed { index, s ->
            stringBuilder.add(I18n.format("itemDesc.type", I18n.format("itemDesc.tool")))
            if (toolClasses.size > 1) {
                if (index == 0)
                    stringBuilder.add(I18n.format("itemDesc.toolClasses", s.capitalize(), item.getHarvestLevel(this, s, mc.player, null)))
                else
                    stringBuilder.add(I18n.format("itemDesc.toolClassSpace", s.capitalize(), item.getHarvestLevel(this, s, mc.player, null)))
            } else
                stringBuilder.add(I18n.format("itemDesc.toolClass", s.capitalize(), item.getHarvestLevel(this, s, mc.player, null)))
            stringBuilder.add("Enchantable: ${isItemEnchantable.toString().capitalize()}")
        }

        //TODO Item tool
    }

    else if (item is ItemHoe){
        stringBuilder.add(I18n.format("itemDesc.type", I18n.format("itemDesc.tool")))
        stringBuilder.add(I18n.format("itemDesc.toolClass", I18n.format("itemDesc.hoe"), 0))
    }

    else if (item is ItemSword) {
        stringBuilder.add(I18n.format("itemDesc.type", I18n.format("itemDesc.sword")))
        stringBuilder.add(I18n.format("itemDesc.enchantable", isItemEnchantable.toString().capitalize()))
        stringBuilder.add(I18n.format("itemDesc.enchantability", (item as ItemSword).itemEnchantability))
    }

    else if (item is ItemBlock) {
        val block = (item as ItemBlock).block
        val state = block.getStateFromMeta(metadata)
        if (block.hasTileEntity(state))
            stringBuilder.add(I18n.format("itemDesc.type", "itemDesc.tileEntity"))
        else
            stringBuilder.add(I18n.format("itemDesc.type", "itemDesc.block"))
        stringBuilder.add(I18n.format("itemDesc.hardness", block.getBlockHardness(state, mc.player.world, mc.player.position)))
        stringBuilder.add(I18n.format("itemDesc.solid", state.material.isSolid.toString().capitalize()))
        if (state.material.isToolNotRequired)
            stringBuilder.add(I18n.format("itemDesc.toolRequire.false"))
        else {
            stringBuilder.add(I18n.format("itemDesc.toolRequired.true"))
            stringBuilder.add(I18n.format("itemDesc.mostEffective",
                    mc.player.inventory.asSequence().filter { it.canHarvestBlock(state) }.maxBy { it.getDestroySpeed(state) }?.displayName
                    ?: "itemDesc.none"))

        }


        //TODO Block Desc
    }

    else if (item is ItemFood){
        stringBuilder.add(I18n.format("itemDesc.type", I18n.format("itemDesc.food")))
        stringBuilder.add(I18n.format("itemDesc.foodValue", (item as ItemFood).getHealAmount(this)))
        stringBuilder.add(I18n.format("itemDesc.saturationValue", (item as ItemFood).getSaturationModifier(this)))
        //TODO Food Desc
    }

    else if (item is ItemEnchantedBook){

        //TODO Enchant Desc
    }

    else if (item is ItemWrittenBook){

        //TODO Book Desk

    }
    else if (item is ItemArmor){
        stringBuilder.add(I18n.format("itemDesc.type", I18n.format("itemDesc.armor")))
        val equipSlot = (item as ItemArmor).getEquipmentSlot(this)?: (item as ItemArmor).equipmentSlot
        stringBuilder.add(I18n.format("itemDesc.slot", equipSlot.getName().capitalize()))
        stringBuilder.add(I18n.format("itemDesc.enchantable", isItemEnchantable.toString().capitalize()))
        stringBuilder.add(I18n.format("itemDesc.enchantability", (item as ItemArmor).itemEnchantability))
        stringBuilder.add(I18n.format("itemDesc.repairable",  (item as ItemArmor).isRepairable.toString().capitalize()))
        if ((item as ItemArmor).isRepairable)
            stringBuilder.add(I18n.format("itemDesc.repairItem",  (item as ItemArmor).armorMaterial.repairItemStack.displayName))
        stringBuilder.add(I18n.format("itemDesc.toughness",  (item as ItemArmor).armorMaterial.toughness))
    }

    else if (hasTagCompound()){
        //TODO Potion Desc
        tagCompound?.get("Potion")?.let {
            val potion = ForgeRegistries.POTION_TYPES.getValue(ResourceLocation(it.toString()))
        }
    }
    else{
        stringBuilder.add(I18n.format("itemDesc.type", I18n.format("itemDesc.material")))
    }

    stringBuilder.addAll(desc)

    return stringBuilder
}

fun IInventory.asSequence(): Sequence<ItemStack> {
    return Sequence {
        object : Iterator<ItemStack> {
            private var index = 0
            private val size get() = this@asSequence.sizeInventory - 1

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
            private val size get() = this@asNumberedSequence.sizeInventory - 1

            override fun hasNext() = index < size

            override fun next(): Pair<ItemStack, Int> {
                if (!hasNext()) throw IndexOutOfBoundsException("index: $index, size: $size")
                return this@asNumberedSequence[index] to index++
            }
        }
    }
}

inline fun IInventory.forEach(body: (ItemStack) -> Unit) {
    (0 until sizeInventory).forEach { body(this[it]) }
}

operator fun IInventory.get(index: Int): ItemStack = getStackInSlot(index)