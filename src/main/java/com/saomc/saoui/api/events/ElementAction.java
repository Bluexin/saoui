package com.saomc.saoui.api.events;

import com.saomc.saoui.api.screens.Actions;
import com.saomc.saoui.api.screens.ElementType;
import com.saomc.saoui.api.screens.GuiSelection;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

/**
 * This is the event thats triggered when an element is clicked
 * Use this to assign your own functions to elements
 * <p>
 * Created by Tencao on 03/08/2016.
 */
public class ElementAction extends Event {

    private String name;
    private String category;
    private String parent;
    private Actions action;
    private int data;
    private GuiSelection gui;
    private boolean isOpen;
    private boolean isLocked;
    private ElementType elementType;

    public ElementAction(String name, String category, String parent, Actions action, int data, GuiSelection gui, boolean open, boolean locked, ElementType elementType) {
        this.name = name;
        this.category = category;
        this.parent = parent;
        this.action = action;
        this.data = data;
        this.gui = gui;
        this.isOpen = open;
        this.isLocked = locked;
        this.elementType = elementType;
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
     * @return Returns what category this belongs too
     * WARNING - Can be null
     */
    public String getParent() {
        return this.parent;
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

    /**
     * Checks to see if the category is locked
     * before firing an event. Useful for making
     * sure multiple categories don't open at once
     *
     * @return Returns true if locked
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Used to quickly identify the type of element
     * @return Returns the elements type
     */
    public ElementType getElementType() {
        return elementType;
    }
}
