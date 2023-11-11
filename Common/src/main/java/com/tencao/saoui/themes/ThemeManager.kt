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
import com.tencao.saoui.commands.BaseCommands
import com.tencao.saoui.commands.DebugCommands
import com.tencao.saoui.config.ConfigHandler
import com.tencao.saoui.themes.elements.Hud
import com.tencao.saoui.util.check
import com.tencao.saoui.util.localize
import com.tencao.saoui.util.toTextComponent
import com.tencao.saoui.util.translate
import net.minecraft.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.event.ClickEvent
import net.minecraft.util.text.event.HoverEvent
import net.minecraftforge.resource.IResourceType
import net.minecraftforge.resource.ISelectiveResourceReloadListener
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

//        if (!isReloading) GLCore.setFont(Client.mc, OptionCore.CUSTOM_FONT.isEnabled)
    }

    // TODO: this supports async stuff now
    override fun onResourceManagerReload(resourceManager: IResourceManager) {
        themeList = ThemeDetector.listThemes()
        isReloading = true
        load()
        isReloading = false
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager, resourcePredicate: Predicate<IResourceType>) {
        TODO("Not yet implemented")
    }

    private fun reportLoading() {
        val style = Style.EMPTY
            .withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, BaseCommands.useCommand(DebugCommands.PRINT_ERRORS)))

        Client.player?.displayClientMessage(
            "saoui.menu.errors".translate(
                if (currentTheme.nameTranslationKey.check())
                    currentTheme.nameTranslationKey.localize()
                else currentTheme.name.localize()
            ).apply {
                this.style = Style.EMPTY.withHoverEvent(
                    HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        currentTheme.id.toString().toTextComponent()
                    )
                )
            }.apply {
                this.style = style
            },
            false
        )

        if (AbstractThemeLoader.Reporter.errors.isNotEmpty())
            Client.player?.displayClientMessage(
                "saoui.menu.clicktoexpand".translate("(click to expand)")
                .apply {
                    this.style = style
                        .withColor(TextFormatting.GRAY)
                        .withItalic(true)
                },
                false
            )
    }
}
