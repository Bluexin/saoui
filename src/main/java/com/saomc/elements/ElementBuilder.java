package com.saomc.elements;

import com.saomc.api.screens.GuiSelection;
import com.saomc.api.screens.IElement;
import com.saomc.api.screens.IIcon;
import com.saomc.util.LogCore;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This is the main constructor for elements
 * <p>
 * Created by Tencao on 29/07/2016.
 */
public class ElementBuilder extends IElement {

    private static ElementBuilder ref;
    private Map<String, Elements> elementlist = new HashMap<>();
    private boolean alreadyRun;

    private ElementBuilder() {
        // nill
    }

    @SideOnly(Side.CLIENT)
    public static synchronized ElementBuilder getInstance() {
        if (ref == null)
            // Only return one instance
            ref = new ElementBuilder();
        return ref;
    }

    public Object clone()
            throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
        // Cloning not supported
    }

    /**
     * Gets the element from the list
     *
     * @param name The name of the element
     * @return Returns the element
     */
    public Elements getElement(String name) {
        return elementlist.get(name);
    }

    /**
     * Gets all elements for a gui
     *
     * @param gui The name of the gui
     * @return Returns the list of elements belonging to the category
     */
    public List<Elements> getforGui(GuiSelection gui) {
        return elementlist.values().stream().filter(e -> e.getGui() == gui).collect(Collectors.toList());
    }

    /**
     * Gets all elements for a category
     *
     * @param category The name of the category
     * @return Returns the list of elements belonging to the category
     */
    public List<Elements> getforCategory(String category) {
        return elementlist.values().stream().filter(e -> e.getCategory().equals(category)).collect(Collectors.toList());
    }

    /**
     * Gets all the menus belonging to a gui
     *
     * @param gui The gui the menus belong to
     * @return Returns all the menus that belong to the gui
     */
    public List<Elements> getMenus(GuiSelection gui) {
        return elementlist.values().stream().filter(e -> e.getGui() == gui && e.getParent() == null).collect(Collectors.toList());
    }

    /**
     * Gets all slots belonging to a gui
     *
     * @param gui The gui the slots belong to
     * @return Returns all slots that belong to that gui
     */
    public List<Elements> getSlots(GuiSelection gui) {
        return elementlist.values().stream().filter(e -> e.getGui() == gui && e.getParent() != null).collect(Collectors.toList());
    }

    /**
     * Adds a new Menu via the API
     *
     * @param category The category name for the menu (Used by other categories and slots)
     * @param icon     The display icon for the category
     * @param gui      The class of the GUI this belongs to
     */
    @Override
    public void addMenu(String category, IIcon icon, GuiSelection gui) {
        LogCore.logInfo("initiated addMenu");
        if (elementlist.entrySet().stream().filter(e -> e.getKey().equals(category.toLowerCase())).noneMatch(e -> e.getValue().getCategory().equals(category.toLowerCase()))) {
            elementlist.put(category.toLowerCase(), new Elements(category.toLowerCase(), category, icon, gui, 0, 24, 20, 20, true));
            LogCore.logInfo("Element added, name - " + category + "   category - " + category + "   type - Menu");
        } else LogCore.logWarn("Warning, attempted to add the same element twice \n " +
                "Element name - " + category + "\n" +
                "Element Category - " + category + "\n" +
                "Element Type - Menu"
        );
    }

    /**
     * Adds a new Slot via the API
     *
     * @param name   The display name/category name of the Slot
     * @param parent The parent category
     * @param icon   The display icon for the category
     * @param gui    The class of the GUI this belongs to
     */
    @Override
    public void addSlot(String name, String parent, IIcon icon, GuiSelection gui) {
        if (elementlist.entrySet().stream().filter(e -> e.getKey().equals(name.toLowerCase())).noneMatch(e -> e.getValue().getParent().equals(parent.toLowerCase()))) {
            elementlist.put(name.toLowerCase(), new Elements(name.toLowerCase(), parent, name, icon, gui, 0, 0, 100, 60));
            LogCore.logInfo("Element added, name - " + name + "   parent - " + parent + "   type - Slot");
        } else LogCore.logWarn("Warning, attempted to add the same element twice \n " +
                "Element name - " + name + "\n" +
                "Element Parent - " + parent + "\n" +
                "Element Type - Slot"
        );
    }

    /**
     * Disables a Menu gracefully
     *
     * @param name The name of the menu to remove
     * @param gui  The class of the GUI this belongs to. Two examples are the GuiMainMenu, and IngameMenuGUI
     */
    @Override
    public void disableMenu(String name, GuiSelection gui) {
        if (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen.getClass().equals(gui))
            elementlist.get(name).setEnabled(false);
    }

    /**
     * Disables a Slot gracefully
     *
     * @param name   The name of the slot to remove
     * @param parent The parent category
     * @param gui    The class of the GUI this belongs to. Two examples are the GuiMainMenu, and IngameMenuGUI
     */
    @Override
    public void disableSlot(String name, String parent, GuiSelection gui) {
        if (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen.getClass().equals(gui)) {
            if (elementlist.get(name).getParent().equals(parent))
                elementlist.get(name).setEnabled(false);
        }
    }

    /**
     * Removes a Menu forcefully
     *
     * @param name The name of the menu to remove
     */
    @Override
    public void removeMenu(String name, GuiSelection gui) {
        if (elementlist.get(name).getParent() == null)
            elementlist.remove(name);
    }

    /**
     * Removes a Slot forcefully
     *
     * @param name   The name of the slot to remove
     * @param parent The parent category
     */
    @Override
    public void removeSlot(String name, String parent, GuiSelection gui) {
        if (elementlist.get(name).getParent().equals(parent))
            elementlist.remove(name);
    }

    /**
     * Enables all elements that belongs to a category
     *
     * @param category The category to enable elements for
     */
    public void enableSubElements(String category) {
        elementlist.values().stream().filter(e -> e.getParent().equals(category)).forEach(e -> e.setEnabled(true));
    }

    /**
     * Disables all elements that belongs to a category
     *
     * @param category The category to disable elements for
     */
    public void disableSubElements(String category) {
        elementlist.values().stream().filter(e -> e.getParent().equals(category)).forEach(e -> e.setEnabled(false));
    }

}
