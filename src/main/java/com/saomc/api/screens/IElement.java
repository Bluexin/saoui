package com.saomc.api.screens;

/**
 * This is used to add and remove elements within the SAO UI
 * <p>
 * ElementCore are what's displayed on the ingame menu, and the custom main menu. It's used to display everything from custom categories, or custom buttons.
 * <p>
 * Please use this responsibly
 * <p>
 * Created by Tencao on 29/07/2016.
 */
public abstract class IElement {

    /**
     * This adds a new Menu onscreen. Menus are main categories, appearing as the first choices onscreen
     *
     * @param category The category name for the menu (Used by other categories and slots)
     * @param icon     The display icon for the category
     * @param gui      The class of the GUI this belongs to. Two examples are the GuiMainMenu, and IngameMenuGUI
     */
    public void addMenu(String category, IIcon icon, GuiSelection gui) {
    }

    /**
     * This adds a Slot. Slots are effectively buttons onscreen, which when pressed, fires an ActionPressed event
     * This should be your main method of adding new functions to the menu
     *
     * @param name   The display name of the Slot
     * @param parent The parent category
     * @param icon   The display icon for the category
     * @param gui    The class of the GUI this belongs to. Two examples are the GuiMainMenu, and IngameMenuGUI
     */
    public void addSlot(String name, String parent, IIcon icon, GuiSelection gui) {
    }

    /**
     * This is to gracefully remove a Menu. You should rarely, if ever have a need to remove this, and advise most people not to
     *
     * @param name The name of the menu to remove
     * @param gui  The class of the GUI this belongs to
     */
    public void disableMenu(String name, GuiSelection gui) {
    }

    /**
     * This is to gracefully remove a Slot. This can be useful if like the Category, you intend on replacing an already defined slot with your own version
     *
     * @param name   The name of the slot to remove
     * @param parent The parent category
     * @param gui    The class of the GUI this belongs to
     */
    public void disableSlot(String name, String parent, GuiSelection gui) {
    }

    /**
     * This is to force remove a Menu. You should rarely, if ever have a need to remove this, and advise most people not to
     *
     * @param name The name of the menu to remove
     * @param gui  The class of the GUI this belongs to
     */
    public void removeMenu(String name, GuiSelection gui) {
    }

    /**
     * This is to force remove a Slot. You should rarely, if ever have a need to remove this, and advise most people not to
     *
     * @param name   The name of the slot to remove
     * @param parent The parent category
     * @param gui    The class of the GUI this belongs to
     */
    public void removeSlot(String name, String parent, GuiSelection gui) {
    }

}
