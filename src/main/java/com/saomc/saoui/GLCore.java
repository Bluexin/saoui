package com.saomc.saoui;

import com.saomc.saoui.util.ColorUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public final class GLCore {

    private GLCore() {
    }

    private static Minecraft glMinecraft() {
        return Minecraft.getMinecraft();
    }

    private static FontRenderer glFont() {
        final Minecraft mc = glMinecraft();

        return mc != null ? mc.fontRendererObj : null;
    }

    private static TextureManager glTextureManager() {
        final Minecraft mc = glMinecraft();

        return mc != null ? mc.getTextureManager() : null;
    }

    public static void glColor(float red, float green, float blue) {
        GL11.glColor3f(red, green, blue);
    }

    public static void glColor(float red, float green, float blue, float alpha) {
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void glColorRGBA(ColorUtil color) {
        glColorRGBA(color.rgba);
    }

    public static void glColorRGBA(int rgba) {
        final float red = (float) ((rgba >> 24) & 0xFF) / 0xFF;
        final float green = (float) ((rgba >> 16) & 0xFF) / 0xFF;
        final float blue = (float) ((rgba >> 8) & 0xFF) / 0xFF;
        final float alpha = (float) ((rgba) & 0xFF) / 0xFF;

        glColor(red, green, blue, alpha);
    }

    private static int glFontColor(int rgba) {
        final int alpha = (rgba) & 0xFF;
        final int red = (rgba >> 24) & 0xFF;
        final int blue = (rgba >> 8) & 0xFF;
        final int green = (rgba >> 16) & 0xFF;

        return (alpha << 24) | (red << 16) | (blue << 8) | (green);
    }

    public static void glString(FontRenderer font, String string, int x, int y, int argb, boolean shadow) {
        if (font != null) font.drawString(string, x, y, glFontColor(argb), shadow);
    }

    public static void glString(FontRenderer font, String string, int x, int y, int argb) {
        glString(font, string, x, y, argb, false);
    }

    public static void glString(String string, int x, int y, int argb, boolean shadow) {
        glString(glFont(), string, x, y, argb, shadow);
    }

    public static void glString(String string, int x, int y, int argb) {
        glString(string, x, y, argb, false);
    }

    public static void setFont(Minecraft mc, boolean custom) {
        if (mc.fontRendererObj == null) return;
        ResourceLocation fontLocation = custom ? new ResourceLocation(SAOCore.MODID, "textures/ascii.png") : new ResourceLocation("textures/font/ascii.png");
        GameSettings gs = mc.gameSettings;
        mc.fontRendererObj = new FontRenderer(gs, fontLocation, mc.getTextureManager(), false);
        if (gs.language != null) {
            mc.fontRendererObj.setUnicodeFlag(mc.isUnicode());
            mc.fontRendererObj.setBidiFlag(mc.getLanguageManager().isCurrentLanguageBidirectional());
        }
        ((IReloadableResourceManager) mc.getResourceManager()).registerReloadListener(mc.fontRendererObj);
    }

    private static int glStringWidth(FontRenderer font, String string) {
        if (font != null) return font.getStringWidth(string);
        else return 0;
    }

    public static int glStringWidth(String string) {
        return glStringWidth(glFont(), string);
    }

    private static int glStringHeight(FontRenderer font) {
        if (font != null) return font.FONT_HEIGHT;
        else return 0;
    }

    public static int glStringHeight() {
        return glStringHeight(glFont());
    }

    private static void glBindTexture(TextureManager textureManager, ResourceLocation location) {
        if (textureManager != null) textureManager.bindTexture(location);
    }

    public static void glBindTexture(ResourceLocation location) {
        glBindTexture(glTextureManager(), location);
    }

    public static void glTexturedRect(double x, double y, double z, double width, double height, double srcX, double srcY, double srcWidth, double srcHeight) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, (y + height), z, (double) ((float) (srcX) * f), (double) ((float) (srcY + srcHeight) * f1));
        tessellator.addVertexWithUV(x + width, y + height, z, (double) ((float) (srcX + srcWidth) * f), (double) ((float) (srcY + srcHeight) * f1));
        tessellator.addVertexWithUV(x + width, y, z, (double) ((float) (srcX + srcWidth) * f), (double) ((float) (srcY) * f1));
        tessellator.addVertexWithUV(x, y, z, (double) ((float) (srcX) * f), (double) ((float) (srcY) * f1));
        tessellator.draw();
    }

    public static void glTexturedRect(double x, double y, double z, double srcX, double srcY, double width, double height) {
        glTexturedRect(x, y, z, width, height, srcX, srcY, width, height);
    }

    public static void glTexturedRect(double x, double y, double width, double height, double srcX, double srcY, double srcWidth, double srcHeight) {
        glTexturedRect(x, y, 0, width, height, srcX, srcY, srcWidth, srcHeight);
    }

    public static void glTexturedRect(double x, double y, double srcX, double srcY, double width, double height) {
        glTexturedRect(x, y, 0, srcX, srcY, width, height);
    }

    public static void addVertex(double x, double y, double z) {
        Tessellator.instance.addVertex(x, y, z);
    }

    public static void addVertex(double x, double y, double z, double srcX, double srcY) {
        Tessellator.instance.addVertexWithUV(x, y, z, srcX, srcY);
    }

    public static void addVertex(double x, double y, double z, double srcX, double srcY, float red, float green, float blue, float alpha) {
        glColor(red, green, blue, alpha);
        Tessellator.instance.addVertexWithUV(x, y, z, srcX, srcY);
    }

    public static void begin() {
        Tessellator.instance.startDrawingQuads();
    }

    public static void begin(int glMode) {
        Tessellator.instance.startDrawing(glMode);
    }

    public static void draw() {
        Tessellator.instance.draw();
    }

    public static void glRect(int x, int y, int width, int height) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertex((double) (x), (double) (y + height), 0.0D);
        tessellator.addVertex((double) (x + width), (double) (y + height), 0.0D);
        tessellator.addVertex((double) (x + width), (double) (y), 0.0D);
        tessellator.addVertex((double) (x), (double) (y), 0.0D);
        tessellator.draw();
    }

    public static void glAlphaTest(boolean flag) {
        if (flag) GL11.glEnable(GL11.GL_ALPHA_TEST);
        else GL11.glDisable(GL11.GL_ALPHA_TEST);
    }

    public static void alphaFunc(int src, int dst) {
        GL11.glAlphaFunc(src, dst);
    }

    public static void glBlend(boolean flag) {
        if (flag) GL11.glEnable(GL11.GL_BLEND);
        else GL11.glDisable(GL11.GL_BLEND);
    }

    public static void blendFunc(int src, int dst) {
        GL11.glBlendFunc(src, dst);
    }

    public static void tryBlendFuncSeparate(int a, int b, int c, int d) {
        OpenGlHelper.glBlendFunc(a, b, c, d);
    }

    public static void depthMask(boolean flag) {
        GL11.glDepthMask(flag);
    }

    public static void glDepthTest(boolean flag) {
        if (flag) GL11.glEnable(GL11.GL_DEPTH_TEST);
        else GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    public static void glDepthFunc(int flag) {
        GL11.glDepthFunc(flag);
    }

    public static void glRescaleNormal(boolean flag) {
        if (flag) GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        else GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    public static void glTexture2D(boolean flag) {
        if (flag) GL11.glEnable(GL11.GL_TEXTURE_2D);
        else GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public static void glCullFace(boolean flag) {
        if (flag) GL11.glEnable(GL11.GL_CULL_FACE);
        else GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public static void glTranslatef(float x, float y, float z) {
        GL11.glTranslatef(x, y, z);
    }

    public static void glNormal3f(float x, float y, float z) {
        GL11.glNormal3f(x, y, z);
    }

    public static void glRotatef(float angle, float x, float y, float z) {
        GL11.glRotatef(angle, x, y, z);
    }

    public static void glScalef(float x, float y, float z) {
        GL11.glScalef(x, y, z);
    }

    public static void lighting(boolean flag) {
        if (flag) GL11.glEnable(GL11.GL_LIGHTING);
        else GL11.glDisable(GL11.GL_LIGHTING);
    }

    public static void glStartUI(Minecraft mc) {
        mc.mcProfiler.startSection(SAOCore.MODID + "[ '" + SAOCore.NAME + "' ]");
    }

    public static void glEndUI(Minecraft mc) {
        mc.mcProfiler.endSection();
    }

    public static void start() {
        GL11.glPushMatrix();
    }

    public static void end() {
        GL11.glPopMatrix();
    }


    /**
     * returns an AABB with corners x1, y1, z1 and x2, y2, z2
     */
    public static AxisAlignedBB fromBounds(double x1, double y1, double z1, double x2, double y2, double z2) {
        double d0 = Math.min(x1, x2);
        double d1 = Math.min(y1, y2);
        double d2 = Math.min(z1, z2);
        double d3 = Math.max(x1, x2);
        double d4 = Math.max(y1, y2);
        double d5 = Math.max(z1, z2);
        return AxisAlignedBB.getBoundingBox(d0, d1, d2, d3, d4, d5);
    }
}
