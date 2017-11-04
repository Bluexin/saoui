package com.saomc.saoui.api.elements.neo

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.resources.StringNames
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.math.BoundingBox2D
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.renderer.GlStateManager

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
open class NeoIconLabelElement(icon: IIcon, private val label: String, var width: Int = 84, var height: Int = 18, x: Int = 0, y: Int = 0) : NeoIconElement(icon, x, y) {

    private var onClickBody: (Vec2d) -> Boolean = { true }
    private var onClickOutBody: (Vec2d) -> Unit = { Unit }

    override val boundingBox = BoundingBox2D(vec(x, y), vec(width + x, height + y))

    override fun draw(mouse: Vec2d, partialTicks: Float) { // TODO: scrolling if too many elements
        GlStateManager.pushMatrix()
        GLCore.glColorRGBA(getColor(mouse))
        GLCore.glBindTexture(StringNames.slot)
        GLCore.glTexturedRect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble(), 0.0, 40.0, 84.0, 18.0)
        val color = getTextColor(mouse)
        GLCore.glColorRGBA(color)
        if (icon.rl != null) GLCore.glBindTexture(icon.rl!!)
        icon.glDrawUnsafe(x + 1, y + 1)
        GLCore.glString(label, x + 22, y + height / 2, color, centered = true)

        val centering = ((elements.count(NeoElement::visible) - 1) * childrenYSeparator) / 2.0
        GlStateManager.translate(x.toDouble() + childrenXOffset, y.toDouble() + childrenYOffset - centering, 0.0)
        var nmouse = mouse - vec(x + childrenXOffset, y + childrenYOffset - centering)
        elements.filter { it.visible }.forEach {
            it.draw(nmouse, partialTicks)
            GlStateManager.translate(childrenXSeparator.toDouble(), childrenYSeparator.toDouble(), 0.0)
            nmouse -= vec(childrenXSeparator, childrenYSeparator)
        }
        GlStateManager.popMatrix()
    }

    override val childrenXOffset = width + 5
}

/*

fun a() {
    if (element.mouseOver(cursorX, cursorY, -1)) mouseMoved(mc, cursorX, cursorY)

    if (element.getVisibility() > 0) {
        val hoverState = element.hoverState(cursorX, cursorY)
        val color0 = getColor(hoverState, true)
        val color1 = getColor(hoverState, false)

        val left = element.getX(false)
        val top = element.getY(false)

        val iconOffset = (element.getHeight() - 16) / 2
        val captionOffset = (element.getHeight() - 8) / 2

        GLCore.glBlend(true)
        GLCore.glBindTexture(StringNames.slot)
        if (hoverState == 2) {
            GLCore.glColor(1.0f, 1.0f, 1.0f)
            GLCore.glTexturedRect(left.toDouble(), top.toDouble(), element.getWidth(), element.getHeight(), 0.0, 21.0, (100 - 16).toDouble(), (20 - 2).toDouble())
            if (element.getCategory().equals("logout") && element.getParent().equals("settings"))
                renderHighlightText(if (OptionCore.LOGOUT.isEnabled()) element.getCaption() else " ", left + iconOffset * 2 + 16 + 4, top + captionOffset, ColorUtil.multiplyAlpha(color1, element.getVisibility()))
            else
                renderHighlightText(element.getCaption(), left + iconOffset * 2 + 16 + 4, top + captionOffset, ColorUtil.multiplyAlpha(color1, element.getVisibility()))
        } else {
            GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color0, element.getVisibility()))
            GLCore.glTexturedRect(left.toDouble(), top.toDouble(), element.getWidth(), element.getHeight(), 0.0, 1.0, (100 - 16).toDouble(), (20 - 2).toDouble())
            if (element.getCategory().equals("logout") && element.getParent().equals("settings"))
                GLCore.glString(if (OptionCore.LOGOUT.isEnabled()) element.getCaption() else " ", left + iconOffset * 2 + 16 + 4, top + captionOffset, ColorUtil.multiplyAlpha(color0, element.getVisibility()), OptionCore.TEXT_SHADOW.isEnabled())
            else
                GLCore.glString(if (element.getCaption().length() < 50) element.getCaption() else element.getCaption().substring(0, 50), left + iconOffset * 2 + 16 + 4, top + captionOffset, ColorUtil.multiplyAlpha(color0, element.getVisibility()), OptionCore.TEXT_SHADOW.isEnabled())
        }
        GLCore.glBindTexture(if (OptionCore.SAO_UI.isEnabled()) StringNames.gui else StringNames.guiCustom)

        GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color1, element.getVisibility()))
        GLCore.glTexturedRect((left + iconOffset).toDouble(), (top + iconOffset).toDouble(), 140.0, 25.0, 16.0, 16.0)

        GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color0, element.getVisibility()))
        element.getIcon().glDraw(left + iconOffset, top + iconOffset)
        GLCore.glBlend(false)
        GLCore.glAlphaTest(true)

        //GLCore.glString(element.getCaption(), left + iconOffset * 2 + 16 + 4, top + captionOffset, ColorUtil.multiplyAlpha(color0, element.getVisibility()));
    }
}

fun getColor(hoverState: Int, bg: Boolean): Int {
    return if (bg) if (hoverState == 1) bgColor.rgba else if (hoverState == 2) ColorUtil.HOVER_COLOR.rgba else bgColor.rgba and disabledMask.rgba else if (hoverState == 1) ColorUtil.DEFAULT_FONT_COLOR.rgba else if (hoverState == 2) ColorUtil.HOVER_FONT_COLOR.rgba else ColorUtil.DEFAULT_FONT_COLOR.rgba and disabledMask.rgba
}

*/
