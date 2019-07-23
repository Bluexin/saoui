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

package com.saomc.saoui.api.events;

import be.bluexin.saomclib.party.IParty;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Part of saoui
 * Fired when the SAO UI initialises its party storage.
 * <p>
 * Temporary, future versions will (attempt to) fix inter-mod compatibilities.
 * Shouldn't be fired by other mods!! (unless you're really sure you know what you're doing)
 * <p>
 * Mods could use this event to get the same reference the SAO UI uses, to use the data however they want.
 *
 * @author Bluexin
 */
public class EventInitParty extends Event {
    private IParty implementation;

    public EventInitParty(IParty implementation) {
        this.implementation = implementation;
    }

    /**
     * Gets the currently set implementation.
     *
     * @return current implementation
     */
    public IParty getImplementation() {
        return this.implementation;
    }

    /**
     * Sets the implementation to be used by the SAO UI to retrieve information.
     *
     * @param party an instance to be used to store information about the party
     */
    public void setImplementation(IParty party) {
        this.implementation = party;
    }
}
