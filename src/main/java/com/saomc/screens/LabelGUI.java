package com.saomc.screens;

import com.saomc.GLCore;
import com.saomc.screens.window.WindowAlign;
import com.saomc.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LabelGUI extends Elements {

    public String caption;
    public ColorUtil fontColor;
    private WindowAlign align;

    public LabelGUI(ParentElement gui, int xPos, int yPos, int width, String string, WindowAlign windowAlign) {
        super(gui, xPos, yPos, width, 20);
        caption = string;
        fontColor = ColorUtil.DEFAULT_FONT_COLOR;
        align = windowAlign;
    }

    public LabelGUI(ParentElement gui, int xPos, int yPos, String string, WindowAlign windowAlign) {
        this(gui, xPos, yPos, 200, string, windowAlign);
    }

    @Override
    public void draw(Minecraft mc, int cursorX, int cursorY) {
        super.draw(mc, cursorX, cursorY);

        if (visibility > 0) {
            final int left = align.getX(this, false, GLCore.glStringWidth(caption)) + getOffsetX();
            final int top = getY(false);

            GLCore.glString(caption, left, top + (height - GLCore.glStringHeight()) / 2, fontColor.multiplyAlpha(visibility));
        }
    }

    private int getOffsetX() {
        if (align == WindowAlign.LEFT) {
            return 8;
        } else if (align == WindowAlign.RIGHT) {
            return -8;
        } else {
            return 0;
        }
    }

}
