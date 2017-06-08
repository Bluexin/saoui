package com.saomc.saoui.themes.elements.menus

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.resources.StringNames
import com.saomc.saoui.util.ColorUtil
import net.minecraft.client.Minecraft

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
data class ElementData(val type: MenuDefEnum, val icon: IIcon?, val name: String) {

    private lateinit var parent: MenuElementParent
    private var x: Int = 0
    private var y: Int = 0
    private var width: Double = 60.0
    private var height: Double = 20.0
    private var visibility: Float = 1.0F
    private var isOpen: Boolean = false
    private var disabled: Boolean = false

    fun draw(mc: Minecraft, mouseX: Int, mouseY: Int) {

        val hoverState = hoverState(mouseX, mouseY)
        val color0 = getColor(hoverState, true)
        val color1 = getColor(hoverState, false)

        GLCore.glBlend(true)
        GLCore.glBindTexture(if (OptionCore.SAO_UI.isEnabled) StringNames.gui else StringNames.guiCustom)
        GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color0, visibility))
        GLCore.glTexturedRect(x.toDouble() + parent.parentX, y.toDouble() + parent.parentY, 0.0, 25.0, width, height)
        GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color1, visibility))
        GLCore.glBindTexture(if (OptionCore.SAO_UI.isEnabled) StringNames.icons else StringNames.iconsCustom)
        icon?.glDrawUnsafe(this.x + parent.parentX + 2, this.y + parent.parentY + 2)
        GLCore.glBlend(false)
        GLCore.glAlphaTest(true)
    }

    fun init(parent: MenuElementParent) {
        this.parent = parent
        if (type == MenuDefEnum.ICON_BUTTON){
            this.width = 20.0
            this.height = 20.0
        }

    }

    fun getColor(hoverState: Int, bg: Boolean): Int {
        return if (bg) if (hoverState == 1) ColorUtil.DEFAULT_COLOR.rgba else if (hoverState == 2) ColorUtil.HOVER_COLOR.rgba else ColorUtil.DEFAULT_COLOR.rgba and ColorUtil.DISABLED_MASK.rgba else if (hoverState == 1) ColorUtil.DEFAULT_FONT_COLOR.rgba else if (hoverState == 2) ColorUtil.HOVER_FONT_COLOR.rgba else ColorUtil.DEFAULT_FONT_COLOR.rgba and ColorUtil.DISABLED_MASK.rgba
    }

    fun getY(): Int{
        return this.y
    }

    fun hoverState(cursorX: Int, cursorY: Int): Int {
        return if (mouseOver(cursorX, cursorY)) 2 else if (this.isOpen) 2 else if (this.disabled) 0 else 1
    }

    fun mouseOver(cursorX: Int, cursorY: Int): Boolean {
        if (this.visibility >= 0.6) {
            val left = x + parent.parentX
            val top = y + parent.parentY

            return cursorX >= left &&
                    cursorY >= top &&
                    cursorX <= left + this.width &&
                    cursorY <= top + this.height
        } else {
            return false
        }
    }

    fun setY(y: Int){
        this.y = y
    }

    fun close() = Unit
}
