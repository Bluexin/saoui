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

package com.saomc.saoui

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.saomc.saoui.SAOCore.mc
import com.saomc.saoui.themes.ThemeLoader
import com.saomc.saoui.util.ColorUtil
import com.teamwizardry.librarianlib.core.util.kotlin.builder
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.WorldVertexBufferUploader
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.vector.Matrix4f
import net.minecraft.util.math.vector.Vector2f
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.util.text.TranslationTextComponent
import org.lwjgl.opengl.GL11
import kotlin.math.max
import kotlin.math.min

object GLCore {

    val glFont: FontRenderer get() = mc.fontRenderer
    private val glTextureManager get() = mc.textureManager
    val tessellator: Tessellator
        get() = Tessellator.getInstance()
    val bufferBuilder: BufferBuilder = tessellator.buffer

    @JvmOverloads
    fun color(red: Float, green: Float, blue: Float, alpha: Float = 1f) {
        RenderSystem.blendColor(red, green, blue, alpha)
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
    fun glString(font: FontRenderer, matrixStack: MatrixStack, string: String, x: Int, y: Int, argb: Int, shadow: Boolean = false, centered: Boolean = false) {
        if (shadow)
            font.drawTextWithShadow(matrixStack, TranslationTextComponent(string), x.toFloat(), y.toFloat() - if (centered) font.FONT_HEIGHT / 2f else 0f, glFontColor(argb))
        else
            font.drawString(matrixStack, string, x.toFloat(), y.toFloat() - if (centered) font.FONT_HEIGHT / 2f else 0f, glFontColor(argb))
    }

    @JvmOverloads
    fun glString(matrixStack: MatrixStack, string: String, x: Int, y: Int, argb: Int, shadow: Boolean = false, centered: Boolean = false) {
        glString(glFont, matrixStack, string, x, y, argb, shadow, centered)
    }

    @JvmOverloads
    fun glString(matrixStack: MatrixStack,string: String, pos: Vec2d, argb: Int, shadow: Boolean = false, centered: Boolean = false) {
        glString(matrixStack, string, pos.x.toInt(), pos.y.toInt(), argb, shadow, centered)
    }

    fun setFont(mc: Minecraft, custom: Boolean) {
        if (mc.textureManager == null) return
        val font = ResourceLocation(SAOCore.MODID, "textures/${ThemeLoader.currentTheme}/ascii.png")
        val fontLocation: ResourceLocation =
        if (custom && checkTexture(font)){
            font
        }
        else {
            ResourceLocation("textures/font/ascii.png")
        }
        /*
        mc.fontRenderer = FontRenderer(mc.gameSettings, fontLocation, mc.textureManager, false)
        if (mc.gameSettings.language != null) {
            mc.fontRenderer.unicodeFlag = mc.isUnicode
            mc.fontRenderer.bidiFlag = mc.languageManager.isCurrentLanguageBidirectional
        }
        (mc.resourceManager as IReloadableResourceManager).registerReloadListener(mc.fontRenderer)*/
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
            mc.resourceManager.getResource(location)
            true
        } catch (e: Exception) {
            false
        }
    }

    @JvmOverloads
    fun glTexturedRectV2(pos: Vector3d, size: Vector2f, srcPos: Vector2f = Vector2f.ZERO, srcSize: Vector2f = size, textureSize: Vector2f = Vector2f(256f, 256f)) {
        glTexturedRectV2(
                x = pos.x, y = pos.y, z = pos.z,
                width = size.x, height = size.y,
                srcX = srcPos.x, srcY = srcPos.y,
                srcWidth = srcSize.x, srcHeight = srcSize.y,
                textureW = textureSize.x, textureH = textureSize.y
        )
    }

    @JvmOverloads
    @Deprecated("Use version with MatrixStack")
    fun glTexturedRectV2(x: Double, y: Double, z: Double = 0.0, width: Float, height: Float, srcX: Float = 0f, srcY: Float = 0f, srcWidth: Float = width, srcHeight: Float = height, textureW: Float = 256f, textureH: Float = 256f) {
        val f = 1f / textureW
        val f1 = 1f / textureH
        begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        bufferBuilder.pos(x, y + height, z).tex(srcX * f, srcY + srcHeight * f1).endVertex()
        bufferBuilder.pos(x + width, y + height, z).tex(srcX + srcWidth * f, srcY + srcHeight * f1).endVertex()
        bufferBuilder.pos(x + width, y, z).tex(srcX + srcWidth * f, srcY * f1).endVertex()
        bufferBuilder.pos(x, y, z).tex(srcX * f, srcY * f1).endVertex()
        bufferBuilder.finishDrawing()
        RenderSystem.enableAlphaTest()
        WorldVertexBufferUploader.draw(bufferBuilder)
        draw()
    }

