package com.saomc.saoui.api.events;

import com.saomc.saoui.api.screens.Actions;
import com.saomc.saoui.api.screens.ElementType;
import com.saomc.saoui.api.screens.GuiSelection;
import com.saomc.saoui.api.screens.ParentElement;
import cpw.mods.fml.common.eventhandler.Event;
import jdk.nashorn.internal.objects.annotations.Getter;

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
    private ParentElement parentElement;

    public ElementAction(String name, String category, String parent, Actions action, int data, GuiSelection gui, boolean open, boolean locked, ElementType elementType, ParentElement parentElement) {
        this.name = name;
        this.category = category;
        this.parent = parent;
        this.action = action;
        this.data = data;
        this.gui = gui;
        this.isOpen = open;
        this.isLocked = locked;
        this.elementType = elementType;
        this.parentElement = parentElement;
    }

    /**
     * @return Returns the elements name that fired the event
     */
    @Getter
    public String getName() {
        return this.name;
    }

    /**
     * @return Returns what category this belongs too
     * WARNING - Can be null
     */
    @Getter
    @Nullable
    public String getCategory() {
        return this.category;
    }

    /**
     * @return Returns what category this belongs too
     * WARNING - Can be null
     */
    @Getter
    @Nullable
    public String getParent() {
        return this.parent;
    }

    /**
     * @return Returns what action was used
     */
    @Getter
    public Actions getAction() {
        return this.action;
    }

    /**
     * @return Returns data
     */
    @Getter
    public int getData() {
        return this.data;
    }

    /**
     * @return Returns the gui this was sent from
     */
    @Getter
    public GuiSelection getGui() {
        return gui;
    }

    /**
     * Checks to see if the element is open or not
     * Useful for deciding whether to fire an event onClose, or onOpen
     *
     * @return Returns whether the element is already open
     */
    @Getter
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
    @Getter
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Used to quickly identify the type of element
     * @return Returns the elements type
     */
    @Getter
    public ElementType getElementType() {
        return elementType;
    }

    /**
     * Used to get the ParentElement this belongs to
     * Needed for certain tasks such as firing a prompt window
     *
     * @return Returns the ParentElement
     */
    @Getter
    @Nullable
    public ParentElement getParentElement() {
        return parentElement;
    }
}
