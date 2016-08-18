package com.saomc.api.events;

import com.saomc.api.screens.Actions;
import com.saomc.api.screens.GuiSelection;
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
    private GuiSelection gui;
    private boolean isOpen;

    public ElementAction(String name, String category, Actions action, int data, GuiSelection gui, boolean open) {
        this.name = name;
        this.category = category;
        this.action = action;
        this.data = data;
        this.gui = gui;
        this.isOpen = open;
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

    /**
     * @return Returns the gui this was sent from
     */
    public GuiSelection getGui() {
        return gui;
    }

    /**
     * Checks to see if the element is open or not
     * Useful for deciding whether to fire an event onClose, or onOpen
     *
     * @return Returns whether the element is already open
     */
    public boolean isOpen() {
        return isOpen;
    }
}
