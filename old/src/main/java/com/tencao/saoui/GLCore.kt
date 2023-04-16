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

package com.tencao.saoui

import com.tencao.saomclib.Client
import com.tencao.saomclib.utils.math.Vec2d
import com.tencao.saomclib.utils.math.vec
import com.tencao.saoui.themes.ThemeManager
import com.tencao.saoui.util.ColorUtil
import com.tencao.saoui.util.append
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import kotlin.math.max
import kotlin.math.min

@SideOnly(Side.CLIENT)
object GLCore {

    private val mc = Minecraft.getMinecraft()
    val glFont: FontRenderer get() = mc.fontRenderer
    private val glTextureManager get() = mc.textureManager
    val tessellator: Tessellator
        get() = Tessellator.getInstance()
    val bufferBuilder: BufferBuilder
        get() = tessellator.buffer

    @JvmOverloads
    fun color(red: Float, green: Float, blue: Float, alpha: Float = 1f) {
        GlStateManager.color(red, green, blue, alpha)
    }

    fun color(color: ColorUtil) {
        color(color.rgba)
    }

    fun color(rgba: Int) {
        val red = (rgba shr 24 and 0xFF).toFloat() / 0xFF
        val green = (rgba shr 16 and 0xFF).toFloat() / 0xFF
        val blue = (rgba shr 8 and 0xFF).toFloat() / 0xFF
        val alpha = (rgba and 0xFF).toFloat() / 0xFF

        color(red, green, blue, alpha)
    }

    fun color(rgba: Int, lightLevel: Float) {
        val red = (rgba shr 24 and 0xFF).toFloat() / 0xFF
        val green = (rgba shr 16 and 0xFF).toFloat() / 0xFF
        val blue = (rgba shr 8 and 0xFF).toFloat() / 0xFF
        val alpha = (rgba and 0xFF).toFloat() / 0xFF
        val light = max(lightLevel, 0.15f)

        color(red * light, green * light, blue * light, alpha)
    }

    private fun glFontColor(rgba: Int): Int {
        val alpha = rgba and 0xFF
        val red = rgba shr 24 and 0xFF
        val blue = rgba shr 8 and 0xFF
        val green = rgba shr 16 and 0xFF

        return alpha shl 24 or (red shl 16) or (blue shl 8) or green
    }

    @JvmOverloads
    fun glString(
        font: FontRenderer?,
        string: String,
        x: Int,
        y: Int,
        argb: Int,
        shadow: Boolean = false,
        centered: Boolean = false
    ) {
        font?.drawString(
            string,
            x.toFloat(),
            y.toFloat() - if (centered) font.FONT_HEIGHT / 2f else 0f,
            glFontColor(argb),
            shadow
        )
    }

    @JvmOverloads
    fun glString(string: String, x: Int, y: Int, argb: Int, shadow: Boolean = false, centered: Boolean = false) {
        glString(glFont, string, x, y, argb, shadow, centered)
    }

    @JvmOverloads
    fun glString(string: String, pos: Vec2d, argb: Int, shadow: Boolean = false, centered: Boolean = false) {
        glString(string, pos.xi, pos.yi, argb, shadow, centered)
    }

    fun setFont(mc: Minecraft, custom: Boolean) {
        if (mc.textureManager == null) return

        val fontLocation: ResourceLocation =
            if (custom) {
                takeTextureIfExists(
                    ThemeManager.currentTheme.texturesRoot.append("ascii.png")
                ) ?: takeTextureIfExists(
                    ResourceLocation(
                        ThemeManager.currentTheme.themeRoot.resourceDomain,
                        "textures/ascii.png"
                    )
                ) ?: ResourceLocation(SAOCore.MODID, "textures/ascii.png")
            } else {
                ResourceLocation("textures/font/ascii.png")
            }
        mc.fontRenderer = FontRenderer(mc.gameSettings, fontLocation, mc.textureManager, false)
        if (mc.gameSettings.language != null) {
            mc.fontRenderer.unicodeFlag = mc.isUnicode
            mc.fontRenderer.bidiFlag = mc.languageManager.isCurrentLanguageBidirectional
        }
        (mc.resourceManager as IReloadableResourceManager).registerReloadListener(mc.fontRenderer)
    }

    @JvmOverloads
    fun glStringWidth(string: String, font: FontRenderer? = glFont): Int {
        return font?.getStringWidth(string) ?: 0
    }

    @JvmOverloads
    fun glStringHeight(font: FontRenderer? = glFont): Int {
        return font?.FONT_HEIGHT ?: 0
    }

    @JvmOverloads
    fun glBindTexture(location: ResourceLocation, textureManager: TextureManager = glTextureManager) {
        textureManager.bindTexture(location)
    }

