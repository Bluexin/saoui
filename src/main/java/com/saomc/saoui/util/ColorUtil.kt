package com.saomc.saoui.util

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
enum class ColorUtil constructor(var rgba: Int) {
    DEFAULT_COLOR(0xFFFFFFFF.toInt()),
    DEFAULT_FONT_COLOR(0x888888FF.toInt()),
    DEFAULT_BOX_COLOR(0xBBBBBBFF.toInt()),
    DEFAULT_BOX_FONT_COLOR(0x555555FF),

    HOVER_COLOR(0xC99B13FF.toInt()),
    HOVER_FONT_COLOR(0xFFFFFFFF.toInt()),

    DISABLED_MASK(0xFFFFFF42.toInt()),

    CONFIRM_COLOR(0x4782E3FF),
    CONFIRM_COLOR_LIGHT(0x629DFFFF),

    CANCEL_COLOR(0xE34747FF.toInt()),
    CANCEL_COLOR_LIGHT(0xFF6262FF.toInt()),

    CURSOR_COLOR(0x8EE1E8),

    DEAD_COLOR(0xC94141FF.toInt()),
    HARDCORE_DEAD_COLOR(0x990000FF.toInt());

    fun multiplyAlpha(alpha: Float): Int {
        return ColorUtil.multiplyAlpha(this.rgba, alpha)
    }

    fun mediumColor(color: ColorUtil): Int {
        return this.mediumColor(color.rgba)
    }

    fun mediumColor(rgba: Int): Int {
        return ColorUtil.mediumColor(this.rgba, rgba)
    }

    companion object {

        fun multiplyAlpha(rgba: Int, alpha: Float): Int {
            val value = ((rgba and 0xFF) * alpha).toInt()

            return rgba and 0xFFFFFF00.toInt() or (value and 0xFF)
        }

        fun mediumColor(rgba0: Int, rgba1: Int): Int {
            return ((rgba0 shr 24 and 0xFF) + (rgba1 shr 24 and 0xFF)) / 2 shl 24 or
                    (((rgba0 shr 16 and 0xFF) + (rgba1 shr 16 and 0xFF)) / 2 shl 16) or
                    (((rgba0 shr 8 and 0xFF) + (rgba1 shr 8 and 0xFF)) / 2 shl 8) or
                    ((rgba0 and 0xFF) + (rgba1 and 0xFF)) / 2
        }
    }

    infix fun and(other: ColorUtil) = this.rgba and other.rgba
}

enum class ColorIntent {
    NORMAL,
    HOVERED,
    DISABLED_MASK
}
