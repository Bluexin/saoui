package com.saomc.saoui.themes.elements

import com.saomc.saoui.themes.util.HudDrawContext

/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
interface ElementParent {
    fun getX(ctx: HudDrawContext): Double

    fun getY(ctx: HudDrawContext): Double

    fun getZ(ctx: HudDrawContext): Double

    val name: String
}
