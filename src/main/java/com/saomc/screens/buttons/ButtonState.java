package com.saomc.screens.buttons;

import com.saomc.api.screens.IIcon;
import com.saomc.screens.ParentElement;
import com.saomc.screens.menu.Categories;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ButtonState extends ButtonGUI {

    private final StateHandler state;

    public ButtonState(ParentElement gui, Categories saoID, int xPos, int yPos, int w, int h, String string, IIcon icon, StateHandler handler) {
        super(gui, saoID, xPos, yPos, w, h, string, icon);
        state = handler;
    }

    public ButtonState(ParentElement gui, Categories saoID, int xPos, int yPos, int w, String string, IIcon icon, StateHandler handler) {
        this(gui, saoID, xPos, yPos, w, 20, string, icon, handler);
    }

    public ButtonState(ParentElement gui, Categories saoID, int xPos, int yPos, String string, IIcon icon, StateHandler handler) {
        this(gui, saoID, xPos, yPos, 100, string, icon, handler);
    }

    @Override
    public void update(Minecraft mc) {
        if (state != null) enabled = state.isStateEnabled(mc, this);

        super.update(mc);
    }

}
