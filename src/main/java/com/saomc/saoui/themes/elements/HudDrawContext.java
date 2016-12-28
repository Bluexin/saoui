package com.saomc.saoui.themes.elements;

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

    public String getUsername() {
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

    public double getUsernameWidth() {
        return usernameWidth;
    }

    public double getHpPct() {
        return hpPct;
    }

    public void setTime(float time) {
        this.time = time;
        this.hpPct = StaticPlayerHelper.getHealth(mc, mc.player, time) / StaticPlayerHelper.getMaxHealth(mc.player);
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public RenderItem getItemRenderer() {
        return itemRenderer;
    }
}
