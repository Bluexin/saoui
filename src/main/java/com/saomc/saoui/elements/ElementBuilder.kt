package com.saomc.saoui.elements

import com.google.common.collect.LinkedHashMultimap
import com.saomc.saoui.SAOCore
import com.saomc.saoui.api.screens.*
import com.saomc.saoui.config.OptionCore
import net.minecraft.client.Minecraft
import net.minecraft.inventory.IInventory
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.function.Predicate

/**
 * This is used to add and remove elements within the SAO UI
 *
 *
 * Created by Tencao on 29/07/2016.
 */
class ElementBuilder private constructor()
    : IElementBuilder {
    private val elementList = LinkedHashMultimap.create<String, Element>()
    private val alreadyRun: Boolean = false

    /**
     * Gets the enabled state of the element from the list

     * @param name The name of the element
     * *
     * @return Returns true if enabled
     */
    internal fun isParentEnabled(name: String): Boolean {
        return elementList.values().stream().filter { e -> e.category == name.toLowerCase() }.anyMatch({ it.isEnabled })
    }

    /**
     * Enables all sub-elements that belong to a parent
     * and disables the parent elements

     * @param category The category these belong too
     * *
     * @param parent The categories parent
     * *
     * @param gui The name of the gui these belong too
     */
    fun enableChildElements(category: String, parent: String, gui: GuiSelection) {
        SAOCore.LOGGER.debug("enableChildElements fired")
        if (elementList.containsKey(category.toLowerCase())) {
            elementList.get(category.toLowerCase()).stream().filter { e -> e.gui == gui }.forEachOrdered { e -> e.isEnabled = true }
            setCategoryFocus(gui, parent, false)
            setOpen(gui, category, true)
            SAOCore.LOGGER.debug("enableChildElements success")
        }
    }

    private fun setCategoryFocus(gui: GuiSelection, category: String, state: Boolean) {
        elementList.get(category.toLowerCase()).stream().filter { e -> e.gui == gui }.forEach { e -> e.isFocus = state }
        SAOCore.LOGGER.debug("Setting category focus for $category to $state")
    }

    /**
     * Disables all sub-elements that belong to a parent
     * and enables the parent elements

     * @param category The category these belong too
     * *
     * @param parent The categories parent
     * *
     * @param gui The name of the gui these belong too
     */
    fun disableChildElements(category: String, parent: String, gui: GuiSelection) {
        SAOCore.LOGGER.debug("disableChildElements fired")

        if (elementList.containsKey(category.toLowerCase())) {
            elementList.get(category.toLowerCase()).stream().filter { e -> e.gui == gui }.forEachOrdered { e -> e.isEnabled = false }
            setCategoryFocus(gui, parent, true)
            setOpen(gui, category, false)
            SAOCore.LOGGER.debug("disableChildElements success")
        }
    }

    /**
     * Sets the open state of the element
     * Important to making sure the same
     * event isn't fired twice

     * @param gui The GUI it belongs to
     * *
     * @param category The category of the Element
     * *
     * @param state The open state you wish to set
     */
    fun setOpen(gui: GuiSelection, category: String, state: Boolean) {
        SAOCore.LOGGER.debug("Setting category open state for $category to $state")
        elementList.values().stream().filter { e -> e.gui == gui && e.category == category }.findFirst().ifPresent { e ->
            e.isOpen = state
            e.isHighlight = state
        }
    }

    /**
     * This is called on GUI Close to make sure elements reset properly

     * @param gui The GUI that's closing
     */
    fun resetElements(gui: GuiSelection) {
        elementList.values().stream().filter { e -> e.gui == gui }.forEach { e ->
            if (e.elementType != ElementType.MENU)
                e.isEnabled = false
            else
                e.isFocus = true
            e.isOpen = false
            e.isHighlight = false
            e.visibility = 1.0f
        }
    }

    /**
     * Gets the element menu from the list

     * @param name The name of the element
     * *
     * @param gui The name of the gui
     * *
     * @return Returns the element
     */
    override fun getElementMenu(name: String, gui: GuiSelection): Element {
        return elementList.get("none").stream().filter { e -> e.category == name.toLowerCase() && e.gui == gui }.findFirst().orElse(null)
    }

    /**
     * Gets the element slot from the list

     * @param parent The parent category of the element
     * *
     * @param name The name of the element
     * *
     * @param gui The name of the gui
     * *
     * @return Returns the element
     */
    override fun getElementSlot(parent: String, name: String, gui: GuiSelection): Element {
        return elementList.get(parent.toLowerCase()).stream().filter { e -> e.category == name.toLowerCase() && e.gui == gui }.findFirst().orElse(null)
    }

    /**
     * Gets the element parent from the list

     * @param name The name of the element
     * *
     * @param gui The name of the gui
     * *
     * @return Returns the element
     */
    override fun getElementParent(name: String, gui: GuiSelection): Element {
        return elementList.values().stream().filter { e -> e.category == name.toLowerCase() && e.gui == gui }.findFirst().orElse(null)
    }

    /**
     * Gets all elements for a gui

     * @param gui The name of the gui
     * *
     * @return Returns the list of elements belonging to the category
     */
    override fun getforGui(gui: GuiSelection): List<Element> {
        return elementList.values().filter { e -> e.gui == gui }
    }

    /**
     * Gets all elements for a category

     * @param parent The name of the category
     * *
     * @return Returns the list of elements belonging to the category
     */
    override fun getCategorySlots(gui: GuiSelection, parent: String): List<Element> {
        return elementList.get(parent.toLowerCase()).filter { e -> e.gui == gui }
    }

    /**
     * Gets all the menus belonging to a gui

     * @param gui The gui the menus belong to
     * *
     * @return Returns all the menus that belong to the gui
     */
    override fun getMenus(gui: GuiSelection): List<Element> {
        return elementList.values().filter { e -> e.elementType == ElementType.MENU && e.gui == gui }
    }

    /**
     * Gets all slots belonging to a gui

     * @param gui The gui the slots belong to
     * *
     * @return Returns all slots that belong to that gui
     */
    override fun getSlots(gui: GuiSelection): List<Element> {
        return elementList.values().filter { e -> e.elementType == ElementType.SLOT && e.gui == gui }
    }

    /**
     * This adds a new Menu onscreen. Menus are main categories, appearing as the first choices onscreen

     * @param category The category name for the menu (Used by other categories and slots)
     * *
     * @param icon     The display icon for the category
     * *
     * @param gui      The class of the GUI this belongs to. Two examples are the GuiMainMenu, and IngameMenuGUI
     */
    override fun addMenu(category: String, icon: IIcon, gui: GuiSelection) {
        if (elementList.values().stream().filter { e -> e.elementType == ElementType.MENU }.noneMatch { e -> e.category == category.toLowerCase() && e.gui == gui }) {
            elementList.put("none", Element(category.toLowerCase(), category, icon, gui, 0, 0, 20, 20))
            SAOCore.LOGGER.debug("Element added, name - $category   category - $category   type - Menu")
        } else
            SAOCore.LOGGER.warn("Warning, attempted to add the same element twice \n " +
                    "Element name - " + category + "\n" +
                    "Element Category - " + category + "\n" +
                    "Element Type - Menu"
            )
    }

    /**
     * This adds a Slot. Slots are effectively buttons onscreen, which when pressed, fires an ActionPressed event
     * This should be your main method of adding new functions to the menu

     * @param name   The display name of the Slot
     * *
     * @param parent The parent category
     * *
     * @param icon   The display icon for the category
     * *
     * @param gui    The class of the GUI this belongs to. Two examples are the GuiMainMenu, and IngameMenuGUI
     */
    override fun addSlot(name: String, parent: String, icon: IIcon, gui: GuiSelection) {
        if (elementList.get(parent.toLowerCase()).stream().noneMatch { element -> element.category == name.toLowerCase() && element.gui == gui }) {
            elementList.put(parent.toLowerCase(), Element(name.toLowerCase(), parent.toLowerCase(), name, icon, gui, 0, 0, 100, 20))
            SAOCore.LOGGER.debug("Element added, name - $name   parent - $parent   type - Slot")
        } else
            SAOCore.LOGGER.warn("Warning, attempted to add the same element twice \n " +
                    "Element name - " + name + "\n" +
                    "Element Parent - " + parent + "\n" +
                    "Element Type - Slot"
            )
    }

    /**
     * This adds a Slot. Slots are effectively buttons onscreen, which when pressed, fires an ActionPressed event
     * This should be your main method of adding new functions to the menu

     * @param name   The display name of the Slot
     * *
     * @param parent The parent category
     * *
     * @param icon   The display icon for the category
     * *
     * @param gui    The class of the GUI this belongs to. Two examples are the GuiMainMenu, and IngameMenuGUI
     */
    fun addOption(name: String, parent: String, icon: IIcon, gui: GuiSelection, option: OptionCore) {
        if (elementList.get(parent.toLowerCase()).stream().noneMatch { element -> element.category == name.toLowerCase() && element.gui == gui }) {
            elementList.put(parent.toLowerCase(), Element(name.toLowerCase(), parent.toLowerCase(), name, icon, gui, option, 0, 0, 100, 20))
            SAOCore.LOGGER.debug("Element added, name - $name   parent - $parent   type - Slot")
        } else
            SAOCore.LOGGER.warn("Warning, attempted to add the same element twice \n " +
                    "Element name - " + name + "\n" +
                    "Element Parent - " + parent + "\n" +
                    "Element Type - Slot"
            )
    }

    /**
     * This adds an Inventory list. Like slots, they will be rendered the same way, and will fire an ActionPressed event
     * This should be your main method of adding an inventory list, and you should not be adding each item individually

     * @param parent The parent category
     * *
     * @param gui    The class of the GUI this belongs to. Two examples are the GuiMainMenu, and IngameMenuGUI
     * *
     * @param itemFilter  This is the group of items to be rendered
     */
    override fun addInventory(parent: String, gui: GuiSelection, itemFilter: ItemFilter) {
        if (Minecraft.getMinecraft().player == null)
            SAOCore.LOGGER.fatal("WARNING - Attempted to addInventory before world load \n" +
                    "Parent - " + parent + "\n" +
                    "Gui - " + gui + "\n" +
                    "Inventory - Players Inventory")
        else if (elementList.get(parent.toLowerCase()).stream().noneMatch { element -> element.elementType == ElementType.INVENTORY && element.itemFilter === itemFilter && element.gui == gui }) {
            elementList.put(parent.toLowerCase(), Element(parent.toLowerCase(), gui, itemFilter, Minecraft.getMinecraft().player.inventory))
            SAOCore.LOGGER.debug("Element added, name - ItemList   parent - $parent   type - Items")
        } else
            SAOCore.LOGGER.warn("Warning, attempted to add the same element twice \n " +
                    "Element name - ItemList\n" +
                    "Element Parent - " + parent + "\n" +
                    "Element Type - Items"
            )
    }

    /**
     * This is the same as the regular addInventory, but allows you to specify a custom inventory.
     * If you plan to use the vanilla inventory, use the normal one, otherwise use this one.

     * @param parent    The parent category
     * *
     * @param gui       The class of the GUI this belongs to. Two examples are the GuiMainMenu, and IngameMenuGUI
     * *
     * @param itemFilter     This is the group of items to be rendered
     * *
     * @param inventory The inventory you want to send
     */
    override fun addInventory(parent: String, gui: GuiSelection, itemFilter: ItemFilter, inventory: IInventory) {
        if (Minecraft.getMinecraft().player == null)
            SAOCore.LOGGER.fatal("WARNING - Attempted to addInventory before world load \n" +
                    "Parent - " + parent + "\n" +
                    "Gui - " + gui + "\n" +
                    "Inventory - " + inventory.name)
        else if (elementList.get(parent.toLowerCase()).stream().noneMatch { element -> element.elementType == ElementType.INVENTORY && element.itemFilter === itemFilter && element.inventory === inventory && element.gui == gui }) {
            elementList.put(parent.toLowerCase(), Element(parent.toLowerCase(), gui, itemFilter, inventory))
            SAOCore.LOGGER.debug("Element added, name - ItemList   parent - $parent   type - Items")
        } else
            SAOCore.LOGGER.warn("Warning, attempted to add the same element twice \n " +
                    "Element name - ItemList\n" +
                    "Element Parent - " + parent + "\n" +
                    "Element Type - Items"
            )
    }

    /**
     * This is to gracefully remove a Menu. You should rarely, if ever have a need to remove this, and advise most people not to

     * @param name The name of the menu to remove
     * *
     * @param gui  The class of the GUI this belongs to
     */
    override fun disableMenu(name: String, gui: GuiSelection) {
        elementList.values().stream().filter { e -> e.gui == gui && e.category == name.toLowerCase() }.forEach { e -> e.isRemoved = true }
    }

    /**
     * This is to gracefully remove a Slot. This can be useful if like the Category, you intend on replacing an already defined slot with your own version

     * @param name   The name of the slot to remove
     * *
     * @param parent The parent category
     * *
     * @param gui    The class of the GUI this belongs to
     */
    override fun disableSlot(name: String, parent: String, gui: GuiSelection) {
        elementList.get(parent.toLowerCase()).stream().filter { e -> e.gui == gui && e.category == name.toLowerCase() }.forEach { e -> e.isRemoved = true }
    }

    /**
     * This is to force remove a Menu. You should rarely, if ever have a need to remove this, and advise most people not to

     * @param name The name of the menu to remove
     * *
     * @param gui  The class of the GUI this belongs to
     */
    override fun removeMenu(name: String, gui: GuiSelection) {
        elementList.values().stream().filter { e -> e.gui == gui && e.category == name.toLowerCase() }.forEach { e -> elementList.remove("none", e) }
    }

    /**
     * This is to force remove a Slot. You should rarely, if ever have a need to remove this, and advise most people not to

     * @param name   The name of the slot to remove
     * *
     * @param parent The parent category
     * *
     * @param gui    The class of the GUI this belongs to
     */
    override fun removeSlot(name: String, parent: String, gui: GuiSelection) {
        elementList.get(parent.toLowerCase()).stream().filter { e -> e.gui == gui && e.category == name.toLowerCase() }.forEach { e -> elementList.remove(parent.toLowerCase(), e) }
    }

    /**
     * Enables all elements that belongs to a category

     * @param category The category to enable elements for
     */
    override fun enableSubElements(category: String) {
        elementList.get(category.toLowerCase()).forEach { e -> e.isEnabled = true }
    }

    /**
     * Disables all elements that belongs to a category

     * @param category The category to disable elements for
     */
    override fun disableSubElements(category: String) {
        elementList.get(category.toLowerCase()).forEach { e -> e.isEnabled = false }
    }

    fun cleanItemFilters() {
        elementList.values().removeIf { e -> e.elementType == ElementType.INVENTORY || e.elementType == ElementType.ITEM }
    }

    fun cleanTempElements() {
        elementList.values().removeIf { e -> e.elementType == ElementType.INVENTORY || e.elementType == ElementType.ITEM || e.elementType == ElementType.PLAYER }
    }

    companion object {

        private var ref: ElementBuilder = ElementBuilder()

        /**
         * Should only be called by SAOCore, once.
         * Please use [ElementProvider.getBuilder] instead.

         * @return default element builder
         */
        // Only return one instance
        val instance: ElementBuilder
            @SideOnly(Side.CLIENT)
            @Synchronized get() {
                return ref
            }
    }

}
