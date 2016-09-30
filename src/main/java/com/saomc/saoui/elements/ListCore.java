package com.saomc.saoui.elements;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.saomc.saoui.GLCore;
import com.saomc.saoui.SoundCore;
import com.saomc.saoui.api.events.ElementAction;
import com.saomc.saoui.api.screens.Actions;
import com.saomc.saoui.api.screens.ElementType;
import com.saomc.saoui.resources.StringNames;
import com.saomc.saoui.screens.inventory.InventoryCore;
import com.saomc.saoui.util.ColorUtil;
import com.saomc.saoui.util.LogCore;
import com.saomc.saoui.util.OptionCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Created by Tencao on 23/08/2016.
 */
public class ListCore{
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private float scrolledValue;
    private int scrollValue;
    private ParentElement parent;
    public ColorUtil bgColor, disabledMask;
    public Container container;
    private int lastDragY, dragY;
    //private boolean dragging;
    public final List<Element> elements;
    public List<Element> items;
    private Element parentElement;
    private boolean isVisible;
    private boolean isFocus;
    private int width;
    private int height;
    private int top;
    private int x;
    private int y;
    private double scrollTextX;
    private String lastScrollElementCache;

    private TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
    private ModelManager modelManager = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager();
    private ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
    private RenderItem itemRender = new RenderItem(textureManager, modelManager, itemColors);

    public ListCore(ParentElement gui){
        this.scrollValue = 0;
        this.parent = gui;
        this.elements = new ArrayList<>();
        this.items = new ArrayList<>();
        //this.dragging = false;
        bgColor = ColorUtil.DEFAULT_COLOR;
        disabledMask = ColorUtil.DISABLED_MASK;
    }

    public void update (Minecraft mc){
        //This part is the initial setting up of the list.
        if (parentElement == null && !elements.isEmpty() && elements.get(0).getParent() != null && !elements.get(0).getParent().equals("none"))
            parentElement = ElementBuilder.getInstance().getElementParent(elements.get(0).getParent(), elements.get(0).getGui());
        if (!elements.isEmpty()) isVisible = elements.stream().anyMatch(Element::isEnabled);
        if (parentElement != null) {
            x = parentElement.getX(true) + parentElement.getWidth() + 14;
            y = parentElement.getY(true) + parentElement.getHeight() / 2;
        }

        if (width <= 0) width = elements.stream().mapToInt(Element::getWidth).max().orElse(width);

        //This initiates a dynamic width for slots
        if (!elements.isEmpty() && elements.stream().noneMatch(e -> e.getElementType() == ElementType.MENU || e.getElementType() == ElementType.INVENTORY)) {
            int textSize = elements.stream().filter(e -> e.getCaption() != null).mapToInt(e -> e.getCaption().length()).max().orElse(13);
            if (textSize > 13) {
                if (textSize >= 50) textSize = 50;
                if (width !=  100 + textSize + 5) {
                    final int newWidth = 100 + textSize + 5;
                    elements.forEach(e -> e.setWidth(newWidth));
                }
            }
        }

        //This initiates a dynamic width for items
        if (!items.isEmpty()) {
            int textSize = items.stream().mapToInt(Element::getWidth).max().orElse(13);
            if (textSize > 13) {
                if (textSize >= 50) textSize = 50;
                final int newWidth = 100 + textSize;
                items.forEach(e -> e.setWidth(newWidth));
            }
        }

        //This first checks to make sure the parent is open before starting to add elements, to prevent cpu waste
        if (elements.stream().anyMatch(e -> e.getElementType() == ElementType.INVENTORY) && parentElement != null && parentElement.isOpen()) {
            //This part handles adding new items from the inventory to slots
            elements.stream().filter(e -> e.getElementType() == ElementType.INVENTORY).forEach(e -> {
                for (int i = 0; i < e.getInventory().getSizeInventory(); i++) {
                    final int slot = i;
                    //Checks to make sure the slot isn't already stored before trying to add it
                    if (items.stream().noneMatch(items -> items.isSlotStored(slot))) {
                        ItemStack item = e.getInventory().getStackInSlot(i);
                        //checks to see if the item is valid for storing
                        if (item != null && e.getItemFilter().isFine(item, false)) {
                            //If the item perfectly matches an existing element, add it
                            if (items.stream().anyMatch(items -> items.getItem() == item.getItem() && items.compareStack(item))) {
                                items.stream().filter(items -> items.getItem() == item.getItem() && items.compareStack(item)).forEach(items -> items.addSlot(slot));
                            } else // Otherwise create a new element
                                items.add(new Element(e.getParent(), e.getGui(), item.getItem(), i, e.getInventory()));
                        }
                    }
                }
            });
        }

        //Issues an item check to make sure all items are still correctly listed and the item in slot hasn't changed
        if (!items.isEmpty()) items.forEach(Element::itemCheck);

        isFocus = elements.get(0).isFocus();

        height = getListSize();
        //
        //This part issues an update call for each element
        for (int i = elements.size() - 1; i >= 0; i--) update(mc, i, elements.get(i));
    }

