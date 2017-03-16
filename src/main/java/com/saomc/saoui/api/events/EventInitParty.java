package com.saomc.saoui.api.events;

import com.saomc.saoui.api.social.party.IParty;
import cpw.mods.fml.common.eventhandler.Event;

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
