package com.saomc.saoui.themes.elements.menus

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.resources.StringNames
import net.minecraft.client.Minecraft

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
class PlaceholderElement(val x: Int, val y: Int, val icon: IIcon) {

    private lateinit var parent: MenuElementParent

    fun draw(mc: Minecraft, mouseX: Int, mouseY: Int) {
        GLCore.glColor(1f, 1f, 1f, 1f)
        GLCore.glBlend(true)
        GLCore.glBindTexture(if (OptionCore.SAO_UI.isEnabled) StringNames.gui else StringNames.guiCustom)
        GLCore.glTexturedRect(x.toDouble() + parent.parentX, y.toDouble() + parent.parentY, 0.0, 25.0, 20.0, 20.0)
        GLCore.glBindTexture(if (OptionCore.SAO_UI.isEnabled) StringNames.icons else StringNames.iconsCustom)
        icon.glDrawUnsafe(this.x + parent.parentX + 2, this.y + parent.parentY + 2)
        GLCore.glBlend(false)
    }

    fun init(parent: MenuElementParent) {
        this.parent = parent
    }

    fun close() = Unit
}
