package com.saomc.screens.menu;

import com.saomc.GLCore;
import com.saomc.resources.StringNames;
import com.saomc.screens.MenuGUI;
import com.saomc.screens.ParentElement;
import com.saomc.util.ColorUtil;
import com.saomc.util.OptionCore;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Panel extends MenuGUI {

    public ColorUtil bgColor;

    public Panel(ParentElement gui, int xPos, int yPos, int w, int h) {
        super(gui, xPos, yPos, w, h);
        bgColor = ColorUtil.DEFAULT_COLOR;
    }

    @Override
    public void draw(Minecraft mc, int cursorX, int cursorY) {
        if ((visibility > 0) && (height > 0)) {
            GLCore.glBindTexture(OptionCore.SAO_UI.getValue() ? StringNames.gui : StringNames.guiCustom);

            final int left = getX(false);
            final int top = getY(false);

            final int shadowSize = (x == 0 ? 0 : 5);

            GLCore.glColorRGBA(bgColor.multiplyAlpha(visibility));

            if (shadowSize > 0) {
                GLCore.glTexturedRect(left - shadowSize, top - shadowSize, 5 - shadowSize, 120 - shadowSize, shadowSize, shadowSize);
                GLCore.glTexturedRect(left + width, top - shadowSize, 15, 120 - shadowSize, shadowSize, shadowSize);
                GLCore.glTexturedRect(left - shadowSize, top + height, 5 - shadowSize, 130, shadowSize, shadowSize);
                GLCore.glTexturedRect(left + width, top + height, 15, 130, shadowSize, shadowSize);

                GLCore.glTexturedRect(left, top - shadowSize, width, shadowSize, 5, 120 - shadowSize, 10, shadowSize);
                GLCore.glTexturedRect(left - shadowSize, top, shadowSize, height, 5 - shadowSize, 120, shadowSize, 10);
                GLCore.glTexturedRect(left + width, top, shadowSize, height, 15, 120, shadowSize, 10);
                GLCore.glTexturedRect(left, top + height, width, shadowSize, 5, 130, 10, shadowSize);
            }

            GLCore.glTexturedRect(left, top, width, height, 5, 120, 10, 10);

            if (x == 0) {
                GLCore.glColorRGBA(ColorUtil.DEFAULT_COLOR.multiplyAlpha(visibility));

                GLCore.glTexturedRect(left + 5, top, 156, 25, 10, 10);
            }
        }

        super.draw(mc, cursorX, cursorY);
    }

}
