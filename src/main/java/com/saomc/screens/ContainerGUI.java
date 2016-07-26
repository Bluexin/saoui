package com.saomc.screens;

import com.saomc.screens.buttons.Actions;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ContainerGUI extends Elements {

    public final List<Elements> elements;

    public ContainerGUI(ParentElement gui, int xPos, int yPos, int w, int h) {
        super(gui, xPos, yPos, w, h);
        elements = new ArrayList<>();
    }

    @Override
    public void update(Minecraft mc) {
        focus = false;

        for (int i = elements.size() - 1; i >= 0; i--) update(mc, i, elements.get(i));
    }

    void update(Minecraft mc, int index, Elements element) {
        if (element.removed()) {
            elements.remove(index);
            return;
        }

        element.update(mc);
        focus |= element.focus;
    }

    @Override
    public void draw(Minecraft mc, int cursorX, int cursorY) {
        super.draw(mc, cursorX, cursorY);

        for (int i = elements.size() - 1; i >= 0; i--) elements.get(i).draw(mc, cursorX, cursorY);
    }

    @Override
    public boolean keyTyped(Minecraft mc, char ch, int key) {
        for (int i = elements.size() - 1; i >= 0; i--)
            if (elements.get(i).focus && elements.get(i).keyTyped(mc, ch, key))
                actionPerformed(elements.get(i), Actions.KEY_TYPED, key);

        return super.keyTyped(mc, ch, key);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int cursorX, int cursorY, int button) {
        for (int i = elements.size() - 1; i >= 0; i--) {
            if (i >= elements.size()) {
                if (elements.size() > 0) i = elements.size() - 1;
                else break;
            }

            if (elements.get(i).mouseOver(cursorX, cursorY))
                if (elements.get(i).mousePressed(mc, cursorX, cursorY, button))
                    actionPerformed(elements.get(i), Actions.getAction(button, true), button);
        }

        return super.mousePressed(mc, cursorX, cursorY, button);
    }

    @Override
    public boolean mouseReleased(Minecraft mc, int cursorX, int cursorY, int button) {
        for (int i = elements.size() - 1; i >= 0; i--) {
            if (i >= elements.size()) {
                if (elements.size() > 0) i = elements.size() - 1;
                else break;
            }

            if (elements.get(i).mouseOver(cursorX, cursorY, button))
                if (elements.get(i).mouseReleased(mc, cursorX, cursorY, button))
                    actionPerformed(elements.get(i), Actions.getAction(button, false), button);
        }

        return super.mouseReleased(mc, cursorX, cursorY, button);
    }

    @Override
    public boolean mouseWheel(Minecraft mc, int cursorX, int cursorY, int delta) {
        for (int i = elements.size() - 1; i >= 0; i--) {
            if (i >= elements.size()) {
                if (elements.size() > 0) i = elements.size() - 1;
                else break;
            }

            if (elements.get(i).mouseOver(cursorX, cursorY))
                if (elements.get(i).mouseWheel(mc, cursorX, cursorY, delta))
                    actionPerformed(elements.get(i), Actions.MOUSE_WHEEL, delta);
        }

        return super.mouseWheel(mc, cursorX, cursorY, delta);
    }

    @Override
    public void close(Minecraft mc) {
        elements.stream().forEach(el -> el.close(mc));
        elements.clear();

        super.close(mc);
    }

}
