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

import com.tencao.saomclib.Client
import com.tencao.saoui.GLCore
import com.tencao.saoui.commands.GeneralCommands
import com.tencao.saoui.config.ConfigHandler
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.config.Settings
import com.tencao.saoui.themes.elements.Hud
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.event.ClickEvent
import net.minecraft.util.text.event.HoverEvent
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
        private set
    lateinit var currentTheme: ThemeMetadata
        private set
    private var isReloading = false

    fun load(theme: ResourceLocation = ConfigHandler.currentTheme) {
        val oldTheme = ConfigHandler.currentTheme
        currentTheme = themeList[theme] ?: themeList[oldTheme] ?: themeList[ConfigHandler.DEFAULT_THEME]!!

        Settings.unregister(oldTheme)
        ConfigHandler.currentTheme = currentTheme.id

        currentTheme.type.loader().load(currentTheme)
        reportLoading()

        if (!isReloading) GLCore.setFont(Minecraft.getMinecraft(), OptionCore.CUSTOM_FONT.isEnabled)
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager, types: Predicate<IResourceType>) {
        if (types.test(VanillaResourceType.TEXTURES)) {
            themeList = ThemeDetector.listThemes()
            isReloading = true
            load()
            isReloading = false
        }
    }

    private fun reportLoading() {
        Client.minecraft.ingameGUI?.chatGUI?.let {
            val style = Style().apply {
                clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/saoui ${GeneralCommands.PRINT_ERRORS.getID()}")
            }
            TextComponentTranslation(
                "saoui.menu.errors",
                AbstractThemeLoader.Reporter.errors.size,
                TextComponentString(
                    if (I18n.hasKey(currentTheme.nameTranslationKey)) I18n.format(currentTheme.nameTranslationKey)
                    else currentTheme.name
                ).apply {
                    this.style = Style().setHoverEvent(
                        HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            TextComponentString(currentTheme.id.toString())
                        )
                    )
                }
            ).apply {
                this.style = style
                it.printChatMessage(this)
            }
            if (AbstractThemeLoader.Reporter.errors.isNotEmpty()) TextComponentTranslation("saoui.menu.clicktoexpand").apply {
                this.style = style.createShallowCopy()
                    .setColor(TextFormatting.GRAY)
                    .setItalic(true)
                it.printChatMessage(this)
            }
        }
    }
}
