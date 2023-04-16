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

package com.tencao.saoui.util

import com.tencao.saoui.api.info.IPlayerStatsProvider

/**
 * Part of saoui

 * @author Bluexin
 */
class PlayerStats private constructor(val stats: IPlayerStatsProvider) {
    companion object {
        private var instance: PlayerStats? = null

        fun init(provider: IPlayerStatsProvider) {
            check(instance == null) { "PlayerStats already initialized!" }
            instance = PlayerStats(provider)
        }

        fun instance(): PlayerStats {
            checkNotNull(instance) { "PlayerStats not initialized!" }
            return instance!!
        }
    }
}
