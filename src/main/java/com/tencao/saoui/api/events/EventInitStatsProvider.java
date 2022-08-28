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

package com.tencao.saoui.api.events;

import com.tencao.saoui.api.info.IPlayerStatsProvider;
import net.minecraftforge.eventbus.api.Event;

/**
 * Part of saoui
 * Fired when the SAO UI initialises its player stats retrieval fallback.
 * <p>
 * Temporary, future versions will (attempt to) fix inter-mod compatibilities.
 * Shouldn't be fired by other mods!! (unless you're really sure you know what you're doing)
 * <p>
 * Mods could use this event to get the same reference the SAO UI uses, to use the data however they want.
 *
 * @author Bluexin
 */
public class EventInitStatsProvider extends Event {
    private IPlayerStatsProvider implementation;

    public EventInitStatsProvider(IPlayerStatsProvider implementation) {
        this.implementation = implementation;
    }

    /**
     * Gets the currently set implementation.
     *
     * @return current implementation
     */
    public IPlayerStatsProvider getImplementation() {
        return this.implementation;
    }

    /**
     * Sets the implementation to be used by the SAO UI to retrieve information from.
     *
     * @param provider an instance to be used to retrieve information from
     */
    public void setImplementation(IPlayerStatsProvider provider) {
        this.implementation = provider;
    }
}
