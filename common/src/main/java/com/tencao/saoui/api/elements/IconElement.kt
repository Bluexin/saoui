/*
 * Copyright (C) 2016-2019 Arnaud 'Bluexin' Solé
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.tencao.saoui.api.elements

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.vertex.PoseStack
import com.tencao.saoui.GLCore
import com.tencao.saoui.Vector2d
import com.tencao.saoui.api.elements.registry.DrawType
import com.tencao.saoui.api.screens.IIcon
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.resources.StringNames
import com.tencao.saoui.screens.MouseButton
import com.tencao.saoui.util.*
import com.tencao.saoui.util.math.*
import net.minecraft.resources.ResourceLocation
import org.joml.Vector2d
import java.util.*
import kotlin.collections.set
import kotlin.math.max
import kotlin.math.min

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
open class IconElement(
    val icon: IIcon,
    override var pos: Vector2d = Vector2d(0, 0),
    override var destination: Vector2d = pos,
    var width: Int = 19,
    var height: Int = 19,
    open val description: MutableList<String> = mutableListOf()
) : NeoParent {

    val RES_ITEM_GLINT = ResourceLocation("textures/misc/enchanted_item_glint.png")
    var onClickBody: (Vector2d, MouseButton) -> Boolean = { _, _ -> true }
        private set
    private var onClickOutBody: (Vector2d, MouseButton) -> Unit = { _, _ -> }
    override val elements: MutableList<NeoElement> = mutableListOf()
    override var scroll = -3
        set(value) {
            val c = validElementsSequence.count()
            if (c > 6) field = (value /*+ c*/) % (c)
