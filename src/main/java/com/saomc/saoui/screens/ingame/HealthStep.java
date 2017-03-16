package com.saomc.saoui.screens.ingame;

import com.saomc.saoui.GLCore;
import com.saomc.saoui.social.StaticPlayerHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

@SideOnly(Side.CLIENT)
public enum HealthStep { // Could be loaded from file. Currently the HPBar loads it's own colors.

    VERY_LOW(0.1F, 0xBD0000FF),
    LOW(0.2F, 0xF40000FF),
    VERY_DAMAGED(0.3F, 0xF47800FF),
    DAMAGED(0.4F, 0xF4BD00FF),
    OKAY(0.5F, 0xEDEB38FF),
    GOOD(1.0F, 0x93F43EFF),
    CREATIVE(-1.0F, 0xB32DE3FF);

    private final float healthLimit;
    private final int color;

    HealthStep(float limit, int rgba) {
        healthLimit = limit;
        color = rgba;
    }

    public static HealthStep getStep(Minecraft mc, EntityLivingBase entity, float time) {
        return getStep(entity, StaticPlayerHelper.getHealth(mc, entity, time) / StaticPlayerHelper.getMaxHealth(entity));
    }

    public static HealthStep getStep(EntityLivingBase entity, double health) {
        return entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode ? CREATIVE : getStep(health);
    }

    public static HealthStep getStep(double health) {
        HealthStep step = first();
        while ((health > step.getLimit()) && (step.ordinal() + 1 < values().length)) step = step.next();
        return step == CREATIVE ? GOOD : step;
    }

    private static HealthStep first() {
        return values()[0];
    }

    private HealthStep next() {
        return values()[ordinal() + 1];
    }

    private float getLimit() {
        return healthLimit;
    }

    public final void glColor() {
        GLCore.glColorRGBA(color);
    }

}
