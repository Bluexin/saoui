package com.saomc.saoui.api.screens;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ParentElement {

    int getX(boolean relative);

    int getY(boolean relative);

}
