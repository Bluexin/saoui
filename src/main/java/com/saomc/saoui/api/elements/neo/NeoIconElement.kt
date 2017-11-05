package com.saomc.saoui.api.elements.neo

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.resources.StringNames
import com.saomc.saoui.util.ColorUtil
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
open class NeoIconElement(val icon: IIcon, var x: Int = 0, var y: Int = 0) : NeoParent() {

    private var onClickBody: (Vec2d) -> Boolean = { true }
    private var onClickOutBody: (Vec2d) -> Unit = { Unit }

    override val boundingBox = BoundingBox2D(vec(x, y), vec(20 + x, 20 + y))

    override fun draw(mouse: Vec2d, partialTicks: Float) { // TODO: scrolling if too many elements
        GlStateManager.pushMatrix()
        GLCore.glColorRGBA(ColorUtil.multiplyAlpha(getColor(mouse), opacity))
        GLCore.glBindTexture(StringNames.gui)
        GLCore.glTexturedRect(x.toDouble(), y.toDouble(), 0.0, 25.0, 20.0, 20.0)
        GLCore.glColorRGBA(ColorUtil.multiplyAlpha(getTextColor(mouse), opacity))
        if (icon.rl != null) GLCore.glBindTexture(icon.rl!!)
        icon.glDrawUnsafe(x + 2, y + 2)

        drawChildren(mouse, partialTicks)
        GlStateManager.popMatrix()
    }

    protected open fun drawChildren(mouse: Vec2d, partialTicks: Float) {
        val selectedIdx = if (elementsSequence.any { it is NeoCategoryButton }) elements.indexOfFirst { it.selected } else -1
        val skipFront =
                if (selectedIdx >= 0) (selectedIdx - (elements.size / 2 - (elements.size + 1) % 2) + elements.size) % elements.size
                else 0
        val centering = ((elements.size + elements.size % 2 - 2) * childrenYSeparator) / 2.0
        GlStateManager.translate(x.toDouble() + childrenXOffset, y.toDouble() + childrenYOffset - centering, 0.0)
        var nmouse = mouse - vec(x + childrenXOffset, y + childrenYOffset - centering)
        val seq = elementsSequence.filter(NeoElement::visible)
        seq.drop(skipFront).forEach {
            it.draw(nmouse, partialTicks)
            GlStateManager.translate(childrenXSeparator.toDouble(), childrenYSeparator.toDouble(), 0.0)
            nmouse -= vec(childrenXSeparator, childrenYSeparator)
        }
        seq.take(skipFront).forEach {
            it.draw(nmouse, partialTicks)
            GlStateManager.translate(childrenXSeparator.toDouble(), childrenYSeparator.toDouble(), 0.0)
            nmouse -= vec(childrenXSeparator, childrenYSeparator)
        }
    }

    open fun getColor(mouse: Vec2d) = if (disabled) ColorUtil.DEFAULT_COLOR and ColorUtil.DISABLED_MASK else if (selected || mouse in this) ColorUtil.HOVER_COLOR.rgba else ColorUtil.DEFAULT_COLOR.rgba

    open fun getTextColor(mouse: Vec2d) = if (disabled) ColorUtil.DEFAULT_FONT_COLOR and ColorUtil.DISABLED_MASK else if (selected || mouse in this) ColorUtil.HOVER_FONT_COLOR.rgba else ColorUtil.DEFAULT_FONT_COLOR.rgba

    override fun click(pos: Vec2d): Boolean {
        return if (pos in boundingBox) {
            onClickBody(pos)
        } else {
            var npos = pos - vec(x + childrenXOffset, y + childrenYOffset - ((elements.count(NeoElement::visible) - 1) * childrenYSeparator) / 2.0)
            var ok = false
            val selectedIdx = if (elementsSequence.any { it is NeoCategoryButton }) elements.indexOfFirst { it.selected } else -1
            val skip =
                    if (selectedIdx >= 0) (selectedIdx - (elements.size / 2 - (elements.size + 1) % 2) + elements.size) % elements.size
                    else 0
            val seq = elementsSequence.filter(NeoElement::visible)
            seq.drop(skip).forEach {
                ok = it.click(npos) || ok
                npos -= vec(childrenXSeparator, childrenYSeparator)
            }
            seq.take(skip).forEach {
                ok = it.click(npos) || ok
                npos -= vec(childrenXSeparator, childrenYSeparator)
            }
            if (ok) true
            else {
                onClickOutBody(pos)
                false
            }
        }
    }

    override val childrenXOffset = 25
    override val childrenYSeparator = 20

    open fun onClick(body: (Vec2d) -> Boolean) {
        onClickBody = body
    }

    open fun onClickOut(body: (Vec2d) -> Unit) {
        onClickOutBody = body
    }

    override var visible = true

    override var selected = false

    override var disabled = false

    override var opacity = 1f

    override fun hide() {
        visible = false
    }

    override fun show() {
        visible = true
    }
}
