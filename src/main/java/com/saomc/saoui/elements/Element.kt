package com.saomc.saoui.elements

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.elements.controllers.IController
import com.saomc.saoui.resources.StringNames
import com.saomc.saoui.screens.util.ItemIcon
import com.saomc.saoui.util.ColorIntent
import com.saomc.saoui.util.ColorUtil
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.clamp
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.BoundingBox2D
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.config.GuiUtils

open class Element(val icon: IIcon, override var controllingParent: IController,
                   open override var pos: Vec2d = Vec2d.ZERO,
                   override var destination: Vec2d = pos,
                   var width: Int = 19,
                   var height: Int = 19,
                   open val description: MutableList<String> = mutableListOf(),
                   override var function: () -> Unit = {}): IElement {


    val transparency: Float
    get() {
        var transparency = if (isFocus())
            opacity
        else
            opacity / 2
        if (transparency < 0f) transparency = 0f
        return transparency
    }

    val canDraw: Boolean
            get() = opacity >= 0.03 || scale != Vec2d.ZERO


    override var boundingBox = BoundingBox2D(pos, pos + vec(20, 20))

    override var scroll: Int = -3
        set(value) {
            val c = controllingParent.validElementsSequence.count()
            if (c > 6) field = (value) % (c)
        }

    override var opacity = 1f
        set(value) {
            field = value.clamp(0f, 1f)
        }

    override var scale = Vec2d.ONE

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
        GLCore.popMatrix()
    }

    override fun drawForeground(mouse: Vec2d, partialTicks: Float) {
        if (!canDraw) return
        if (mouse in this) {
            drawHoveringText(mouse)
            GLCore.lighting(false)
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
        } else fontColorScheme[ColorIntent.NORMAL] ?: ColorUtil.DEFAULT_FONT_COLOR.rgba
    }

    open fun mouseOverEffect(){
        GLCore.glBindTexture(RES_ITEM_GLINT)
        GLCore.depth(true)
        GlStateManager.depthMask(false)
        GlStateManager.depthFunc(514)
        GLCore.glBlend(true)
        GLCore.blendFunc(GlStateManager.SourceFactor.SRC_COLOR.factor, GlStateManager.DestFactor.ONE.factor)
        GlStateManager.matrixMode(5890)
        GLCore.pushMatrix()
        GLCore.scale(8.0f, 8.0f, 8.0f)
        val f = (net.minecraft.client.Minecraft.getSystemTime() % 3000L).toFloat() / 3000.0f / 8.0f
        GLCore.translate(f, 0.0f, 0.0f)
        GLCore.glRotatef(-50.0f, 0.0f, 0.0f, 1.0f)
        GLCore.glTexturedRectV2(pos.x, pos.y, width = width.toDouble(), height = height.toDouble())
        GLCore.popMatrix()
        GLCore.pushMatrix()
        GLCore.scale(8.0f, 8.0f, 8.0f)
        val f1 = (net.minecraft.client.Minecraft.getSystemTime() % 4873L).toFloat() / 4873.0f / 8.0f
        GLCore.translate(-f1, 0.0f, 0.0f)
        GLCore.glRotatef(10.0f, 0.0f, 0.0f, 1.0f)
        GLCore.glTexturedRectV2(pos.x, pos.y, width = width.toDouble(), height = height.toDouble())
        GLCore.popMatrix()
        GlStateManager.matrixMode(5888)
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        GlStateManager.depthFunc(515)
        GlStateManager.depthMask(true)
        GLCore.depth(false)
    }

    open fun drawHoveringText(mouse: Vec2d) {
        GuiUtils.drawHoveringText(description, mouse.xi, mouse.yi, Minecraft().displayWidth, Minecraft().displayHeight, -1, GLCore.glFont)
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

    fun setBgColor(intent: ColorIntent, rgba: Int): Element {
        bgColorScheme[intent] = rgba
        return this
    }

    fun setBgColor(intent: ColorIntent, color: ColorUtil): Element = setBgColor(intent, color.rgba)

    fun setFontColor(intent: ColorIntent, rgba: Int): Element {
        fontColorScheme[intent] = rgba
        return this
    }

    fun setFontColor(intent: ColorIntent, color: ColorUtil): Element = setFontColor(intent, color.rgba)

    companion object{
        val RES_ITEM_GLINT = ResourceLocation("textures/misc/enchanted_item_glint.png")
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

}