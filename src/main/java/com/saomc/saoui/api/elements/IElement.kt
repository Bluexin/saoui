package com.saomc.saoui.api.elements

import com.saomc.saoui.api.screens.Actions
import com.saomc.saoui.themes.elements.menus.CategoryData
import net.minecraft.client.Minecraft

/**
 * Used for creating and adding custom elements
 */
interface IElement {

    val name: String

    val width: Int

    val height: Int

    val type: MenuDefEnum

    val elementType: ElementDefEnum

    var isOpen: Boolean

    fun draw(mc: Minecraft, cursorX: Int, cursorY: Int)

    fun init(parent: MenuElementParent, categoryData: CategoryData)

    /** Return true if you want this element to be part of the menu, otherwise return false to not be tied to it **/
    fun isMenu(): Boolean

    fun mouseOver(cursorX: Int, cursorY: Int): Boolean

    /** Fires a Action event if returned true **/
    fun mouseClicked(cursorX: Int, cursorY: Int, action: Actions): Boolean

    /** Fires a Action event if returned true **/
    fun mouseScroll(cursorX: Int, cursorY: Int, delta: Int): Boolean

    fun close()
}
