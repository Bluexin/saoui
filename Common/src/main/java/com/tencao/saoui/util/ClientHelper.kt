package com.tencao.saoui.util

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen


/**
 * Calls a profiled block, with given key.
 */
inline fun Minecraft.profile(key: String, body: () -> Unit) {
    this.profiler.push(key)
    body()
    this.profiler.pop()
}

/**
 * From LibrarianLib, with edits mostly made by Tencao
 *
 * Using easy access methods to help and assist functions.
 */
object Client {
    val minecraft: Minecraft
        get() = Minecraft.getInstance()
    val resourceManager
        get() = minecraft.resourceManager
    val renderManager
        get() = minecraft.entityRenderDispatcher
    val textureManager
        get() = minecraft.textureManager
    val player
        get() = minecraft.player

    fun displayGuiScreen(screen: Screen?) {
        minecraft.setScreen(screen)
    }
}

val Minecraft.scaledWidth: Int
    get() = this.window.guiScaledWidth
val Minecraft.scaledHeight: Int
    get() = this.window.guiScaledHeight
