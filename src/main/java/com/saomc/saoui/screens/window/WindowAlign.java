package com.saomc.saoui.screens.window;

import com.saomc.saoui.elements.Element;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum WindowAlign {

    CENTER((element, relative, width) -> element.getX(relative) + (element.getWidth() - width) / 2),

    LEFT((element, relative, width) -> element.getX(relative)),

    RIGHT((element, relative, width) -> element.getX(relative) + (element.getWidth() - width));

    private final SAOPositioner positioner;

    WindowAlign(SAOPositioner pos) {
        positioner = pos;
    }

    public int getX(Element element, boolean relative, int size) {
        return positioner.getX(element, relative, size);
    }

    private interface SAOPositioner {
        int getX(Element element, boolean relative, int width);
    }

}
