package com.saomc.screens;

import com.saomc.GLCore;
import com.saomc.resources.StringNames;
import com.saomc.util.ColorUtil;
import net.minecraft.client.Minecraft;

public class MenuSlotGUI extends MenuGUI {

    public MenuSlotGUI(ParentElement gui, int xPos, int yPos) {
        super(gui, xPos, yPos, 100, 60);
        fullArrow = true;
        innerMenu = false;
    }

    @Override
    public void draw(Minecraft mc, int cursorX, int cursorY) {
        if (visibility > 0 && parent != null && height > 0) {
            if (x > 0) {
                GLCore.glBindTexture(StringNames.slot);
                GLCore.glColorRGBA(ColorUtil.DEFAULT_COLOR.multiplyAlpha(visibility));

                final int left = getX(false);
                final int top = getY(false) + 1;

                final int arrowTop = super.getY(false) - height / 2;

                GLCore.glTexturedRect(left - 2, top, 2, height - 1, 0, 0, 20, 4);

                //GLCore.glBindTexture(OptionCore.SAO_UI.getValue() ? StringNames.gui : StringNames.guiCustom);
                //GLCore.glTexturedRect(left - 10, arrowTop + (height - 10) / 2, 20, 25 + (fullArrow ? 10 : 0), 10, 10);
            } else if (x < 0) {
                GLCore.glBindTexture(StringNames.slot);
                GLCore.glColorRGBA(ColorUtil.DEFAULT_COLOR.multiplyAlpha(visibility));

                final int left = getX(false);
                final int top = getY(false) + 1;

                final int arrowTop = super.getY(false) - height / 2;

                GLCore.glTexturedRect(left + width, top, 2, height - 1, 0, 0, 20, 4);

                //GLCore.glBindTexture(OptionCore.SAO_UI.getValue() ? StringNames.gui : StringNames.guiCustom);
                //GLCore.glTexturedRect(left + width, arrowTop + (height - 10) / 2, 30, 25 + (fullArrow ? 10 : 0), 10, 10);
            }
        }

        super.draw(mc, cursorX, cursorY);
    }
}
