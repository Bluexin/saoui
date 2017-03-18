package com.saomc.saoui.screens.ingame;

import be.bluexin.saomclib.capabilities.PartyCapability;
import be.bluexin.saomclib.party.IParty;
import com.saomc.saoui.GLCore;
import com.saomc.saoui.config.ConfigHandler;
import com.saomc.saoui.config.OptionCore;
import com.saomc.saoui.effects.StatusEffects;
import com.saomc.saoui.neo.screens.IngameMenuGUI;
import com.saomc.saoui.resources.StringNames;
import com.saomc.saoui.screens.death.DeathScreen;
import com.saomc.saoui.social.StaticPlayerHelper;
import com.saomc.saoui.themes.ThemeLoader;
import com.saomc.saoui.themes.elements.HudPartType;
import com.saomc.saoui.themes.util.HudDrawContext;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*;

@SideOnly(Side.CLIENT)
public class IngameGUI extends GuiIngameForge {

    private FontRenderer fontRenderer;
    private RenderGameOverlayEvent eventParent;
    private int offsetUsername;

    @Deprecated // use getContext() instead of directly calling this
    private HudDrawContext ctx;

    public IngameGUI(Minecraft mc) {
        super(mc);
    }

    @Override
    public void renderGameOverlay(float partialTicks, boolean hasScreen, int mouseX, int mouseY) {
        mc.mcProfiler.startSection("setup");
        fontRenderer = mc.fontRendererObj;
        String username = mc.thePlayer.getDisplayName();
        int maxNameWidth = fontRenderer.getStringWidth(username);
        int usernameBoxes = 1 + (maxNameWidth + 4) / 5;
        offsetUsername = 18 + usernameBoxes * 5;
        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        eventParent = new RenderGameOverlayEvent(partialTicks, res, mouseX, mouseY);
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();
        getContext().setTime(partialTicks);
        getContext().setScaledResolution(res);
        getContext().setZ(zLevel);
        getContext().setPlayer(mc.thePlayer);
        GLCore.glBlend(true);
        mc.mcProfiler.endSection();

        super.renderGameOverlay(partialTicks, hasScreen, mouseX, mouseY);

        if (OptionCore.FORCE_HUD.isEnabled() && !this.mc.playerController.shouldDrawHUD() && this.mc.renderViewEntity instanceof EntityPlayer) {
            if (renderHealth) renderHealth(width, height);
            if (renderArmor) renderArmor(width, height);
            if (renderFood) renderFood(width, height);
            if (renderHealthMount) renderHealthMount(width, height);
            if (renderAir) renderAir(width, height);
            mc.entityRenderer.setupOverlayRendering();
        } // Basically adding what super doesn't render by default

    }

    @Override
    protected void renderCrosshairs(int w, int h) {
        if (pre(CROSSHAIRS)) return;
        ThemeLoader.HUD.draw(HudPartType.CROSS_HAIR, getContext()); // TODO: rework
        if (OptionCore.CROSS_HAIR.isEnabled() && !(mc.currentScreen instanceof IngameMenuGUI || mc.currentScreen instanceof DeathScreen)) {
            super.renderCrosshairs(w, h);
        }
        post(CROSSHAIRS);
    }

    @Override
    protected void renderArmor(int width, int height) {
        if (OptionCore.VANILLA_UI.isEnabled()) super.renderArmor(width, height);
        else {
            if (replaceEvent(ARMOR)) return;
            ThemeLoader.HUD.draw(HudPartType.ARMOR, getContext());
            post(ARMOR);
        }
    }

    @Override
    protected void renderHotbar(int w, int h, float partialTicks) {
        if (replaceEvent(HOTBAR)) return;
        else if (OptionCore.DEFAULT_HOTBAR.isEnabled()) super.renderHotbar(w, h, partialTicks);
        else {
            mc.mcProfiler.startSection("hotbar");
            ThemeLoader.HUD.draw(HudPartType.HOTBAR, getContext());
            mc.mcProfiler.endSection();
        }

        post(HOTBAR);
    }

