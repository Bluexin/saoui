package com.saomc.saoui.elements

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.elements.gui.MouseButton
import com.saomc.saoui.elements.gui.unaryPlus
import com.saomc.saoui.elements.registry.DrawType
import com.saomc.saoui.resources.StringNames
import com.saomc.saoui.screens.util.ItemIcon
import com.saomc.saoui.util.ColorIntent
import com.saomc.saoui.util.ColorUtil
import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.clamp
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.BoundingBox2D
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraftforge.fml.client.config.GuiUtils
import java.util.*
import kotlin.math.max
import kotlin.math.min

open class IconElement(val icon: IIcon, override var parent: IElement? = null, override val originPos: Vec2d= Vec2d.ZERO, override var destination: Vec2d = originPos, open val description: MutableList<String> = mutableListOf(), override val init: (IElement.() -> Unit)? = null) : IElement {

    override var width: Int = 19

    override var height: Int = 19

    override var pos: Vec2d = Vec2d.ZERO

    override val childrenXOffset = 25

    override val childrenYSeparator = 20

    override var hasOpened: Boolean = false

    override var onClickBody: (Vec2d, MouseButton) -> Boolean = { _, _ -> false }

    override val elements: MutableList<IElement> = mutableListOf()

    override val futureOperations: MutableList<IElement.() -> Unit> = LinkedList()

    val canDraw: Boolean
        get() = opacity >= 0.03 || scale != Vec2d.ZERO

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

    override var boundingBox = BoundingBox2D(Vec2d.ZERO, Vec2d.ZERO)
        get() = BoundingBox2D(pos, pos + vec(20, 20))

    override var scroll = -3
        set(value) {
            val c = validElementsSequence.count()
            if (c > 6) field = (value) % (c)
        }

    override var opacity = 1f
        set(value) {
            field = value.clamp(0f, 1f)
        }

    init {
        init?.invoke(this)
    }

    override fun open(reInit: Boolean) {
        super.open(reInit)
        +basicAnimation(this, "pos") {
            duration = 20f
            from = Vec2d.ZERO
            easing = Easing.easeInOutQuint
        }
    }

    protected fun childrenOrderedForRendering(): Sequence<IElement> {
        val count = visibleElementsSequence.count()
        return if (count == 0) emptySequence()
        else {
            val selectedIdx = visibleElementsSequence.indexOfFirst { it.highlighted }
            when {
                selectedIdx >= 0 -> {
                    val skipFront = (selectedIdx - (count / 2 - (count + 1) % 2) + count) % count
                    visibleElementsSequence.drop(skipFront) + visibleElementsSequence.take(skipFront)
                }
                validElementsSequence.count() < 7 -> visibleElementsSequence
                else -> {
                    val s = visibleElementsSequence + visibleElementsSequence
                    s.drop(min(max((scroll + count) % count, 0), count)).take(min(7, count))
                }
            }
        }
    }

    override fun drawBackground(mouse: Vec2d, partialTicks: Float) {
        if (!canDraw) return
        GLCore.pushMatrix()
        if (scale != Vec2d.ONE) GLCore.glScalef(scale.xf, scale.yf, 1f)
        val mouseCheck = mouse in this || selected
        GLCore.glBlend(true)
        GLCore.color(ColorUtil.multiplyAlpha(getColor(mouse), transparency))
        GLCore.glBindTexture(StringNames.gui)
        GLCore.glTexturedRectV2(pos.x, pos.y, width = width.toDouble(), height = height.toDouble(), srcX = 1.0, srcY = 26.0)
        if (mouseCheck && OptionCore.MOUSE_OVER_EFFECT.isEnabled)
            mouseOverEffect()
        GLCore.glBlend(false)
        GLCore.color(1f, 1f, 1f, 1f)
        drawChildren(mouse, partialTicks, DrawType.BACKGROUND)
        GLCore.popMatrix()
    }

