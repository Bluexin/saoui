package com.saomc.saoui.effects;

import com.saomc.saoui.GLCore;
import com.saomc.saoui.resources.StringNames;
import com.saomc.saoui.util.OptionCore;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

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

        entity.getActivePotionEffects().stream().filter(potionEffect0 -> potionEffect0 != null).forEach(potionEffect0 -> {

            if (potionEffect0.getPotion() == MobEffects.SLOWNESS && potionEffect0.getAmplifier() > 5)
                effects.add(PARALYZED);
            else if (potionEffect0.getPotion() == MobEffects.POISON) effects.add(POISONED);
            else if (potionEffect0.getPotion() == MobEffects.HUNGER) effects.add(ROTTEN);
            else if (potionEffect0.getPotion() == MobEffects.NAUSEA) effects.add(ILL);
            else if (potionEffect0.getPotion() == MobEffects.WEAKNESS) effects.add(WEAK);
            else if (potionEffect0.getPotion() == MobEffects.WITHER) effects.add(CURSED);
            else if (potionEffect0.getPotion() == MobEffects.BLINDNESS) effects.add(BLIND);
            else if (potionEffect0.getPotion() == MobEffects.SATURATION) effects.add(SATURATION);
            else if (potionEffect0.getPotion() == MobEffects.SPEED) effects.add(SPEED_BOOST);
            else if (potionEffect0.getPotion() == MobEffects.WATER_BREATHING) effects.add(WATER_BREATH);
            else if (potionEffect0.getPotion() == MobEffects.STRENGTH) effects.add(STRENGTH);
            else if (potionEffect0.getPotion() == MobEffects.ABSORPTION) effects.add(ABSORPTION);
            else if (potionEffect0.getPotion() == MobEffects.FIRE_RESISTANCE) effects.add(FIRE_RES);
            else if (potionEffect0.getPotion() == MobEffects.HASTE) effects.add(HASTE);
            else if (potionEffect0.getPotion() == MobEffects.HEALTH_BOOST) effects.add(HEALTH_BOOST);
            else if (potionEffect0.getPotion() == MobEffects.INSTANT_HEALTH) effects.add(INST_HEALTH);
            else if (potionEffect0.getPotion() == MobEffects.INVISIBILITY) effects.add(INVISIBILITY);
            else if (potionEffect0.getPotion() == MobEffects.JUMP_BOOST) effects.add(JUMP_BOOST);
            else if (potionEffect0.getPotion() == MobEffects.NIGHT_VISION) effects.add(NIGHT_VISION);
            else if (potionEffect0.getPotion() == MobEffects.REGENERATION) effects.add(REGEN);
            else if (potionEffect0.getPotion() == MobEffects.RESISTANCE) effects.add(RESIST);
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
        GLCore.glBindTexture(OptionCore.SAO_UI.getValue() ? StringNames.effects : StringNames.effectsCustom);
        GLCore.glTexturedRect(x, y, z, getSrcX(), getSrcY(), SRC_WIDTH, SRC_HEIGHT);
    }

    public final void glDraw(int x, int y) {
        GLCore.glBindTexture(OptionCore.SAO_UI.getValue() ? StringNames.effects : StringNames.effectsCustom);
        GLCore.glTexturedRect(x, y, getSrcX(), getSrcY(), SRC_WIDTH, SRC_HEIGHT);
    }

}