    @Override
    protected void renderAir(int width, int height) {
        if (OptionCore.VANILLA_UI.isEnabled()) super.renderAir(width, height);
        else {
            if (replaceEvent(AIR)) return;
            mc.mcProfiler.startSection("air");
            EntityPlayer player = (EntityPlayer) this.mc.renderViewEntity;
            GLCore.glBlend(true);
            int left = width / 2 + 91;
            int top = height - right_height;

            if (player.isInsideOfMaterial(Material.water)) {
                int air = player.getAir();
                int full = MathHelper.ceiling_double_int((double) (air - 2) * 10.0D / 300.0D);
                int partial = MathHelper.ceiling_double_int((double) air * 10.0D / 300.0D) - full;

                for (int i = 0; i < full + partial; ++i) {
                    drawTexturedModalRect(left - i * 8 - 9, top, (i < full ? 16 : 25), 18, 9, 9);
                }
                right_height += 10;
            }

            GLCore.glBlend(false);
            mc.mcProfiler.endSection();
            // Linked to renderHealth
            post(AIR);
        }
    }

    @Override
    public void renderHealth(int width, int height) {
        if (OptionCore.VANILLA_UI.isEnabled()) super.renderHealth(width, height);
        else {
            if (replaceEvent(HEALTH)) return;
            mc.mcProfiler.startSection("health");
            ThemeLoader.HUD.draw(HudPartType.HEALTH_BOX, getContext());
            mc.mcProfiler.endSection();
            post(HEALTH);

            final int healthBarWidth = 234;
            final int healthWidth = 216;
            final int healthHeight = OptionCore.SAO_UI.isEnabled() ? 9 : 4;
            int stepOne = (int) (healthWidth / 3.0F - 3);
            int stepTwo = (int) (healthWidth / 3.0F * 2.0F - 3);
            int stepThree = healthWidth - 3;

            renderFood(healthWidth, healthHeight, offsetUsername, stepOne, stepTwo, stepThree);

            GLCore.glColor(1.0F, 1.0F, 1.0F, 1.0F);

            mc.mcProfiler.startSection("effects");

            final int offsetForEffects = offsetUsername + healthBarWidth - 4;
            final List<StatusEffects> effects = StatusEffects.getEffects(mc.thePlayer);

            GLCore.glBindTexture(OptionCore.SAO_UI.isEnabled() ? StringNames.gui : StringNames.guiCustom);

            for (int i = 0; i < effects.size(); i++) effects.get(i).glDraw(offsetForEffects + i * 11, 2, zLevel);

            mc.mcProfiler.endSection();

            renderParty();
        }
    }

    private void renderParty() {
        IParty pt = ((PartyCapability) mc.thePlayer.getExtendedProperties(PartyCapability.Companion.getKEY().toString())).getParty();
        if ((pt == null || !pt.isParty()) && ConfigHandler.debugFakePT == 0) return;

        mc.mcProfiler.startSection("party");

        GLCore.glAlphaTest(true);
        GLCore.glBlend(true);

        List<EntityPlayer> members = pt == null ? new ArrayList<>(ConfigHandler.debugFakePT) : pt.getMembers();
        members.removeIf(p -> p == mc.thePlayer);
        for (int i = 0; i < ConfigHandler.debugFakePT; i++) members.add(mc.thePlayer);
        getContext().setPt(members);
        ThemeLoader.HUD.draw(HudPartType.PARTY, getContext());

        mc.mcProfiler.endSection();
    }

    @Override
    public void renderFood(int width, int height) {
        if (OptionCore.VANILLA_UI.isEnabled()) super.renderFood(width, height);
        // See below, called by renderHealth
    }

