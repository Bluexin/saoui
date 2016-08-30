package com.saomc.saoui.elements;

import com.saomc.saoui.GLCore;
import com.saomc.saoui.SoundCore;
import com.saomc.saoui.api.events.ElementAction;
import com.saomc.saoui.api.screens.Actions;
import com.saomc.saoui.resources.StringNames;
import com.saomc.saoui.util.ColorUtil;
import com.saomc.saoui.util.LogCore;
import com.saomc.saoui.util.OptionCore;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Created by Tencao on 23/08/2016.
 */
public class ListCore{
    private boolean focused;
    private int scrollValue;
    private ParentElement parent;
    private int lastDragY, dragY;
    private boolean dragging;
    public final List<Element> elements;

    public ListCore(ParentElement gui){
        parent = gui;
        elements = new ArrayList<>();
    }

    public void update (Minecraft mc){
        for (int i = elements.size() - 1; i >= 0; i--) update(mc, i, elements.get(i));
    }

    public void update(Minecraft mc, int index, Element element) {
        if (!element.isMenu()) {
            final int elementY = element.getY(false);
            final int elementSize = element.getHeight();
            final int listY = elements.size() >= 9 ? elements.get(9).getY(false) : 0;
            final int listSize = elements.size();

            if (elementY < listY) element.setVisibility(Math.max(1.0F - (float) (listY - elementY) / listSize, 0.0F));
            else if (elementY + elementSize > listY + listSize)
                element.setVisibility(Math.max(1.0F - (float) ((elementY + elementSize) - (listY + listSize)) / listSize, 0.0F));
            else element.setVisibility(1);

            if (element.getVisibility() < 0.6F) element.setVisibility(0);
            else element.setVisibility(element.getVisibility() * element.getVisibility());
            //scroll(0);
        }


    }

    public void draw(Minecraft mc, int cursorX, int cursorY) {
        for (int i = elements.size() - 1; i >= 0; i--)
            if (elements.get(i).isEnabled()) {
                if (elements.get(i).isMenu()) {
                    drawMenu(mc, cursorX, cursorY, elements.get(i));
                } else drawSlot(mc, cursorX, cursorY, elements.get(i));
            }
    }

