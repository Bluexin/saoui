package com.saomc.screens;

import com.saomc.screens.buttons.ActionHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ParentElement extends ActionHandler {

    int getX(boolean relative);

    int getY(boolean relative);

}
