package com.saomc.saoui

import com.saomc.saoui.util.ColorUtil
import com.teamwizardry.librarianlib.features.math.Vec2d
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.*

@SideOnly(Side.CLIENT)
object GLCore {

    private val mc = Minecraft.getMinecraft()
    val glFont get() = mc.fontRendererObj!!
    val glTextureManager get() = mc.textureManager!!

    private val contextcapabilities by lazy { GLContext.getCapabilities() }
    private val openGL14 by lazy { contextcapabilities.OpenGL14 || contextcapabilities.GL_EXT_blend_func_separate }
    private val extBlendFuncSeparate by lazy { contextcapabilities.GL_EXT_blend_func_separate && !contextcapabilities.OpenGL14 }

    fun color(red: Float, green: Float, blue: Float, alpha: Float = 1f) {
        GL11.glColor4f(red, green, blue, alpha)
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
        font?.drawString(string, x, y - if (centered) font.FONT_HEIGHT / 2 else 0, glFontColor(argb), shadow)
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
        mc.fontRendererObj = FontRenderer(mc.gameSettings, fontLocation, mc.textureManager, false)
        if (mc.gameSettings.language != null) {
            mc.fontRendererObj.unicodeFlag = mc.isUnicode
            mc.fontRendererObj.bidiFlag = mc.languageManager.isCurrentLanguageBidirectional
        }
        (mc.resourceManager as IReloadableResourceManager).registerReloadListener(mc.fontRendererObj)
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

    /*@JvmOverloads
    fun glTexturedRectV2(pos: Vec3d, size: Vec2d, srcPos: Vec2d = Vec2d.ZERO, srcSize: Vec2d = size, textureSize: Vec2d = vec(256, 256)) {
        glTexturedRectV2(
                x = pos.x, y = pos.y, z = pos.z,
                width = size.x, height = size.y,
                srcX = srcPos.x, srcY = srcPos.y,
                srcWidth = srcSize.x, srcHeight = srcSize.y,
                textureW = textureSize.xi, textureH = textureSize.yi
        )
    }*/

    @JvmOverloads
    fun glTexturedRectV2(x: Double, y: Double, z: Double = 0.0, width: Double, height: Double, srcX: Double = 0.0, srcY: Double = 0.0, srcWidth: Double = width, srcHeight: Double = height, textureW: Int = 256, textureH: Int = 256) {
        val f = 1f / textureW
        val f1 = 1f / textureH
        val tessellator = Tessellator.instance
        begin()
        tessellator.addVertexWithUV(x, y + height, z, (srcX.toFloat() * f).toDouble(), ((srcY + srcHeight).toFloat() * f1).toDouble())
        tessellator.addVertexWithUV(x + width, y + height, z, ((srcX + srcWidth).toFloat() * f).toDouble(), ((srcY + srcHeight).toFloat() * f1).toDouble())
        tessellator.addVertexWithUV(x + width, y, z, ((srcX + srcWidth).toFloat() * f).toDouble(), (srcY.toFloat() * f1).toDouble())
        tessellator.addVertexWithUV(x, y, z, (srcX.toFloat() * f).toDouble(), (srcY.toFloat() * f1).toDouble())
        tessellator.draw()
    }

    fun addVertex(x: Double, y: Double, z: Double) {
        Tessellator.instance.addVertex(x, y, z)
    }

    fun addVertex(x: Double, y: Double, z: Double, srcX: Double, srcY: Double) {
        Tessellator.instance.addVertexWithUV(x, y, z, srcX, srcY)
    }

    fun addVertex(x: Double, y: Double, z: Double, srcX: Double, srcY: Double, red: Float, green: Float, blue: Float, alpha: Float) {
        Tessellator.instance.setColorRGBA_F(red, green, blue, alpha)
        Tessellator.instance.addVertexWithUV(x, y, z, srcX, srcY)
    }

    @JvmOverloads
    fun begin(glMode: Int = GL11.GL_QUADS) {
        Tessellator.instance.startDrawing(glMode)
    }

    fun draw() {
        Tessellator.instance.draw()
    }

    fun glRect(x: Int, y: Int, width: Int, height: Int) {
        val tessellator = Tessellator.instance
        begin()
        tessellator.addVertex(x.toDouble(), (y + height).toDouble(), 0.0)
        tessellator.addVertex((x + width).toDouble(), (y + height).toDouble(), 0.0)
        tessellator.addVertex((x + width).toDouble(), y.toDouble(), 0.0)
        tessellator.addVertex(x.toDouble(), y.toDouble(), 0.0)
        tessellator.draw()
    }

    fun glAlphaTest(flag: Boolean) {
        if (flag) GL11.glEnable(GL11.GL_ALPHA_TEST)
        else GL11.glEnable(GL11.GL_ALPHA_TEST)
    }

    fun alphaFunc(src: Int, dst: Int) {
        GL11.glAlphaFunc(src, dst.toFloat())
    }

    fun glBlend(flag: Boolean) {
        if (flag) GL11.glEnable(GL11.GL_BLEND)
        else GL11.glEnable(GL11.GL_BLEND)
    }

    fun blendFunc(src: Int, dst: Int) {
        GL11.glBlendFunc(src, dst)
    }

    fun tryBlendFuncSeparate(sFactorRGB: Int, dFactorRGB: Int, sfactorAlpha: Int, dfactorAlpha: Int) {
        if (openGL14) {
            if (extBlendFuncSeparate) {
                EXTBlendFuncSeparate.glBlendFuncSeparateEXT(sFactorRGB, dFactorRGB, sfactorAlpha, dfactorAlpha)
            } else {
                GL14.glBlendFuncSeparate(sFactorRGB, dFactorRGB, sfactorAlpha, dfactorAlpha)
            }
        } else {
            GL11.glBlendFunc(sFactorRGB, dFactorRGB)
        }
    }

    fun depthMask(flag: Boolean) {
        GL11.glDepthMask(flag)
    }

    fun depth(flag: Boolean) {
        if (flag) GL11.glEnable(GL11.GL_DEPTH_TEST)
        else GL11.glEnable(GL11.GL_DEPTH_TEST)
    }

    fun glDepthFunc(flag: Int) {
        GL11.glDepthFunc(flag)
    }

    fun glRescaleNormal(flag: Boolean) {
        if (flag) GL11.glEnable(GL12.GL_RESCALE_NORMAL)
        else GL11.glEnable(GL12.GL_RESCALE_NORMAL)
    }

    fun glTexture2D(flag: Boolean) {
        if (flag) GL11.glEnable(GL11.GL_TEXTURE_2D)
        else GL11.glDisable(GL11.GL_TEXTURE_2D)
    }

    fun glCullFace(flag: Boolean) {
        if (flag) GL11.glEnable(GL11.GL_CULL_FACE)
        else GL11.glDisable(GL11.GL_CULL_FACE)
    }

    @Deprecated("Use overloaded method instead", replaceWith = ReplaceWith("translate(x, y, z)"))
    fun glTranslatef(x: Float, y: Float, z: Float) = translate(x, y, z)

    fun glNormal3f(x: Float, y: Float, z: Float) {
        GL11.glNormal3f(x, y, z)
    }

    fun glRotatef(angle: Float, x: Float, y: Float, z: Float) {
        GL11.glRotatef(angle, x, y, z)
    }

    @Deprecated("Use overloaded method instead", replaceWith = ReplaceWith("scale(x, y, z)"))
    fun glScalef(x: Float, y: Float, z: Float) = scale(x, y, z)

    fun lighting(flag: Boolean) {
        if (flag) GL11.glEnable(GL11.GL_LIGHTING)
        else GL11.glDisable(GL11.GL_LIGHTING)
    }

    fun pushMatrix() {
        GL11.glPushMatrix()
    }

    fun popMatrix() {
        GL11.glPopMatrix()
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
        return AxisAlignedBB.getBoundingBox(d0, d1, d2, d3, d4, d5)
    }

    fun translate(x: Double, y: Double, z: Double) {
        GL11.glTranslated(x, y, z)
    }

    fun translate(x: Float, y: Float, z: Float) {
        GL11.glTranslatef(x, y, z)
    }

    fun scale(x: Double, y: Double, z: Double) {
        GL11.glScaled(x, y, z)
    }

    fun scale(x: Float, y: Float, z: Float) {
        GL11.glScalef(x, y, z)
    }
}
