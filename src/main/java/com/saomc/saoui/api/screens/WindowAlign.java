package com.saomc.saoui.api.screens;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum WindowAlign {

    HORIZONTAL_CENTER((x, parentElement, relative, width) -> getX(x, parentElement, relative) + (x - width) / 2),

    HORIZONTAL_LEFT((x, parentElement, relative, width) ->getX(x, parentElement, relative)),

    HORIZONTAL_RIGHT((x, parentElement, relative, width) -> getX(x, parentElement, relative) + (x - width)),

    VERTICAL_CENTER((y, parentElement, relative, height) -> getY(y, parentElement, relative) + (y - height) / 2),

    VERTICAL_LEFT((y, parentElement, relative, height) -> getY(y, parentElement, relative)),

    VERTICAL_RIGHT((y, parentElement, relative, height) -> getY(y, parentElement, relative) + (y - height));

    private final SAOPositioner positioner;

    WindowAlign(SAOPositioner pos) {
        positioner = pos;
    }

    public int getPos(int pos, ParentElement parentElement, boolean relative, int size) {
        return positioner.getPos(pos, parentElement, relative, size);
    }

    private interface SAOPositioner {
        int getPos(int pos, ParentElement parentElement, boolean relative, int width);
    }

    private static int getX(int pos, ParentElement parentElement, boolean relative) {
        return relative ? pos : pos + (parentElement != null ? parentElement.getX(relative) : 0);
    }

    private static int getY(int pos, ParentElement parentElement, boolean relative) {
        return relative ? pos : pos + (parentElement != null ? parentElement.getY(relative) : 0);
    }

}