    private void renderFood(int healthWidth, int healthHeight, int offsetUsername, int stepOne, int stepTwo, int stepThree) {
        if (replaceEvent(FOOD)) return;
        mc.mcProfiler.startSection("food");
        HudDrawContext ctx = getContext();
        final int foodValue = (int) (StaticPlayerHelper.getHungerFract(mc, mc.thePlayer, ctx.getPartialTicks()) * healthWidth);
//        int h = foodValue < 12 ? 12 - foodValue : 0;
//        int o = healthHeight;
        GLCore.glAlphaTest(true);
        GLCore.glBlend(true);
        GLCore.glColorRGBA(0x8EE1E8);
        /*for (int i = 0; i < foodValue; i++) {
            GLCore.glTexturedRect(offsetUsername + i + 4, 9, zLevel, h, 240, 1, o);
            if (foodValue < healthWidth && i >= foodValue - 3) o--;

            if (foodValue <= 12) {
                h++;
                if (h > 12) break;
            } else if ((i >= stepOne && i <= stepOne + 3) || (i >= stepTwo && i <= stepTwo + 3) || (i >= stepThree)) {
                h++;

                if (h > 12) break;
            }
        }*/

        if (foodValue >= stepTwo && foodValue < stepThree)
            GLCore.glTexturedRect(offsetUsername + foodValue, 9, zLevel, 11, 249, 7, 4);
        if (foodValue >= stepOne && foodValue < stepTwo + 4)
            GLCore.glTexturedRect(offsetUsername + foodValue, 9, zLevel, 4, 249, 7, 4);
        if (foodValue < stepOne + 4 && foodValue > 0) {
            GLCore.glTexturedRect(offsetUsername + foodValue + 2, 9, zLevel, 0, 249, 4, 4);
            for (int i = 0; i < foodValue - 2; i++)
                GLCore.glTexturedRect(offsetUsername + i + 4, 9, zLevel, 0, 249, 4, 4);
        }

        mc.mcProfiler.endSection();
        post(FOOD);
    }

    @Override
    protected void renderExperience(int width, int height) {
        if (OptionCore.VANILLA_UI.isEnabled()) super.renderExperience(width, height);
        else {
            if (pre(EXPERIENCE)) return;
            if (!OptionCore.FORCE_HUD.isEnabled() && !this.mc.playerController.shouldDrawHUD()) return;
            mc.mcProfiler.startSection("expLevel");

            ThemeLoader.HUD.draw(HudPartType.EXPERIENCE, getContext());

            mc.mcProfiler.endSection();
            post(EXPERIENCE);
        }
    }

    @Override
    protected void renderJumpBar(int width, int height) {
        if (OptionCore.VANILLA_UI.isEnabled()) super.renderJumpBar(width, height);
        else {
            if (replaceEvent(JUMPBAR)) return;
            renderExperience(width, height);

            mc.mcProfiler.startSection("jumpBar");
            ThemeLoader.HUD.draw(HudPartType.JUMP_BAR, getContext());
            mc.mcProfiler.endSection();

            post(JUMPBAR);
        }
    }

    @Override
    protected void renderHealthMount(int width, int height) {
        if (OptionCore.VANILLA_UI.isEnabled()) super.renderHealthMount(width, height);
        else {
            EntityPlayer player = (EntityPlayer) mc.renderViewEntity;
            if (player == null) return;
            Entity tmp = player.ridingEntity;
            if (!(tmp instanceof EntityLivingBase)) return;

            if (replaceEvent(HEALTHMOUNT)) return;
            // Not implemented yet
            post(HEALTHMOUNT);
        }
    }

