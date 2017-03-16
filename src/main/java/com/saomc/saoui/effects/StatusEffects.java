package com.saomc.saoui.effects;

import com.saomc.saoui.GLCore;
import com.saomc.saoui.config.OptionCore;
import com.saomc.saoui.resources.StringNames;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@SideOnly(Side.CLIENT)
public enum StatusEffects {

    PARALYZED,
    POISONED,
    STARVING,
    HUNGRY,
    ROTTEN,
    ILL,
    WEAK,
    CURSED,
    BLIND,
    WET,
    DROWNING,
    BURNING,
    SATURATION,
    SPEED_BOOST,
    WATER_BREATH,
    STRENGTH,
    ABSORPTION,
    FIRE_RES,
    HASTE,
    HEALTH_BOOST,
    INST_HEALTH, // Probably won't be used here
    INVISIBILITY,
    JUMP_BOOST,
    NIGHT_VISION,
    REGEN,
    RESIST;

    private static final int SRC_X = 0;
    private static final int SRC_Y = 135;
    private static final int SRC_WIDTH = 15;
    private static final int SRC_HEIGHT = 10;

    @SuppressWarnings("unchecked")
    public static List<StatusEffects> getEffects(EntityLivingBase entity) {
        final List<StatusEffects> effects = new ArrayList<>();

        ((Collection<PotionEffect>) entity.getActivePotionEffects()).stream().filter(Objects::nonNull).forEach(potionEffect0 -> {

            if (potionEffect0.getPotionID() == Potion.moveSlowdown.id && potionEffect0.getAmplifier() > 5)
                effects.add(PARALYZED);
            else if (potionEffect0.getPotionID() == Potion.poison.id) effects.add(POISONED);
            else if (potionEffect0.getPotionID() == Potion.hunger.id) effects.add(ROTTEN);
            else if (potionEffect0.getPotionID() == Potion.confusion.id) effects.add(ILL);
            else if (potionEffect0.getPotionID() == Potion.weakness.id) effects.add(WEAK);
            else if (potionEffect0.getPotionID() == Potion.wither.id) effects.add(CURSED);
            else if (potionEffect0.getPotionID() == Potion.blindness.id) effects.add(BLIND);
            else if (potionEffect0.getPotionID() == Potion.saturation.id) effects.add(SATURATION);
            else if (potionEffect0.getPotionID() == Potion.moveSpeed.id) effects.add(SPEED_BOOST);
            else if (potionEffect0.getPotionID() == Potion.waterBreathing.id) effects.add(WATER_BREATH);
            else if (potionEffect0.getPotionID() == Potion.damageBoost.id) effects.add(STRENGTH);
            else if (potionEffect0.getPotionID() == Potion.absorption.id) effects.add(ABSORPTION);
            else if (potionEffect0.getPotionID() == Potion.fireResistance.id) effects.add(FIRE_RES);
            else if (potionEffect0.getPotionID() == Potion.digSpeed.id) effects.add(HASTE);
            else if (potionEffect0.getPotionID() == Potion.healthBoost.id) effects.add(HEALTH_BOOST);
            else if (potionEffect0.getPotionID() == Potion.heal.id) effects.add(INST_HEALTH);
            else if (potionEffect0.getPotionID() == Potion.invisibility.id) effects.add(INVISIBILITY);
            else if (potionEffect0.getPotionID() == Potion.jump.id) effects.add(JUMP_BOOST);
            else if (potionEffect0.getPotionID() == Potion.nightVision.id) effects.add(NIGHT_VISION);
            else if (potionEffect0.getPotionID() == Potion.regeneration.id) effects.add(REGEN);
            else if (potionEffect0.getPotionID() == Potion.resistance.id) effects.add(RESIST);
        });

        if (entity instanceof EntityPlayer) {
            if (((EntityPlayer) entity).getFoodStats().getFoodLevel() <= 6)
                effects.add(STARVING);
            else if (((EntityPlayer) entity).getFoodStats().getFoodLevel() <= 18)
                effects.add(HUNGRY);
        }

        if (entity.isInWater()) {
            if (entity.getAir() <= 0) effects.add(DROWNING);
            else if (entity.getAir() < 300) effects.add(WET);
        }

        if (entity.isBurning()) effects.add(BURNING);

        return effects;
    }

    private int getSrcX() {
        return SRC_X + (ordinal() % 14) * SRC_WIDTH;
    }

    private int getSrcY() {
        return SRC_Y + ordinal() / 14 * SRC_HEIGHT;
    }

    public final void glDraw(int x, int y, float z) {
        GLCore.glBindTexture(OptionCore.SAO_UI.isEnabled() ? StringNames.effects : StringNames.effectsCustom);
        GLCore.glTexturedRect(x, y, z, getSrcX(), getSrcY(), SRC_WIDTH, SRC_HEIGHT);
    }

    public final void glDraw(int x, int y) {
        GLCore.glBindTexture(OptionCore.SAO_UI.isEnabled() ? StringNames.effects : StringNames.effectsCustom);
        GLCore.glTexturedRect(x, y, getSrcX(), getSrcY(), SRC_WIDTH, SRC_HEIGHT);
    }

}