    public void update(Minecraft mc, int index, Element element) {
        //Sets up the X and Y for the elements
        if (element.isEnabled())
            if (element.getElementType() != ElementType.MENU) {
                if (!ElementBuilder.getInstance().isParentEnabled(element.getParent()))
                    element.setEnabled(false);
                element.setY(getOffset(index));
                element.setX(getX(true));
            } else {
                element.setX(getX(true));
                element.setY(getY(true) + 24 * elements.indexOf(element));
            }
        //This part will auto disable all elements if it's parent is disabled (Not open)
        if (!element.isEnabled()){
            element.setOpen(false);
            element.setHighlight(false);
        }

        //This sets the visibility for elements above or below the normal 5 shown. Will only trigger if the list is bigger than 5

        if (element.getElementType() != ElementType.MENU && element.isEnabled() && elements.size() >= 5) {
            final int elementY = element.getY(false);
            final int elementSize = element.getHeight();

            final int listY = getListY(false);
            final int listSize = getListSize();

            if (elementY < listY) element.setVisibility(Math.max(1.0F - (float) (listY - elementY) / listSize, 0.0F));
            else if (elementY + elementSize > listY + listSize)
                element.setVisibility(Math.max(1.0F - (float) ((elementY + elementSize) - (listY + listSize)) / listSize, 0.0F));
            else element.setVisibility(1);

            if (element.getVisibility() < 0.6F) element.setVisibility(0);
            else element.setVisibility(element.getVisibility() * element.getVisibility());
            scroll(0);
        }
    }

    public void draw(Minecraft mc, int cursorX, int cursorY) {
        //Sets the current scroll value to be used in the scroll part of the code
        scrolledValue = (scrolledValue + scrollValue) / 2;
        //This will draw a list if the parent element exists (basically for all sub menus excluding the main buttons)
        if (parentElement != null && isVisible) drawList();

        //RenderHelper.enableGUIStandardItemLighting();
        for (int i = elements.size() - 1; i >= 0; i--)
            if (elements.get(i).isEnabled()) {
                if (elements.get(i).getElementType() == ElementType.MENU)
                    drawMenu(mc, cursorX, cursorY, elements.get(i));
                 else if (elements.get(i).getElementType() == ElementType.SLOT || elements.get(i).getElementType() == ElementType.OPTION)
                    drawSlot(mc, cursorX, cursorY, elements.get(i));
                else if (elements.get(i).getElementType() == ElementType.INVENTORY)
                    drawItem(mc, cursorX, cursorY, elements.get(i));
            }
        //RenderHelper.disableStandardItemLighting();
    }