    @Override
    protected void renderHUDText(int width, int height) {
        if (OptionCore.VANILLA_UI.isEnabled() || OptionCore.DEFAULT_DEBUG.isEnabled())
            super.renderHUDText(width, height);
        else {
            mc.mcProfiler.startSection("forgeHudText");
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            ArrayList<String> listL = new ArrayList<>();
            ArrayList<String> listR = new ArrayList<>();

            if (mc.isDemo()) {
                long time = mc.theWorld.getTotalWorldTime();
                if (time >= 120500L) listR.add(I18n.format("demo.demoExpired"));
                else
                    listR.add(I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int) (120500L - time))));
            }

            if (this.mc.gameSettings.showDebugInfo && !pre(DEBUG)) {
                this.mc.mcProfiler.startSection("debug");
                FontRenderer fontrenderer = this.mc.fontRendererObj;
                int i3;
                int j3;
                int k3;
                GL11.glPushMatrix();
                fontrenderer.drawStringWithShadow("Minecraft 1.7.10 (" + this.mc.debug + ")", 2, 2, 16777215);
                fontrenderer.drawStringWithShadow(this.mc.debugInfoRenders(), 2, 12, 16777215);
                fontrenderer.drawStringWithShadow(this.mc.getEntityDebug(), 2, 22, 16777215);
                fontrenderer.drawStringWithShadow(this.mc.debugInfoEntities(), 2, 32, 16777215);
                fontrenderer.drawStringWithShadow(this.mc.getWorldProviderName(), 2, 42, 16777215);
                long i5 = Runtime.getRuntime().maxMemory();
                long j5 = Runtime.getRuntime().totalMemory();
                long k5 = Runtime.getRuntime().freeMemory();
                long l5 = j5 - k5;
                String s = "Used memory: " + l5 * 100L / i5 + "% (" + l5 / 1024L / 1024L + "MB) of " + i5 / 1024L / 1024L + "MB";
                i3 = 14737632;
                this.drawString(fontrenderer, s, width - fontrenderer.getStringWidth(s) - 2, 2, 14737632);
                s = "Allocated memory: " + j5 * 100L / i5 + "% (" + j5 / 1024L / 1024L + "MB)";
                this.drawString(fontrenderer, s, width - fontrenderer.getStringWidth(s) - 2, 12, 14737632);
                int offset = 22;
                for (String brd : FMLCommonHandler.instance().getBrandings(false)) {
                    this.drawString(fontrenderer, brd, width - fontrenderer.getStringWidth(brd) - 2, offset += 10, 14737632);
                }
                j3 = MathHelper.floor_double(this.mc.thePlayer.posX);
                k3 = MathHelper.floor_double(this.mc.thePlayer.posY);
                int l3 = MathHelper.floor_double(this.mc.thePlayer.posZ);
                this.drawString(fontrenderer, String.format("x: %.5f (%d) // c: %d (%d)", Double.valueOf(this.mc.thePlayer.posX), Integer.valueOf(j3), Integer.valueOf(j3 >> 4), Integer.valueOf(j3 & 15)), 2, 64, 14737632);
                this.drawString(fontrenderer, String.format("y: %.3f (feet pos, %.3f eyes pos)", Double.valueOf(this.mc.thePlayer.boundingBox.minY), Double.valueOf(this.mc.thePlayer.posY)), 2, 72, 14737632);
                this.drawString(fontrenderer, String.format("z: %.5f (%d) // c: %d (%d)", Double.valueOf(this.mc.thePlayer.posZ), Integer.valueOf(l3), Integer.valueOf(l3 >> 4), Integer.valueOf(l3 & 15)), 2, 80, 14737632);
                int i4 = MathHelper.floor_double((double) (this.mc.thePlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
                this.drawString(fontrenderer, "f: " + i4 + " (" + Direction.directions[i4] + ") / " + MathHelper.wrapAngleTo180_float(this.mc.thePlayer.rotationYaw), 2, 88, 14737632);

                if (this.mc.theWorld != null && this.mc.theWorld.blockExists(j3, k3, l3)) {
                    Chunk chunk = this.mc.theWorld.getChunkFromBlockCoords(j3, l3);
                    this.drawString(fontrenderer, "lc: " + (chunk.getTopFilledSegment() + 15) + " b: " + chunk.getBiomeGenForWorldCoords(j3 & 15, l3 & 15, this.mc.theWorld.getWorldChunkManager()).biomeName + " bl: " + chunk.getSavedLightValue(EnumSkyBlock.Block, j3 & 15, k3, l3 & 15) + " sl: " + chunk.getSavedLightValue(EnumSkyBlock.Sky, j3 & 15, k3, l3 & 15) + " rl: " + chunk.getBlockLightValue(j3 & 15, k3, l3 & 15, 0), 2, 96, 14737632);
                }

                this.drawString(fontrenderer, String.format("ws: %.3f, fs: %.3f, g: %b, fl: %d", Float.valueOf(this.mc.thePlayer.capabilities.getWalkSpeed()), Float.valueOf(this.mc.thePlayer.capabilities.getFlySpeed()), Boolean.valueOf(this.mc.thePlayer.onGround), Integer.valueOf(this.mc.theWorld.getHeightValue(j3, l3))), 2, 104, 14737632);

                if (this.mc.entityRenderer != null && this.mc.entityRenderer.isShaderActive()) {
                    this.drawString(fontrenderer, String.format("shader: %s", this.mc.entityRenderer.getShaderGroup().getShaderGroupName()), 2, 112, 14737632);
                }

                GL11.glPopMatrix();
                this.mc.mcProfiler.endSection();
                post(DEBUG);
            }

            RenderGameOverlayEvent.Text event = new RenderGameOverlayEvent.Text(eventParent, listL, listR);
            if (!MinecraftForge.EVENT_BUS.post(event)) {
                int top = 20;
                for (String msg : listL) {
                    if (msg == null) continue;
                    drawRect(1, top - 1, 2 + fontRenderer.getStringWidth(msg) + 1, top + fontRenderer.FONT_HEIGHT - 1, -1873784752);
                    fontRenderer.drawString(msg, 2, top, 14737632);
                    top += fontRenderer.FONT_HEIGHT;
                }

                top = 2;
                for (String msg : listR) {
                    if (msg == null) continue;
                    int w = fontRenderer.getStringWidth(msg);

                    final int slotsY = (height - 9 * 22) / 2;
//                        (res.getScaledHeight() - (slotCount * 22)) / 2;

                /*for (int i = 0; i < slotCount; i++) {
                    GLCore.glColorRGBA(i == inv.currentItem ? 0xFFBA66AA : 0xCDCDCDAA);
                    GLCore.glTexturedRect(res.getScaledWidth() - 24, slotsY + (22 * i), zLevel, 0, 25, 20, 20);
                }*/

                    int left = width - (OptionCore.HOR_HOTBAR.isEnabled() || top < slotsY - fontRenderer.FONT_HEIGHT - 2 ? 2 : 26) - w;
                    drawRect(left - 1, top - 1, left + w + 1, top + fontRenderer.FONT_HEIGHT - 1, -1873784752);
                    fontRenderer.drawString(msg, left, top, 14737632);
                    top += fontRenderer.FONT_HEIGHT;
                }
            }

            mc.mcProfiler.endSection();
            post(TEXT);
        }
    }

    private boolean replaceEvent(ElementType el) {
        if (eventParent.type == el && eventParent.isCanceled()) {
            eventParent.setCanceled(false);
            eventParent.setResult(Event.Result.ALLOW);
            pre(el);
            return true;
        }
        return false;
    }

    // c/p from GuiIngameForge
    private boolean pre(ElementType type) {
        return MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(eventParent, type));
    }

    private void post(ElementType type) {
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(eventParent, type));
    }

    @SuppressWarnings("deprecation")
    private HudDrawContext getContext() {
        if (ctx == null) this.ctx = new HudDrawContext(mc.thePlayer, mc, itemRenderer);
        return ctx;
    }

}
