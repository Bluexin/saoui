package com.saomc.saoui.effects

import com.saomc.saoui.GLCore
import com.saomc.saoui.SAOCore
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.MobEffects
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

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
    RESIST,
    SLOWNESS;

    val resource by lazy { ResourceLocation(SAOCore.MODID, "textures/hud/status_icons/${name.toLowerCase()}.png") }

    @JvmOverloads
    @Suppress("unused")
    fun glDraw(x: Int, y: Int, z: Float = 0f) {
        GLCore.glBindTexture(resource)
        GLCore.glTexturedRectV2(x.toDouble(), y.toDouble(), z.toDouble(), 16.0, 16.0, srcWidth = 64.0, srcHeight = 64.0, textureW = 64, textureH = 64)
    }

    companion object {
        fun getEffects(entity: EntityLivingBase): List<StatusEffects> {
            val effects = LinkedList<StatusEffects>()

            entity.activePotionEffects.filterNotNull().forEach {
                if (it.potion === MobEffects.SLOWNESS)
                    effects.add(if (it.amplifier > 5) PARALYZED else SLOWNESS)
                else if (it.potion === MobEffects.POISON)
                    effects.add(POISONED)
                else if (it.potion === MobEffects.HUNGER)
                    effects.add(ROTTEN)
                else if (it.potion === MobEffects.NAUSEA)
                    effects.add(ILL)
                else if (it.potion === MobEffects.WEAKNESS)
                    effects.add(WEAK)
                else if (it.potion === MobEffects.WITHER)
                    effects.add(CURSED)
                else if (it.potion === MobEffects.BLINDNESS)
                    effects.add(BLIND)
                else if (it.potion === MobEffects.SATURATION)
                    effects.add(SATURATION)
                else if (it.potion === MobEffects.SPEED)
                    effects.add(SPEED_BOOST)
                else if (it.potion === MobEffects.WATER_BREATHING)
                    effects.add(WATER_BREATH)
                else if (it.potion === MobEffects.STRENGTH)
                    effects.add(STRENGTH)
                else if (it.potion === MobEffects.ABSORPTION)
                    effects.add(ABSORPTION)
                else if (it.potion === MobEffects.FIRE_RESISTANCE)
                    effects.add(FIRE_RES)
                else if (it.potion === MobEffects.HASTE)
                    effects.add(HASTE)
                else if (it.potion === MobEffects.HEALTH_BOOST)
                    effects.add(HEALTH_BOOST)
                else if (it.potion === MobEffects.INSTANT_HEALTH)
                    effects.add(INST_HEALTH)
                else if (it.potion === MobEffects.INVISIBILITY)
                    effects.add(INVISIBILITY)
                else if (it.potion === MobEffects.JUMP_BOOST)
                    effects.add(JUMP_BOOST)
                else if (it.potion === MobEffects.NIGHT_VISION)
                    effects.add(NIGHT_VISION)
                else if (it.potion === MobEffects.REGENERATION)
                    effects.add(REGEN)
                else if (it.potion === MobEffects.RESISTANCE)
                    effects.add(RESIST)
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
