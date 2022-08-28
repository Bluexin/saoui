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

package com.tencao.saoui.effects

import com.tencao.saomclib.GLCore
import com.tencao.saoui.SAOCore
import com.tencao.saoui.api.screens.IIcon
import com.tencao.saoui.resources.StringNames
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.potion.Effects
import net.minecraft.util.ResourceLocation
import java.util.*

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

    private val icons
        get() = ResourceLocation(SAOCore.MODID, "${StringNames.statusIcons}${name.lowercase(Locale.getDefault())}.png")

    @Suppress("unused")
    override fun glDraw(x: Int, y: Int, z: Float) {
        GLCore.glBindTexture(icons)
        GLCore.glTexturedRectV2(x.toDouble(), y.toDouble(), z.toDouble(), 16.0, 16.0, srcWidth = 16.0, srcHeight = 16.0, textureW = 16, textureH = 16)
    }

    companion object {
        fun getEffects(entity: LivingEntity): List<StatusEffects> {
            val effects = LinkedList<StatusEffects>()

            entity.activePotionEffects.filterNotNull().forEach {
                when (it.potion) {
                    Effects.SLOWNESS -> effects.add(if (it.amplifier > 5) PARALYZED else SLOWNESS)
                    Effects.POISON -> effects.add(POISONED)
                    Effects.HUNGER -> effects.add(ROTTEN)
                    Effects.NAUSEA -> effects.add(ILL)
                    Effects.WEAKNESS -> effects.add(WEAK)
                    Effects.WITHER -> effects.add(CURSED)
                    Effects.BLINDNESS -> effects.add(BLIND)
                    Effects.SATURATION -> effects.add(SATURATION)
                    Effects.SPEED -> effects.add(SPEED_BOOST)
                    Effects.WATER_BREATHING -> effects.add(WATER_BREATH)
                    Effects.STRENGTH -> effects.add(STRENGTH)
                    Effects.ABSORPTION -> effects.add(ABSORPTION)
                    Effects.FIRE_RESISTANCE -> effects.add(FIRE_RES)
                    Effects.HASTE -> effects.add(HASTE)
                    Effects.HEALTH_BOOST -> effects.add(HEALTH_BOOST)
                    Effects.INSTANT_HEALTH -> effects.add(INST_HEALTH)
                    Effects.INVISIBILITY -> effects.add(INVISIBILITY)
                    Effects.JUMP_BOOST -> effects.add(JUMP_BOOST)
                    Effects.NIGHT_VISION -> effects.add(NIGHT_VISION)
                    Effects.REGENERATION -> effects.add(REGEN)
                    Effects.RESISTANCE -> effects.add(RESIST)
                }
            }

            if (entity is PlayerEntity) {
                if (entity.foodStats.foodLevel <= 6) {
                    effects.add(STARVING)
                } else if (entity.foodStats.foodLevel <= 18) {
                    effects.add(HUNGRY)
                }
            }

            if (entity.isInWater) {
                if (entity.air <= 0) {
                    effects.add(DROWNING)
                } else if (entity.air < 300) effects.add(WET)
            }

            if (entity.isBurning) effects.add(BURNING)

            return effects
        }
    }

    override fun getRL() = icons
}
