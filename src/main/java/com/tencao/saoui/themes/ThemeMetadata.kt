package com.tencao.saoui.themes

import net.minecraft.util.ResourceLocation

data class ThemeMetadata(
    val id: ResourceLocation,
    /**
     * Root for the theme's configuration files (hud.json, settings.json, ...)
     */
    val themeRoot: ResourceLocation,
    val name: String,
    val type: ThemeFormat
) {
    /**
     * Root for the theme's textures
     */
    val texturesRoot = ResourceLocation(themeRoot.resourceDomain, "textures/${id.resourcePath}/")
}

enum class ThemeFormat(val hudFileSuffix: String, val loader: () -> AbstractThemeLoader) {
    XML("hud.xml", ::XmlThemeLoader),
    JSON("hud.json", ::JsonThemeLoader);

    companion object {
        fun fromFile(fileName: String): ThemeFormat? = values().firstOrNull { fileName.endsWith(it.hudFileSuffix) }
    }
}
