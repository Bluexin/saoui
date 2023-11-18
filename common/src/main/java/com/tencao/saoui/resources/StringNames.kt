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

package com.tencao.saoui.resources

import com.tencao.saoui.Constants.LOGGER
import com.tencao.saoui.Constants.MODID
import com.tencao.saoui.GLCore
import com.tencao.saoui.effects.StatusEffects
import com.tencao.saoui.themes.ThemeManager
import net.minecraft.resources.ResourceLocation
import java.util.*

object StringNames {

    fun init() {
        gui = if (GLCore.checkTexture(ResourceLocation(MODID, "textures/${ThemeManager.currentTheme}/gui.png"))) {
            ResourceLocation(MODID, "textures/${ThemeManager.currentTheme}/gui.png")
        } else {
            LOGGER.info("Theme {${ThemeManager.currentTheme}} missing custom gui data, will default to sao.")
            defaultGui
        }
        /*
        slot = if (GLCore.checkTexture(ResourceLocation(MODID, "textures/slot.png"))){
            ResourceLocation(MODID, "textures/slot.png")
        }
        else defaultSlot*/
        slot = defaultSlot
        entities = if (GLCore.checkTexture(ResourceLocation(MODID, "textures/${ThemeManager.currentTheme}/entities.png"))) {
            ResourceLocation(MODID, "textures/${ThemeManager.currentTheme}/entities.png")
        } else {
            LOGGER.info("Theme {${ThemeManager.currentTheme}} missing custom entity health bars, will default to sao.")
            defaultEntities
        }
        particleLarge = if (GLCore.checkTexture(ResourceLocation(MODID, "textures/${ThemeManager.currentTheme}/particlelarge.png"))) {
            ResourceLocation(MODID, "textures/${ThemeManager.currentTheme}/particlelarge.png")
        } else {
            LOGGER.info("Theme {${ThemeManager.currentTheme}} missing custom death particles, will default to sao.")
            defaultParticleLarge
        }
        val missingEffects = StatusEffects.values().filter {
            !GLCore.checkTexture(
                ResourceLocation(
                    MODID,
                    "textures/${ThemeManager.currentTheme}/status_icons/${it.name.lowercase(
                        Locale.getDefault()
                    )}.png"
                )
            )
        }
        statusIcons = if (missingEffects.isEmpty()) {
            "textures/${ThemeManager.currentTheme}/status_icons/"
        } else {
            LOGGER.info("Theme {${ThemeManager.currentTheme}} missing custom status icons, will default to sao.")
            LOGGER.info("Missing Effects: ${missingEffects.map { it.name }}")
            defaultStatusIcons
        }
    }

    lateinit var gui: ResourceLocation
    lateinit var slot: ResourceLocation
    lateinit var entities: ResourceLocation
    lateinit var particleLarge: ResourceLocation
    lateinit var statusIcons: String

    val defaultGui = ResourceLocation(MODID, "textures/sao/gui.png")
    val defaultSlot = ResourceLocation(MODID, "textures/slot.png")
    val defaultEntities = ResourceLocation(MODID, "textures/sao/entities.png")
    val defaultParticleLarge = ResourceLocation(MODID, "textures/sao/particlelarge.png")
    const val defaultStatusIcons = "textures/sao/status_icons/"
}
