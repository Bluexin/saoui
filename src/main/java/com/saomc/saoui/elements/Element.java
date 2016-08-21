package com.saomc.saoui.elements;

import com.saomc.saoui.api.screens.GuiSelection;
import com.saomc.saoui.api.screens.IIcon;

/**
 * This is the element class used to store the element object.
 * To add an element, use the ElementBuilder, otherwise it will not be correctly added to the UI
 * <p>
 * Created by Tencao on 30/07/2016.
 */

public class Element {
    private String category;
    private String caption;
    private String parent;
    private IIcon icon;
    private GuiSelection gui;
    private boolean enabled;
    private int x, y;
    private int width;
    private int height;
    private float visibility;
    private boolean focus;
    private boolean highlight;
    private boolean isMenu;
    private boolean isRemoved;

    private Element() {
    }

    Element(String category, String parent, String caption, IIcon icon, GuiSelection gui, int x, int y, int width, int height) {
        this.parent = parent;
        this.caption = category;
        this.category = caption;
        this.icon = icon;
        this.gui = gui;
        this.enabled = isMenu;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.visibility = 1.0F;
        this.isMenu = false;
    }

    Element(String category, String caption, IIcon icon, GuiSelection gui, int x, int y, int width, int height, boolean isMenu) {
        this.caption = category;
        this.category = caption;
        this.parent = "none";
        this.icon = icon;
        this.gui = gui;
        this.enabled = isMenu;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.visibility = 1.0F;
        this.isMenu = isMenu;
    }

    public Element main() {
        return new Element();
    }

    /**
     * Gets the enabled state of the element
     *
     * @return Returns true if enabled
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Sets the enabled state of the element
     *
     * @param state The state you wish to set
     */
    public void setEnabled(boolean state) {
        this.enabled = state;
    }

    /**
     * Gets the current visibility of the element
     *
     * @return Returns the current visibility
     */
    public float getVisibility() {
        return this.visibility;
    }

    /**
     * Sets the visibility of the element
     *
     * @param visibility The visibility you wish to set
     */
    public void setVisibility(float visibility) {
        this.visibility = visibility;
    }

    /**
     * Gets the focus state of the element
     *
     * @return Returns true if the element is focused
     */
    public boolean isFocus() {
        return this.focus;
    }

    /**
     * Sets the focus state of the element
     *
     * @param focus The state you wish to set
     */
    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    /**
     * Gets the elements category
     *
     * @return Returns the elements category
     */
    public String getCategory() {
        return this.category;
    }

    /**
     * Gets the elements parent
     *
     * @return Returns the elements parent
     */
    public String getParent() {
        return this.parent;
    }

    /**
     * Gets the Icon tied to the element
     *
     * @return Returns the elemets Icon
     */
    public IIcon getIcon() {
        return this.icon;
    }

    /**
     * Gets the GUI the element belongs too
     *
     * @return Retusn the GUI
     */
    public GuiSelection getGui() {
        return this.gui;
    }

    /**
     * Get the X position of the element
     *
     * @return Returns the elements x position
     */
    public int getX() {
        return this.x;
    }

    /**
     * Sets the X position of the element
     *
     * @param x The x position you wish to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Get the Y position of the element
     *
     * @return Returns the elements y position
     */
    public int getY() {
        return this.y;
    }

    /**
     * Sets the Y position of the element
     *
     * @param y The y position you wish to set
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Gets the width of the element
     *
     * @return Returns the elements width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Gets the height of the element
     *
     * @return Returns the elements height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Checks if the element is a menu
     *
     * @return Returns true if menu
     */
    public boolean isMenu() {
        return this.isMenu;
    }

    /**
     * Gets the highlight state of the element
     *
     * @return Returns true if highlight
     */
    public boolean isHighlight() {
        return this.highlight;
    }

    /**
     * Sets the highlight state of the element
     *
     * @param state The state you wish to set
     */
    public void setHighlight(boolean state) {
        this.highlight = highlight;
    }

    /**
     * Gets the caption of the element
     *
     * @return Returns the caption
     */
    public String getCaption() {
        return this.caption;
    }

    /**
     * Checks if the Element has been disabled
     *
     * @return Returns true if disabled
     */
    public boolean isRemoved() {
        return isRemoved;
    }

    /**
     * Flags the Element as disabled
     *
     * @param removed The state you wish to set
     */
    public void setRemoved(boolean removed) {
        isRemoved = removed;
    }
}
