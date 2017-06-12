package com.saomc.saoui

import com.saomc.saoui.util.ColorUtil
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
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
object GLCore {

    private val mc = Minecraft.getMinecraft()
    private val glFont = mc.fontRenderer
    private val glTextureManager = mc.textureManager

    fun glColor(red: Float, green: Float, blue: Float) {
        GlStateManager.color(red, green, blue)
    }

    fun glColor(red: Float, green: Float, blue: Float, alpha: Float) {
        GlStateManager.color(red, green, blue, alpha)
    }

    fun glColorRGBA(color: ColorUtil) {
        glColorRGBA(color.rgba)
    }

    fun glColorRGBA(rgba: Int) {
        val red = (rgba shr 24 and 0xFF).toFloat() / 0xFF
        val green = (rgba shr 16 and 0xFF).toFloat() / 0xFF
        val blue = (rgba shr 8 and 0xFF).toFloat() / 0xFF
        val alpha = (rgba and 0xFF).toFloat() / 0xFF

        glColor(red, green, blue, alpha)
    }

    private fun glFontColor(rgba: Int): Int {
        val alpha = rgba and 0xFF
        val red = rgba shr 24 and 0xFF
        val blue = rgba shr 8 and 0xFF
        val green = rgba shr 16 and 0xFF

        return alpha shl 24 or (red shl 16) or (blue shl 8) or green
    }

    @JvmOverloads fun glString(font: FontRenderer?, string: String, x: Int, y: Int, argb: Int, shadow: Boolean = false) {
        font?.drawString(string, x.toFloat(), y.toFloat(), glFontColor(argb), shadow)
    }

    @JvmOverloads fun glString(string: String, x: Int, y: Int, argb: Int, shadow: Boolean = false) {
        glString(glFont, string, x, y, argb, shadow)
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

    private fun glStringWidth(font: FontRenderer?, string: String): Int {
        return font?.getStringWidth(string) ?: 0
    }

    fun glStringWidth(string: String): Int {
        return glStringWidth(glFont, string)
    }

    private fun glStringHeight(font: FontRenderer?): Int {
        return font?.FONT_HEIGHT ?: 0
    }

    fun glStringHeight(): Int {
        return glStringHeight(glFont)
    }

    private fun glBindTexture(textureManager: TextureManager?, location: ResourceLocation) {
        textureManager?.bindTexture(location)
    }

    fun glBindTexture(location: ResourceLocation) {
        glBindTexture(glTextureManager, location)
    }

    fun glTexturedRect(x: Double, y: Double, z: Double, width: Double, height: Double, srcX: Double, srcY: Double, srcWidth: Double, srcHeight: Double) {
        val f = 0.00390625f
        val f1 = 0.00390625f
        val tessellator = Tessellator.getInstance()
        tessellator.buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        tessellator.buffer.pos(x, y + height, z).tex((srcX.toFloat() * f).toDouble(), ((srcY + srcHeight).toFloat() * f1).toDouble()).endVertex()
        tessellator.buffer.pos(x + width, y + height, z).tex(((srcX + srcWidth).toFloat() * f).toDouble(), ((srcY + srcHeight).toFloat() * f1).toDouble()).endVertex()
        tessellator.buffer.pos(x + width, y, z).tex(((srcX + srcWidth).toFloat() * f).toDouble(), (srcY.toFloat() * f1).toDouble()).endVertex()
        tessellator.buffer.pos(x, y, z).tex((srcX.toFloat() * f).toDouble(), (srcY.toFloat() * f1).toDouble()).endVertex()
        tessellator.draw()
    }

    fun glTexturedRect(x: Double, y: Double, z: Double, srcX: Double, srcY: Double, width: Double, height: Double) {
        glTexturedRect(x, y, z, width, height, srcX, srcY, width, height)
    }

    fun glTexturedRect(x: Double, y: Double, width: Double, height: Double, srcX: Double, srcY: Double, srcWidth: Double, srcHeight: Double) {
        glTexturedRect(x, y, 0.0, width, height, srcX, srcY, srcWidth, srcHeight)
    }

    fun glTexturedRect(x: Double, y: Double, srcX: Double, srcY: Double, width: Double, height: Double) {
        glTexturedRect(x, y, 0.0, srcX, srcY, width, height)
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

    @JvmOverloads fun begin(glMode: Int = GL11.GL_QUADS, format: VertexFormat = DefaultVertexFormats.POSITION_TEX_COLOR) {
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

    fun glDepthTest(flag: Boolean) {
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

    fun glStartUI(mc: Minecraft) {
        mc.mcProfiler.startSection(SAOCore.MODID + "[ '" + SAOCore.NAME + "' ]")
    }

    fun glEndUI(mc: Minecraft) {
        mc.mcProfiler.endSection()
    }

    fun start() {
        GlStateManager.pushMatrix()
    }

    fun end() {
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
}
