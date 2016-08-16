package com.saomc.elements;

import com.saomc.SoundCore;
import com.saomc.api.screens.IIcon;
import com.saomc.screens.menu.Categories;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ElementCore implements ParentElement, IIcon {

    public final ParentElement parent;
    public final Elements element;

    private boolean removed;

    protected ElementCore(ParentElement gui, Elements elements) {
        parent = gui;
        this.element = elements;

        removed = false;
    }

    public void update(Minecraft mc) {
    }

    public void draw(Minecraft mc, int cursorX, int cursorY) {
        if (mouseOver(cursorX, cursorY)) {
            mouseMoved(mc, cursorX, cursorY);
        }
    }

    public boolean keyTyped(Minecraft mc, char ch, int key) {
        return false;
    }

    public boolean mouseOver(int cursorX, int cursorY, int flag) {
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

    void mouseMoved(Minecraft mc, int cursorX, int cursorY) {
    }

    public boolean mouseReleased(Minecraft mc, int cursorX, int cursorY, int button) {
        return false;
    }

    public boolean mouseWheel(Minecraft mc, int cursorX, int cursorY, int delta) {
        return false;
    }

    @Override
    public int getX(boolean relative) {
        return relative ? element.getX() : element.getX() + (parent != null ? parent.getX(relative) : 0);
    }

    @Override
    public int getY(boolean relative) {
        return relative ? element.getY() : element.getY() + (parent != null ? parent.getY(relative) : 0);
    }

    public void click(SoundHandler handler, boolean flag) {
        if (flag) {
            SoundCore.play(handler, SoundCore.MENU_POPUP);
        } else {
            SoundCore.play(handler, SoundCore.DIALOG_CLOSE);
        }
    }

    public Categories ID() {
        return Categories.NONE;
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

}
