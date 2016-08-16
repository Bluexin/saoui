package com.saomc.api.events;

import com.saomc.api.screens.Actions;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This is the event thats triggered when an element is clicked
 * Use this to assign your own functions to elements
 * <p>
 * Created by Tencao on 03/08/2016.
 */
public class ElementAction extends Event {

    private String name;
    private String category;
    private Actions action;
    private int data;

    public ElementAction(String name, String category, Actions action, int data) {
        this.name = name;
        this.category = category;
        this.action = action;
        this.data = data;
    }

    /**
     * @return Returns the elements name that fired the event
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return Returns what category this belongs too
     * WARNING - Can be null
     */
    public String getCategory() {
        return this.category;
    }

    /**
     * @return Returns what action was used
     */
    public Actions getAction() {
        return this.action;
    }

    /**
     * @return Returns data
     */
    public int getData() {
        return this.data;
    }
}
