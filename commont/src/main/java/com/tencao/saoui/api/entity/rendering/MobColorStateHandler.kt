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
package com.tencao.saoui.api.entity.rendering

import com.tencao.saoui.util.Client
import com.tencao.saoui.util.PlayerHelper
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.animal.Wolf
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.piglin.Piglin
import net.minecraft.world.entity.player.Player
import java.lang.ref.WeakReference

/**
 * Part of saoui
 *
 *
 * Default implementation for mobs.
 * This will be the most common implementation for mobs and NPCs.
 *
 * @author Bluexin
 */
class MobColorStateHandler internal constructor(entity: LivingEntity) :
    IColorStateHandler {
    private val theEnt: WeakReference<LivingEntity> = WeakReference(entity)

    /**
     * Caches value when it can (ie the value will never change again)
     */
    private var cached: ColorState? = null

    private val mc = Client.minecraft

    private val color: ColorState
        get() {
            val entity = theEnt.get() ?: return ColorState.INVALID
            if (cached != null) cached
            return when {
                entity is Player && !PlayerHelper.isFakePlayer(entity) -> ColorState.INNOCENT.also { cached = it }
                entity is Wolf && entity.isAggressive -> ColorState.KILLER
                entity is Piglin && entity.isAggressive -> ColorState.KILLER
                entity is TamableAnimal && entity.owner != null -> if (entity.owner == mc.player) ColorState.INNOCENT else ColorState.VIOLENT
                entity is Mob -> if (entity.target is Player || entity is Enemy) ColorState.KILLER else ColorState.VIOLENT
                entity !is Enemy -> ColorState.INNOCENT.also { cached = it }
                else -> ColorState.INVALID
            }
        }

    /**
     * @return the color state the entity should be showing.
     */
    override fun getColorState(): ColorState {
        return color
    }
}
