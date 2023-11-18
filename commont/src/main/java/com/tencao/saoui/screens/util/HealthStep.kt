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

package com.tencao.saoui.screens.util

import com.tencao.saoui.GLCore
import com.tencao.saoui.social.StaticPlayerHelper
import com.tencao.saoui.util.Client
import com.tencao.saoui.util.isOnline
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import java.util.*
import kotlin.math.max
import kotlin.math.roundToInt

enum class HealthStep constructor(private val limit: Float, var rgba: Int) {

    VERY_LOW(0.1f, 0xBD0000FF.toInt()),
    LOW(0.2f, 0xF40000FF.toInt()),
    VERY_DAMAGED(0.3f, 0xF47800FF.toInt()),
    DAMAGED(0.4f, 0xF4BD00FF.toInt()),
    OKAY(0.5f, 0xEDEB38FF.toInt()),
    GOOD(1.0f, 0x93F43EFF.toInt()),
    CREATIVE(-1.0f, 0xBBF6F3FF.toInt()),
    DEV(-1.0f, 0xB32DE3FF.toInt()),
    INVALID(-1.0f, 0x8B8B8BFF.toInt());

    fun rgba() = rgba

    private operator fun next(): HealthStep {
        return values()[ordinal + 1]
    }

    fun glColor() {
        GLCore.color(rgba)
    }

    companion object {

        val healthMap: WeakHashMap<Player, Float> = WeakHashMap()

        private const val HEALTH_ANIMATION_FACTOR = 0.075f
        private const val HEALTH_FRAME_FACTOR = HEALTH_ANIMATION_FACTOR * HEALTH_ANIMATION_FACTOR * 0x40f * 0x64f

        fun updateHealthSmooth(partialTicks: Float) {
            Minecraft.getInstance().profiler.push("updateHealthSmooth")
            Client.minecraft.level?.players()?.forEach { player ->
                var healthSmooth = healthMap.getOrPut(player) { player.maxHealth }
                val maxHealth: Float = player.maxHealth
                val health: Float = player.health
                when {
                    health == maxHealth -> healthSmooth = maxHealth
                    health <= 0 -> {
                        val value = (18 - player.deathTime).toFloat() / 18
                        healthSmooth = max(0.0f, health * value)
                    }

                    (healthSmooth * 10).roundToInt() != (health * 10).roundToInt() -> healthSmooth += (health - healthSmooth) * (gameTimeDelay(
                        partialTicks
                    ) * HEALTH_ANIMATION_FACTOR)

                    else -> healthSmooth = health

                }

                healthSmooth = max(0.0f, healthSmooth)
                healthMap[player] = healthSmooth
            }
            Minecraft.getInstance().profiler.pop()
        }

        fun getStep(entity: LivingEntity): HealthStep {
            return getStep(
                entity,
                ((entity as? Player)?.healthSmooth ?: entity.health) / StaticPlayerHelper.getMaxHealth(entity)
                    .toDouble()
            )
        }

        fun getStep(entity: LivingEntity?, health: Double): HealthStep {
            var state = GOOD
            if (entity == null) state = INVALID
            if (entity is Player) {
                Client.minecraft.connection?.onlinePlayers?.firstOrNull { it.profile.id == entity.uuid }?.gameMode?.let {
                    if (it.isCreative) {
                        state = CREATIVE
                    } else if (!it.isSurvival) {
                        state = INVALID
                    }
                }
            }
            return if (state != GOOD) state
            else getStep(health)
        }

        fun getStep(player: PlayerInfo, health: Double): HealthStep {
            var state = GOOD
            if (!player.profile.isOnline) state = INVALID
            else Client.minecraft.connection?.onlinePlayers?.firstOrNull { it.profile.id == player.profile.id }?.gameMode?.let {
                if (it.isCreative) {
                    state = CREATIVE
                } else if (!it.isSurvival) {
                    state = INVALID
                }
            } ?: let { state = INVALID }

            return if (state != GOOD) state
            else getStep(health)
        }

        fun getStep(health: Double): HealthStep {
            var step = first()
            while (health > step.limit && step.ordinal + 1 < values().size) step = step.next()
            return if (step == CREATIVE) GOOD else step
        }

        private fun first(): HealthStep {
            return values()[0]
        }

        private fun gameTimeDelay(time: Float): Float {
            return if (time >= 0f) time else HEALTH_FRAME_FACTOR / gameFPS()
        }


        private fun gameFPS(): Int {
            return Minecraft.fps
        }
    }

}


val Player.healthSmooth: Float
    get() = HealthStep.healthMap.getOrPut(this){this.health}