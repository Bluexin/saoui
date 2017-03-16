package com.saomc.saoui.api.screens;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ParentElement {

    int getX(boolean relative);

    int getY(boolean relative);

}
