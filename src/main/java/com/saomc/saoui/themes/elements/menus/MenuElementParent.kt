package com.saomc.saoui.themes.elements.menus


/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
interface MenuElementParent {
    val parentX: Int

    val parentY: Int

    val parentZ: Int

    val name: String

    fun openCategory(name: String)

    fun closeCategory(name: String)
}
