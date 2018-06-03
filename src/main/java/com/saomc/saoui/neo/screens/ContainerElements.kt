package com.saomc.saoui.neo.screens

import com.saomc.saoui.api.elements.neo.NeoCategoryButton
import com.saomc.saoui.api.elements.neo.NeoIconLabelElement
import com.saomc.saoui.api.screens.IIcon
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack

@NeoGuiDsl
fun NeoCategoryButton.itemList(inventory: IInventory, filter: (iss: ItemStack) -> Boolean) {
    inventory.asSequence().filter(filter).forEach {
        +ItemStackElement(it, vec(0, 0)) // TODO: pack similar stacks together
    }
}

class ItemStackElement(private val itemStack: ItemStack, pos: Vec2d) :
        NeoIconLabelElement(icon = ItemIcon(itemStack), pos = pos) {

    override val label: String
        get() = I18n.format("saoui.formatItem", itemStack.displayName, itemStack.count)
}

class ItemIcon(private val itemStack: ItemStack) : IIcon {
    private val itemRenderer by lazy { Minecraft.getMinecraft().renderItem }
    private val fontRenderer by lazy { Minecraft.getMinecraft().fontRenderer }

    override fun glDraw(x: Int, y: Int) {
        val f = itemStack.animationsToGo.toFloat()/* - partialTicks*/

        if (f > 0.0f) {
            GlStateManager.pushMatrix()
            val f1 = 1.0f + f / 5.0f
            GlStateManager.translate((x + 8).toFloat(), (y + 12).toFloat(), 0.0f)
            GlStateManager.scale(1.0f / f1, (f1 + 1.0f) / 2.0f, 1.0f)
            GlStateManager.translate((-(x + 8)).toFloat(), (-(y + 12)).toFloat(), 0.0f)
        }

        itemRenderer.renderItemAndEffectIntoGUI(itemStack, x, y)

        if (f > 0.0f) GlStateManager.popMatrix()

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

inline fun IInventory.forEach(body: (ItemStack) -> Unit) {
    (0 until sizeInventory).forEach { body(this[it]) }
}

operator fun IInventory.get(index: Int): ItemStack = getStackInSlot(index)