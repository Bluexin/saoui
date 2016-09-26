package com.saomc.saoui.elements;

import com.saomc.saoui.api.screens.ElementType;
import com.saomc.saoui.api.screens.GuiSelection;
import com.saomc.saoui.api.screens.IIcon;
import com.saomc.saoui.api.screens.ItemFilter;
import com.saomc.saoui.util.ColorUtil;
import com.saomc.saoui.util.LogCore;
import com.saomc.saoui.util.OptionCore;
import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This is the element class used to store the element object.
 * To add an element, use the ElementBuilder, otherwise it will not be correctly added to the UI
 * <p>
 * Created by Tencao on 30/07/2016.
 */

public class Element implements ParentElement{
    private ParentElement parentElement;
    private ElementType elementType;
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
    private boolean highlight = false;
    private boolean isRemoved;
    private boolean isOpen = false;
    private OptionCore option;

    //Item Specific Information
    private ItemFilter itemFilter;
    private IInventory inventory;
    private Item item;
    private List<Integer> slots;

    private Element() {
    }

    //Slot specific
    Element(String category, String parent, String caption, IIcon icon, GuiSelection gui, int x, int y, int width, int height) {
        this.elementType = ElementType.SLOT;
        this.parent = parent;
        this.caption = caption;
        this.category = category;
        this.icon = icon;
        this.gui = gui;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.visibility = 1.0F;
    }

    //Option specific
    Element(String category, String parent, String caption, IIcon icon, GuiSelection gui, OptionCore option, int x, int y, int width, int height) {
        this.elementType = ElementType.OPTION;
        this.parent = parent;
        this.caption = caption;
        this.category = category;
        this.icon = icon;
        this.gui = gui;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.visibility = 1.0F;
        this.option = option;
    }

    //Menu specific
    Element(String category, String caption, IIcon icon, GuiSelection gui, int x, int y, int width, int height) {
        this.elementType = ElementType.MENU;
        this.caption = caption;
        this.category = category;
        this.parent = "none";
        this.icon = icon;
        this.gui = gui;
        this.enabled = true;
        this.focus = true;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.visibility = 1.0F;
    }

    //Inventory specific
    Element(String parent, GuiSelection gui, ItemFilter itemFilter, IInventory inventory) {
        this.elementType = ElementType.INVENTORY;
        this.parent = parent;
        this.category = "itemfilter";
        this.caption = "";
        this.gui = gui;
        this.itemFilter = itemFilter;
        this.inventory = inventory;
    }

    //Item specific
    Element(String parent, GuiSelection gui, Item item, int slot, IInventory inventory) {
        this.elementType = ElementType.ITEM;
        this.parent = parent;
        this.caption = getItemName();
        this.category = getItemName().toLowerCase();
        this.gui = gui;
        this.item = item;
        this.slots.add(slot);
        this.inventory = inventory;
    }

    public Element main() {
        return new Element();
    }

    /**
     * Gets the enabled state of the element
     *
     * @return Returns true if enabled
     */
    @Getter
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Sets the enabled state of the element
     *
     * @param state The state you wish to set
     */
    @Setter
    public void setEnabled(boolean state) {
        this.enabled = state;
        this.focus = state;
        if (!state) visibility = 1.0F;
    }

    /**
     * Gets the current visibility of the element
     *
     * @return Returns the current visibility
     */
    @Getter
    public float getVisibility() {
        return this.visibility;
    }

    /**
     * Sets the visibility of the element
     *
     * @param visibility The visibility you wish to set
     */
    @Setter
    public void setVisibility(float visibility) {
        this.visibility = visibility;
    }

    /**
     * Gets the focus state of the element
     *
     * @return Returns true if the element is focused
     */
    @Getter
    public boolean isFocus() {
        return this.focus;
    }

    /**
     * Sets the focus state of the element
     *
     * @param focus The state you wish to set
     */
    @Setter
    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    /**
     * Checks if this element is already open
     *
     * @return Returns true if already open
     */
    @Getter
    public boolean isOpen() { return this.isOpen;}

