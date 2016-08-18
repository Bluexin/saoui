package com.saomc.elements;

import com.saomc.api.screens.GuiSelection;
import com.saomc.api.screens.IElementBuilder;
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
 * This is used to add and remove elements within the SAO UI
 * <p>
 * Created by Tencao on 29/07/2016.
 */
public class ElementBuilder implements IElementBuilder {

    private static ElementBuilder ref;
    private Map<String, Element> elementlist = new HashMap<>();
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
     * Enables all sub-elements that belong to a parent
     *
     * @param parent The parent category these belong too
     * @param gui The name of the gui these belong too
     */
    public void enableChildElements(String parent, GuiSelection gui){
        elementlist.values().stream().filter(e -> e.getGui() == gui && e.getParent().equals(parent) && !e.isEnabled()).forEach(e -> e.setEnabled(true));
    }

    /**
     * Disables all sub-elements that belong to a parent
     *
     * @param parent The parent category these belong too
     * @param gui The name of the gui these belong too
     */
    public void disableChildElements(String parent, GuiSelection gui){
        elementlist.values().stream().filter(e -> e.getGui() == gui && e.getParent().equals(parent) && e.isEnabled()).forEach(e -> e.setEnabled(false));
    }

    /**
     * This is called on GUI Close to make sure elements reset properly
     *
     * @param gui The GUI that's closing
     */
    public void resetElements(GuiSelection gui){
        elementlist.values().stream().filter(e -> e.getGui() == gui && !e.isMenu()).forEach(e -> e.setEnabled(false));
    }

    /**
     * Gets the element from the list
     *
     * @param name The name of the element
     * @return Returns the element
     */
    @Override
    public Element getElement(String name) {
        return elementlist.get(name);
    }

    /**
     * Gets all elements for a gui
     *
     * @param gui The name of the gui
     * @return Returns the list of elements belonging to the category
     */
    @Override
    public List<Element> getforGui(GuiSelection gui) {
        return elementlist.values().stream().filter(e -> e.getGui() == gui && !e.isRemoved()).collect(Collectors.toList());
    }

    /**
     * Gets all elements for a category
     *
     * @param category The name of the category
     * @return Returns the list of elements belonging to the category
     */
    @Override
    public List<Element> getCategoryContent(String category) {
        return elementlist.values().stream().filter(e -> e.getCategory().equals(category)).collect(Collectors.toList());
    }

    /**
     * Gets all the menus belonging to a gui
     *
     * @param gui The gui the menus belong to
     * @return Returns all the menus that belong to the gui
     */
    @Override
    public List<Element> getMenus(GuiSelection gui) {
        return elementlist.values().stream().filter(e -> e.getGui() == gui && e.getParent() == null).collect(Collectors.toList());
    }

    /**
     * Gets all slots belonging to a gui
     *
     * @param gui The gui the slots belong to
     * @return Returns all slots that belong to that gui
     */
    @Override
    public List<Element> getSlots(GuiSelection gui) {
        return elementlist.values().stream().filter(e -> e.getGui() == gui && e.getParent() != null).collect(Collectors.toList());
    }

    private boolean isParentMenu(String parent, GuiSelection gui){
        return elementlist.values().stream().filter(e -> e.getGui() == gui && e.getCategory().equals(parent)).anyMatch(Element::isMenu);
    }

    /**
     * Gets the Y coord to render
     *
     * @param gui The gui to search for
     * @return The Y coord to render
     */
    private int getYForMenu(GuiSelection gui){
        int value = elementlist.values().stream().filter(e -> e.isMenu() && e.getGui() == gui).mapToInt(Element::getY).max().orElse(-24);
        return value + 24;
    }

    /**
     * Gets the Y coord to render
     *
     * @param parent The parent to search for
     * @param gui The gui to search for
     * @return The Y coord to render
     */
    private int getYForSlot(String parent, GuiSelection gui){
        int value = elementlist.values().stream().filter(e -> e.getParent().equals(parent) && e.getGui() == gui).mapToInt(Element::getY).max().orElse(-20);
        return value + 20;
    }

    private int getXForSlot(String parent, GuiSelection gui){
        if (isParentMenu(parent, gui)) return 25;
        else {
            int value = elementlist.values().stream().filter(e -> e.getCategory().equals(parent) && e.getGui() == gui).mapToInt(Element::getX).sum();
            return value + 100;
        }
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
        if (elementlist.entrySet().stream().filter(e -> e.getKey().equals(category.toLowerCase())).noneMatch(e -> e.getValue().getCategory().equals(category.toLowerCase()))) {
            elementlist.put(category.toLowerCase(), new Element(category.toLowerCase(), category, icon, gui, 0, getYForMenu(gui), 20, 20, true));
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
        if (elementlist.entrySet().stream().filter(e -> e.getKey().equals(name.toLowerCase())).noneMatch(e -> e.getValue().getParent().equals(parent.toLowerCase()))) {
            elementlist.put(name.toLowerCase(), new Element(name.toLowerCase(), parent, name, icon, gui, getXForSlot(parent, gui), getYForSlot(parent, gui), 100, 60));
            LogCore.logDebug("Element added, name - " + name + "   parent - " + parent + "   type - Slot");
        } else LogCore.logWarn("Warning, attempted to add the same element twice \n " +
                "Element name - " + name + "\n" +
                "Element Parent - " + parent + "\n" +
                "Element Type - Slot"
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
        elementlist.values().stream().filter(e -> e.getGui() == gui && e.getCategory().equals(name)).forEach(e -> e.setRemoved(true));
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
        elementlist.values().stream().filter(e -> e.getGui() == gui && e.getCategory().equals(name) && e.getParent().equals(parent)).forEach(e -> e.setRemoved(true));
    }

    /**
     * This is to force remove a Menu. You should rarely, if ever have a need to remove this, and advise most people not to
     *
     * @param name The name of the menu to remove
     * @param gui  The class of the GUI this belongs to
     */
    @Override
    public void removeMenu(String name, GuiSelection gui) {
        if (elementlist.get(name).getParent() == null)
            elementlist.remove(name);
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
        if (elementlist.get(name).getParent().equals(parent))
            elementlist.remove(name);
    }

    /**
     * Enables all elements that belongs to a category
     *
     * @param category The category to enable elements for
     */
    @Override
    public void enableSubElements(String category) {
        elementlist.values().stream().filter(e -> e.getParent().equals(category)).forEach(e -> e.setEnabled(true));
    }

    /**
     * Disables all elements that belongs to a category
     *
     * @param category The category to disable elements for
     */
    @Override
    public void disableSubElements(String category) {
        elementlist.values().stream().filter(e -> e.getParent().equals(category)).forEach(e -> e.setEnabled(false));
    }

}
