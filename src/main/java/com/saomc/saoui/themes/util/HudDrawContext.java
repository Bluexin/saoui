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

package com.saomc.saoui.themes.util;

import com.saomc.saoui.api.info.IPlayerStatsProvider;
import com.saomc.saoui.api.themes.IHudDrawContext;
import com.saomc.saoui.effects.StatusEffects;
import com.saomc.saoui.screens.ingame.HealthStep;
import com.saomc.saoui.social.StaticPlayerHelper;
import com.saomc.saoui.util.PlayerStats;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Part of saoui by Bluexin.
 * Provides extra info for what's about to be drawn.
 *
 * @author Bluexin
 */
@SideOnly(Side.CLIENT)
public class HudDrawContext implements IHudDrawContext {
    /*
    Feel free to add anything you'd need here.
     */
    private final String username;
    private final RenderItem itemRenderer;
    private final Minecraft mc;
    private final double usernameWidth;
    private final IPlayerStatsProvider stats;
    private EntityPlayer player;
    private HealthStep healthStep;
    private float z;
    private float hp;
    private float maxHp;
    private ScaledResolution scaledResolution;
    private float partialTicks;
    private int i;
    private List<EntityPlayer> pt;
    private List<StatusEffects> effects;

    public HudDrawContext(EntityPlayer player, Minecraft mc, RenderItem itemRenderer) {
        this.username = player.getDisplayNameString();
        this.mc = mc;
        this.player = player;
        this.itemRenderer = itemRenderer;
        this.stats = PlayerStats.Companion.instance().getStats();

        this.usernameWidth = (1 + (mc.fontRenderer.getStringWidth(username) + 4) / 5.0) * 5;
    }

    public void setPt(List<EntityPlayer> pt) {
        this.pt = pt;
    }

    @Override
    public String username() {
        return username;
    }

    public Minecraft getMc() {
        return mc;
    }

    @Override
    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public FontRenderer getFontRenderer() {
        return mc.fontRenderer;
    }

    @Override
    public double usernamewidth() {
        return usernameWidth;
    }

    @Override
    public double hpPct() {
        return Math.min(hp / maxHp, 1.0);
    }

    @Override
    public float hp() {
        return hp;
    }

    @Override
    public float maxHp() {
        return maxHp;
    }

    /**
     * Aka partialTicks
     */
    public void setTime(float time) {
        this.hp = StaticPlayerHelper.INSTANCE.getHealth(mc, player, time);
        this.maxHp = StaticPlayerHelper.INSTANCE.getMaxHealth(player);
        healthStep = HealthStep.Companion.getStep(player, hpPct());
        partialTicks = time;
        this.effects = StatusEffects.Companion.getEffects(this.player);
    }

    @Override
    public EntityPlayer getPlayer() {
        return player;
    }

    public void setPlayer(EntityPlayer player) {
        this.player = player;
    }

    @Override
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

    @Override
    public boolean offhandEmpty(int slot) {
        return player.inventory.offHandInventory.get(0).isEmpty();
    }

    @Override
    public int strWidth(String s) {
        return mc.fontRenderer.getStringWidth(s);
    }

    @Override
    public float absorption() {
        return player.getAbsorptionAmount();
    }

    @Override
    public int level() {
        return stats.getLevel(player);
    }

    @Override
    public float experience() {
        return stats.getExpPct(player);
    }

    @Override
    public float horsejump() {
        return ((EntityPlayerSP) player).getHorseJumpPower();
    }

    public int getI() {
        return i;
    }

    @Override
    public void setI(int i) {
        this.i = i;
    }

    @Override
    public int i() {
        return getI();
    }

    @Override
    public String ptName(int index) {
        return validatePtIndex(index) ? pt.get(index).getDisplayNameString() : "???";
    }

    @Override
    public float ptHp(int index) {
        return validatePtIndex(index) ? StaticPlayerHelper.INSTANCE.getHealth(mc, pt.get(index), partialTicks) : 0f;
    }

    @Override
    public float ptMaxHp(int index) {
        return validatePtIndex(index) ? StaticPlayerHelper.INSTANCE.getMaxHealth(pt.get(index)) : 0f;
    }

    @Override
    public float ptHpPct(int index) {
        return validatePtIndex(index) ? ptHp(index) / ptMaxHp(index) : 0f;
    }

    @Override
    public int ptSize() {
        return pt == null ? 0 : pt.size();
    }

    @Override
    public HealthStep ptHealthStep(int index) {
        return HealthStep.Companion.getStep(ptHpPct(index));
    }

    @Override
    public float foodLevel() {
        return StaticPlayerHelper.INSTANCE.getHungerLevel(mc, player, partialTicks);
    }

    @Override
    public float saturationLevel() {
        return player.getFoodStats().getSaturationLevel();
    }

    @Override
    public List<StatusEffects> statusEffects() {
        return effects;
    }

    @Override
    public boolean hasMount() {
        return player.isRiding();
    }

    @Override
    public float mountHp() {
        Entity t = player.getRidingEntity();
        if (t instanceof EntityLivingBase) {
            return ((EntityLivingBase) t).getHealth();
        } else return 0f;
    }

    @Override
    public float mountMaxHp() {
        Entity t = player.getRidingEntity();
        if (t instanceof EntityLivingBase) {
            return ((EntityLivingBase) t).getMaxHealth();
        } else return 1f;
    }

    @Override
    public boolean inWater() {
        return player.isInsideOfMaterial(Material.WATER);
    }

    @Override
    public int air() {
        return player.getAir();
    }

    @Override
    public int armor() {
        return ForgeHooks.getTotalArmorValue(player);
    }

    private boolean validatePtIndex(int index) {
        return index >= 0 && index < ptSize();
    }
}