//            Constants.LOG.info("Result: $field (tried $value)")
        }
    val transparency: Float
        get() {
            var transparency = if (isFocus()) {
                opacity
            } else {
                opacity / 2
            }
            if (transparency < 0f) {
                transparency = 0f
            }
            return transparency
        }
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
    override val childrenXOffset = 25
    override val childrenYSeparator = 20
    override var parent: INeoParent? = null
    override val futureOperations: MutableList<NeoParent.() -> Unit> = LinkedList()
    override var visible = true
    override var highlighted = false
    override var isOpen: Boolean = false
    override var selected = false
    override var disabled = false
    override var opacity = 1f
        set(value) {
            field = value.coerceIn(0f, 1f)
        }
    override var scale = Vector2d(1, 1)
    override fun init() {
        /*+basicAnimation(this, "pos") {
            duration = 20f
            from = Vector2d.ZERO
            easing = Easing.easeInOutQuint
        }*/
    }

    protected fun childrenOrderedForRendering(): Sequence<NeoElement> {
        val count = visibleElementsSequence.count()
        return if (count == 0) emptySequence()
        else {
            val selectedIdx =
                if (visibleElementsSequence.any { it is CategoryButton }) visibleElementsSequence.indexOfFirst { it.highlighted } else -1
            when {
                selectedIdx >= 0 -> {
                    val skipFront = (selectedIdx - (count / 2 - (count + 1) % 2) + count) % count
                    visibleElementsSequence.drop(skipFront) + visibleElementsSequence.take(skipFront)
                }

                visibleElementsSequence.count() < 7 -> visibleElementsSequence
                else -> {
                    val s = visibleElementsSequence + visibleElementsSequence
                    s.drop(min(max((scroll + count) % count, 0), count)).take(min(7, count))
                }
            }
        }
    }

    override fun drawBackground(mouse: Vector2d, partialTicks: Float, poseStack: PoseStack) {
        if (!canDraw) return
        GLCore.pushMatrix()
        if (scale != Vector2d(1, 1)) GLCore.scale(scale.xf, scale.yf, 1f)
        val mouseCheck = mouse in this || selected
        GLCore.glBlend(true)
        GLCore.color(ColorUtil.multiplyAlpha(getColor(mouse), transparency))
        GLCore.glBindTexture(StringNames.gui)
        GLCore.glTexturedRectV2(
            pos.x,
            pos.y,
            width = width.toDouble(),
            height = height.toDouble(),
            srcX = 1.0,
            srcY = 26.0
        )
        if (mouseCheck && OptionCore.MOUSE_OVER_EFFECT.isEnabled && !disabled) {
            mouseOverEffect()
        }
        GLCore.glBlend(false)
        GLCore.color(1f, 1f, 1f, 1f)
        drawChildren(mouse, partialTicks, poseStack, DrawType.BACKGROUND)
        GLCore.popMatrix()
    }

    override fun draw(mouse: Vector2d, partialTicks: Float, poseStack: PoseStack) {
        if (!canDraw) return
        GLCore.pushMatrix()
        GLCore.glBlend(true)
        GLCore.color(ColorUtil.multiplyAlpha(getTextColor(mouse), transparency))
        if (icon.rl != null) {
            GLCore.glBindTexture(icon.rl!!)
        }
        icon.glDraw(pos + vec(1, 1), 5f)

        GLCore.glBlend(false)
        GLCore.color(1f, 1f, 1f, 1f)
        drawChildren(mouse, partialTicks, poseStack, DrawType.DRAW)
        GLCore.popMatrix()
    }

    override fun drawForeground(mouse: Vector2d, partialTicks: Float, poseStack: PoseStack) {
        if (!canDraw) return
        GLCore.pushMatrix()
        if (mouse in this) {
            drawHoveringText(poseStack, mouse)
            GLCore.lighting(false)
        }
        drawChildren(mouse, partialTicks, poseStack, DrawType.FOREGROUND)
        GLCore.popMatrix()
    }

    fun mouseOverEffect() {
        GLCore.glBindTexture(RES_ITEM_GLINT)
        GLCore.depth(true)
        GLCore.depthMask(false)
        GLCore.glDepthFunc(514)
        GLCore.glBlend(true)
        GLCore.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE)
        GLCore.matrixMode(5890)
        GLCore.pushMatrix()
        GLCore.scale(8.0f, 8.0f, 8.0f)
        val f = (getSystemTime() % 3000L).toFloat() / 3000.0f / 8.0f
        GLCore.translate(f, 0.0f, 0.0f)
        GLCore.glRotate(-50.0f, 0.0f, 0.0f, 1.0f)
        GLCore.glTexturedRectV2(pos.x, pos.y, width = width.toDouble(), height = height.toDouble())
        GLCore.popMatrix()
        GLCore.pushMatrix()
        GLCore.scale(8.0f, 8.0f, 8.0f)
        val f1 = (getSystemTime() % 4873L).toFloat() / 4873.0f / 8.0f
        GLCore.translate(-f1, 0.0f, 0.0f)
        GLCore.glRotate(10.0f, 0.0f, 0.0f, 1.0f)
        GLCore.glTexturedRectV2(pos.x, pos.y, width = width.toDouble(), height = height.toDouble())
        GLCore.popMatrix()
        GLCore.matrixMode(5888)
        GLCore.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        GLCore.glDepthFunc(515)
        GLCore.depthMask(true)
        // GLCore.depth(false)
    }

    open fun drawHoveringText(poseStack: PoseStack, mouse: Vector2d) {
        Client.minecraft.screen?.renderComponentTooltip(poseStack, description.map { it.toTextComponent() }, mouse.xi, mouse.yi)
    }

    fun drawChildren(mouse: Vector2d, partialTicks: Float, poseStack: PoseStack, drawType: DrawType) {
        var nmouse = mouse - pos - vec(childrenXOffset, childrenYOffset)
        if (visibleElementsSequence.count() > 0) {
            val c = visibleElementsSequence.take(7).count()
            val centering = ((c + c % 2 - 2) * childrenYSeparator) / 2.0
            GLCore.translate(pos.x + childrenXOffset, pos.y + childrenYOffset - centering, 0.0)
            nmouse = mouse - pos - vec(childrenXOffset, childrenYOffset - centering)
            childrenOrderedForRendering().forEachIndexed { i, it ->
                if (c == 7 && (i == 0 || i == 6)) it.opacity /= 2
                when (drawType) {
                    DrawType.BACKGROUND -> it.drawBackground(nmouse, partialTicks, poseStack)
                    DrawType.DRAW -> it.draw(nmouse, partialTicks, poseStack)
                    DrawType.FOREGROUND -> it.drawForeground(nmouse, partialTicks, poseStack)
                }
                GLCore.translate(childrenXSeparator.toDouble(), childrenYSeparator.toDouble(), 0.0)
                nmouse -= vec(childrenXSeparator, childrenYSeparator)
                if (c == 7 && (i == 0 || i == 6)) it.opacity *= 2
            }
            nmouse = mouse - pos - vec(childrenXOffset, childrenYOffset - centering)
        }

        otherElementsSequence.forEach {
            when (drawType) {
                DrawType.BACKGROUND -> it.drawBackground(nmouse, partialTicks, poseStack)
                DrawType.DRAW -> it.draw(nmouse, partialTicks, poseStack)
                DrawType.FOREGROUND -> it.drawForeground(nmouse, partialTicks, poseStack)
            }
        }
    }

    open fun getColor(mouse: Vector2d): Int {
        return if (disabled) bgColorScheme[ColorIntent.DISABLED] ?: ColorUtil.DISABLED_COLOR.rgba
        else if (highlighted || mouse in this || selected) {
            bgColorScheme[ColorIntent.HOVERED] ?: ColorUtil.DEFAULT_COLOR.rgba
        } else bgColorScheme[ColorIntent.NORMAL] ?: ColorUtil.HOVER_COLOR.rgba
    }

    open fun getTextColor(mouse: Vector2d): Int {
        return if (disabled) fontColorScheme[ColorIntent.DISABLED]
            ?: ColorUtil.DISABLED_FONT_COLOR.rgba else if (highlighted || mouse in this || selected) {
            fontColorScheme[ColorIntent.HOVERED] ?: ColorUtil.HOVER_FONT_COLOR.rgba
        } else if (transparency <= 0.5f) {
            fontColorScheme[ColorIntent.DISABLED] ?: ColorUtil.DISABLED_FONT_COLOR.rgba
        } else fontColorScheme[ColorIntent.NORMAL] ?: ColorUtil.DEFAULT_FONT_COLOR.rgba
    }

    override fun mouseClicked(pos: Vector2d, mouseButton: MouseButton): Boolean {
        if (this.parent is CategoryButton && this.highlighted && this.elementsSequence.none { it is CategoryButton && it.highlighted }) {
            when (mouseButton) {
                MouseButton.SCROLL_UP -> {
                    --scroll
                    return true
                }

                MouseButton.SCROLL_DOWN -> {
                    ++scroll
                    return true
                }

                else -> Unit
            }
        }
        return if (mouseButton == MouseButton.LEFT && pos in this) onClickBody(pos, mouseButton)
        else {
            val children = childrenOrderedForRendering() // childrenOrderedForAppearing
            val c = validElementsSequence.take(7).count()
            var npos =
                pos - this.pos - vec(childrenXOffset, childrenYOffset - (c + c % 2 - 2) * childrenYSeparator / 2.0)
            var ok = false
            children.forEach {
                ok = it.mouseClicked(npos, mouseButton) || ok
                npos -= vec(childrenXSeparator, childrenYSeparator)
            }
            if (ok) true
            else {
                onClickOutBody(pos, mouseButton)
                false
            }
        }
    }

    override fun keyTyped(typedChar: String, keyCode: Int) {
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
    open fun onClick(body: (Vector2d, MouseButton) -> Boolean): NeoElement {
        onClickBody = body
        return this
    }

    open fun onClickOut(body: (Vector2d, MouseButton) -> Unit): NeoElement {
        onClickOutBody = body
        return this
    }

    override fun hide() {
        visible = false
    }

    override fun show() {
        visible = true
//        Constants.LOG.info("Showing $this")
    }

    override fun toString(): String {
        return "NeoIconElement(icon=$icon, pos=$pos, destination=$destination, visible=$visible, selected=$highlighted, disabled=$disabled, opacity=$opacity)"
    }
}
