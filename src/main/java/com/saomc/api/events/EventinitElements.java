package com.saomc.api.events;

import com.saomc.api.screens.IElement;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by Tencao on 04/08/2016.
 */
public class EventinitElements extends Event {
    private IElement implementation;

    public EventinitElements(IElement implementation) {
        this.implementation = implementation;
    }

    /**
     * Gets the currently set implementation.
     *
     * @return current implementation
     */
    public IElement getImplementation() {
        return this.implementation;
    }

    /**
     * Sets the implementation to be used by the SAO UI to retrieve information from.
     *
     * @param provider an instance to be used to retrieve information from
     */
    public void setImplementation(IElement provider) {
        this.implementation = provider;
    }
}
