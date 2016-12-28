package com.saomc.saoui.elements;

import com.google.common.collect.LinkedHashMultimap;
import com.saomc.saoui.api.screens.*;
import com.saomc.saoui.util.LogCore;
import com.saomc.saoui.config.OptionCore;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This is used to add and remove elements within the SAO UI
 * <p>
 * Created by Tencao on 29/07/2016.
 */
public class ElementBuilder implements IElementBuilder {

    private static ElementBuilder ref;
    private LinkedHashMultimap<String, Element> elementList = LinkedHashMultimap.create();
    private boolean alreadyRun;

    private ElementBuilder() {
        // nill
    }

    /**
     * Should only be called by SAOCore, once.
     * Please use {@link ElementProvider#getBuilder()} instead.
     *
     * @return default element builder
     */
    @SideOnly(Side.CLIENT)
    public static synchronized ElementBuilder getInstance() {
        if (ref == null)
            // Only return one instance
            ref = new ElementBuilder();
        return ref;
    }

    /**
     * Gets the enabled state of the element from the list
     *
     * @param name The name of the element
     * @return Returns true if enabled
     */
    boolean isParentEnabled(String name) {
        return elementList.values().stream().filter(e -> e.getCategory().equals(name.toLowerCase())).anyMatch(Element::isEnabled);
    }

    /**
     * Enables all sub-elements that belong to a parent
     * and disables the parent elements
     *
     * @param category The category these belong too
     * @param parent The categories parent
     * @param gui The name of the gui these belong too
     */
    public void enableChildElements(String category, String parent, GuiSelection gui){
        LogCore.logDebug("enableChildElements fired");
        if (elementList.containsKey(category.toLowerCase())) {
            elementList.get(category.toLowerCase()).stream().filter(e -> e.getGui() == gui).forEachOrdered(e -> e.setEnabled(true));
            setCategoryFocus(gui, parent, false);
            setOpen(gui, category, true);
            LogCore.logDebug("enableChildElements success");
        }
    }

    private void setCategoryFocus(GuiSelection gui, String category, boolean state){
        elementList.get(category.toLowerCase()).stream().filter(e -> e.getGui() == gui).forEach(e -> e.setFocus(state));
        LogCore.logDebug("Setting category focus for " + category + " to " + state);
    }

    /**
     * Disables all sub-elements that belong to a parent
     * and enables the parent elements
     *
     * @param category The category these belong too
     * @param parent The categories parent
     * @param gui The name of the gui these belong too
     */
    public void disableChildElements(String category, String parent, GuiSelection gui){
        LogCore.logDebug("disableChildElements fired");

        if (elementList.containsKey(category.toLowerCase())) {
            elementList.get(category.toLowerCase()).stream().filter(e -> e.getGui() == gui).forEachOrdered(e -> e.setEnabled(false));
            setCategoryFocus(gui, parent, true);
            setOpen(gui, category, false);
            LogCore.logDebug("disableChildElements success");
        }
    }

    /**
     * Sets the open state of the element
     * Important to making sure the same
     * event isn't fired twice
     *
     * @param gui The GUI it belongs to
     * @param category The category of the Element
     * @param state The open state you wish to set
     */
    public void setOpen(GuiSelection gui, String category, boolean state){
        LogCore.logDebug("Setting category open state for " + category + " to " + state);
        elementList.values().stream().filter(e -> e.getGui() == gui && e.getCategory().equals(category)).findFirst().ifPresent(e -> {
            e.setOpen(state);
            e.setHighlight(state);
        });
    }

    /**
     * This is called on GUI Close to make sure elements reset properly
     *
     * @param gui The GUI that's closing
     */
    public void resetElements(GuiSelection gui){
        elementList.values().stream().filter(e -> e.getGui() == gui).forEach(e -> {
            if (e.getElementType() != ElementType.MENU) e.setEnabled(false);
            else e.setFocus(true);
            e.setOpen(false);
            e.setHighlight(false);
            e.setVisibility(1.0F);
        });
    }

    /**
     * Gets the element menu from the list
     *
     * @param name The name of the element
     * @param gui The name of the gui
     * @return Returns the element
     */
    @Override
    public Element getElementMenu(String name, GuiSelection gui) {
        return elementList.get("none").stream().filter(e -> e.getCategory().equals(name.toLowerCase()) && e.getGui() == gui).findFirst().orElse(null);
    }

    /**
     * Gets the element slot from the list
     *
     * @param parent The parent category of the element
     * @param name The name of the element
     * @param gui The name of the gui
     * @return Returns the element
     */
    @Override
    public Element getElementSlot(String parent, String name, GuiSelection gui) {
        return elementList.get(parent.toLowerCase()).stream().filter(e -> e.getCategory().equals(name.toLowerCase()) && e.getGui() == gui).findFirst().orElse(null);
    }

