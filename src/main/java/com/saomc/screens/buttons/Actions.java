package com.saomc.screens.buttons;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum Actions {

    UNKNOWN,

    LEFT_PRESSED,
    RIGHT_PRESSED,
    MIDDLE_PRESSED,

    LEFT_RELEASED,
    RIGHT_RELEASED,
    MIDDLE_RELEASED,

    KEY_TYPED,
    MOUSE_WHEEL;

    public static Actions getAction(int button, boolean pressed) {
        return button >= 0 && button <= 2 ? values()[button + (pressed ? LEFT_PRESSED.ordinal() : LEFT_RELEASED.ordinal())] : UNKNOWN;
    }

}