    /**
     * Sets the open state of this element
     *
     * @param state The state you wish to set
     */
    @Setter
    public void setOpen(boolean state) { this.isOpen = state; }

    /**
     * Gets the elements category
     *
     * @return Returns the elements category
     */
    @Getter
    public String getCategory() {
        return this.category;
    }

    /**
     * Gets the elements parent
     *
     * @return Returns the elements parent
     */
    @Getter
    @Nullable
    public String getParent() {
        return this.parent;
    }

    /**
     * Gets the Icon tied to the element
     *
     * @return Returns the elemets Icon
     */
    @Getter
    @Nullable
    public IIcon getIcon() {
        return this.icon;
    }

    /**
     * Gets the GUI the element belongs too
     *
     * @return Retusn the GUI
     */
    @Getter
    public GuiSelection getGui() {
        return this.gui;
    }

    /**
     * Get the X position of the element
     *
     * @return Returns the elements x position
     */
    @Getter
    public int getX(boolean relative) {
        return relative ? x : x + (parentElement != null ? parentElement.getX(relative) : 0);
    }

    /**
     * Sets the X position of the element
     *
     * @param x The x position you wish to set
     */
    @Setter
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Get the Y position of the element
     *
     * @return Returns the elements y position
     */
    @Getter
    public int getY(boolean relative) {
        return relative ? y : y + (parentElement != null ? parentElement.getY(relative) : 0);
    }

    /**
     * Sets the Y position of the element
     *
     * @param y The y position you wish to set
     */
    @Setter
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Gets the width of the element
     *
     * @return Returns the elements width
     */
    @Getter
    public int getWidth() {
        return this.width;
    }

    /**
     * Gets the height of the element
     *
     * @return Returns the elements height
     */
    @Getter
    public int getHeight() {
        return this.height;
    }

    /**
     * Gets the width of the element
     *
     * @param width The width you wish to set
     */
    @Setter
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Sets the height of the element
     *
     * @param height The height you wish to set
     */
    @Setter
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Gets the elements type to allow for fast and easy filtering
     *
     * @return Returns the elements type
     */
    @Getter
    public ElementType getElementType() {
        return elementType;
    }

    /**
     * Gets the highlight state of the element
     *
     * @return Returns true if highlight
     */
    @Getter
    public boolean isHighlight() {
        return this.highlight;
    }

    /**
     * Sets the highlight state of the element
     *
     * @param state The state you wish to set
     */
    @Setter
    public void setHighlight(boolean state) {
        this.highlight = state;
    }

    /**
     * Gets the caption of the element
     *
     * @return Returns the caption
     */
    @Getter
    @Nullable
    public String getCaption() {
        return this.caption;
    }

    /**
     * Checks if the Element has been disabled
     *
     * @return Returns true if disabled
     */
    @Getter
    public boolean isRemoved() {
        return isRemoved;
    }

    /**
     * Flags the Element as disabled
     *
     * @param removed The state you wish to set
     */
    @Setter
    public void setRemoved(boolean removed) {
        isRemoved = removed;
    }

    /**
     * Sets the parent element
     *
     * @param parentElement The parent element
     */
    @Setter
    public void setParentElement(ParentElement parentElement){
        this.parentElement = parentElement;
    }

    @Getter
    private boolean mouseOver(int cursorX, int cursorY) {
        return mouseOver(cursorX, cursorY, -1);
    }

    @Getter
    public final boolean mouseOver(int cursorX, int cursorY, int flag) {
        if ((visibility >= 0.6) && (enabled)) {
            final int left = getX(false);
            final int top = getY(false);

            return (
                    (cursorX >= left) &&
                            (cursorY >= top) &&
                            (cursorX <= left + width) &&
                            (cursorY <= top + height)
            );
        } else {
            return false;
        }
    }