    @JvmOverloads
    fun glTexturedRectV2(matrixStack: MatrixStack, x: Float, y: Float, z: Float = 0f, width: Float, height: Float, srcX: Float = 0f, srcY: Float = 0f, srcWidth: Float = width, srcHeight: Float = height, textureW: Float = 256f, textureH: Float = 256f) {
        val f = 1f / textureW
        val f1 = 1f / textureH
        begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        bufferBuilder.pos(matrixStack.last.matrix, x, y + height, z).tex(srcX * f, srcY + srcHeight * f1).endVertex()
        bufferBuilder.pos(matrixStack.last.matrix, x + width, y + height, z).tex(srcX + srcWidth * f, srcY + srcHeight * f1).endVertex()
        bufferBuilder.pos(matrixStack.last.matrix, x + width, y, z).tex(srcX + srcWidth * f, srcY * f1).endVertex()
        bufferBuilder.pos(matrixStack.last.matrix, x, y, z).tex(srcX * f, srcY * f1).endVertex()
        draw()
    }

    fun addVertex(x: Double, y: Double, z: Double) {
        bufferBuilder.pos(x, y, z).endVertex()
    }

    fun addVertex(x: Double, y: Double, z: Double, srcX: Float, srcY: Float) {
        bufferBuilder.pos(x, y, z).tex(srcX, srcY).endVertex()
    }

    fun addVertex(x: Double, y: Double, z: Double, srcX: Float, srcY: Float, red: Float, green: Float, blue: Float, alpha: Float) {
        bufferBuilder.pos(x, y, z).tex(srcX, srcY).color(red, green, blue, alpha).endVertex()
    }

    fun addVertex(x: Double, y: Double, z: Double, red: Float, green: Float, blue: Float, alpha: Float) {
        bufferBuilder.pos(x, y, z).color(red, green, blue, alpha).endVertex()
    }

    fun addVertex(x: Float, y: Float, z: Float, srcX: Float, srcY: Float, red: Float, green: Float, blue: Float, alpha: Float) {
        bufferBuilder.normal(x, y, z).tex(srcX, srcY).color(red, green, blue, alpha).endVertex()
    }

    fun addVertex(matrix: Matrix4f, x: Float, y: Float, z: Float) {
        bufferBuilder.pos(matrix, x, y, z).endVertex()
    }

    fun addVertex(matrix: Matrix4f, x: Float, y: Float, z: Float, srcX: Float, srcY: Float) {
        bufferBuilder.pos(matrix, x, y, z).tex(srcX, srcY).endVertex()
    }

    fun addVertex(matrix: Matrix4f, x: Float, y: Float, z: Float, srcX: Float, srcY: Float, red: Float, green: Float, blue: Float, alpha: Float) {
        bufferBuilder.pos(matrix, x, y, z).tex(srcX, srcY).color(red, green, blue, alpha).endVertex()
    }

    fun addVertex(matrix: Matrix4f, x: Float, y: Float, z: Float, red: Float, green: Float, blue: Float, alpha: Float) {
        bufferBuilder.pos(matrix, x, y, z).color(red, green, blue, alpha).endVertex()
    }

    @JvmOverloads
    fun begin(glMode: Int = GL11.GL_QUADS, format: VertexFormat = DefaultVertexFormats.POSITION_TEX_COLOR) {
        bufferBuilder.begin(glMode, format)
    }

    fun draw() {
        tessellator.draw()
    }

    @Deprecated("Use below")
    fun glRect(x: Int, y: Int, width: Int, height: Int) {
        begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        bufferBuilder.pos(x.toDouble(), (y + height).toDouble(), 0.0)
        bufferBuilder.pos((x + width).toDouble(), (y + height).toDouble(), 0.0)
        bufferBuilder.pos((x + width).toDouble(), y.toDouble(), 0.0)
        bufferBuilder.pos(x.toDouble(), y.toDouble(), 0.0)
        draw()
    }

    fun glRect(matrix: Matrix4f, x: Int, y: Int, width: Int, height: Int) {
        begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        bufferBuilder.pos(x.toDouble(), (y + height).toDouble(), 0.0)
        bufferBuilder.pos((x + width).toDouble(), (y + height).toDouble(), 0.0)
        bufferBuilder.pos((x + width).toDouble(), y.toDouble(), 0.0)
        bufferBuilder.pos(x.toDouble(), y.toDouble(), 0.0)
        draw()
    }



