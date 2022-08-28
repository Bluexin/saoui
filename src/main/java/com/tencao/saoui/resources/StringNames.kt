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

import com.tencao.saomclib.GLCore
import com.tencao.saoui.SAOCore
import com.tencao.saoui.effects.StatusEffects
import com.tencao.saoui.themes.ThemeLoader
import net.minecraft.util.ResourceLocation
import java.util.*

object StringNames {

    fun init() {
        gui = if (GLCore.checkTexture(ResourceLocation(SAOCore.MODID, "textures/${ThemeLoader.currentTheme}/gui.png"))) {
            ResourceLocation(SAOCore.MODID, "textures/${ThemeLoader.currentTheme}/gui.png")
        } else {
            SAOCore.LOGGER.info("Theme {${ThemeLoader.currentTheme}} missing custom gui data, will default to sao.")
            defaultGui
        }
        /*
        slot = if (GLCore.checkTexture(ResourceLocation(SAOCore.MODID, "textures/slot.png"))){
            ResourceLocation(SAOCore.MODID, "textures/slot.png")
        }
        else defaultSlot*/
        slot = defaultSlot
        entities = if (GLCore.checkTexture(ResourceLocation(SAOCore.MODID, "textures/${ThemeLoader.currentTheme}/entities.png"))) {
            ResourceLocation(SAOCore.MODID, "textures/${ThemeLoader.currentTheme}/entities.png")
        } else {
            SAOCore.LOGGER.info("Theme {${ThemeLoader.currentTheme}} missing custom entity health bars, will default to sao.")
            defaultEntities
        }
        particleLarge = if (GLCore.checkTexture(ResourceLocation(SAOCore.MODID, "textures/${ThemeLoader.currentTheme}/particlelarge.png"))) {
            ResourceLocation(SAOCore.MODID, "textures/${ThemeLoader.currentTheme}/particlelarge.png")
        } else {
            SAOCore.LOGGER.info("Theme {${ThemeLoader.currentTheme}} missing custom death particles, will default to sao.")
            defaultParticleLarge
        }
        val missingEffects = StatusEffects.values().filter {
            !GLCore.checkTexture(
                ResourceLocation(
                    SAOCore.MODID,
                    "textures/${ThemeLoader.currentTheme}/status_icons/${it.name.lowercase(
                        Locale.getDefault()
                    )}.png"
                )
            )
        }
        statusIcons = if (missingEffects.isEmpty()) {
            "textures/${ThemeLoader.currentTheme}/status_icons/"
        } else {
            SAOCore.LOGGER.info("Theme {${ThemeLoader.currentTheme}} missing custom status icons, will default to sao.")
            SAOCore.LOGGER.info("Missing Effects: ${missingEffects.map { it.name }}")
            defaultStatusIcons
        }
    }

    lateinit var gui: ResourceLocation
    lateinit var slot: ResourceLocation
    lateinit var entities: ResourceLocation
    lateinit var particleLarge: ResourceLocation
    lateinit var statusIcons: String

    val defaultGui = ResourceLocation(SAOCore.MODID, "textures/sao/gui.png")
    val defaultSlot = ResourceLocation(SAOCore.MODID, "textures/slot.png")
    val defaultEntities = ResourceLocation(SAOCore.MODID, "textures/sao/entities.png")
    val defaultParticleLarge = ResourceLocation(SAOCore.MODID, "textures/sao/particlelarge.png")
    const val defaultStatusIcons = "textures/sao/status_icons/"
}