    override fun draw(mouse: Vec2d, partialTicks: Float) {
        if (!canDraw) return
        GLCore.pushMatrix()
        GLCore.glBlend(true)
        GLCore.color(ColorUtil.multiplyAlpha(getTextColor(mouse), transparency))
        if (icon.rl != null) {
            GLCore.glBindTexture(icon.rl!!)
            icon.glDraw(pos + vec(1, 1), 5f)
        }
        if (icon is ItemIcon)
            icon.glDraw(pos + vec(1, 1), 5f)

        GLCore.glBlend(false)
        GLCore.color(1f, 1f, 1f, 1f)
        drawChildren(mouse, partialTicks, DrawType.DRAW)
        GLCore.popMatrix()
    }

    override fun drawForeground(mouse: Vec2d, partialTicks: Float) {
        if (!canDraw) return
        if (mouse in this) {
            drawHoveringText(mouse)
            GLCore.lighting(false)
        }
        drawChildren(mouse, partialTicks, DrawType.FOREGROUND)
    }

    open fun drawHoveringText(mouse: Vec2d) {
        GuiUtils.drawHoveringText(description, mouse.xi, mouse.yi, Minecraft().displayWidth, Minecraft().displayHeight, width - 20, GLCore.glFont)
        /*GuiUtils.drawHoveringText(description, mouse.xi, mouse.yi, width, height, -1, GLCore.glFont)
        if (description.isNotEmpty()) {
            GlStateManager.disableRescaleNormal()
            RenderHelper.disableStandardItemLighting()
            GlStateManager.disableLighting()
            GlStateManager.disableDepth()
            var i = 0
            for (s in description) {
                val j: Int = GLCore.glFont.getStringWidth(s)
                if (j > i) {
                    i = j
                }
            }
            var l1 = mouse.x + 12
            var i2 = mouse.y - 12
            var k = 8
            if (description.size > 1) {
                k += 2 + (description.size - 1) * 10
            }
            if (l1 + i > width) {
                l1 -= 28 + i
            }
            if (i2 + k + 6 > height) {
                i2 = height - k - 6.0
            }
            val zLevel = 300.0
            GLCore.drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, zLevel, -267386864, -267386864)
            GLCore.drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, zLevel, -267386864, -267386864)
            GLCore.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, zLevel, -267386864, -267386864)
            GLCore.drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, zLevel,  -267386864, -267386864)
            GLCore.drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, zLevel, -267386864, -267386864)
            GLCore.drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, zLevel, 1347420415, 1344798847)
            GLCore.drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, zLevel, 1347420415, 1344798847)
            GLCore.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, zLevel, 1347420415, 1347420415)
            GLCore.drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, zLevel, 1344798847, 1344798847)
            for (k1 in description.indices) {
                val s1 = description[k1]
                GLCore.glString(s1.toString(), l1.toInt(), i2.toInt(), -1, true)
                if (k1 == 0) {
                    i2 += 2
                }
                i2 += 10
            }
            GlStateManager.enableLighting()
            GlStateManager.enableDepth()
            RenderHelper.enableStandardItemLighting()
            GlStateManager.enableRescaleNormal()
        }*/
    }