    /**
     * This is used to draw the line and arrow for sub menus
     */
    private void drawList() {
        if (isVisible && parent != null && height > 0) {
            if (x > 0) {
                GLCore.glBlend(true);
                GLCore.glBindTexture(StringNames.slot);
                GLCore.glBindTexture(OptionCore.SAO_UI.isEnabled() ? StringNames.gui : StringNames.guiCustom);
                GLCore.glColorRGBA(ColorUtil.DEFAULT_COLOR.multiplyAlpha(1.0F));

                final int left = getX(false);
                final int top = getListY(false) + 1;

                final int arrowTop = getY(false) - height / 2;
                //final int yOffset = elements.size() > 5 ? 5 : elements.size();
                boolean fullArrow = false;

                GLCore.glTexturedRect(left - 2, top, 2, height - 1, 40, 41, 2, 4);
                GLCore.glTexturedRect(left - 10, arrowTop + (height - 10) / 2, 20, 25, 10, 10);
                GLCore.glBlend(false);
            } else if (x < 0) {
                GLCore.glBlend(true);
                GLCore.glBindTexture(StringNames.slot);
                GLCore.glBindTexture(OptionCore.SAO_UI.isEnabled() ? StringNames.gui : StringNames.guiCustom);
                GLCore.glColorRGBA(ColorUtil.DEFAULT_COLOR.multiplyAlpha(1.0F));

                final int left = getX(false);
                final int top = getListY(false) + 1;

                final int arrowTop = getY(false) - height / 2;

                GLCore.glTexturedRect(left + width, top, 2, height - 1, 40, 41, 2, 4);
                GLCore.glTexturedRect(left + width, arrowTop + (height - 10) / 2, 30, 25, 10, 10);
                /*
                if (elements.size() > 5) {
                    GLCore.glTexturedRect(left + width, top, 2, height - 1, 40, 41, 2, 4);
                    GLCore.glTexturedRect(left + width, arrowTop + (height - 10) / 2, 30, 25, 10, 10);
                }
                else {
                    GLCore.glTexturedRect(left + width, 105, 2, height - 1, 40, 41, 2, 4);
                    GLCore.glTexturedRect(left + width, arrowTop + (height - 10) / 2, 30, 25, 10, 10);
                }*/
                GLCore.glBlend(false);
            }
        }
    }