    private fun innerBlit(
        matrix: Matrix4f,
        x1: Int,
        x2: Int,
        y1: Int,
        y2: Int,
        blitOffset: Int,
        minU: Float,
        maxU: Float,
        minV: Float,
        maxV: Float
    ) {
        val bufferbuilder = Tessellator.getInstance().buffer
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
        bufferbuilder.pos(matrix, x1.toFloat(), y2.toFloat(), blitOffset.toFloat()).tex(minU, maxV).endVertex()
        bufferbuilder.pos(matrix, x2.toFloat(), y2.toFloat(), blitOffset.toFloat()).tex(maxU, maxV).endVertex()
        bufferbuilder.pos(matrix, x2.toFloat(), y1.toFloat(), blitOffset.toFloat()).tex(maxU, minV).endVertex()
        bufferbuilder.pos(matrix, x1.toFloat(), y1.toFloat(), blitOffset.toFloat()).tex(minU, minV).endVertex()
        bufferbuilder.finishDrawing()
        RenderSystem.enableAlphaTest()
        WorldVertexBufferUploader.draw(bufferbuilder)
    }

    /**
     * Draws a rectangle with a vertical gradient between the specified colors (ARGB format). Args : x1, y1, x2, y2,
     * topColor, bottomColor
     */
    fun drawGradientRect(left: Double, top: Double, right: Double, bottom: Double, zLevel: Double, startColor: Int, endColor: Int) {
        val f = (startColor shr 24 and 255).toFloat() / 255.0f
        val f1 = (startColor shr 16 and 255).toFloat() / 255.0f
        val f2 = (startColor shr 8 and 255).toFloat() / 255.0f
        val f3 = (startColor and 255).toFloat() / 255.0f
        val f4 = (endColor shr 24 and 255).toFloat() / 255.0f
        val f5 = (endColor shr 16 and 255).toFloat() / 255.0f
        val f6 = (endColor shr 8 and 255).toFloat() / 255.0f
        val f7 = (endColor and 255).toFloat() / 255.0f
        RenderSystem.disableTexture()
        RenderSystem.enableBlend()
        RenderSystem.disableAlphaTest()
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
        RenderSystem.shadeModel(7425)
        begin(7, DefaultVertexFormats.POSITION_COLOR)
        bufferBuilder.pos(right, top, zLevel).color(f1, f2, f3, f).endVertex()
        bufferBuilder.pos(left, top, zLevel).color(f1, f2, f3, f).endVertex()
        bufferBuilder.pos(left, bottom, zLevel).color(f5, f6, f7, f4).endVertex()
        bufferBuilder.pos(right, bottom, zLevel).color(f5, f6, f7, f4).endVertex()
        draw()
        RenderSystem.shadeModel(7424)
        RenderSystem.disableBlend()
        RenderSystem.enableAlphaTest()
        RenderSystem.enableTexture()
    }

    /**
     * Draws a rectangle with a vertical gradient between the specified colors (ARGB format). Args : x1, y1, x2, y2,
     * topColor, bottomColor
     */
    fun drawGradientRect(mstack: Matrix4f, left: Float, top: Float, right: Float, bottom: Float, zLevel: Float, startColor: Int, endColor: Int = startColor) {
        val f = (startColor shr 24 and 255).toFloat() / 255.0f
        val f1 = (startColor shr 16 and 255).toFloat() / 255.0f
        val f2 = (startColor shr 8 and 255).toFloat() / 255.0f
        val f3 = (startColor and 255).toFloat() / 255.0f
        val f4 = (endColor shr 24 and 255).toFloat() / 255.0f
        val f5 = (endColor shr 16 and 255).toFloat() / 255.0f
        val f6 = (endColor shr 8 and 255).toFloat() / 255.0f
        val f7 = (endColor and 255).toFloat() / 255.0f
        RenderSystem.disableTexture()
        RenderSystem.enableBlend()
        RenderSystem.disableAlphaTest()
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
        RenderSystem.shadeModel(7425)
        begin(7, DefaultVertexFormats.POSITION_COLOR)
        bufferBuilder.pos(mstack, right, top, zLevel).color(f1, f2, f3, f).endVertex()
        bufferBuilder.pos(mstack, left, top, zLevel).color(f1, f2, f3, f).endVertex()
        bufferBuilder.pos(mstack, left, bottom, zLevel).color(f5, f6, f7, f4).endVertex()
        bufferBuilder.pos(mstack, right, bottom, zLevel).color(f5, f6, f7, f4).endVertex()
        draw()
        RenderSystem.shadeModel(7424)
        RenderSystem.disableBlend()
        RenderSystem.enableAlphaTest()
        RenderSystem.enableTexture()
    }


    @Deprecated("Use RenderType",
        ReplaceWith("RenderType", "net.minecraft.client.renderer")
    )
    fun glAlphaTest(flag: Boolean) {
        if (flag)
            RenderSystem.enableAlphaTest()
        else
            RenderSystem.disableAlphaTest()
    }

