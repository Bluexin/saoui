package com.saomc.saoui.themes.elements.menus

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.elements.ElementDefEnum
import com.saomc.saoui.api.elements.MenuDefEnum
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.resources.StringNames
import com.saomc.saoui.social.StaticPlayerHelper
import com.saomc.saoui.social.party.PartyHelper
import com.saomc.saoui.util.ColorUtil
import net.minecraft.client.Minecraft

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
data class ElementData(val type: MenuDefEnum, val icon: IIcon?, val name: String, val displayName: String, val elementType: ElementDefEnum, val categoryData: CategoryData) {

    private lateinit var parent: MenuElementParent
    private var x: Int = 0
    private var y: Int = 0
    private var width: Int = 20
    private var height: Int = 20
    private var visibility: Float = 1.0F
    private var scrollTextX: Float = 0.0F
    var isOpen: Boolean = false

    //TODO Replace all of this with xml based rendering
    fun draw(mc: Minecraft, mouseX: Int, mouseY: Int) {
        if (type == MenuDefEnum.ICON_BUTTON)
            drawIcon(mc, mouseX, mouseY)
        else
            drawSlot(mc, mouseX, mouseY)
    }

    fun drawIcon(mc: Minecraft, mouseX: Int, mouseY: Int){
        val hoverState = hoverState(mouseX, mouseY)
        val color0 = getColor(hoverState, true)
        val color1 = getColor(hoverState, false)
        val x = x + parent.parentX
        val y = y + parent.parentY

        GLCore.glBlend(true)
        GLCore.glBindTexture(if (OptionCore.SAO_UI.isEnabled) StringNames.gui else StringNames.guiCustom)
        GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color0, visibility))
        GLCore.glTexturedRect(x.toDouble(), y.toDouble(), 0.0, 25.0, width.toDouble(), height.toDouble())
        GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color1, visibility))
        GLCore.glBindTexture(if (OptionCore.SAO_UI.isEnabled) StringNames.icons else StringNames.iconsCustom)
        GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color1, visibility))
        icon?.glDrawUnsafe(x + 2, y + 2)
    }

    fun drawSlot(mc: Minecraft, mouseX: Int, mouseY: Int){
        val hoverState = hoverState(mouseX, mouseY)
        val color0 = getColor(hoverState, true)
        val color1 = getColor(hoverState, false)
        val iconOffset = (height - 16) / 2
        val captionOffset = (height - 8) / 2
        val x = x + parent.parentX
        val y = y + parent.parentY

        GLCore.glBlend(true)
        GLCore.glBindTexture(StringNames.slot)

        if (hoverState == 2) {
            GLCore.glColor(1.0f, 1.0f, 1.0f)
            GLCore.glTexturedRect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble(), 0.0, 21.0, (100 - 16).toDouble(), (20 - 2).toDouble())
            renderHighlightText(displayName, x  + iconOffset * 2 + 16 + 4, y + captionOffset, ColorUtil.multiplyAlpha(color1, visibility))
        } else {
            scrollTextX = 0.0F
            GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color0, visibility))
            GLCore.glTexturedRect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble(), 0.0, 1.0, (100 - 16).toDouble(), (20 - 2).toDouble())
            GLCore.glString(if (displayName.length < 50) displayName else displayName.substring(0, 50), x + iconOffset * 2 + 16 + 4, y + captionOffset, ColorUtil.multiplyAlpha(color0, visibility), OptionCore.TEXT_SHADOW.isEnabled)
        }

        GLCore.glColorRGBA(ColorUtil.multiplyAlpha(color0, visibility))
        GLCore.glBindTexture(if (OptionCore.SAO_UI.isEnabled) StringNames.icons else StringNames.iconsCustom)
        icon?.glDrawUnsafe(x + iconOffset, y + iconOffset)
    }

    fun init(parent: MenuElementParent) {
        this.parent = parent
        //SAOCore.LOGGER.info("Element " + name + ", type " + type)
        if (type != MenuDefEnum.ICON_BUTTON){
            this.width = 100
            this.height = 20
        }
        this.x = categoryData.getX()
        this.y = categoryData.getY(this)
    }

    fun getColor(hoverState: Int, bg: Boolean): Int {
        return if (bg) if (hoverState == 1) ColorUtil.DEFAULT_COLOR.rgba else if (hoverState == 2) ColorUtil.HOVER_COLOR.rgba else ColorUtil.DEFAULT_COLOR.rgba and ColorUtil.DISABLED_MASK.rgba else if (hoverState == 1) ColorUtil.DEFAULT_FONT_COLOR.rgba else if (hoverState == 2) ColorUtil.HOVER_FONT_COLOR.rgba else ColorUtil.DEFAULT_FONT_COLOR.rgba and ColorUtil.DISABLED_MASK.rgba
    }

    fun getY(): Int{
        return this.y
    }

    fun getWidth(): Int{
        return this.width
    }

    fun getHeight(): Int{
        return this.height
    }

    fun hoverState(cursorX: Int, cursorY: Int): Int {
        if (elementType == ElementDefEnum.OPTION) isOpen = OptionCore.valueOf(name).isEnabled
        else if (elementType == ElementDefEnum.PLAYER) isOpen = StaticPlayerHelper.getParty()?.isMember(StaticPlayerHelper.findOnlinePlayer(Minecraft.getMinecraft(), name)!!)?: false
        return if (isOpen) 2 else if (!categoryData.isFocus()) 0 else if (mouseOver(cursorX, cursorY)) 2 else if (this.isOpen) 2 else 1
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

    /**
     * This is a special render check for highlighted elements
     * This will check if the name is longer than the element and
     * if so, will initiate scrolling. Additionally, this is also
     * used to check whether the element being highlighted has
     * changed in order to properly reset the scrolling
     *
     * @param string Display name
     * @param x      X coord to render
     * @param y      Y coord to render
     * @param argb   Color code
     */
    private fun renderHighlightText(string: String, x: Int, y: Int, argb: Int) {
        if (string.length >= 50) {
            val name: String

            if (string.length > scrollTextX)
                name = string.substring(scrollTextX.toInt())
            else {
                scrollTextX = 0.0F
                name = string
            }

            GLCore.glString(name, x, y, argb, OptionCore.TEXT_SHADOW.isEnabled)

            scrollTextX += 0.01F
        } else
            GLCore.glString(string, x, y, argb, OptionCore.TEXT_SHADOW.isEnabled)
    }

    fun setY(y: Int){
        this.y = y
    }

    fun setX(x: Int){
        this.x = x
    }

    fun close() = Unit
}
