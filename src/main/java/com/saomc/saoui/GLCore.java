package com.saomc.saoui;

import com.saomc.saoui.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class GLCore {

    private static Minecraft mc = Minecraft.getMinecraft();

    private GLCore() {
    }

    private static FontRenderer glFont() {
        return mc.fontRenderer;
    }

    private static TextureManager glTextureManager() {
        return mc.getTextureManager();
    }

    public static void glColor(float red, float green, float blue) {
        GlStateManager.color(red, green, blue);
    }

    public static void glColor(float red, float green, float blue, float alpha) {
        GlStateManager.color(red, green, blue, alpha);
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
        ResourceLocation fontLocation = custom ? new ResourceLocation(SAOCore.MODID, "textures/ascii.png") : new ResourceLocation("textures/font/ascii.png");
        mc.fontRenderer = new FontRenderer(mc.gameSettings, fontLocation, mc.getTextureManager(), false);
        if (mc.gameSettings.language != null) {
            mc.fontRenderer.setUnicodeFlag(mc.isUnicode());
            mc.fontRenderer.setBidiFlag(mc.getLanguageManager().isCurrentLanguageBidirectional());
        }
        ((IReloadableResourceManager) mc.getResourceManager()).registerReloadListener(mc.fontRenderer);
    }

    private static int glStringWidth(FontRenderer font, String string) {
        return font != null ? font.getStringWidth(string) : 0;
    }

    public static int glStringWidth(String string) {
        return glStringWidth(glFont(), string);
    }

    private static int glStringHeight(FontRenderer font) {
        return font != null ? font.FONT_HEIGHT : 0;
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
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        tessellator.getBuffer().pos(x, (y + height), z).tex((double) ((float) (srcX) * f), (double) ((float) (srcY + srcHeight) * f1)).endVertex();
        tessellator.getBuffer().pos(x + width, y + height, z).tex((double) ((float) (srcX + srcWidth) * f), (double) ((float) (srcY + srcHeight) * f1)).endVertex();
        tessellator.getBuffer().pos(x + width, y, z).tex((double) ((float) (srcX + srcWidth) * f), (double) ((float) (srcY) * f1)).endVertex();
        tessellator.getBuffer().pos(x, y, z).tex((double) ((float) (srcX) * f), (double) ((float) (srcY) * f1)).endVertex();
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
        Tessellator.getInstance().getBuffer().pos(x, y, z).endVertex();
    }

    public static void addVertex(double x, double y, double z, double srcX, double srcY) {
        Tessellator.getInstance().getBuffer().pos(x, y, z).tex(srcX, srcY).endVertex();
    }

    public static void addVertex(double x, double y, double z, double srcX, double srcY, float red, float green, float blue, float alpha) {
        Tessellator.getInstance().getBuffer().pos(x, y, z).tex(srcX, srcY).color(red, green, blue, alpha).endVertex();
    }

    public static void begin() {
        begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
    }

    public static void begin(int glMode, VertexFormat format) {
        Tessellator.getInstance().getBuffer().begin(glMode, format);
    }

    public static void draw() {
        Tessellator.getInstance().draw();
    }

    public static void glRect(int x, int y, int width, int height) {
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        tessellator.getBuffer().pos((double) (x), (double) (y + height), 0.0D);
        tessellator.getBuffer().pos((double) (x + width), (double) (y + height), 0.0D);
        tessellator.getBuffer().pos((double) (x + width), (double) (y), 0.0D);
        tessellator.getBuffer().pos((double) (x), (double) (y), 0.0D);
        tessellator.draw();
    }

    public static void glAlphaTest(boolean flag) {
        if (flag) GlStateManager.enableAlpha();
        else GlStateManager.disableAlpha();
    }

    public static void alphaFunc(int src, int dst) {
        GlStateManager.alphaFunc(src, dst);
    }

    public static void glBlend(boolean flag) {
        if (flag) GlStateManager.enableBlend();
        else GlStateManager.disableBlend();
    }

    public static void blendFunc(int src, int dst) {
        GlStateManager.blendFunc(src, dst);
    }

    public static void tryBlendFuncSeparate(int a, int b, int c, int d) {
        GlStateManager.tryBlendFuncSeparate(a, b, c, d);
    }

    public static void depthMask(boolean flag) {
        GlStateManager.depthMask(flag);
    }

    public static void glDepthTest(boolean flag) {
        if (flag) GlStateManager.enableDepth();
        else GlStateManager.disableDepth();
    }

    public static void glDepthFunc(int flag) {
        GL11.glDepthFunc(flag);
    }

    public static void glRescaleNormal(boolean flag) {
        if (flag) GlStateManager.enableRescaleNormal();
        else GlStateManager.disableRescaleNormal();
    }

    public static void glTexture2D(boolean flag) {
        if (flag) GL11.glEnable(GL11.GL_TEXTURE_2D);
        else GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public static void glCullFace(boolean flag) {
        if (flag) GlStateManager.enableCull();
        else GlStateManager.disableCull();
    }

    public static void glTranslatef(float x, float y, float z) {
        GlStateManager.translate(x, y, z);
    }

    public static void glNormal3f(float x, float y, float z) {
        GL11.glNormal3f(x, y, z);
    }

    public static void glRotatef(float angle, float x, float y, float z) {
        GlStateManager.rotate(angle, x, y, z);
    }

    public static void glScalef(float x, float y, float z) {
        GlStateManager.scale(x, y, z);
    }

    public static void lighting(boolean flag) {
        if (flag) GlStateManager.enableLighting();
        else GlStateManager.disableLighting();
    }

    public static void glStartUI(Minecraft mc) {
        mc.mcProfiler.startSection(SAOCore.MODID + "[ '" + SAOCore.NAME + "' ]");
    }

    public static void glEndUI(Minecraft mc) {
        mc.mcProfiler.endSection();
    }

    public static void start() { GlStateManager.pushMatrix(); }

    public static void end() {
        GlStateManager.popMatrix();
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
        return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }
}