    private void drawMenu(Minecraft mc, int cursorX, int cursorY, Element element) {
        if (element.mouseOver(cursorX, cursorY)) element.mouseMoved(mc, cursorX, cursorY);

        if (element.getVisibility() > 0) {
            GLCore.glBindTexture(OptionCore.SAO_UI.getValue() ? StringNames.gui : StringNames.guiCustom);

            final int hoverState = element.hoverState(cursorX, cursorY);
            final int color0 = element.getColor(hoverState, true);
            final int color1 = element.getColor(hoverState, false);
            final int left = element.getX(false);
            final int top = element.getY(false);
            final int iconOffset = 2;

            GLCore.glBlend(true);
            GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color0, element.getVisibility()));
            GLCore.glTexturedRect(left, top, 0, 25, 20, 20);
            GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color1, element.getVisibility()));
            element.getIcon().glDraw(left + iconOffset, top + iconOffset);
            GLCore.glBlend(false);
        }

    }

    private void drawSlot(Minecraft mc, int cursorX, int cursorY, Element element) {
        if (element.mouseOver(cursorX, cursorY)) element.mouseMoved(mc, cursorX, cursorY);

        if (element.getVisibility() > 0) {
            GLCore.glBindTexture(StringNames.slot);

            final int hoverState = element.hoverState(cursorX, cursorY);
            final int color0 = element.getColor(hoverState, true);
            final int color1 = element.getColor(hoverState, false);
            final int left = element.getX(false);
            final int top = element.getY(false);
            final int iconOffset = (element.getHeight() - 16) / 2;
            final int captionOffset = (element.getHeight() - GLCore.glStringHeight()) / 2;

            GLCore.glBlend(true);
            GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color1, element.getVisibility()));
            if (hoverState == 3)
                GLCore.glTexturedRect(left, top, element.getWidth(), element.getHeight(), 0, 20, element.getWidth() - 15, element.getHeight());
            else
                GLCore.glTexturedRect(left, top, element.getWidth(), element.getHeight(), 0, 0, element.getWidth() - 15, element.getHeight());

            GLCore.glBindTexture(OptionCore.SAO_UI.getValue() ? StringNames.gui : StringNames.guiCustom);

            GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color1, element.getVisibility()));
            GLCore.glTexturedRect(left + iconOffset, top + iconOffset, 140, 25, 16, 16);

            GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color0, element.getVisibility()));
            element.getIcon().glDraw(left + iconOffset, top + iconOffset);


            GLCore.glString(element.getCaption(), left + iconOffset * 2 + 16 + 4, top + captionOffset, ColorUtil.multiplyAlpha(color1, element.getVisibility()));
            GLCore.glBlend(false);
        }
    }

    public List<Element> getElements() {
        return elements;
    }

    public Element getElement(int index) {
        return elements.get(index);
    }

    private int getOffset(int index) {
        return elements.stream().limit(index).mapToInt(Element::getHeight).sum();
    }

    protected int getSize() {
        return getOffset(elements.size());
    }

    public boolean keyTyped(Minecraft mc, char ch, int key) {
        //This will be used for typing into elements, if and when it's supported
        return false;
    }

    public boolean mouseOver(int cursorX, int cursorY, int flag) {
        if (!elements.stream().noneMatch(e -> e.mouseOver(cursorX, cursorY, flag))) {
            if (dragging) {
                dragY += scroll(cursorY - lastDragY);
                lastDragY = cursorY;
            }

            dragging = false;
            return false;
        } else return true;
    }


    public boolean mousePressed(Minecraft mc, int cursorX, int cursorY, int button) {
        for (int i = elements.size() - 1; i >= 0; i--) {
            if (i >= elements.size()) {
                if (elements.size() > 0) i = elements.size() - 1;
                else break;
            }

            if (elements.get(i).mouseOver(cursorX, cursorY) && elements.get(i).mousePressed(mc, cursorX, cursorY, button))
                    return true;

        }

        return false;
    }

    public boolean mouseReleased(Minecraft mc, int cursorX, int cursorY, int button) {
        for (int i = elements.size() - 1; i >= 0; i--) {
            if (i >= elements.size()) {
                if (elements.size() > 0) i = elements.size() - 1;
                else break;
            }

            if (elements.get(i).mouseOver(cursorX, cursorY) && elements.get(i).mousePressed(mc, cursorX, cursorY, button))
                return true;
        }

        return false;
    }

    public boolean mouseWheel(Minecraft mc, int cursorX, int cursorY, int delta) {
        for (int i = elements.size() - 1; i >= 0; i--) {
            if (i >= elements.size()) {
                if (elements.size() > 0) i = elements.size() - 1;
                else break;
            }

            elements.stream()
                    .filter(el -> el.mouseOver(cursorX, cursorY) && el.mouseWheel(mc, cursorX, cursorY, delta))
                    .forEach(el -> actionPerformed(el, Actions.MOUSE_WHEEL, delta));

        }

        return false;
    }

    public void actionPerformed(Element element, Actions action, int data) {
        MinecraftForge.EVENT_BUS.post(new ElementAction(element.getCaption(), element.getCategory(), action, data, element.getGui(), element.isFocus()));
        element.setFocus(!element.isFocus());
        SoundCore.play(Minecraft.getMinecraft().getSoundHandler(), SoundCore.DIALOG_CLOSE);
    }

    public void close(Minecraft mc){
        elements.clear();
    }

    private int scroll(int delta) {
        final int value = scrollValue;
        if (elements.size() <= 9) scrollValue = Math.min(Math.max(scrollValue - delta, 0), getOffset(elements.size()) - getSize());
        else {
            scrollValue -= delta;
            scrollValue %= getOffset(elements.size());
        }
        return Math.abs(value - scrollValue);
    }
}
