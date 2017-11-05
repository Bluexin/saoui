package com.saomc.saoui.neo.screens

import com.saomc.saoui.api.elements.neo.NeoCategoryButton
import com.saomc.saoui.api.elements.neo.NeoElement
import com.saomc.saoui.api.elements.neo.NeoIconElement
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.config.OptionCore
import com.teamwizardry.librarianlib.features.animator.Animator
import com.teamwizardry.librarianlib.features.helpers.vec
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
abstract class NeoGui(protected var x: Double, protected var y: Double) : GuiScreen() {
    protected val elements = mutableListOf<NeoElement>()

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
//        println("drawScreen")
        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, 0.0)

        val mousePos = vec(mouseX - x, mouseY - y)
        elements.forEach { it.draw(mousePos, partialTicks) }

        GlStateManager.popMatrix()
    }

    override fun updateScreen() {
//        println("updateScreen")
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        val mouse = vec(mouseX - x, mouseY - y)
        elements.forEach { it.click(mouse) }
    }

    override fun doesGuiPauseGame() = OptionCore.GUI_PAUSE.isEnabled

    fun tlCategory(icon: IIcon, body: (NeoCategoryButton.() -> Unit)? = null) {
        val cat = NeoCategoryButton(NeoIconElement(icon, 0, 25 * elements.size))
        if (body != null) cat.body()
        this.elements += cat
    }

    companion object {
        val animator = Animator()
    }
}
