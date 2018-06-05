package com.saomc.saoui.api.elements.neo

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.resources.StringNames
import com.saomc.saoui.util.ColorIntent
import com.saomc.saoui.util.ColorUtil
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.BoundingBox2D
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.renderer.GlStateManager

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
open class NeoIconElement(val icon: IIcon, override var pos: Vec2d = Vec2d.ZERO, override var destination: Vec2d = pos) : NeoParent() {

    private var onClickBody: (Vec2d) -> Boolean = { true }
    private var onClickOutBody: (Vec2d) -> Unit = { Unit }

    val bgColorScheme = mutableMapOf(
            ColorIntent.NORMAL to ColorUtil.DEFAULT_COLOR.rgba,
            ColorIntent.HOVERED to ColorUtil.HOVER_COLOR.rgba,
            ColorIntent.DISABLED_MASK to ColorUtil.DISABLED_MASK.rgba
    )

    val fontColorScheme = mutableMapOf(
            ColorIntent.NORMAL to ColorUtil.DEFAULT_FONT_COLOR.rgba,
            ColorIntent.HOVERED to ColorUtil.HOVER_FONT_COLOR.rgba,
            ColorIntent.DISABLED_MASK to ColorUtil.DISABLED_MASK.rgba
    )

    override val boundingBox get() = BoundingBox2D(pos, pos + vec(20, 20))

    protected fun childrenOrderedForRendering(): Sequence<NeoElement> {
        val selectedIdx = if (elementsSequence.any { it is NeoCategoryButton }) elements.indexOfFirst { it.selected } else -1
        val skipFront =
                if (selectedIdx >= 0) (selectedIdx - (elements.size / 2 - (elements.size + 1) % 2) + elements.size) % elements.size
                else 0
        val seq = elementsSequence.filter(NeoElement::visible)
        return seq.drop(skipFront) + seq.take(skipFront)
    }

    override fun draw(mouse: Vec2d, partialTicks: Float) { // TODO: scrolling if too many elements
        if (opacity < 0.03 || scale == Vec2d.ZERO) return
        GlStateManager.pushMatrix()
        if (scale != Vec2d.ONE) GLCore.glScalef(scale.xf, scale.yf, 1f)
        GLCore.glColorRGBA(ColorUtil.multiplyAlpha(getColor(mouse), opacity))
        GLCore.glBindTexture(StringNames.gui)
        GLCore.glTexturedRect(pos, 1.0, 26.0, 19.0, 19.0)
        GLCore.glColorRGBA(ColorUtil.multiplyAlpha(getTextColor(mouse), opacity))
        if (icon.rl != null) GLCore.glBindTexture(icon.rl!!)
        icon.glDrawUnsafe(pos + vec(1, 1))

        drawChildren(mouse, partialTicks)
        GlStateManager.popMatrix()
    }

    protected open fun drawChildren(mouse: Vec2d, partialTicks: Float) {
        val centering = ((elements.size + elements.size % 2 - 2) * childrenYSeparator) / 2.0
        GlStateManager.translate(pos.x + childrenXOffset, pos.y + childrenYOffset - centering, 0.0)
        var nmouse = mouse - pos - vec(childrenXOffset, childrenYOffset - centering)
        childrenOrderedForRendering().forEach {
            it.draw(nmouse, partialTicks)
            GlStateManager.translate(childrenXSeparator.toDouble(), childrenYSeparator.toDouble(), 0.0)
            nmouse -= vec(childrenXSeparator, childrenYSeparator)
        }
    }

    open fun getColor(mouse: Vec2d): Int {
        val base = if (selected || mouse in this) {
            bgColorScheme[ColorIntent.HOVERED] ?: ColorUtil.DEFAULT_COLOR.rgba
        } else bgColorScheme[ColorIntent.NORMAL] ?: ColorUtil.HOVER_COLOR.rgba

        return if (disabled) base and (bgColorScheme[ColorIntent.DISABLED_MASK]
                ?: ColorUtil.DISABLED_MASK.rgba) else base
    }

    open fun getTextColor(mouse: Vec2d): Int {
        val base = if (selected || mouse in this) {
            fontColorScheme[ColorIntent.HOVERED] ?: ColorUtil.HOVER_FONT_COLOR.rgba
        } else fontColorScheme[ColorIntent.NORMAL] ?: ColorUtil.DEFAULT_FONT_COLOR.rgba

        return if (disabled) base and (fontColorScheme[ColorIntent.DISABLED_MASK]
                ?: ColorUtil.DISABLED_MASK.rgba) else base
    }

    override fun click(pos: Vec2d): Boolean {
        return if (pos in this) {
            onClickBody(pos)
        } else {
            var npos = pos - this.pos - vec(childrenXOffset, childrenYOffset - ((elements.count(NeoElement::visible) - 1) * childrenYSeparator) / 2.0)
            var ok = false
            childrenOrderedForRendering().forEach {
                ok = it.click(npos) || ok
                npos -= vec(childrenXSeparator, childrenYSeparator)
            }
            /*childrenOrderedForRendering().any {
                val r = it.click(npos)
                npos -= vec(childrenXSeparator, childrenYSeparator)
                r
            }*/
            if (ok) true
            else {
                onClickOutBody(pos)
                false
            }
        }
    }

    fun setBgColor(intent: ColorIntent, rgba: Int): NeoIconElement {
        bgColorScheme[intent] = rgba
        return this
    }

    fun setBgColor(intent: ColorIntent, color: ColorUtil): NeoIconElement = setBgColor(intent, color.rgba)

    fun setFontColor(intent: ColorIntent, rgba: Int): NeoIconElement {
        fontColorScheme[intent] = rgba
        return this
    }

    fun setFontColor(intent: ColorIntent, color: ColorUtil): NeoIconElement = setFontColor(intent, color.rgba)

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

    override var scale = Vec2d.ONE

    override fun hide() {
        visible = false
    }

    override fun show() {
        visible = true
    }
}
