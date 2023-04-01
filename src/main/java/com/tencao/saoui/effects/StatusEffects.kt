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

import com.tencao.saoui.GLCore
import com.tencao.saoui.api.screens.IIcon
import com.tencao.saoui.resources.StringNames
import com.tencao.saoui.util.append
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.MobEffects
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
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

    private val icons
        get() = StringNames.statusIcons.append("${name.lowercase(Locale.getDefault())}.png")

    @Suppress("unused")
    override fun glDraw(x: Int, y: Int, z: Float) {
        GLCore.glBindTexture(icons)
        GLCore.glTexturedRectV2(x.toDouble(), y.toDouble(), z.toDouble(), 16.0, 16.0, srcWidth = 16.0, srcHeight = 16.0, textureW = 16, textureH = 16)
    }

    companion object {
        fun getEffects(entity: EntityLivingBase): List<StatusEffects> {
            val effects = LinkedList<StatusEffects>()

            entity.activePotionEffects.filterNotNull().forEach {
                when (it.potion) {
                    MobEffects.SLOWNESS -> effects.add(if (it.amplifier > 5) PARALYZED else SLOWNESS)
                    MobEffects.POISON -> effects.add(POISONED)
                    MobEffects.HUNGER -> effects.add(ROTTEN)
                    MobEffects.NAUSEA -> effects.add(ILL)
                    MobEffects.WEAKNESS -> effects.add(WEAK)
                    MobEffects.WITHER -> effects.add(CURSED)
                    MobEffects.BLINDNESS -> effects.add(BLIND)
                    MobEffects.SATURATION -> effects.add(SATURATION)
                    MobEffects.SPEED -> effects.add(SPEED_BOOST)
                    MobEffects.WATER_BREATHING -> effects.add(WATER_BREATH)
                    MobEffects.STRENGTH -> effects.add(STRENGTH)
                    MobEffects.ABSORPTION -> effects.add(ABSORPTION)
                    MobEffects.FIRE_RESISTANCE -> effects.add(FIRE_RES)
                    MobEffects.HASTE -> effects.add(HASTE)
                    MobEffects.HEALTH_BOOST -> effects.add(HEALTH_BOOST)
                    MobEffects.INSTANT_HEALTH -> effects.add(INST_HEALTH)
                    MobEffects.INVISIBILITY -> effects.add(INVISIBILITY)
                    MobEffects.JUMP_BOOST -> effects.add(JUMP_BOOST)
                    MobEffects.NIGHT_VISION -> effects.add(NIGHT_VISION)
                    MobEffects.REGENERATION -> effects.add(REGEN)
                    MobEffects.RESISTANCE -> effects.add(RESIST)
                }
            }

            if (entity is EntityPlayer) {
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
