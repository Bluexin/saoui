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

import com.tencao.saoui.SAOCore
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.INBT

/**
 * Part of saoui
 *
 *
 * Possible colors an entity can have.
 *
 * @author Bluexin
 */
enum class ColorState(var rgba: Int) {

    /**
     * Green, default color.
     * Used for passive mobs, friendly NPCs and nice players by default.
     */
    INNOCENT(0x93F43EFF.toInt()),

    /**
     * Orange.
     * Used for mobs not targeting you, and harmful players by default.
     */
    VIOLENT(0xF49B00FF.toInt()),

    /**
     * Red.
     * Used for mobs targeting you, and criminal players by default.
     */
    KILLER(0xB91111FF.toInt()),

    /**
     * Red.
     * Used for bosses by default.
     */
    BOSS(0xBD0000FF.toInt()),

    /**
     * Turquoise.
     * Used for creative players by default.
     */
    CREATIVE(0xBBF6F3FF.toInt()),

    /**
     * Black.
     * Used for GM players by default (OP mode on servers).
     */
    OP(0x000000FF),

    /**
     * Grey.
     * Used for errors by default.
     */
    INVALID(0x8B8B8BFF.toInt()),

    /**
     * Purple.
     * Used for devs of the SAOUI by default.
     */
    DEV(0x79139EFF);

    fun nbt(): INBT {
        val nbt = CompoundNBT()
        nbt.putString(STATE_TAG, name)
        return nbt
    }

    companion object {
        const val STATE_TAG = "${SAOCore.MODID}_state"
    }
}
