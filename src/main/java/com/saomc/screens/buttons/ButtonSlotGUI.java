package com.saomc.screens.buttons;

import com.saomc.GLCore;
import com.saomc.resources.StringNames;
import com.saomc.screens.Elements;
import com.saomc.screens.ParentElement;
import com.saomc.screens.menu.Categories;
import com.saomc.util.ColorUtil;
import com.saomc.util.IconCore;
import com.saomc.util.OptionCore;
import net.minecraft.client.Minecraft;

public class ButtonSlotGUI extends Elements {

    private final Categories id;

    public String caption;
    public IconCore icon;
    public boolean highlight;

    public ButtonSlotGUI(ParentElement gui, Categories saoID, int xPos, int yPos, String string, IconCore iconCore) {
        super(gui, xPos, yPos, 100, 20);
        id = saoID;
        caption = string;
        icon = iconCore;
        highlight = false;
    }

    public ButtonSlotGUI(ParentElement gui, Categories slot, int xPos, int yPos) {
        this(gui, slot, xPos, yPos, "", IconCore.NONE);
    }


    @Override
    public void draw(Minecraft mc, int cursorX, int cursorY) {
        if (visibility > 0) {
            GLCore.glBindTexture(StringNames.slot);

            final int hoverState = hoverState(cursorX, cursorY);
            final int color0 = getColor(hoverState, true);
            final int color1 = getColor(hoverState, false);
            final int left = getX(false);
            final int top = getY(false);
            final int width2 = width / 2;
            final int height2 = height / 2;
            final int iconOffset = (height - 16) / 2;
            final int captionOffset = (height - GLCore.glStringHeight()) / 2;

            GLCore.glBlend(true);
            GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color1, visibility));
            if (hoverState == 3)
                GLCore.glTexturedRect(left, top, width, height, 0, 20, width - 15, height);
            else
                GLCore.glTexturedRect(left, top, width, height, 0, 0, width - 15, height);

            GLCore.glBindTexture(OptionCore.SAO_UI.getValue() ? StringNames.gui : StringNames.guiCustom);

            GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color1, visibility));
            GLCore.glTexturedRect(left + iconOffset, top + iconOffset, 140, 25, 16, 16);

            GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color0, visibility));
            icon.glDraw(left + iconOffset, top + iconOffset);


            GLCore.glString(caption, left + iconOffset * 2 + 16 + 4, top + captionOffset, ColorUtil.multiplyAlpha(color1, visibility));
            GLCore.glBlend(false);
        }
    }

    public boolean mouseReleased(Minecraft mc, int cursorX, int cursorY, int button) {
        return (button == 0);
    }

    protected int getColor(int hoverState, boolean bg) {
        return bg ? hoverState == 1 ? ColorUtil.DEFAULT_COLOR.rgba : hoverState >= 2 ? ColorUtil.HOVER_COLOR.rgba : ColorUtil.DISABLED_MASK.rgba : hoverState == 1 ? ColorUtil.DEFAULT_FONT_COLOR.rgba : hoverState >= 2 ? ColorUtil.HOVER_FONT_COLOR.rgba : ColorUtil.DEFAULT_FONT_COLOR.rgba & ColorUtil.DISABLED_MASK.rgba;
    }

    public int hoverState(int cursorX, int cursorY) {
        return mouseOver(cursorX, cursorY) ? 2 : highlight ? 3 : enabled ? 1 : 0;
    }

    public Categories ID() {
        return id;
    }
}
