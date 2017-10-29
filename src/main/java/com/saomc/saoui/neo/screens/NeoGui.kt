package com.saomc.saoui.neo.screens

import com.saomc.saoui.api.elements.neo.NeoElement
import com.saomc.saoui.api.elements.neo.NeoIconElement
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.util.IconCore
import com.teamwizardry.librarianlib.features.helpers.vec
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
/*abstract*/ class NeoGui(protected val x: Double, protected val y: Double) : GuiScreen() {
    protected val elements = mutableListOf<NeoElement>()

    override fun initGui() {
        tlCategory(IconCore.PROFILE) {
            onClick {
                this@tlCategory.elements.clear()
                +NeoIconElement(IconCore.EQUIPMENT, 0, 0)
                +NeoIconElement(IconCore.ITEMS, 0, 20)
                +NeoIconElement(IconCore.SKILLS, 0, 40)
                true
            }
        }
        tlCategory(IconCore.SOCIAL) {
            onClick {
                this@tlCategory.elements.clear()
                +NeoIconElement(IconCore.PARTY, 0, 0)
                true
            }
        }
        tlCategory(IconCore.MESSAGE)
        tlCategory(IconCore.NAVIGATION)
        tlCategory(IconCore.SETTINGS) {
            onClick {
                this@tlCategory.elements.clear()
                +NeoIconElement(IconCore.OPTION, 0, 0)
                true
            }
        }
    }

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

    override fun doesGuiPauseGame() = false

    fun tlCategory(icon: IIcon, body: (NeoIconElement.() -> Unit)? = null) {
        val cat = NeoIconElement(icon, 0, 20 * elements.size)
        if (body != null) cat.body()
        this.elements += cat
    }
}
