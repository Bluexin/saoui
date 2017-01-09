package com.saomc.saoui.themes.util;

import com.saomc.saoui.api.themes.IHudDrawContext;
import com.saomc.saoui.screens.ingame.HealthStep;
import com.saomc.saoui.social.StaticPlayerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Part of saoui by Bluexin.
 * Provides extra info for what's about to be drawn.
 *
 * @author Bluexin
 */
public class HudDrawContext implements IHudDrawContext {
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
    private float hp;
    private float maxHp;
    private ScaledResolution scaledResolution;
    private float partialTicks;

    public HudDrawContext(EntityPlayer player, Minecraft mc, RenderItem itemRenderer) {
        this.username = player.getDisplayNameString();
        this.mc = mc;
        this.player = player;
        this.itemRenderer = itemRenderer;

        this.usernameWidth = (1 + (mc.fontRendererObj.getStringWidth(username) + 4) / 5) * 5;
    }

    @Override
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

    @Override
    public double usernamewidth() {
        return usernameWidth;
    }

    @Override
    public double hpPct() {
        return hp / maxHp;
    }

    @Override
    public float hp() {
        return hp;
    }

    @Override
    public float maxHp() {
        return maxHp;
    }

    public void setTime(float time) {
        this.time = time;
        this.hp = StaticPlayerHelper.getHealth(mc, mc.player, time);
        this.maxHp = StaticPlayerHelper.getMaxHealth(mc.player);
        healthStep = HealthStep.getStep(hpPct());
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public RenderItem getItemRenderer() {
        return itemRenderer;
    }

    @Override
    public HealthStep healthStep() {
        return healthStep;
    }

    @Override
    public int selectedslot() {
        return player.inventory.currentItem;
    }

    @Override
    public int scaledwidth() {
        return scaledResolution.getScaledWidth();
    }

    @Override
    public int scaledheight() {
        return scaledResolution.getScaledHeight();
    }

    public void setScaledResolution(ScaledResolution scaledResolution) {
        this.scaledResolution = scaledResolution;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    @Override
    public boolean offhandEmpty(int slot) {
        return slot >= 0 && player.inventory.offHandInventory.length > slot && player.inventory.offHandInventory[slot] == null;
    }
}
