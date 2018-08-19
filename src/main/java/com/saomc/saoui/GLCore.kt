package com.saomc.saoui

import com.saomc.saoui.util.ColorUtil
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
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

@SideOnly(Side.CLIENT)
object GLCore {

    private val mc = Minecraft.getMinecraft()
    private val glFont get() = mc.fontRenderer
    private val glTextureManager get() = mc.textureManager

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

    private fun glFontColor(rgba: Int): Int {
        val alpha = rgba and 0xFF
        val red = rgba shr 24 and 0xFF
        val blue = rgba shr 8 and 0xFF
        val green = rgba shr 16 and 0xFF

        return alpha shl 24 or (red shl 16) or (blue shl 8) or green
    }

    @JvmOverloads
    fun glString(font: FontRenderer?, string: String, x: Int, y: Int, argb: Int, shadow: Boolean = false, centered: Boolean = false) {
        font?.drawString(string, x.toFloat(), y.toFloat() - if (centered) font.FONT_HEIGHT / 2f else 0f, glFontColor(argb), shadow)
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
        val fontLocation = if (custom) ResourceLocation(SAOCore.MODID, "textures/ascii.png") else ResourceLocation("textures/font/ascii.png")
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

    @JvmOverloads
    fun glTexturedRectV2(pos: Vec3d, size: Vec2d, srcPos: Vec2d = Vec2d.ZERO, srcSize: Vec2d = size, textureSize: Vec2d = vec(256, 256)) {
        glTexturedRectV2(
                x = pos.x, y = pos.y, z = pos.z,
                width = size.x, height = size.y,
                srcX = srcPos.x, srcY = srcPos.y,
                srcWidth = srcSize.x, srcHeight = srcSize.y,
                textureW = textureSize.xi, textureH = textureSize.yi
        )
    }

    @JvmOverloads
    fun glTexturedRectV2(x: Double, y: Double, z: Double = 0.0, width: Double, height: Double, srcX: Double = 0.0, srcY: Double = 0.0, srcWidth: Double = width, srcHeight: Double = height, textureW: Int = 256, textureH: Int = 256) {
        val f = 1f / textureW
        val f1 = 1f / textureH
        val tessellator = Tessellator.getInstance()
        tessellator.buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        tessellator.buffer.pos(x, y + height, z).tex((srcX.toFloat() * f).toDouble(), ((srcY + srcHeight).toFloat() * f1).toDouble()).endVertex()
        tessellator.buffer.pos(x + width, y + height, z).tex(((srcX + srcWidth).toFloat() * f).toDouble(), ((srcY + srcHeight).toFloat() * f1).toDouble()).endVertex()
        tessellator.buffer.pos(x + width, y, z).tex(((srcX + srcWidth).toFloat() * f).toDouble(), (srcY.toFloat() * f1).toDouble()).endVertex()
        tessellator.buffer.pos(x, y, z).tex((srcX.toFloat() * f).toDouble(), (srcY.toFloat() * f1).toDouble()).endVertex()
        tessellator.draw()
    }

    fun addVertex(x: Double, y: Double, z: Double) {
        Tessellator.getInstance().buffer.pos(x, y, z).endVertex()
    }

    fun addVertex(x: Double, y: Double, z: Double, srcX: Double, srcY: Double) {
        Tessellator.getInstance().buffer.pos(x, y, z).tex(srcX, srcY).endVertex()
    }

    fun addVertex(x: Double, y: Double, z: Double, srcX: Double, srcY: Double, red: Float, green: Float, blue: Float, alpha: Float) {
        Tessellator.getInstance().buffer.pos(x, y, z).tex(srcX, srcY).color(red, green, blue, alpha).endVertex()
    }

    @JvmOverloads
    fun begin(glMode: Int = GL11.GL_QUADS, format: VertexFormat = DefaultVertexFormats.POSITION_TEX_COLOR) {
        Tessellator.getInstance().buffer.begin(glMode, format)
    }

    fun draw() {
        Tessellator.getInstance().draw()
    }

    fun glRect(x: Int, y: Int, width: Int, height: Int) {
        val tessellator = Tessellator.getInstance()
        tessellator.buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        tessellator.buffer.pos(x.toDouble(), (y + height).toDouble(), 0.0)
        tessellator.buffer.pos((x + width).toDouble(), (y + height).toDouble(), 0.0)
        tessellator.buffer.pos((x + width).toDouble(), y.toDouble(), 0.0)
        tessellator.buffer.pos(x.toDouble(), y.toDouble(), 0.0)
        tessellator.draw()
    }

    fun glAlphaTest(flag: Boolean) {
        if (flag)
            GlStateManager.enableAlpha()
        else
            GlStateManager.disableAlpha()
    }

    fun alphaFunc(src: Int, dst: Int) {
        GlStateManager.alphaFunc(src, dst.toFloat())
    }

    fun glBlend(flag: Boolean) {
        if (flag)
            GlStateManager.enableBlend()
        else
            GlStateManager.disableBlend()
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
        if (flag)
            GlStateManager.enableDepth()
        else
            GlStateManager.disableDepth()
    }

    fun glDepthFunc(flag: Int) {
        GL11.glDepthFunc(flag)
    }

    fun glRescaleNormal(flag: Boolean) {
        if (flag)
            GlStateManager.enableRescaleNormal()
        else
            GlStateManager.disableRescaleNormal()
    }

    fun glTexture2D(flag: Boolean) {
        if (flag)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
        else
            GL11.glDisable(GL11.GL_TEXTURE_2D)
    }

    fun glCullFace(flag: Boolean) {
        if (flag)
            GlStateManager.enableCull()
        else
            GlStateManager.disableCull()
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
        if (flag)
            GlStateManager.enableLighting()
        else
            GlStateManager.disableLighting()
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
        val d0 = Math.min(x1, x2)
        val d1 = Math.min(y1, y2)
        val d2 = Math.min(z1, z2)
        val d3 = Math.max(x1, x2)
        val d4 = Math.max(y1, y2)
        val d5 = Math.max(z1, z2)
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
