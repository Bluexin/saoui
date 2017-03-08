package com.saomc.saoui.themes.elements.menus

import com.saomc.saoui.util.IconCore
import net.minecraft.client.Minecraft

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
class PlaceholderElement(val x: Int, val y: Int, val icon: IconCore) {

    private lateinit var parent: MenuElementParent

    fun draw(mc: Minecraft, cursorX: Int, cursorY: Int) {
        icon.glDraw(x + parent.getX(), y + parent.getY())
    }

    fun init(parent: MenuElementParent) {
        this.parent = parent
    }

    fun close() = Unit
}
