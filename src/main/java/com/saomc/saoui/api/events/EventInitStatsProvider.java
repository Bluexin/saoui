package com.saomc.saoui.api.events;

import com.saomc.saoui.api.info.IPlayerStatsProvider;
import net.minecraftforge.fml.common.eventhandler.Event;

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
