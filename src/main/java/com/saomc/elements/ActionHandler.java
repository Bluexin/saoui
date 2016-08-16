package com.saomc.elements;

import com.saomc.api.screens.Actions;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ActionHandler {

    void actionPerformed(ElementCore element, Actions action, int data);

}