    @Deprecated("Use RenderType",
        ReplaceWith("RenderType", "net.minecraft.client.renderer")
    )
    fun alphaFunc(src: Int, dst: Int) {
        RenderSystem.alphaFunc(src, dst.toFloat())
    }

    fun glBlend(flag: Boolean) {
        if (flag)
            RenderSystem.enableBlend()
        else
            RenderSystem.disableBlend()
    }

    fun blendFunc(src: Int, dst: Int) {
        RenderSystem.blendFunc(src, dst)
    }

    fun tryBlendFuncSeparate(a: Int, b: Int, c: Int, d: Int) {
        RenderSystem.blendFuncSeparate(a, b, c, d)
    }

    fun depthMask(flag: Boolean) {
        RenderSystem.depthMask(flag)
    }

    fun depth(flag: Boolean) {
        if (flag)
            RenderSystem.enableDepthTest()
        else
            RenderSystem.disableDepthTest()
    }

    fun glDepthFunc(flag: Int) {
        RenderSystem.depthFunc(flag)
    }

    @Deprecated("Use RenderType",
        ReplaceWith("RenderType", "net.minecraft.client.renderer")
    )
    fun glRescaleNormal(flag: Boolean) {
        if (flag)
            RenderSystem.enableRescaleNormal()
        else
            RenderSystem.disableRescaleNormal()
    }

    fun glTexture2D(flag: Boolean) {
        if (flag)
            RenderSystem.enableTexture()
        else
            RenderSystem.disableTexture()
    }

    fun glCullFace(flag: Boolean) {
        if (flag)
            RenderSystem.enableCull()
        else
            RenderSystem.disableCull()
    }

    @Deprecated("Use MatrixStack",
        ReplaceWith("MatrixStack.translate(x, y, z)", "com.mojang.blaze3d.matrix.MatrixStack")
    )
    fun glTranslatef(x: Float, y: Float, z: Float) {
        RenderSystem.translatef(x, y, z)
    }

    fun glNormal3f(x: Float, y: Float, z: Float) {
        RenderSystem.normal3f(x, y, z)
    }

    @Deprecated("Use MatrixStack",
        ReplaceWith("MatrixStack.rotate(quaternion)", "com.mojang.blaze3d.matrix.MatrixStack")
    )
    fun glRotatef(angle: Float, x: Float, y: Float, z: Float) {
        RenderSystem.rotatef(angle, x, y, z)
    }

    @Deprecated("Use MatrixStack",
        ReplaceWith("MatrixStack.scale(x, y, z)", "com.mojang.blaze3d.matrix.MatrixStack")
    )
    fun glScalef(x: Float, y: Float, z: Float) {
        RenderSystem.scalef(x, y, z)
    }

    @Deprecated("Use RenderType",
        ReplaceWith("RenderType", "net.minecraft.client.renderer")
    )
    fun lighting(flag: Boolean) {
        if (flag)
            RenderSystem.enableLighting()
        else
            RenderSystem.disableLighting()
    }

    @Deprecated("Use MatrixStack",
        ReplaceWith("MatrixStack.push()", "com.mojang.blaze3d.matrix.MatrixStack")
    )
    fun pushMatrix() {
        RenderSystem.pushMatrix()
    }

    @Deprecated("Use MatrixStack",
        ReplaceWith("MatrixStack.pop()", "com.mojang.blaze3d.matrix.MatrixStack")
    )
    fun popMatrix() {
        RenderSystem.popMatrix()
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

    @Deprecated("Use MatrixStack",
        ReplaceWith("MatrixStack.translate(x, y, z)", "com.mojang.blaze3d.matrix.MatrixStack")
    )
    fun translate(x: Double, y: Double, z: Double) {
        RenderSystem.translated(x, y, z)
    }

    @Deprecated("Use MatrixStack",
        ReplaceWith("MatrixStack.translate(x, y, z)", "com.mojang.blaze3d.matrix.MatrixStack")
    )
    fun translate(x: Float, y: Float, z: Float) {
        RenderSystem.translatef(x, y, z)
    }

    @Deprecated("Use MatrixStack",
        ReplaceWith("MatrixStack.scale(x, y, z)", "com.mojang.blaze3d.matrix.MatrixStack")
    )
    fun scale(x: Double, y: Double, z: Double) {
        RenderSystem.scaled(x, y, z)
    }

    @Deprecated("Use MatrixStack",
        ReplaceWith("MatrixStack.scale(x, y, z)", "com.mojang.blaze3d.matrix.MatrixStack")
    )
    fun scale(x: Float, y: Float, z: Float) {
        RenderSystem.scalef(x, y, z)
    }
}
