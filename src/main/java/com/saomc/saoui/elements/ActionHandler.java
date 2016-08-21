package com.saomc.saoui.elements;

import com.saomc.saoui.api.screens.Actions;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ActionHandler {

    void actionPerformed(ElementCore element, Actions action, int data);

}
