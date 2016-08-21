package com.saomc.saoui.api.events;

import com.saomc.saoui.api.screens.IElementBuilder;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by Tencao on 04/08/2016.
 */
public class EventinitElements extends Event { // TODO: is this really needed?
    private IElementBuilder implementation;

    public EventinitElements(IElementBuilder implementation) {
        this.implementation = implementation;
    }

    /**
     * Gets the currently set implementation.
     *
     * @return current implementation
     */
    public IElementBuilder getImplementation() {
        return this.implementation;
    }

    /**
     * Sets the implementation to be used by the SAO UI to retrieve information from.
     *
     * @param provider an instance to be used to retrieve information from
     */
    public void setImplementation(IElementBuilder provider) {
        this.implementation = provider;
    }
}
