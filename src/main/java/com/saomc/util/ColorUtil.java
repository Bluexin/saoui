package com.saomc.util;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum ColorUtil {
    DEFAULT_COLOR(0xFFFFFFFF),
    DEFAULT_FONT_COLOR(0x888888FF),
    DEFAULT_BOX_COLOR(0xBBBBBBFF),
    DEFAULT_BOX_FONT_COLOR(0x555555FF),

    HOVER_COLOR(0xFFBA66FF),
    HOVER_FONT_COLOR(0xFFFFFFFF),

    DISABLED_MASK(0xFFFFFF42),

    CONFIRM_COLOR(0x4782E3FF),
    CONFIRM_COLOR_LIGHT(0x629DFFFF),

    CANCEL_COLOR(0xE34747FF),
    CANCEL_COLOR_LIGHT(0xFF6262FF),

    CURSOR_COLOR(0x8EE1E8),

    DEAD_COLOR(0xC94141FF),
    HARDCORE_DEAD_COLOR(0x990000FF);

    public final int rgba;

    ColorUtil(int rgba) {
        this.rgba = rgba;
    }

    public static int multiplyAlpha(int rgba, float alpha) {
        final int value = (int) (((rgba) & 0xFF) * alpha);

        return (rgba & 0xFFFFFF00) | (value & 0xFF);
    }

    public static int mediumColor(int rgba0, int rgba1) {
        return (
                (((((rgba0 >> 24) & 0xFF) + ((rgba1 >> 24) & 0xFF)) / 2) << 24) |
                        (((((rgba0 >> 16) & 0xFF) + ((rgba1 >> 16) & 0xFF)) / 2) << 16) |
                        (((((rgba0 >> 8) & 0xFF) + ((rgba1 >> 8) & 0xFF)) / 2) << 8) |
                        (((((rgba0) & 0xFF) + ((rgba1) & 0xFF)) / 2))
        );
    }

    public int multiplyAlpha(float alpha) {
        return ColorUtil.multiplyAlpha(this.rgba, alpha);
    }

    public int mediumColor(ColorUtil color) {
        return this.mediumColor(color.rgba);
    }

    public int mediumColor(int rgba) {
        return ColorUtil.mediumColor(this.rgba, rgba);
    }

}