    fun drawChildren(mouse: Vec2d, partialTicks: Float, drawType: DrawType) {
        if (hasOpened && visibleElementsSequence.count() > 0) {
            val c = validElementsSequence.take(7).count()
            val centering = ((c + c % 2 - 2) * childrenYSeparator) / 2.0
            GLCore.translate(pos.x + childrenXOffset, pos.y + childrenYOffset - centering, 0.0)
            var nmouse = mouse - pos - vec(childrenXOffset, childrenYOffset - centering)
            childrenOrderedForRendering().forEachIndexed { i, it ->
                if (c == 7 && (i == 0 || i == 6)) it.opacity /= 2
                when (drawType) {
                    DrawType.BACKGROUND -> it.drawBackground(nmouse, partialTicks)
                    DrawType.DRAW -> it.draw(nmouse, partialTicks)
                    DrawType.FOREGROUND -> it.drawForeground(nmouse, partialTicks)
                }
                GLCore.translate(childrenXSeparator.toDouble(), childrenYSeparator.toDouble(), 0.0)
                nmouse -= vec(childrenXSeparator, childrenYSeparator)
                if (c == 7 && (i == 0 || i == 6)) it.opacity *= 2
            }
            nmouse = mouse - pos - vec(childrenXOffset, childrenYOffset - centering)
            otherElementsSequence.forEach {
                when (drawType) {
                    DrawType.BACKGROUND -> it.drawBackground(nmouse, partialTicks)
                    DrawType.DRAW -> it.draw(nmouse, partialTicks)
                    DrawType.FOREGROUND -> it.drawForeground(nmouse, partialTicks)
                }
            }
        }
    }

    open fun getColor(mouse: Vec2d): Int {
        return if (disabled) bgColorScheme[ColorIntent.DISABLED] ?: ColorUtil.DISABLED_COLOR.rgba
        else if (highlighted || mouse in this || selected) {
            bgColorScheme[ColorIntent.HOVERED] ?: ColorUtil.DEFAULT_COLOR.rgba
        } else bgColorScheme[ColorIntent.NORMAL] ?: ColorUtil.HOVER_COLOR.rgba
    }

    open fun getTextColor(mouse: Vec2d): Int {
        return if (disabled) fontColorScheme[ColorIntent.DISABLED]
                ?: ColorUtil.DISABLED_FONT_COLOR.rgba else if (highlighted || mouse in this || selected) {
            fontColorScheme[ColorIntent.HOVERED] ?: ColorUtil.HOVER_FONT_COLOR.rgba
        } else if (transparency <= 0.5f) {
            fontColorScheme[ColorIntent.DISABLED] ?: ColorUtil.DISABLED_FONT_COLOR.rgba
        } else fontColorScheme[ColorIntent.NORMAL] ?: ColorUtil.DEFAULT_FONT_COLOR.rgba
    }

    override fun mouseClicked(pos: Vec2d, mouseButton: MouseButton): Boolean {
        if (this.hasOpened && this.elementsSequence.none { it.hasOpened }) {
            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (mouseButton) {
                MouseButton.SCROLL_UP -> {
                    --scroll
                    return true
                }
                MouseButton.SCROLL_DOWN -> {
                    ++scroll
                    return true
                }
            }
        }
        return if (mouseButton == MouseButton.LEFT && pos in this) {
            //First attempt to do any onClick function
            if (!onClickBody(pos, mouseButton)){
                //If no onClick function exists, open sub-elements
                if (elementsSequence.none { it.hasOpened })
                    elementsSequence.forEach { it.open(false) }
                else elementsSequence.forEach { it.close(false) }
            }
            true
        }
        else {
            val children = childrenOrderedForRendering() // childrenOrderedForAppearing
            val c = validElementsSequence.take(7).count()
            var npos = pos - this.pos - vec(childrenXOffset, childrenYOffset - (c + c % 2 - 2) * childrenYSeparator / 2.0)
            var ok = false
            children.forEach {
                ok = it.mouseClicked(npos, mouseButton) || ok
                npos -= vec(childrenXSeparator, childrenYSeparator)
            }
            if (ok) true
            else {
                //TODO onClickOutBody
                false
            }
        }
    }

    fun setBgColor(intent: ColorIntent, rgba: Int): IconElement {
        bgColorScheme[intent] = rgba
        return this
    }

    fun setBgColor(intent: ColorIntent, color: ColorUtil): IconElement = setBgColor(intent, color.rgba)

    fun setFontColor(intent: ColorIntent, rgba: Int): IconElement {
        fontColorScheme[intent] = rgba
        return this
    }

    fun setFontColor(intent: ColorIntent, color: ColorUtil): IconElement = setFontColor(intent, color.rgba)

}