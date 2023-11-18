/*
 * Copyright (C) 2020-2021 Tencao
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

package com.tencao.saoui

import com.tencao.saoui.Constants.LOGGER
import com.tencao.saoui.config.ConfigHandler
import com.tencao.saoui.effects.particles.ModParticles
import com.tencao.saoui.events.EventCore
import com.tencao.saoui.screens.CoreGUI
import com.tencao.saoui.themes.ThemeManager
import com.tencao.saoui.util.Client
import me.shedaniel.architectury.event.events.client.ClientLifecycleEvent
import me.shedaniel.architectury.registry.Registries
import me.shedaniel.architectury.registry.Registry
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager
import net.minecraftforge.api.ModLoadingContext
import net.minecraftforge.api.fml.event.config.ModConfigEvent
import net.minecraftforge.fml.config.ModConfig
import org.apache.logging.log4j.Level

object SAOCore {

    var isSAOMCLibServerSide = false

    fun setup(){
        ClientLifecycleEvent.CLIENT_STARTED.register{ModParticles.registerParticles()}
        ModConfigEvent.LOADING.register(ConfigHandler::setupConfig)
        LOGGER.log(Level.INFO, "Initializing client...")
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(ThemeManager)
        //(Client.resourceManager as SimpleReloadableResourceManager).registerReloadListener(ThemeManager)
        ModParticles.registerParticles()
        EventCore.registerEvents()
        loadComplete()
    }

    fun loadComplete() {
        CoreGUI.animator
    }
}
