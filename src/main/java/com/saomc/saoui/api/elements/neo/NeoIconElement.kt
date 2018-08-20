package com.saomc.saoui.api.elements.neo

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.neo.screens.MouseButton
import com.saomc.saoui.resources.StringNames
import com.saomc.saoui.util.ColorIntent
import com.saomc.saoui.util.ColorUtil
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.BoundingBox2D
import com.teamwizardry.librarianlib.features.math.Vec2d

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
open class NeoIconElement(val icon: IIcon, override var pos: Vec2d = Vec2d.ZERO, override var destination: Vec2d = pos) : NeoParent() {

    private var onClickBody: (Vec2d, MouseButton) -> Boolean = { _, _ -> true }
    private var onClickOutBody: (Vec2d, MouseButton) -> Unit = { _, _ -> Unit }

    val bgColorScheme = mutableMapOf(
            ColorIntent.NORMAL to ColorUtil.DEFAULT_COLOR.rgba,
            ColorIntent.HOVERED to ColorUtil.HOVER_COLOR.rgba,
            ColorIntent.DISABLED to ColorUtil.DISABLED_COLOR.rgba
    )

    val fontColorScheme = mutableMapOf(
            ColorIntent.NORMAL to ColorUtil.DEFAULT_FONT_COLOR.rgba,
            ColorIntent.HOVERED to ColorUtil.HOVER_FONT_COLOR.rgba,
            ColorIntent.DISABLED to ColorUtil.DISABLED_FONT_COLOR.rgba
    )

    override val boundingBox get() = BoundingBox2D(pos, pos + vec(20, 20))

    protected fun childrenOrderedForRendering(): Sequence<NeoElement> {
        val selectedIdx = if (visibleElementsSequence.any { it is NeoCategoryButton }) visibleElementsSequence.indexOfFirst { it.selected } else -1
        val s = visibleElementsSequence.count()
        val skipFront =
                if (selectedIdx >= 0) (selectedIdx - (s / 2 - (s + 1) % 2) + s) % s
                else 0
        return visibleElementsSequence.drop(skipFront) + visibleElementsSequence.take(skipFront)
    }

    override fun draw(mouse: Vec2d, partialTicks: Float) { // TODO: scrolling if too many elements
        if (opacity < 0.03 || scale == Vec2d.ZERO) return
        GLCore.pushMatrix()
        if (scale != Vec2d.ONE) GLCore.glScalef(scale.xf, scale.yf, 1f)
        GLCore.color(ColorUtil.multiplyAlpha(getColor(mouse), opacity))
        GLCore.glBindTexture(StringNames.gui)
        GLCore.glTexturedRectV2(pos.x, pos.y, width = 19.0, height = 19.0, srcX = 1.0, srcY = 26.0)
        GLCore.color(ColorUtil.multiplyAlpha(getTextColor(mouse), opacity))
        if (icon.rl != null) GLCore.glBindTexture(icon.rl!!)
        icon.glDrawUnsafe(pos + vec(1, 1))

        drawChildren(mouse, partialTicks)
        GLCore.popMatrix()
    }

    protected open fun drawChildren(mouse: Vec2d, partialTicks: Float) {
        val children = validElementsSequence
        val centering = ((children.count() + children.count() % 2 - 2) * childrenYSeparator) / 2.0
        GLCore.translate(pos.x + childrenXOffset, pos.y + childrenYOffset - centering, 0.0)
        var nmouse = mouse - pos - vec(childrenXOffset, childrenYOffset - centering)
        childrenOrderedForRendering().forEach {
            it.draw(nmouse, partialTicks)
            GLCore.translate(childrenXSeparator.toDouble(), childrenYSeparator.toDouble(), 0.0)
            nmouse -= vec(childrenXSeparator, childrenYSeparator)
        }
    }

    open fun getColor(mouse: Vec2d): Int {
        return if (disabled) bgColorScheme[ColorIntent.DISABLED] ?: ColorUtil.DISABLED_COLOR.rgba
        else if (selected || mouse in this) {
            bgColorScheme[ColorIntent.HOVERED] ?: ColorUtil.DEFAULT_COLOR.rgba
        } else bgColorScheme[ColorIntent.NORMAL] ?: ColorUtil.HOVER_COLOR.rgba
    }

    open fun getTextColor(mouse: Vec2d): Int {
        return if (disabled) fontColorScheme[ColorIntent.DISABLED] ?: ColorUtil.DISABLED_FONT_COLOR.rgba else if (selected || mouse in this) {
            fontColorScheme[ColorIntent.HOVERED] ?: ColorUtil.HOVER_FONT_COLOR.rgba
        } else fontColorScheme[ColorIntent.NORMAL] ?: ColorUtil.DEFAULT_FONT_COLOR.rgba
    }

    override fun click(pos: Vec2d, button: MouseButton): Boolean {
        return if (pos in this) {
            onClickBody(pos, button)
        } else {
            var npos = pos - this.pos - vec(childrenXOffset, childrenYOffset - ((validElementsSequence.count() - 1) * childrenYSeparator) / 2.0)
            var ok = false
            childrenOrderedForRendering().forEach {
                ok = it.click(npos, button) || ok
                npos -= vec(childrenXSeparator, childrenYSeparator)
            }
            if (ok) true
            else {
                onClickOutBody(pos, button)
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

    open fun onClick(body: (Vec2d, MouseButton) -> Boolean) {
        onClickBody = body
    }

    open fun onClickOut(body: (Vec2d, MouseButton) -> Unit) {
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