    /**
     * This is used to draw the icon buttons
     */
    private void drawMenu(Minecraft mc, int cursorX, int cursorY, Element element) {
        if (element.mouseOver(cursorX, cursorY, -1)) mouseMoved(mc, cursorX, cursorY);

        if (element.getVisibility() > 0) {

            final int hoverState = element.hoverState(cursorX, cursorY);
            final int color0 = getColor(hoverState, true);
            final int color1 = getColor(hoverState, false);
            final int left = element.getX(false);
            final int top = element.getY(false);
            final int iconOffset = 2;

            GLCore.glBlend(true);
            GLCore.glBindTexture(OptionCore.SAO_UI.isEnabled() ? StringNames.gui : StringNames.guiCustom);
            GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color0, element.getVisibility()));
            GLCore.glTexturedRect(left, top, 0, 25, 20, 20);
            GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color1, element.getVisibility()));
            element.getIcon().glDraw(left + iconOffset, top + iconOffset);
            GLCore.glBlend(false);
            GLCore.glAlphaTest(true);
        }

    }

    /**
     * This will draw the element buttons
     */
    private void drawSlot(Minecraft mc, int cursorX, int cursorY, Element element) {
        if (element.mouseOver(cursorX, cursorY, -1)) mouseMoved(mc, cursorX, cursorY);

        if (element.getVisibility() > 0) {
            final int hoverState = element.hoverState(cursorX, cursorY);
            final int color0 = getColor(hoverState, true);
            final int color1 = getColor(hoverState, false);

            final int left = element.getX(false);
            final int top = element.getY(false);

            final int iconOffset = (element.getHeight() - 16) / 2;
            final int captionOffset = (element.getHeight() - 8) / 2;

            GLCore.glBlend(true);
            GLCore.glBindTexture(StringNames.slot);
            if (hoverState == 2) {
                GLCore.glColor(1.0F, 1.0F, 1.0F);
                GLCore.glTexturedRect(left, top, element.getWidth(), element.getHeight(), 0, 21, 100 - 16, 20 -2);
                if (element.getCategory().equals("logout") && element.getParent().equals("settings"))
                    renderHighlightText(OptionCore.LOGOUT.isEnabled() ? element.getCaption() : " ", left + iconOffset * 2 + 16 + 4, top + captionOffset, ColorUtil.multiplyAlpha(color1, element.getVisibility()));
                else renderHighlightText(element.getCaption(), left + iconOffset * 2 + 16 + 4, top + captionOffset, ColorUtil.multiplyAlpha(color1, element.getVisibility()));
            } else {
                GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color0, element.getVisibility()));
                GLCore.glTexturedRect(left, top, element.getWidth(), element.getHeight(), 0, 1, 100 - 16, 20 - 2);
                if (element.getCategory().equals("logout") && element.getParent().equals("settings"))
                    GLCore.glString(OptionCore.LOGOUT.isEnabled() ? element.getCaption() : " ", left + iconOffset * 2 + 16 + 4, top + captionOffset, ColorUtil.multiplyAlpha(color0, element.getVisibility()),  OptionCore.TEXT_SHADOW.isEnabled());
                else GLCore.glString(element.getCaption().length() < 50 ? element.getCaption() : element.getCaption().substring(0, 50), left + iconOffset * 2 + 16 + 4, top + captionOffset, ColorUtil.multiplyAlpha(color0, element.getVisibility()),  OptionCore.TEXT_SHADOW.isEnabled());
            }
            GLCore.glBindTexture(OptionCore.SAO_UI.isEnabled() ? StringNames.gui : StringNames.guiCustom);

            GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color1, element.getVisibility()));
            GLCore.glTexturedRect(left + iconOffset, top + iconOffset, 140, 25, 16, 16);

            GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color0, element.getVisibility()));
            element.getIcon().glDraw(left + iconOffset, top + iconOffset);
            GLCore.glBlend(false);
            GLCore.glAlphaTest(true);

            //GLCore.glString(element.getCaption(), left + iconOffset * 2 + 16 + 4, top + captionOffset, ColorUtil.multiplyAlpha(color0, element.getVisibility()));
        }
    }

    private void drawItem(Minecraft mc, int cursorX, int cursorY, Element element){
        if (element.mouseOver(cursorX, cursorY, -1)) mouseMoved(mc, cursorX, cursorY);
        mc.thePlayer.openContainer = (Container)element.getInventory();

        if (element.getVisibility() > 0){
            final int hoverState = element.hoverState(cursorX, cursorY);
            final int color0 = getColor(hoverState, true);
            final int color1 = getColor(hoverState, false);
            final int left = element.getX(false);
            final int top = element.getY(false);

            final int iconOffset = (element.getHeight() - 16) / 2;
            final int captionOffset = (element.getHeight() - 8) / 2;

            final int stackSize = element.getTotalStackSize();

            GLCore.glBlend(true);
            GLCore.glBindTexture(StringNames.slot);
            GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color0, element.getVisibility()));
            if (hoverState == 2) {
                GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color1, element.getVisibility()));
                GLCore.glTexturedRect(left, top, element.getWidth(), element.getHeight(), 0, 21, 10 - 16, 20 -2);
            } else {
                GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color0, element.getVisibility()));
                GLCore.glTexturedRect(left, top, element.getWidth(), element.getHeight(), 0, 1, 100 - 16, 20 - 2);
            }
            GLCore.glBindTexture(OptionCore.SAO_UI.isEnabled() ? StringNames.gui : StringNames.guiCustom);

            GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color1, element.getVisibility()));
            GLCore.glTexturedRect(left + iconOffset, top + iconOffset, 140, 25, 16, 16);

            if (stackSize > 0){
                GLCore.glString("x" + stackSize, left + iconOffset * 2 + 16 + 4, top + captionOffset, ColorUtil.multiplyAlpha(color1, element.getVisibility()),  OptionCore.TEXT_SHADOW.isEnabled());

                RenderHelper.enableGUIStandardItemLighting();
                itemRender.renderItemIntoGUI(element.getItemStack(), x, y);
                RenderHelper.disableStandardItemLighting();

                if (element.getItemStack().isItemEnchanted()) renderEffectSlot(mc, x-  1, y - 1, element);
                else {
                    GLCore.glBlend(true);
                    GLCore.glAlphaTest(true);
                }
            }
            GLCore.glBlend(false);

        }

    }

    private void renderEffectSlot(Minecraft mc, int x, int y, Element element){
        GLCore.glDepthFunc(GL11.GL_EQUAL);
        GLCore.depthMask(false);
        mc.getTextureManager().bindTexture(RES_ITEM_GLINT);
        GLCore.glAlphaTest(true);
        GLCore.glBlend(true);
        GLCore.glColor(0.5F, 0.25F, 0.8F, 1.0F);
        this.renderGlintSlot(x, y, element.getWidth(), element.getHeight());
        GLCore.tryBlendFuncSeparate(770, 771, 1, 0);
        GLCore.depthMask(true);
        GLCore.glDepthFunc(GL11.GL_LEQUAL);
    }

    private void renderGlintSlot(int x, int y, int width, int height){
        for (int j1 = 0; j1 < 2; ++j1)
        {
            GLCore.tryBlendFuncSeparate(772, 1, 0, 0);
            float f = 0.00390625F;
            float f1 = 0.00390625F;
            float f2 = (float)(Minecraft.getSystemTime() % (long)(3000 + j1 * 1873)) / (3000.0F + (float)(j1 * 1873)) * 256.0F;
            float f3 = 0.0F;
            float f4 = 4.0F;

            if (j1 == 1)
            {
                f4 = -1.0F;
            }

            GLCore.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            GLCore.addVertex((double)(x), (double)(y + height), (double)itemRender.zLevel, (double)((f2 + (float)height * f4) * f), (double)((f3 + (float)height) * f1));
            GLCore.addVertex((double)(x + width), (double)(y + height), (double)itemRender.zLevel, (double)((f2 + (float)width + (float)height * f4) * f), (double)((f3 + (float)height) * f1));
            GLCore.addVertex((double)(x + width), (double)(y), (double)itemRender.zLevel, (double)((f2 + (float)width) * f), (double)((f3 + 0.0F) * f1));
            GLCore.addVertex((double)(x), (double)(y), (double)itemRender.zLevel, (double)((f2 + 0.0F) * f), (double)((f3 + 0.0F) * f1));
            GLCore.draw();
        }
    }

    /**
     * This is a special render check for highlighted elements
     * This will check if the name is longer than the element and
     * if so, will initiate scrolling. Additionally, this is also
     * used to check whether the element being highlighted has
     * changed in order to properly reset the scrolling
     *
     * @param string Display name
     * @param x X coord to render
     * @param y Y coord to render
     * @param argb Color code
     */
    private void renderHighlightText(String string, int x, int y, int argb){
        if (lastScrollElementCache != null && !lastScrollElementCache.equals(string)){
            lastScrollElementCache = string;
            scrollTextX = 0;
        }
        if (string.length() >= 50) {
            String name;

            if (string.length() > scrollTextX) name = string.substring((int) scrollTextX);
            else {
                scrollTextX = 0;
                name = string;
            }

            GLCore.glString(name, x, y, argb, OptionCore.TEXT_SHADOW.isEnabled());

            scrollTextX += 0.01;
        } else GLCore.glString(string, x, y, argb, OptionCore.TEXT_SHADOW.isEnabled());
    }

    /**
     * Gets the element lists specific to this ListCore
     * @return Returns the group of elements
     */
    public List<Element> getElements() {
        return elements;
    }

    /**
     * Get a specific element within the list
     * @param index The index position in the list to pull from
     * @return Returns the element
     */
    public Element getElement(int index) {
        return elements.get(index);
    }

    protected int getElementOffset(int index){
        return elements.stream().limit(index).mapToInt(e -> ElementOffset(e, true)).sum();
    }

    int ElementOffset(Element element, boolean normal){
        int height = element.getHeight();
        return normal ? height + 1 : height - 1;
    }

    private int getReverseElementOffset(int index) {
        return elements.stream().skip(index).mapToInt(e -> ElementOffset(e, false)).sum();
    }

    protected int getListSize() {
        return Math.max(Math.min(60, getElementOffset(elements.size())), 0);
    }

    protected int getOffset(int index) {
        int a = Math.round(getElementOffset(index) - scrolledValue);

        if (elements.size() > 9) {
            if (getElementOffset(0) - scrolledValue > -getElementOffset(2)) { // elements can be added above
                if (a >= getElementOffset(8)) {
                    a = Math.round(getElementOffset(0) - getReverseElementOffset(index) - scrolledValue);
                    if (a > getElementOffset(8) - scrolledValue) a = Math.round(getElementOffset(0) - getReverseElementOffset(index) - getElementOffset(elements.size()) - scrolledValue);
                }
            } else if (getElementOffset(elements.size()) - scrolledValue < getElementOffset(6) && index < elements.size() - 8) { // elements can be added below
                a = Math.round(getElementOffset(elements.size()) + getElementOffset(index) - scrolledValue);
            }
        }
        return a - 20;
    }

    public boolean keyTyped(Minecraft mc, char ch, int key) {
        if (!elements.isEmpty() && isFocus){
            keyAction(mc, ch, key);
            return true;
        }
        else return false;
    }

    private void keyAction(Minecraft mc, char ch, int key){
        //This is an easy way to check if any element is already highlighted or not, saves repeated searching
        //TODO - Add scrolling to key pressed
        boolean listHighlight = elements.stream().anyMatch(Element::isHighlight);

        //Down key pressed
        if (key == 208) {
            if (listHighlight) {
                for (int i = elements.size() - 1; i >= 0; i--) {
                    if (elements.get(i).isHighlight()) {
                        elements.get(i).setHighlight(false);
                        if (elements.size() - 1 >= i + 1)
                            elements.get(i + 1).setHighlight(true);
                        else elements.get(0).setHighlight(true);
                        break;
                    }
                }
            } else elements.get(0).setHighlight(true);

        }

        //Up key pressed
        if (key == 200) {
            if (listHighlight) {
                for (int i = elements.size() - 1; i >= 0; i--) {
                    if (elements.get(i).isHighlight()) {
                        elements.get(i).setHighlight(false);
                        if (i == 0)
                            elements.get(elements.size() - 1).setHighlight(true);
                        else elements.get(i - 1).setHighlight(true);
                        break;
                    }
                }
            } else elements.get(elements.size() - 1).setHighlight(true);
        }

        //Right/Enter key pressed
        if (key == 205 || key == 28) {
            if (listHighlight) {
                for (int i = elements.size() - 1; i >= 0; i--) {
                    if (elements.get(i).isHighlight()) {
                        actionPerformed(elements.get(i), Actions.LEFT_RELEASED, 0);
                        break;
                    }
                }
            } else elements.get(0).setHighlight(true);
        }

        //Left/Backspace key pressed
        if (key == 203 || key == 14) {
            if (parentElement != null)
                actionPerformed(parentElement, Actions.LEFT_RELEASED, 0);
        }
    }

    /**
     * This is used to add list dragging
     *
     * @param mc The minecraft instance
     * @param cursorX The mouse cursors X position
     * @param cursorY The mouse cursors Y position
     */
    public void mouseMoved(Minecraft mc, int cursorX, int cursorY) {/*
        if (dragging && isFocus) {
            dragY += scroll(cursorY - lastDragY);
            lastDragY = cursorY;
        }*/
    }

    /**
     * This checks the mouse clicked action
     * Used to control element clicking
     *
     * @param mc The minecraft instance
     * @param cursorX The mouse cursors X position
     * @param cursorY The mouse cursors Y position
     * @param button The button ID
     * @return Return true if the mouse pressed action was fired
     */
    public boolean mousePressed(Minecraft mc, int cursorX, int cursorY, int button) {
        if (mc.currentScreen == null) return false;
        if (button == 0 && isFocus) {
            dragY = 0;
            lastDragY = cursorY;
        }

        for (int i = elements.size() - 1; i >= 0; i--) {
            if (i >= elements.size()) {
                if (elements.size() > 0) i = elements.size() - 1;
                else break;
            }

            if (elements.get(i).mousePressed(mc, cursorX, cursorY, button)) {
                actionPerformed(elements.get(i), Actions.LEFT_PRESSED, button);
                LogCore.logDebug("mousePressed for " + elements.get(i).getCaption());
                //dragging = true;
                return true;
            }

        }

        return false;
    }

    /**
     * This checks the mouse released action
     * Used to control element clicking
     *
     * @param mc The minecraft instance
     * @param cursorX The mouse cursors X position
     * @param cursorY The mouse cursors Y position
     * @param button The button ID
     * @return Return true if the mouse released action was fired
     */
    public boolean mouseReleased(Minecraft mc, int cursorX, int cursorY, int button) {
        if (mc.currentScreen == null) return false;

        boolean wasDragging = false;

        /*
        if (button == 0) {
            if (dragging && isFocus) {
                dragY += scroll(cursorY - lastDragY);
                wasDragging = (dragY > 0);
                lastDragY = cursorY;
            }

            dragging = false;
        }*/
        return (!wasDragging) && (mouseElementReleased(mc, cursorX, cursorY, button));
    }

    /**
     * This checks the element specific
     * part of the mouse released check
     *
     * @param mc The minecraft instance
     * @param cursorX The mouse cursors X position
     * @param cursorY The mouse cursors Y position
     * @param button The button ID
     * @return Return true if the mouse released action was fired
     */
    private boolean mouseElementReleased(Minecraft mc, int cursorX, int cursorY, int button) {
        for (int i = elements.size() - 1; i >= 0; i--) {
            if (i >= elements.size()) {
                if (elements.size() > 0) i = elements.size() - 1;
                else break;
            }

            if (elements.get(i).mouseReleased(mc, cursorX, cursorY, button)) {
                return true;
            }
        }

        return false;
    }

    /**
     * This is the mouse wheel action for the list
     * Used to add or control scrolling
     *
     * @param mc The minecraft instance
     * @param cursorX The mouse cursors X position
     * @param cursorY The mouse cursors Y position
     * @param delta The mouse movement speed
     * @return Return true if the mouse wheel action was fired
     */
    public boolean mouseWheel(Minecraft mc, int cursorX, int cursorY, int delta) {
        if (elements.size() > 5 && isFocus) scroll(Math.abs(delta * 2 * getListSize() / elements.size()) / delta);
        return mouseElementWheel(mc, cursorX, cursorY, delta);
    }

    /**
     * This fires the element specific part of the
     * mouse wheel check
     *
     * @param mc The minecraft instance
     * @param cursorX The mouse cursors X position
     * @param cursorY The mouse cursors Y position
     * @param delta The mouse movement speed
     * @return Return true if the mouse wheel action was fired
     */
    private boolean mouseElementWheel(Minecraft mc, int cursorX, int cursorY, int delta) {
        for (int i = elements.size() - 1; i >= 0; i--) {
            if (i >= elements.size()) {
                if (elements.size() > 0) i = elements.size() - 1;
                else break;
            }

            if (elements.get(i).mouseWheel(mc, cursorX, cursorY)) {
                actionPerformed(elements.get(i), Actions.MOUSE_WHEEL, delta);
                return true;
            }
        }
        return false;
    }

    public boolean mouseOver(int cursorX, int cursorY, int flag){
        if (isFocus && elements.stream().anyMatch(e -> e.mouseOver(cursorX, cursorY, flag))){
            /*
            if (dragging) {
                dragY += scroll(cursorY - lastDragY);
                lastDragY = cursorY;
            }

            dragging = false;*/
            return true;
        } else return false;
    }

    /**
     * This fires the action event for the element
     *
     * @param element The element sending the event
     * @param action The action that triggered the event
     * @param data Button ID
     */
    public static void actionPerformed(Element element, Actions action, int data) {
        MinecraftForge.EVENT_BUS.post(new ElementAction(element.getCaption(), element.getCategory(), element.getParent(), action, data, element.getGui(), element.isOpen(), !element.isFocus(), element.getElementType()));
    }

    /**
     * This is used to cleanly close the Lists
     * Should only really be used when leaving
     * the server or single player world
     */
    public void close(){
        elements.clear();
    }

    /**
     * This retrieves the scroll value when scrolling
     * @param delta
     * @return Returns scroll value
     */
    private int scroll(int delta) {
        final int value = scrollValue;
        if (elements.size() <= 9) scrollValue = Math.min(Math.max(scrollValue - delta, 0), getElementOffset(elements.size()) - getListSize());
        else {
            scrollValue -= delta;
            scrollValue %= getElementOffset(elements.size());
        }
        return Math.abs(value - scrollValue);
    }

    /**
     * This gets the Lists Y coord relative to the screen
     * @param relative True for the actual Y, False for Y relative to the parent GUI
     * @return Returns the Y value
     */
    public int getListY(boolean relative) {
        int value =  relative ? y : y + (parent != null ? parent.getY(relative) : 0);
        return value - (relative ? 0 : height / 2);
    }

    /**
     * This gets the Y coord relative to the screen
     * @param relative True for the actual Y, False for Y relative to the parent GUI
     * @return Returns the Y value
     */
    public int getY(boolean relative) {
        return relative ? y : y + (parent != null ? parent.getY(relative) : 0);
    }

    /**
     * This gets the X coord relative to the screen
     * @param relative True for the actual X, False for X relative to the parent GUI
     * @return Returns the X value
     */
    public int getX(boolean relative) {
        return relative ? x : x + (parent != null ? parent.getX(relative) : 0);
    }


    protected int getColor(int hoverState, boolean bg) {
        return bg ? hoverState == 1 ? bgColor.rgba : hoverState == 2 ? ColorUtil.HOVER_COLOR.rgba : bgColor.rgba & disabledMask.rgba : hoverState == 1 ? ColorUtil.DEFAULT_FONT_COLOR.rgba : hoverState == 2 ? ColorUtil.HOVER_FONT_COLOR.rgba : ColorUtil.DEFAULT_FONT_COLOR.rgba & disabledMask.rgba;
    }

}