    @Getter
    public void mouseMoved(Minecraft mc, int cursorX, int cursorY) {
    }

    @Getter
    public boolean mousePressed(Minecraft mc, int cursorX, int cursorY, int button) {
        return mouseOver(cursorX, cursorY);
    }

    @Getter
    public boolean mouseReleased(Minecraft mc, int cursorX, int cursorY, int button) {
        return mouseOver(cursorX, cursorY);
    }

    @Getter
    public boolean mouseWheel(Minecraft mc, int cursorX, int cursorY) {
        return mouseOver(cursorX, cursorY);
    }

    @Getter
    public int hoverState(int cursorX, int cursorY) {
        if (elementType == ElementType.OPTION){
            return option.isEnabled() ? 2 : mouseOver(cursorX, cursorY) ? 2 : focus ? 1 : 0;
        }
        else if (elementType == ElementType.MENU || elementType == ElementType.SLOT)
            return isOpen() ? 2 : mouseOver(cursorX, cursorY) ? 2 : isHighlight() ? 2 : focus ? 1 : 0;
        else return 1;
    }

    @Getter
    @Nullable
    public ItemFilter getItemFilter() {
        if (elementType != ElementType.INVENTORY) LogCore.logFatal("getItemFilter called on non-inventory element");
        return itemFilter;
    }

    @Getter
    @Nullable
    public IInventory getInventory() {
        if (elementType != ElementType.INVENTORY) LogCore.logFatal("getInventory called on non-inventory element");
        return inventory;
    }

    @Getter
    public int getTotalStackSize() {
        int total = 0;
        for (int slot : slots) {
            if (inventory.getStackInSlot(slots.indexOf(slot)) != null && inventory.getStackInSlot(slots.indexOf(slot)).getItem() == item)
                total += inventory.getStackInSlot(slots.indexOf(slot)).stackSize;
            else slots.remove(slot);
        }

        return total;
    }

    @Getter
    @Nullable
    public String getItemName() {
        return slots.isEmpty() ? inventory.getStackInSlot(slots.indexOf(0)).getDisplayName() : null;
    }

    @Getter
    public boolean isSlotStored(int slot){
        return slots.contains(slot);
    }

    /**
     * This checks to make sure the items are exactly the same
     * For example, a named diamond sword will not be grouped
     * with a regular diamond sword, and a damaged item will not
     * be grouped with a normal item. Additionally, it also
     * separates enchanted items from being grouped together.
     *
     * @param stack The item stack to check against
     * @return Returns true if the item is ok
     */
    @Getter
    public boolean compareStack(ItemStack stack){
        if (slots.isEmpty()) return false;
        else {
            ItemStack mainStack = inventory.getStackInSlot(slots.indexOf(0));
            return mainStack.getItem() == stack.getItem()
                    && mainStack.getItemDamage() == stack.getItemDamage()
                    && mainStack.getDisplayName().equals(stack.getDisplayName())
                    && mainStack.getMetadata() == stack.getMetadata()
                    && (mainStack.isItemEnchantable() && mainStack.getEnchantmentTagList() == stack.getEnchantmentTagList());
        }
    }

    @Getter
    public boolean isSlotsEmpty(){ return slots.isEmpty(); }

    @Getter
    @Nullable
    public Item getItem(){ return item; }

    @Getter
    @Nullable
    public ItemStack getItemStack(){
        if (slots.isEmpty()) return null;
        else if (inventory.getStackInSlot(slots.indexOf(0)).getItem() != item){
            slots.remove(0);
            return getItemStack();
        }
        else return inventory.getStackInSlot(slots.indexOf(0)); }

    @Setter
    public void addSlot(int slot) {
        slots.add(slot);
    }

    public void itemCheck() {
        for(int slot : slots)
            if (inventory.getStackInSlot(slots.indexOf(slot)) == null || compareStack(inventory.getStackInSlot(slots.indexOf(slot))))
                slots.remove(slot);
    }

}
