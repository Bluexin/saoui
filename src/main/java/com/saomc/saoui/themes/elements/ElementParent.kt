package com.saomc.saoui.themes.elements

import com.saomc.saoui.api.themes.IHudDrawContext

/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
interface ElementParent {
    fun getX(ctx: IHudDrawContext): Double

    fun getY(ctx: IHudDrawContext): Double

    fun getZ(ctx: IHudDrawContext): Double

    val name: String
}
