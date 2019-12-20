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

package com.saomc.saoui.api.elements

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.resources.StringNames
import com.saomc.saoui.screens.ItemIcon
import com.saomc.saoui.screens.MouseButton
import com.saomc.saoui.util.ColorIntent
import com.saomc.saoui.util.ColorUtil
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.clamp
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.BoundingBox2D
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import kotlin.math.max
import kotlin.math.min

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
open class IconElement(val icon: IIcon, override var pos: Vec2d = Vec2d.ZERO, override var destination: Vec2d = pos, var width: Int = 19, var height: Int = 19) : NeoParent() {


    val RES_ITEM_GLINT = ResourceLocation("textures/misc/enchanted_item_glint.png")
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
        val count = visibleElementsSequence.count()
        return if (count == 0) emptySequence()
        else {
            val selectedIdx = if (visibleElementsSequence.any { it is CategoryButton }) visibleElementsSequence.indexOfFirst { it.selected } else -1
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

    override fun draw(mouse: Vec2d, partialTicks: Float) {
        if (opacity < 0.03 || scale == Vec2d.ZERO) return
        GLCore.pushMatrix()
        if (scale != Vec2d.ONE) GLCore.glScalef(scale.xf, scale.yf, 1f)
        GLCore.color(ColorUtil.multiplyAlpha(getColor(mouse), opacity))
        GLCore.glBindTexture(StringNames.gui)
        GLCore.glTexturedRectV2(pos.x, pos.y, width = width.toDouble(), height = height.toDouble(), srcX = 1.0, srcY = 26.0)
        if (OptionCore.MOUSE_OVER_EFFECT.isEnabled && mouse in this) mouseOverEffect()
        GLCore.color(ColorUtil.multiplyAlpha(getTextColor(mouse), opacity))
        if (icon.rl != null) {
            GLCore.glBindTexture(icon.rl!!)
            icon.glDrawUnsafe(pos + vec(1, 1))
        }
        if (icon is ItemIcon)
            icon.glDrawUnsafe(pos + vec(1, 1))

        /*GlStateManager.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)
        GLCore.glBindTexture(StringNames.gui)
        val bb = boundingBox
        GLCore.color(0xFF0000FF.toInt())
        GLCore.glTexturedRectV2(pos = Vec3d(bb.pos.x, bb.pos.y, 0.0), size = bb.size, srcPos = vec(0, 61), srcSize = vec(4, 4))
        GlStateManager.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL)*/

        drawChildren(mouse, partialTicks)
        GLCore.popMatrix()
    }

    fun mouseOverEffect(){
        GLCore.glBindTexture(RES_ITEM_GLINT)
        GlStateManager.depthMask(false)
        GlStateManager.depthFunc(514)
        GLCore.glBlend(true)
        GLCore.blendFunc(GlStateManager.SourceFactor.SRC_COLOR.factor, GlStateManager.DestFactor.ONE.factor)
        GlStateManager.matrixMode(5890)
        GLCore.pushMatrix()
        GLCore.scale(8.0f, 8.0f, 8.0f)
        val f = (Minecraft.getSystemTime() % 3000L).toFloat() / 3000.0f / 8.0f
        GLCore.translate(f, 0.0f, 0.0f)
        GLCore.glRotatef(-50.0f, 0.0f, 0.0f, 1.0f)
        GLCore.glTexturedRectV2(pos.x, pos.y, width = width.toDouble(), height = height.toDouble())
        GLCore.popMatrix()
        GLCore.pushMatrix()
        GLCore.scale(8.0f, 8.0f, 8.0f)
        val f1 = (Minecraft.getSystemTime() % 4873L).toFloat() / 4873.0f / 8.0f
        GLCore.translate(-f1, 0.0f, 0.0f)
        GLCore.glRotatef(10.0f, 0.0f, 0.0f, 1.0f)
        GLCore.glTexturedRectV2(pos.x, pos.y, width = width.toDouble(), height = height.toDouble())
        GLCore.popMatrix()
        GlStateManager.matrixMode(5888)
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        GlStateManager.depthFunc(515)
        GlStateManager.depthMask(true)
    }

    protected open fun drawChildren(mouse: Vec2d, partialTicks: Float) {
        if (visibleElementsSequence.count() > 0) {
            val c = validElementsSequence.take(7).count()
            val centering = ((c + c % 2 - 2) * childrenYSeparator) / 2.0
            GLCore.translate(pos.x + childrenXOffset, pos.y + childrenYOffset - centering, 0.0)
            var nmouse = mouse - pos - vec(childrenXOffset, childrenYOffset - centering)
            childrenOrderedForRendering().forEachIndexed { i, it ->
                if (c == 7 && (i == 0 || i == 6)) it.opacity /= 2
                it.draw(nmouse, partialTicks)
                GLCore.translate(childrenXSeparator.toDouble(), childrenYSeparator.toDouble(), 0.0)
                nmouse -= vec(childrenXSeparator, childrenYSeparator)
                if (c == 7 && (i == 0 || i == 6)) it.opacity *= 2
            }
            nmouse = mouse - pos - vec(childrenXOffset, childrenYOffset - centering)
            otherElementsSequence.forEach {
                it.draw(nmouse, partialTicks)
            }
        }
    }

    open fun getColor(mouse: Vec2d): Int {
        return if (disabled) bgColorScheme[ColorIntent.DISABLED] ?: ColorUtil.DISABLED_COLOR.rgba
        else if (selected || mouse in this) {
            bgColorScheme[ColorIntent.HOVERED] ?: ColorUtil.DEFAULT_COLOR.rgba
        } else bgColorScheme[ColorIntent.NORMAL] ?: ColorUtil.HOVER_COLOR.rgba
    }

    open fun getTextColor(mouse: Vec2d): Int {
        return if (disabled) fontColorScheme[ColorIntent.DISABLED]
                ?: ColorUtil.DISABLED_FONT_COLOR.rgba else if (selected || mouse in this) {
            fontColorScheme[ColorIntent.HOVERED] ?: ColorUtil.HOVER_FONT_COLOR.rgba
        } else fontColorScheme[ColorIntent.NORMAL] ?: ColorUtil.DEFAULT_FONT_COLOR.rgba
    }

    override fun mouseClicked(pos: Vec2d, mouseButton: MouseButton): Boolean {
//        SAOCore.LOGGER.info("$pos $mouseButton Checking $this")
        if (this.parent is CategoryButton && this.selected && this.elementsSequence.none { it is CategoryButton && it.selected }) {
            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (mouseButton) {
                MouseButton.SCROLL_UP -> {
                    --scroll
//                    SAOCore.LOGGER.info("Scroll: $scroll")
                    return true
                }
                MouseButton.SCROLL_DOWN -> {
                    ++scroll
//                    SAOCore.LOGGER.info("Scroll: $scroll")
                    return true
                }
            }
        }
        return if (mouseButton == MouseButton.LEFT && pos in this) onClickBody(pos, mouseButton)
        else {
            val children = childrenOrderedForRendering() // childrenOrderedForAppearing
            val c = validElementsSequence .take(7).count()
//            val c = children.count()
            var npos = pos - this.pos - vec(childrenXOffset, childrenYOffset - (c + c % 2 - 2) * childrenYSeparator / 2.0)
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

    override val childrenXOffset = 25
    override val childrenYSeparator = 20

    open fun onClick(body: (Vec2d, MouseButton) -> Boolean): NeoElement {
        onClickBody = body
        return this
    }

    open fun onClickOut(body: (Vec2d, MouseButton) -> Unit): NeoElement {
        onClickOutBody = body
        return this
    }

    override var visible = true

    override var selected = false

    override var disabled = false

    override var opacity = 1f
        set(value) {
            field = value.clamp(0f, 1f)
        }

    override var scale = Vec2d.ONE

    override fun hide() {
        visible = false
    }

    override fun show() {
        visible = true
//        SAOCore.LOGGER.info("Showing $this")
    }

    override fun toString(): String {
        return "NeoIconElement(icon=$icon, pos=$pos, destination=$destination, visible=$visible, selected=$selected, disabled=$disabled, opacity=$opacity)"
    }
}
