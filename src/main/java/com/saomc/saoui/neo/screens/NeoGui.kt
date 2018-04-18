package com.saomc.saoui.neo.screens

import com.saomc.saoui.api.elements.neo.*
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.config.OptionCore
import com.teamwizardry.librarianlib.features.animator.Animation
import com.teamwizardry.librarianlib.features.animator.Animator
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
abstract class NeoGui(override var pos: Vec2d, override var destination: Vec2d = pos) : GuiScreen(), INeoParent {
    protected val elements = mutableListOf<NeoElement>()

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
//        println("drawScreen")
        GlStateManager.pushMatrix()
        GlStateManager.translate(pos.x, pos.y, 0.0)

        val mousePos = vec(mouseX, mouseY) - pos
        elements.forEach { it.draw(mousePos, partialTicks) }

        GlStateManager.popMatrix()
    }

    override fun updateScreen() = elements.forEach(NeoElement::update)

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        val mouse = vec(mouseX, mouseY) - pos
        elements.forEach { it.click(mouse) }
    }

    override fun doesGuiPauseGame() = OptionCore.GUI_PAUSE.isEnabled

    fun tlCategory(icon: IIcon, body: (NeoCategoryButton.() -> Unit)? = null) {
        this.elements += NeoCategoryButton(NeoIconElement(icon, vec(0, 25 * elements.size)), this, body)
    }

    companion object {
        val animator = Animator()
    }
}

operator fun Animation<*>.unaryPlus() {
    NeoGui.animator += this
}
