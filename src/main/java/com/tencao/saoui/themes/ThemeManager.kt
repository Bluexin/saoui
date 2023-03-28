/*
 * Copyright (C) 2016-2019 Arnaud 'Bluexin' Solé
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.tencao.saoui.themes

import com.tencao.saoui.GLCore
import com.tencao.saoui.config.ConfigHandler
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.themes.elements.Hud
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.resource.IResourceType
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener
import net.minecraftforge.client.resource.VanillaResourceType
import java.util.function.Predicate

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
object ThemeManager : ISelectiveResourceReloadListener {

    // TODO: tests
    // TODO: theme versions
    // TODO: loading reporter (amount of issues, details, missing keys, ..?)

    lateinit var HUD: Hud
    lateinit var themeList: Map<ResourceLocation, ThemeMetadata>
    lateinit var currentTheme: ThemeMetadata

    fun load(theme: ResourceLocation = ConfigHandler.currentTheme) {
        val oldTheme = ConfigHandler.currentTheme
        currentTheme = themeList[theme] ?: themeList[oldTheme] ?: themeList[ConfigHandler.DEFAULT_THEME]!!

        ConfigHandler.currentTheme = currentTheme.id

        currentTheme.type.loader().load(currentTheme)

        if (OptionCore.CUSTOM_FONT.isEnabled) GLCore.setFont(Minecraft.getMinecraft(), true)
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager, types: Predicate<IResourceType>) {
        if (types.test(VanillaResourceType.TEXTURES)) {
            themeList = ThemeDetector.listThemes()
            load()
        }
    }
}
