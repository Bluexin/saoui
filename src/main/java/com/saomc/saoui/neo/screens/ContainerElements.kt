package com.saomc.saoui.neo.screens

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.elements.neo.NeoCategoryButton
import com.saomc.saoui.api.elements.neo.NeoIconLabelElement
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.events.EventCore.mc
import com.teamwizardry.librarianlib.features.kotlin.isNotEmpty
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack

@NeoGuiDsl
fun NeoCategoryButton.itemList(inventory: IInventory, filter: (iss: ItemStack) -> Boolean, vararg equippedRange: IntRange = arrayOf(-1..-1)) {
    (0..inventory.sizeInventory).forEach {
        +ItemStackElement(inventory, it, Vec2d.ZERO, equippedRange.any { r -> it in r }, filter)
    }
}

class ItemStackElement(private val inventoryIn: IInventory, private val slot: Int, pos: Vec2d, override var selected: Boolean, private val filter: (iss: ItemStack) -> Boolean) :
        NeoIconLabelElement(icon = ItemIcon({ inventoryIn.getStackInSlot(slot) }), pos = pos) {

    init {
        onClick { _, button ->
            if (button == MouseButton.LEFT) (tlParent as? NeoGui<*>)?.openGui(PopupYesNo(label, itemStack.getTooltip(mc.player, if (mc.gameSettings.advancedItemTooltips) ITooltipFlag.TooltipFlags.ADVANCED else ITooltipFlag.TooltipFlags.NORMAL)))
            true
        }
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
            if (itemStack.count > 1) I18n.format("saoui.formatItems", itemStack.displayName, itemStack.count)
            else I18n.format("saoui.formatItem", itemStack.displayName)
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