package com.saomc.saoui.themes.elements;

import com.saomc.saoui.screens.ingame.HealthStep;
import com.saomc.saoui.social.StaticPlayerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Part of saoui by Bluexin.
 * Provides extra info for what's about to be drawn.
 *
 * @author Bluexin
 */
public class HudDrawContext {
    /*
    Feel free to add anything you'd need here.
     */
    private final String username;
    private final EntityPlayer player;
    private final RenderItem itemRenderer;
    private final Minecraft mc;
    private final double usernameWidth;
    private HealthStep healthStep;
    private double z;
    private float time;
    private double hpPct;

    public HudDrawContext(EntityPlayer player, Minecraft mc, RenderItem itemRenderer) {
        this.username = player.getDisplayNameString();
        this.mc = mc;
        this.player = player;
        this.itemRenderer = itemRenderer;

        this.usernameWidth = (1 + (mc.fontRendererObj.getStringWidth(username) + 4) / 5) * 5;
    }

    @Deprecated // deprecated for use in java only
    public String username() {
        return username;
    }

    public Minecraft getMc() {
        return mc;
    }

    public double getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    @Deprecated
    public double getUsernameWidth() {
        return usernameWidth;
    }

    public double usernamewidth() {
        return usernameWidth;
    }

    @Deprecated // deprecated for use in java only
    public double hpPct() {
        return hpPct;
    }

    public void setTime(float time) {
        this.time = time;
        this.hpPct = StaticPlayerHelper.getHealth(mc, mc.player, time) / StaticPlayerHelper.getMaxHealth(mc.player);
        healthStep = HealthStep.getStep(hpPct);
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public RenderItem getItemRenderer() {
        return itemRenderer;
    }

    public HealthStep healthStep() {
        return healthStep;
    }
}
