package com.saomc.saoui.themes.elements.menus

import com.saomc.saoui.api.elements.CategoryEnum


/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
interface MenuElementParent {
    val parentX: Int

    val parentY: Int

    val parentZ: Int

    val name: String

    fun openCategory(category: CategoryEnum)

    fun closeCategory(category: CategoryEnum)
}
