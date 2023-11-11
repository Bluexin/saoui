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

import com.tencao.saomclib.Client
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.monster.IMob
import net.minecraft.entity.monster.ZombifiedPiglinEntity
import net.minecraft.entity.passive.TameableEntity
import net.minecraft.entity.passive.WolfEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraftforge.common.util.FakePlayer
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
            if (entity.persistentData.contains(ColorState.STATE_TAG)) {
                return ColorState.values().firstOrNull { it.name.equals(entity.persistentData.getString(ColorState.STATE_TAG), true) } ?: ColorState.INVALID
            }
            if (cached != null) cached
            return when {
                // TODO Fix this
                // !entity.isNonBoss -> ColorState.BOSS.also { cached = it }
                entity is PlayerEntity && entity !is FakePlayer -> ColorState.INNOCENT.also { cached = it }
                entity is WolfEntity && entity.isAggressive -> ColorState.KILLER
                entity is ZombifiedPiglinEntity && entity.isAggressive -> ColorState.KILLER
                entity is TameableEntity && entity.owner != null -> if (entity.owner == mc.player) ColorState.INNOCENT else ColorState.VIOLENT
                entity is IMob -> if (entity.canEntityBeSeen(mc.player!!) || entity.attackingEntity is PlayerEntity) ColorState.KILLER else ColorState.VIOLENT
                // entity is LivingEntity -> if (entity.brain.taskEntries.any { it is EntityAIAttackMelee || it is EntityAIAttackRanged || it is EntityAINearestAttackableTarget<*> && it.getAttackClass() is EntityPlayer }) ColorState.KILLER else if ( entity.targetTasks.taskEntries.any {it is EntityAIFindEntityNearestPlayer}) ColorState.VIOLENT else ColorState.INNOCENT
                entity !is IMob -> ColorState.INNOCENT.also { cached = it }
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
