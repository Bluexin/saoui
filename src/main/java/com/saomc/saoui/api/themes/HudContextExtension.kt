package com.saomc.saoui.api.themes

/**
 * Mod extension for HUD rendering
 */
interface HudContextExtension {
    /**
     * The key of this extension
     */
    val key: String

    /**
     * The version of this extension
     */
    val version: String

    /**
     * Aka partialTicks
     */
    fun setTime(time: Float) = Unit
}