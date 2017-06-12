package com.saomc.saoui.api.elements


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