    /**
     * Checks to make sure the texture is valid, returning false means the texture is invalid.
     */
    fun checkTexture(location: ResourceLocation): Boolean {
        return try {
            Client.minecraft.resourceManager.getResource(location)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun takeTextureIfExists(location: ResourceLocation): ResourceLocation? =
        if (checkTexture(location)) location else null

    @JvmOverloads
    fun glTexturedRectV2(
        pos: Vec3d,
        size: Vec2d,
        srcPos: Vec2d = Vec2d.ZERO,
        srcSize: Vec2d = size,
        textureSize: Vec2d = vec(256, 256)
    ) {
        glTexturedRectV2(
            x = pos.x, y = pos.y, z = pos.z,
            width = size.x, height = size.y,
            srcX = srcPos.x, srcY = srcPos.y,
            srcWidth = srcSize.x, srcHeight = srcSize.y,
            textureW = textureSize.xi, textureH = textureSize.yi
        )
    }

    @JvmOverloads
    fun glTexturedRectV2(
        x: Double,
        y: Double,
        z: Double = 0.0,
        width: Double,
        height: Double,
        srcX: Double = 0.0,
        srcY: Double = 0.0,
        srcWidth: Double = width,
        srcHeight: Double = height,
        textureW: Int = 256,
        textureH: Int = 256
    ) {
        val f = 1f / textureW
        val f1 = 1f / textureH
        begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        bufferBuilder.pos(x, y + height, z)
            .tex((srcX.toFloat() * f).toDouble(), ((srcY + srcHeight).toFloat() * f1).toDouble()).endVertex()
        bufferBuilder.pos(x + width, y + height, z)
            .tex(((srcX + srcWidth).toFloat() * f).toDouble(), ((srcY + srcHeight).toFloat() * f1).toDouble())
            .endVertex()
        bufferBuilder.pos(x + width, y, z)
            .tex(((srcX + srcWidth).toFloat() * f).toDouble(), (srcY.toFloat() * f1).toDouble()).endVertex()
        bufferBuilder.pos(x, y, z).tex((srcX.toFloat() * f).toDouble(), (srcY.toFloat() * f1).toDouble()).endVertex()
        draw()
    }

    fun addVertex(x: Double, y: Double, z: Double) {
        bufferBuilder.pos(x, y, z).endVertex()
    }

    fun addVertex(x: Double, y: Double, z: Double, srcX: Double, srcY: Double) {
        bufferBuilder.pos(x, y, z).tex(srcX, srcY).endVertex()
    }

    fun addVertex(
        x: Double,
        y: Double,
        z: Double,
        srcX: Double,
        srcY: Double,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        bufferBuilder.pos(x, y, z).tex(srcX, srcY).color(red, green, blue, alpha).endVertex()
    }

    fun addVertex(x: Double, y: Double, z: Double, red: Float, green: Float, blue: Float, alpha: Float) {
        bufferBuilder.pos(x, y, z).color(red, green, blue, alpha).endVertex()
    }

    fun addVertex(
        x: Float,
        y: Float,
        z: Float,
        srcX: Double,
        srcY: Double,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        bufferBuilder.normal(x, y, z).tex(srcX, srcY).color(red, green, blue, alpha).endVertex()
    }

    @JvmOverloads
    fun begin(glMode: Int = GL11.GL_QUADS, format: VertexFormat = DefaultVertexFormats.POSITION_TEX_COLOR) {
        bufferBuilder.begin(glMode, format)
    }

    fun draw() {
        tessellator.draw()
    }

    fun glRect(x: Int, y: Int, width: Int, height: Int) {
        begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        bufferBuilder.pos(x.toDouble(), (y + height).toDouble(), 0.0)
        bufferBuilder.pos((x + width).toDouble(), (y + height).toDouble(), 0.0)
        bufferBuilder.pos((x + width).toDouble(), y.toDouble(), 0.0)
        bufferBuilder.pos(x.toDouble(), y.toDouble(), 0.0)
        draw()
    }

    /**
     * Draws a rectangle with a vertical gradient between the specified colors (ARGB format). Args : x1, y1, x2, y2,
     * topColor, bottomColor
     */
    fun drawGradientRect(
        left: Double,
        top: Double,
        right: Double,
        bottom: Double,
        zLevel: Double,
        startColor: Int,
        endColor: Int
    ) {
        val f = (startColor shr 24 and 255).toFloat() / 255.0f
        val f1 = (startColor shr 16 and 255).toFloat() / 255.0f
        val f2 = (startColor shr 8 and 255).toFloat() / 255.0f
        val f3 = (startColor and 255).toFloat() / 255.0f
        val f4 = (endColor shr 24 and 255).toFloat() / 255.0f
        val f5 = (endColor shr 16 and 255).toFloat() / 255.0f
        val f6 = (endColor shr 8 and 255).toFloat() / 255.0f
        val f7 = (endColor and 255).toFloat() / 255.0f
        GlStateManager.disableTexture2D()
        GlStateManager.enableBlend()
        GlStateManager.disableAlpha()
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO
        )
        GlStateManager.shadeModel(7425)
        begin(7, DefaultVertexFormats.POSITION_COLOR)
        bufferBuilder.pos(right, top, zLevel).color(f1, f2, f3, f).endVertex()
        bufferBuilder.pos(left, top, zLevel).color(f1, f2, f3, f).endVertex()
        bufferBuilder.pos(left, bottom, zLevel).color(f5, f6, f7, f4).endVertex()
        bufferBuilder.pos(right, bottom, zLevel).color(f5, f6, f7, f4).endVertex()
        draw()
        GlStateManager.shadeModel(7424)
        GlStateManager.disableBlend()
        GlStateManager.enableAlpha()
        GlStateManager.enableTexture2D()
    }

    fun glAlphaTest(flag: Boolean) {
        if (flag) {
            GlStateManager.enableAlpha()
        } else {
            GlStateManager.disableAlpha()
        }
    }

    fun alphaFunc(src: Int, dst: Int) {
        GlStateManager.alphaFunc(src, dst.toFloat())
    }

    fun glBlend(flag: Boolean) {
        if (flag) {
            GlStateManager.enableBlend()
        } else {
            GlStateManager.disableBlend()
        }
    }

    fun blendFunc(src: Int, dst: Int) {
        GlStateManager.blendFunc(src, dst)
    }

    fun tryBlendFuncSeparate(a: Int, b: Int, c: Int, d: Int) {
        GlStateManager.tryBlendFuncSeparate(a, b, c, d)
    }

    fun depthMask(flag: Boolean) {
        GlStateManager.depthMask(flag)
    }

    fun depth(flag: Boolean) {
        if (flag) {
            GlStateManager.enableDepth()
        } else {
            GlStateManager.disableDepth()
        }
    }

    fun glDepthFunc(flag: Int) {
        GL11.glDepthFunc(flag)
    }

    fun glRescaleNormal(flag: Boolean) {
        if (flag) {
            GlStateManager.enableRescaleNormal()
        } else {
            GlStateManager.disableRescaleNormal()
        }
    }

    fun glTexture2D(flag: Boolean) {
        if (flag) {
            GL11.glEnable(GL11.GL_TEXTURE_2D)
        } else {
            GL11.glDisable(GL11.GL_TEXTURE_2D)
        }
    }

    fun glCullFace(flag: Boolean) {
        if (flag) {
            GlStateManager.enableCull()
        } else {
            GlStateManager.disableCull()
        }
    }

    fun glTranslatef(x: Float, y: Float, z: Float) {
        GlStateManager.translate(x, y, z)
    }

    fun glNormal3f(x: Float, y: Float, z: Float) {
        GL11.glNormal3f(x, y, z)
    }

    fun glRotatef(angle: Float, x: Float, y: Float, z: Float) {
        GlStateManager.rotate(angle, x, y, z)
    }

    fun glScalef(x: Float, y: Float, z: Float) {
        GlStateManager.scale(x, y, z)
    }

    fun lighting(flag: Boolean) {
        if (flag) {
            GlStateManager.enableLighting()
        } else {
            GlStateManager.disableLighting()
        }
    }

    fun pushMatrix() {
        GlStateManager.pushMatrix()
    }

    fun popMatrix() {
        GlStateManager.popMatrix()
    }

    /**
     * returns an AABB with corners x1, y1, z1 and x2, y2, z2
     */
    fun fromBounds(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): AxisAlignedBB {
        val d0 = min(x1, x2)
        val d1 = min(y1, y2)
        val d2 = min(z1, z2)
        val d3 = max(x1, x2)
        val d4 = max(y1, y2)
        val d5 = max(z1, z2)
        return AxisAlignedBB(d0, d1, d2, d3, d4, d5)
    }

    fun translate(x: Double, y: Double, z: Double) {
        GlStateManager.translate(x, y, z)
    }

    fun translate(x: Float, y: Float, z: Float) {
        GlStateManager.translate(x, y, z)
    }

    fun scale(x: Double, y: Double, z: Double) {
        GlStateManager.scale(x, y, z)
    }

    fun scale(x: Float, y: Float, z: Float) {
        GlStateManager.scale(x, y, z)
    }
}
