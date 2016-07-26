package com.saomc.screens;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ListGUI extends MenuSlotGUI {

    private float scrolledValue;
    private int scrollValue;
    private int size;
    private int minSize;

    private int lastDragY, dragY;
    private boolean dragging;

    public ListGUI(ParentElement gui, int xPos, int yPos, int h, int minH) {
        super(gui, xPos, yPos);
        fullArrow = false;
        scrollValue = 0;
        size = h;
        minSize = minH;
    }

    public ListGUI(ParentElement gui, int xPos, int yPos) {
        this(gui, xPos, yPos, 0, 0);
    }

    @Override
    protected int getOffset(int index) {
        int a = Math.round(super.getOffset(index) - scrolledValue);

        if (elements.size() > 9) {
            if (super.getOffset(0) - scrolledValue > -super.getOffset(2)) { // elements can be added above
                if (a >= super.getOffset(8)) {
                    a = Math.round(super.getOffset(0) - super.getReverseOffset(index) - scrolledValue);
                    if (a > super.getOffset(8) - scrolledValue)
                        a = Math.round(super.getOffset(0) - super.getReverseOffset(index) - super.getOffset(elements.size()) - scrolledValue);
                }
            } else if (super.getOffset(elements.size()) - scrolledValue < super.getOffset(6) && index < elements.size() - 8) { // elements can be added below
                a = Math.round(super.getOffset(elements.size()) + super.getOffset(index) - scrolledValue);
            }
        }
        return a;
    }

    @Override
    protected int getSize() {
        return Math.max(Math.min(size, super.getOffset(elements.size())), minSize);
    }

    @Override
    protected void update(Minecraft mc, int index, Elements element) {
        super.update(mc, index, element);

        final int elementY = element.getY(false);
        final int elementSize = element.height;

        final int listY = getY(false);
        final int listSize = getSize();

        if (elementY < listY) element.visibility = Math.max(1.0F - (float) (listY - elementY) / listSize, 0.0F);
        else if (elementY + elementSize > listY + listSize)
            element.visibility = Math.max(1.0F - (float) ((elementY + elementSize) - (listY + listSize)) / listSize, 0.0F);
        else element.visibility = 1;

        if (element.visibility < 0.6F) element.visibility = 0;
        else element.visibility *= element.visibility;
        scroll(0);
    }

    @Override
    public void draw(Minecraft mc, int cursorX, int cursorY) {
        scrolledValue = (scrolledValue + scrollValue) / 2;

        super.draw(mc, cursorX, cursorY);
    }

    @Override
    public boolean mouseOver(int cursorX, int cursorY, int flag) {
        if (!super.mouseOver(cursorX, cursorY, flag)) {
            if (dragging) {
                dragY += scroll(cursorY - lastDragY);
                lastDragY = cursorY;
            }

            dragging = false;
            return false;
        } else return true;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int cursorX, int cursorY, int button) {
        if (button == 0) {
            dragY = 0;
            lastDragY = cursorY;
            dragging = true;
        }

        return super.mousePressed(mc, cursorX, cursorY, button);
    }

    @Override
    public void mouseMoved(Minecraft mc, int cursorX, int cursorY) {
        if (dragging) {
            dragY += scroll(cursorY - lastDragY);
            lastDragY = cursorY;
        }
    }

    @Override
    public boolean mouseReleased(Minecraft mc, int cursorX, int cursorY, int button) {
        boolean wasDragging = false;

        if (button == 0) {
            if (dragging) {
                dragY += scroll(cursorY - lastDragY);
                wasDragging = (dragY > 0);
                lastDragY = cursorY;
            }

            dragging = false;
        }

        return (!wasDragging) && (super.mouseReleased(mc, cursorX, cursorY, button));
    }

    @Override
    public boolean mouseWheel(Minecraft mc, int cursorX, int cursorY, int delta) {
        if (elements.size() > 0) {
            if (elements.size() <= 9) scroll(Math.abs(delta * 2 * getSize() / elements.size()) / delta);
            else scroll(Math.abs(delta * 2 * getSize() / 5) / delta);
        }

        return super.mouseWheel(mc, cursorX, cursorY, delta);
    }

    private int scroll(int delta) {
        final int value = scrollValue;
        if (elements.size() <= 9)
            scrollValue = Math.min(Math.max(scrollValue - delta, 0), super.getOffset(elements.size()) - getSize());
        else {
            scrollValue -= delta;
            scrollValue %= super.getOffset(elements.size()) - getSize();
        }
        return Math.abs(value - scrollValue);
    }

}
