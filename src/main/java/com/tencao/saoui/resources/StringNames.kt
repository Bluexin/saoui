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

import com.tencao.saoui.GLCore
import com.tencao.saoui.SAOCore
import com.tencao.saoui.effects.StatusEffects
import com.tencao.saoui.themes.ThemeManager
import com.tencao.saoui.util.append
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
object StringNames {

    private fun logMissingAndUse(type: String, default: ResourceLocation): ResourceLocation {
        SAOCore.LOGGER.info("Theme {} missing custom {}, defaulting to SAO", ThemeManager.currentTheme, type)
        return default
    }

    fun init() {
        val textureRoot = ThemeManager.currentTheme.texturesRoot

        gui = GLCore.useTextureIfExists(textureRoot.append("gui.png"))
            ?: logMissingAndUse("gui", defaultGui)
        slot = GLCore.useTextureIfExists(textureRoot.append("slot.png"))
            ?: logMissingAndUse("slot", defaultSlot)
        entities = GLCore.useTextureIfExists(textureRoot.append("entities.png"))
            ?: logMissingAndUse("entity health bars", defaultEntities)
        particleLarge = GLCore.useTextureIfExists(textureRoot.append("particlelarge.png"))
            ?: logMissingAndUse("death particles", defaultParticleLarge)

        val missingEffects = StatusEffects.values().filter {
            !GLCore.checkTexture(textureRoot.append("status_icons/${it.name.toLowerCase()}.png"))
        }
        statusIcons = if (missingEffects.isEmpty()) {
            textureRoot.append("status_icons/")
        } else logMissingAndUse(
            "status icons ${missingEffects.map(StatusEffects::name)}",
            defaultStatusIcons
        )
    }

    lateinit var gui: ResourceLocation
    lateinit var slot: ResourceLocation
    lateinit var entities: ResourceLocation
    lateinit var particleLarge: ResourceLocation
    lateinit var statusIcons: ResourceLocation

    private val defaultGui = ResourceLocation(SAOCore.MODID, "textures/sao/gui.png")
    private val defaultSlot = ResourceLocation(SAOCore.MODID, "textures/slot.png")
    private val defaultEntities = ResourceLocation(SAOCore.MODID, "textures/sao/entities.png")
    private val defaultParticleLarge = ResourceLocation(SAOCore.MODID, "textures/sao/particlelarge.png")
    private val defaultStatusIcons = ResourceLocation(SAOCore.MODID, "textures/sao/status_icons/")
}
