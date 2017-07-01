package com.saomc.saoui.effects

import com.saomc.saoui.GLCore
import com.saomc.saoui.resources.StringNames
import com.saomc.saoui.config.OptionCore
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.MobEffects
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

import java.util.ArrayList
import java.util.Objects

@SideOnly(Side.CLIENT)
enum class StatusEffects {

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

    private val srcX: Int
        get() = SRC_X + ordinal % 14 * SRC_WIDTH

    private val srcY: Int
        get() = SRC_Y + ordinal / 14 * SRC_HEIGHT

    fun glDraw(x: Int, y: Int, z: Float) {
        GLCore.glBindTexture(if (OptionCore.SAO_UI.isEnabled) StringNames.effects else StringNames.effectsCustom)
        GLCore.glTexturedRect(x.toDouble(), y.toDouble(), z.toDouble(), srcX.toDouble(), srcY.toDouble(), SRC_WIDTH.toDouble(), SRC_HEIGHT.toDouble())
    }

    fun glDraw(x: Int, y: Int) {
        GLCore.glBindTexture(if (OptionCore.SAO_UI.isEnabled) StringNames.effects else StringNames.effectsCustom)
        GLCore.glTexturedRect(x.toDouble(), y.toDouble(), srcX.toDouble(), srcY.toDouble(), SRC_WIDTH.toDouble(), SRC_HEIGHT.toDouble())
    }

    companion object {

        private val SRC_X = 0
        private val SRC_Y = 135
        private val SRC_WIDTH = 15
        private val SRC_HEIGHT = 10

        fun getEffects(entity: EntityLivingBase): List<StatusEffects> {
            val effects = ArrayList<StatusEffects>()

            entity.activePotionEffects.filter{ Objects.nonNull(it) }.forEach { potionEffect0 ->

                if (potionEffect0.potion === MobEffects.SLOWNESS && potionEffect0.amplifier > 5)
                    effects.add(PARALYZED)
                else if (potionEffect0.potion === MobEffects.POISON)
                    effects.add(POISONED)
                else if (potionEffect0.potion === MobEffects.HUNGER)
                    effects.add(ROTTEN)
                else if (potionEffect0.potion === MobEffects.NAUSEA)
                    effects.add(ILL)
                else if (potionEffect0.potion === MobEffects.WEAKNESS)
                    effects.add(WEAK)
                else if (potionEffect0.potion === MobEffects.WITHER)
                    effects.add(CURSED)
                else if (potionEffect0.potion === MobEffects.BLINDNESS)
                    effects.add(BLIND)
                else if (potionEffect0.potion === MobEffects.SATURATION)
                    effects.add(SATURATION)
                else if (potionEffect0.potion === MobEffects.SPEED)
                    effects.add(SPEED_BOOST)
                else if (potionEffect0.potion === MobEffects.WATER_BREATHING)
                    effects.add(WATER_BREATH)
                else if (potionEffect0.potion === MobEffects.STRENGTH)
                    effects.add(STRENGTH)
                else if (potionEffect0.potion === MobEffects.ABSORPTION)
                    effects.add(ABSORPTION)
                else if (potionEffect0.potion === MobEffects.FIRE_RESISTANCE)
                    effects.add(FIRE_RES)
                else if (potionEffect0.potion === MobEffects.HASTE)
                    effects.add(HASTE)
                else if (potionEffect0.potion === MobEffects.HEALTH_BOOST)
                    effects.add(HEALTH_BOOST)
                else if (potionEffect0.potion === MobEffects.INSTANT_HEALTH)
                    effects.add(INST_HEALTH)
                else if (potionEffect0.potion === MobEffects.INVISIBILITY)
                    effects.add(INVISIBILITY)
                else if (potionEffect0.potion === MobEffects.JUMP_BOOST)
                    effects.add(JUMP_BOOST)
                else if (potionEffect0.potion === MobEffects.NIGHT_VISION)
                    effects.add(NIGHT_VISION)
                else if (potionEffect0.potion === MobEffects.REGENERATION)
                    effects.add(REGEN)
                else if (potionEffect0.potion === MobEffects.RESISTANCE) effects.add(RESIST)
            }

            if (entity is EntityPlayer) {
                if (entity.foodStats.foodLevel <= 6)
                    effects.add(STARVING)
                else if (entity.foodStats.foodLevel <= 18)
                    effects.add(HUNGRY)
            }

            if (entity.isInWater) {
                if (entity.air <= 0)
                    effects.add(DROWNING)
                else if (entity.air < 300) effects.add(WET)
            }

            if (entity.isBurning) effects.add(BURNING)

            return effects
        }
    }

}
