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

package com.tencao.saoui.api.info;

import com.tencao.saoui.api.events.EventInitStatsProvider;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

/**
 * Part of saoui
 * Used to retrieve information about a player.
 * The SAO UI will only ever use one instance throughout the whole game session.
 * <p>
 * If you need it, you can implement this interface and provide
 * an instance of your implementation to this mod (trough {@link EventInitStatsProvider}.
 *
 * @author Bluexin
 */
public interface IPlayerStatsProvider {

    /**
     * Getter for the player's level.
     *
     * @param player the player to get the level of
     * @return the level of given player
     */
    default int getLevel(PlayerEntity player) {
        return player.experienceLevel;
    }

    /**
     * Getter for the player's experience percentage (range is [0.0, 1.0[).
     *
     * @param player the player to get the exp of
     * @return the experience % of given player
     */
    default float getExpPct(PlayerEntity player) {
        return player.experience;
    }

    /**
     * Getter for a "stats string" (to be displayed in the menu).
     *
     * @param player the player to get the info about
     * @return the stats of given player
     */
    List<String> getStatsString(PlayerEntity player);
}
