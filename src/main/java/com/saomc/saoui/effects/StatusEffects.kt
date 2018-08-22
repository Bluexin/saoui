package com.saomc.saoui.effects

import com.saomc.saoui.GLCore
import com.saomc.saoui.SAOCore
import com.saomc.saoui.api.screens.IIcon
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import net.minecraft.util.ResourceLocation
import java.util.*

@SideOnly(Side.CLIENT)
enum class StatusEffects : IIcon {

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

    @Suppress("unused")
    override fun glDraw(x: Int, y: Int) {
        GLCore.glBindTexture(resource)
        GLCore.glTexturedRectV2(x.toDouble(), y.toDouble(), 0.0, 16.0, 16.0, srcWidth = 64.0, srcHeight = 64.0, textureW = 64, textureH = 64)
    }

    companion object {
        fun getEffects(entity: EntityLivingBase): List<StatusEffects> {
            val effects = LinkedList<StatusEffects>()

            @Suppress("UNCHECKED_CAST")
            (entity.activePotionEffects as Collection<PotionEffect?>).filterNotNull().forEach {
                when (it.potionID) {
                    Potion.moveSlowdown.id -> effects.add(if (it.amplifier > 5) PARALYZED else SLOWNESS)
                    Potion.poison.id -> effects.add(POISONED)
                    Potion.hunger.id -> effects.add(ROTTEN)
                    Potion.confusion.id -> effects.add(ILL)
                    Potion.weakness.id -> effects.add(WEAK)
                    Potion.wither.id -> effects.add(CURSED)
                    Potion.blindness.id -> effects.add(BLIND)
                    Potion.saturation.id -> effects.add(SATURATION)
                    Potion.moveSpeed.id -> effects.add(SPEED_BOOST)
                    Potion.waterBreathing.id -> effects.add(WATER_BREATH)
                    Potion.damageBoost.id -> effects.add(STRENGTH)
                    Potion.absorption.id -> effects.add(ABSORPTION)
                    Potion.fireResistance.id -> effects.add(FIRE_RES)
                    Potion.digSpeed.id -> effects.add(HASTE)
                    Potion.healthBoost.id -> effects.add(HEALTH_BOOST)
                    Potion.heal.id -> effects.add(INST_HEALTH)
                    Potion.invisibility.id -> effects.add(INVISIBILITY)
                    Potion.jump.id -> effects.add(JUMP_BOOST)
                    Potion.nightVision.id -> effects.add(NIGHT_VISION)
                    Potion.regeneration.id -> effects.add(REGEN)
                    Potion.resistance.id -> effects.add(RESIST)
                }
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

    override fun getRL() = resource
}
