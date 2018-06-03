package com.saomc.saoui.neo.screens

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.elements.neo.NeoIconElement
import com.saomc.saoui.resources.StringNames
import com.saomc.saoui.util.ColorIntent
import com.saomc.saoui.util.ColorUtil
import com.saomc.saoui.util.IconCore
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.renderer.GlStateManager
import kotlin.math.min

open class Popup<T : Any>(var title: String, var text: String, private val buttons: Map<NeoIconElement, T>) : NeoGui<T>(Vec2d.ZERO) {

    override fun initGui() {
        elements.clear()

        pos = vec(width / 2.0, height / 2.0)
        destination = pos

        val w = 300.0
        val h = 150.0
        val childrenXSeparator = min(w * 0.8 / buttons.size, 100.0)
        val childrenXOffset = -childrenXSeparator / buttons.size - 10
        val childrenYOffset = h * 0.4 - 10

        var i = 0
        buttons.forEach { button, result ->
            button.onClick {
                this@Popup.result = result
                onGuiClosed()
                true
            }
            button.pos = vec(childrenXOffset + childrenXSeparator * i++, childrenYOffset)
            button.destination = button.pos
            +button
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        // TODO: these could be moved to Liblib's Sprites. Maybe.

        GlStateManager.pushMatrix()
        GlStateManager.translate(pos.x, pos.y, 0.0)

        val w = 300.0
        val h = 150.0

        GLCore.glBindTexture(StringNames.gui)
        // tmp bg for naw
        GLCore.glColorRGBA(ColorUtil.DEFAULT_COLOR)
        GLCore.glTexturedRect(-w / 2.0, -h / 2.0, w, h, 5.0, 120.0, 2.0, 2.0)
        GLCore.glColorRGBA(ColorUtil.DEAD_COLOR)
        GLCore.glTexturedRect(-1.0, -100.0, 2.0, 200.0, 5.0, 120.0, 2.0, 2.0)
        GLCore.glTexturedRect(-1 - w / 4.0, -100.0, 2.0, 200.0, 5.0, 120.0, 2.0, 2.0)
        GLCore.glTexturedRect(-1 + w / 4.0, -100.0, 2.0, 200.0, 5.0, 120.0, 2.0, 2.0)

        GlStateManager.popMatrix()

        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}

class PopupYesNo(title: String, text: String) : Popup<PopupYesNo.Result>(title, text, mapOf(
        NeoIconElement(IconCore.CONFIRM)
                .setBgColor(ColorIntent.NORMAL, ColorUtil.CONFIRM_COLOR)
                .setBgColor(ColorIntent.HOVERED, ColorUtil.CONFIRM_COLOR_LIGHT)
                .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                to Result.YES,
        NeoIconElement(IconCore.CANCEL)
                .setBgColor(ColorIntent.NORMAL, ColorUtil.CANCEL_COLOR)
                .setBgColor(ColorIntent.HOVERED, ColorUtil.CANCEL_COLOR_LIGHT)
                .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                .setFontColor(ColorIntent.NORMAL, ColorUtil.DEFAULT_COLOR)
                to Result.NO
)) {

    init {
        result = Result.NO
    }

    enum class Result {
        YES,
        NO
    }
}