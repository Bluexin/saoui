package com.tencao.saoui.themes

import net.minecraft.util.ResourceLocation

data class ThemeMetadata(
    val id: ResourceLocation,
    val themeRoot: ResourceLocation,
    val name: String,
    val type: ThemeFormat
) {
    val texturesRoot = ResourceLocation(themeRoot.resourceDomain, "textures/${id.resourcePath}/")
}

enum class ThemeFormat(val hudFileSuffix: String, val loader: () -> AbstractThemeLoader) {
    XML("hud.xml", ::XmlThemeLoader),
    JSON("hud.json", ::JsonThemeLoader);

    companion object {
        fun fromFile(fileName: String): ThemeFormat? = values().firstOrNull { fileName.endsWith(it.hudFileSuffix) }
    }
}