    /**
     * Gets the element parent from the list
     *
     * @param name The name of the element
     * @param gui The name of the gui
     * @return Returns the element
     */
    @Override
    public Element getElementParent(String name, GuiSelection gui) {
        return elementList.values().stream().filter(e -> e.getCategory().equals(name.toLowerCase()) && e.getGui() == gui).findFirst().orElse(null);
    }

    /**
     * Gets all elements for a gui
     *
     * @param gui The name of the gui
     * @return Returns the list of elements belonging to the category
     */
    @Override
    public List<Element> getforGui(GuiSelection gui) {
        return elementList.values().stream().filter(e -> e.getGui() == gui).collect(Collectors.toList());
    }

    /**
     * Gets all elements for a category
     *
     * @param parent The name of the category
     * @return Returns the list of elements belonging to the category
     */
    @Override
    public List<Element> getCategorySlots(GuiSelection gui, String parent) {
        return elementList.get(parent.toLowerCase()).stream().filter(e -> e.getGui() == gui).collect(Collectors.toList());
    }

    /**
     * Gets all the menus belonging to a gui
     *
     * @param gui The gui the menus belong to
     * @return Returns all the menus that belong to the gui
     */
    @Override
    public List<Element> getMenus(GuiSelection gui) {
        return elementList.values().stream().filter(e -> e.getElementType() == ElementType.MENU && e.getGui() == gui).collect(Collectors.toList());
    }

    /**
     * Gets all slots belonging to a gui
     *
     * @param gui The gui the slots belong to
     * @return Returns all slots that belong to that gui
     */
    @Override
    public List<Element> getSlots(GuiSelection gui) {
        return elementList.values().stream().filter(e -> e.getElementType() == ElementType.SLOT && e.getGui() == gui).collect(Collectors.toList());
    }

