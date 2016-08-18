package com.saomc.elements;

import com.saomc.GLCore;
import com.saomc.SoundCore;
import com.saomc.resources.StringNames;
import com.saomc.util.ColorUtil;
import com.saomc.util.IconCore;
import com.saomc.util.OptionCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;

/**
 * The main render class for slots and menus
 *
 * Created by Tencao on 30/07/2016.
 */
public class MenuCore implements ParentElement {

    public final ParentElement parent;
    private ColorUtil bgColor, disabledMask;
    private Element element;
    private boolean removed;

    public MenuCore(ParentElement gui, Element element) {
        parent = gui;
        this.element = element;

        removed = false;
        bgColor = ColorUtil.DEFAULT_COLOR;
        disabledMask = ColorUtil.DISABLED_MASK;
    }

    public void update(Minecraft mc) {
    }

    public void draw(Minecraft mc, int cursorX, int cursorY) {
        if (element.isEnabled()) {
            if (mouseOver(cursorX, cursorY)) mouseMoved(mc, cursorX, cursorY);
            if (element.isMenu()) drawMenu(cursorX, cursorY);
            else drawSlot(cursorX, cursorY);
        }
    }

    private void drawMenu(int cursorX, int cursorY) {
        if (element.getVisibility() > 0) {
            GLCore.glBindTexture(OptionCore.SAO_UI.getValue() ? StringNames.gui : StringNames.guiCustom);

            final boolean hoverState = hoverState(cursorX, cursorY);
            final int color0 = getColor(hoverState, true);
            final int color1 = getColor(hoverState, false);
            final int left = getX(false);
            final int top = getY(false);
            final int iconOffset = 2;

            GLCore.glBlend(true);
            GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color0, element.getVisibility()));
            GLCore.glTexturedRect(left, top, 0, 25, 20, 20);
            GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color1, element.getVisibility()));
            element.getIcon().glDraw(left + iconOffset, top + iconOffset);
            GLCore.glBlend(false);
        }
    }

    private void drawSlot(int cursorX, int cursorY) {
        if (element.getVisibility() > 0 && parent != null && element.getHeight() > 0) {
            GLCore.glBindTexture(StringNames.slot);
            GLCore.glColorRGBA(ColorUtil.DEFAULT_COLOR.multiplyAlpha(element.getVisibility()));

            final int left = getX(false);
            final int top = getY(false) + 1;

            final int arrowTop = getY(false) - element.getHeight() / 2;

            GLCore.glTexturedRect(left - 2, top, 2, element.getHeight() - 1, 0, 0, 20, 4);

            //GLCore.glBindTexture(OptionCore.SAO_UI.getValue() ? StringNames.gui : StringNames.guiCustom);
            //GLCore.glTexturedRect(left - 10, arrowTop + (height - 10) / 2, 20, 25 + (fullArrow ? 10 : 0), 10, 10);
        }

        if (element.getVisibility() > 0) {
            GLCore.glBindTexture(StringNames.slot);

            final boolean hoverState = hoverState(cursorX, cursorY);
            final int color0 = getColor(hoverState, true);
            final int color1 = getColor(hoverState, false);
            final int left = getX(false);
            final int top = getY(false);
            final int width2 = element.getWidth() / 2;
            final int height2 = element.getHeight() / 2;
            final int iconOffset = (element.getHeight() - 16) / 2;
            final int captionOffset = (element.getHeight() - GLCore.glStringHeight()) / 2;

            GLCore.glBlend(true);
            GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color1, element.getVisibility()));
            if (hoverState)
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

    public boolean keyTyped(Minecraft mc, char ch, int key) {
        return false;
    }


    private boolean mouseOver(int cursorX, int cursorY, int flag) {
        if ((element.getVisibility() >= 1) && (element.isEnabled())) {
            final int left = getX(false);
            final int top = getY(false);

            return (
                    (cursorX >= left) &&
                            (cursorY >= top) &&
                            (cursorX <= left + element.getWidth()) &&
                            (cursorY <= top + element.getHeight())
            );
        } else {
            return false;
        }
    }

    public final boolean mouseOver(int cursorX, int cursorY) {
        return mouseOver(cursorX, cursorY, -1);
    }

    public boolean mousePressed(Minecraft mc, int cursorX, int cursorY, int button) {
        return false;
    }

    private void mouseMoved(Minecraft mc, int cursorX, int cursorY) {
    }

    public boolean mouseWheel(Minecraft mc, int cursorX, int cursorY, int delta) {
        return false;
    }

    @Override
    public int getX(boolean relative) {
        return relative ? element.getX() : element.getX() + (parent != null ? parent.getX(false) : 0);
    }

    @Override
    public int getY(boolean relative) {
        return relative ? element.getY() : element.getY() + (parent != null ? parent.getY(false) : 0);
    }

    private int getColor(boolean hoverState, boolean bg) {
        if (element.getIcon() == IconCore.CONFIRM)
            return bg ? !hoverState ? ColorUtil.CONFIRM_COLOR.rgba : ColorUtil.CONFIRM_COLOR_LIGHT.rgba : element.isEnabled() ? ColorUtil.HOVER_FONT_COLOR.rgba : disabledMask.rgba;
        else if (element.getIcon() == IconCore.CANCEL)
            return bg ? !hoverState ? ColorUtil.CANCEL_COLOR.rgba : ColorUtil.CANCEL_COLOR_LIGHT.rgba : element.isEnabled() ? ColorUtil.HOVER_FONT_COLOR.rgba : disabledMask.rgba;
        else
            return bg ? !hoverState ? bgColor.rgba : ColorUtil.HOVER_COLOR.rgba : !hoverState ? ColorUtil.DEFAULT_FONT_COLOR.rgba : ColorUtil.HOVER_FONT_COLOR.rgba;

    }

    public boolean mouseReleased(Minecraft mc, int cursorX, int cursorY, int button) {
        return (button == 0);
    }

    public void click(SoundHandler handler, boolean flag) {
        if (flag) {
            SoundCore.play(handler, SoundCore.MENU_POPUP);
        } else {
            SoundCore.play(handler, SoundCore.DIALOG_CLOSE);
        }
    }

    private boolean hoverState(int cursorX, int cursorY) {
        if (mouseOver(cursorX, cursorY)) element.setHighlight(true);
        else element.setHighlight(false);
        return element.isHighlight();
    }

    public void close(Minecraft mc) {
        if (!removed) {
            remove();
        }
    }

    public void remove() {
        removed = true;
    }

    public boolean removed() {
        return removed;
    }

    public String toString() {
        return "[ ( " + getClass().getName() + " " + element.getX() + " " + element.getY() + " " + element.getWidth() + " " + element.getHeight() + " ) => " + parent + " ]";
    }

    public Element getElement() {
        return element;
    }
}
