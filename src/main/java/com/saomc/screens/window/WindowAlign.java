package com.saomc.screens.window;

import com.saomc.screens.Elements;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum WindowAlign {

    CENTER((element, relative, width) -> element.getX(relative) + (element.width - width) / 2),

    LEFT((element, relative, width) -> element.getX(relative)),

    RIGHT((element, relative, width) -> element.getX(relative) + (element.width - width));

    private final SAOPositioner positioner;

    WindowAlign(SAOPositioner pos) {
        positioner = pos;
    }

    public int getX(Elements element, boolean relative, int size) {
        return positioner.getX(element, relative, size);
    }

    private interface SAOPositioner {
        int getX(Elements element, boolean relative, int width);
    }

}
