package com.saomc.saoui.neo.screens

import com.saomc.saoui.api.elements.neo.NeoElement
import com.saomc.saoui.api.elements.neo.NeoTLCategoryButton
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.config.OptionCore
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
        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, 0.0)

        val mousePos = vec(mouseX - x, mouseY - y)
        elements.forEach { it.draw(mousePos, partialTicks) }

        GlStateManager.popMatrix()
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        val mouse = vec(mouseX - x, mouseY - y)
        elements.forEach { it.click(mouse) }
    }

    override fun doesGuiPauseGame() = OptionCore.GUI_PAUSE.isEnabled

    fun tlCategory(icon: IIcon, body: (NeoTLCategoryButton.() -> Unit)? = null) {
        val cat = NeoTLCategoryButton(icon, 0, 20 * elements.size)
        if (body != null) cat.body()
        this.elements += cat
    }
}
