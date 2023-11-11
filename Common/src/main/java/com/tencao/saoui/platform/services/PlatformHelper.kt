package com.tencao.saoui.platform.services

import net.minecraft.util.ResourceLocation


interface PlatformHelper {
    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    val platformName: String

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    fun isModLoaded(modId: String): Boolean

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    val isDevelopmentEnvironment: Boolean

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    val environmentName: String
        get() = if (isDevelopmentEnvironment) "development" else "production"

    // TODO : gonna need to register them at some point (hopefully this can be done while the game is running too)
    // An alternative would be to use NightConfig directly
    fun config(namespace: ResourceLocation): Configuration
}