    /**
     * This adds a new Menu onscreen. Menus are main categories, appearing as the first choices onscreen
     *
     * @param category The category name for the menu (Used by other categories and slots)
     * @param icon     The display icon for the category
     * @param gui      The class of the GUI this belongs to. Two examples are the GuiMainMenu, and IngameMenuGUI
     */
    @Override
    public void addMenu(String category, IIcon icon, GuiSelection gui) {
        if (elementList.values().stream().filter(e -> e.getElementType() == ElementType.MENU).noneMatch(e -> e.getCategory().equals(category.toLowerCase()) && e.getGui() == gui)){
            elementList.put("none", new Element(category.toLowerCase(), category, icon, gui, 0, 0, 20, 20));
            LogCore.logDebug("Element added, name - " + category + "   category - " + category + "   type - Menu");
        } else LogCore.logWarn("Warning, attempted to add the same element twice \n " +
                "Element name - " + category + "\n" +
                "Element Category - " + category + "\n" +
                "Element Type - Menu"
        );
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
    @Override
    public void addSlot(String name, String parent, IIcon icon, GuiSelection gui) {
        if (elementList.get(parent.toLowerCase()).stream().noneMatch(element -> element.getCategory().equals(name.toLowerCase()) && element.getGui() == gui)){
            elementList.put(parent.toLowerCase(), new Element(name.toLowerCase(), parent.toLowerCase(), name, icon, gui, 0, 0, 100, 20));
            LogCore.logDebug("Element added, name - " + name + "   parent - " + parent + "   type - Slot");
        } else LogCore.logWarn("Warning, attempted to add the same element twice \n " +
                "Element name - " + name + "\n" +
                "Element Parent - " + parent + "\n" +
                "Element Type - Slot"
        );
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
    public void addOption(String name, String parent, IIcon icon, GuiSelection gui, OptionCore option) {
        if (elementList.get(parent.toLowerCase()).stream().noneMatch(element -> element.getCategory().equals(name.toLowerCase()) && element.getGui() == gui)){
            elementList.put(parent.toLowerCase(), new Element(name.toLowerCase(), parent.toLowerCase(), name, icon, gui, option, 0, 0, 100, 20));
            LogCore.logDebug("Element added, name - " + name + "   parent - " + parent + "   type - Slot");
        } else LogCore.logWarn("Warning, attempted to add the same element twice \n " +
                "Element name - " + name + "\n" +
                "Element Parent - " + parent + "\n" +
                "Element Type - Slot"
        );
    }

    /**
     * This adds an Inventory list. Like slots, they will be rendered the same way, and will fire an ActionPressed event
     * This should be your main method of adding an inventory list, and you should not be adding each item individually
     *
     * @param parent The parent category
     * @param gui    The class of the GUI this belongs to. Two examples are the GuiMainMenu, and IngameMenuGUI
     * @param itemFilter  This is the group of items to be rendered
     */
    @Override
    public void addInventory(String parent, GuiSelection gui, ItemFilter itemFilter) {
        if (Minecraft.getMinecraft().player == null)
            LogCore.logFatal("WARNING - Attempted to addInventory before world load \n" +
                    "Parent - " + parent + "\n" +
                    "Gui - " + gui + "\n" +
                    "Inventory - Players Inventory");
        else if (elementList.get(parent.toLowerCase()).stream().noneMatch(element -> element.getElementType() == ElementType.INVENTORY && element.getItemFilter() == itemFilter && element.getGui() == gui)){
            elementList.put(parent.toLowerCase(), new Element(parent.toLowerCase(), gui, itemFilter, Minecraft.getMinecraft().player.inventory));
            LogCore.logDebug("Element added, name - ItemList   parent - " + parent + "   type - Items");
        } else LogCore.logWarn("Warning, attempted to add the same element twice \n " +
                "Element name - ItemList\n" +
                "Element Parent - " + parent + "\n" +
                "Element Type - Items"
        );
    }

    /**
     * This is the same as the regular addInventory, but allows you to specify a custom inventory.
     * If you plan to use the vanilla inventory, use the normal one, otherwise use this one.
     *
     * @param parent    The parent category
     * @param gui       The class of the GUI this belongs to. Two examples are the GuiMainMenu, and IngameMenuGUI
     * @param itemFilter     This is the group of items to be rendered
     * @param inventory The inventory you want to send
     */
    @Override
    public void addInventory(String parent, GuiSelection gui, ItemFilter itemFilter, IInventory inventory) {
        if (Minecraft.getMinecraft().player == null)
            LogCore.logFatal("WARNING - Attempted to addInventory before world load \n" +
                    "Parent - " + parent + "\n" +
                    "Gui - " + gui + "\n" +
                    "Inventory - " + inventory.getName());
        else if (elementList.get(parent.toLowerCase()).stream().noneMatch(element -> element.getElementType() == ElementType.INVENTORY && element.getItemFilter() == itemFilter && element.getInventory() == inventory && element.getGui() == gui)){
            elementList.put(parent.toLowerCase(), new Element(parent.toLowerCase(), gui, itemFilter, inventory));
            LogCore.logDebug("Element added, name - ItemList   parent - " + parent + "   type - Items");
        } else LogCore.logWarn("Warning, attempted to add the same element twice \n " +
                "Element name - ItemList\n" +
                "Element Parent - " + parent + "\n" +
                "Element Type - Items"
        );
    }

    /**
     * This is to gracefully remove a Menu. You should rarely, if ever have a need to remove this, and advise most people not to
     *
     * @param name The name of the menu to remove
     * @param gui  The class of the GUI this belongs to
     */
    @Override
    public void disableMenu(String name, GuiSelection gui) {
        elementList.values().stream().filter(e -> e.getGui() == gui && e.getCategory().equals(name.toLowerCase())).forEach(e -> e.setRemoved(true));
    }

    /**
     * This is to gracefully remove a Slot. This can be useful if like the Category, you intend on replacing an already defined slot with your own version
     *
     * @param name   The name of the slot to remove
     * @param parent The parent category
     * @param gui    The class of the GUI this belongs to
     */
    @Override
    public void disableSlot(String name, String parent, GuiSelection gui) {
        elementList.get(parent.toLowerCase()).stream().filter(e -> e.getGui() == gui && e.getCategory().equals(name.toLowerCase())).forEach(e -> e.setRemoved(true));
    }

    /**
     * This is to force remove a Menu. You should rarely, if ever have a need to remove this, and advise most people not to
     *
     * @param name The name of the menu to remove
     * @param gui  The class of the GUI this belongs to
     */
    @Override
    public void removeMenu(String name, GuiSelection gui) {
        elementList.values().stream().filter(e -> e.getGui() == gui && e.getCategory().equals(name.toLowerCase())).forEach(e -> elementList.remove("none", e));
    }

    /**
     * This is to force remove a Slot. You should rarely, if ever have a need to remove this, and advise most people not to
     *
     * @param name   The name of the slot to remove
     * @param parent The parent category
     * @param gui    The class of the GUI this belongs to
     */
    @Override
    public void removeSlot(String name, String parent, GuiSelection gui) {
        elementList.get(parent.toLowerCase()).stream().filter(e -> e.getGui() == gui && e.getCategory().equals(name.toLowerCase())).forEach(e -> elementList.remove(parent.toLowerCase(), e));
    }

    /**
     * Enables all elements that belongs to a category
     *
     * @param category The category to enable elements for
     */
    @Override
    public void enableSubElements(String category) {
        elementList.get(category.toLowerCase()).forEach(e -> e.setEnabled(true));
    }

    /**
     * Disables all elements that belongs to a category
     *
     * @param category The category to disable elements for
     */
    @Override
    public void disableSubElements(String category) {
        elementList.get(category.toLowerCase()).forEach(e -> e.setEnabled(false));
    }

    public void cleanItemFilters() {
        elementList.values().removeIf(e -> e.getElementType() == ElementType.INVENTORY || e.getElementType() == ElementType.ITEM);
    }

    public void cleanTempElements() {
        elementList.values().removeIf(e -> e.getElementType() == ElementType.INVENTORY || e.getElementType() == ElementType.ITEM || e.getElementType() == ElementType.PLAYER);
    }

}
