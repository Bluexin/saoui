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

package com.tencao.saoui.events

import com.tencao.saoui.SoundCore
import com.tencao.saoui.capabilities.getRenderData
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.playAtEntity
import com.tencao.saoui.renders.StaticRenderer
import com.tencao.saoui.screens.CoreGUI
import com.tencao.saoui.screens.menus.IngameMenu
import net.minecraft.client.gui.screen.IngameMenuScreen
import net.minecraft.client.gui.screen.inventory.CreativeScreen
import net.minecraft.client.gui.screen.inventory.InventoryScreen
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.client.event.RenderLivingEvent

internal object RenderHandler {

    private val deadHandlers = ArrayList<LivingEntity>()
    private var menuGUI = true

    fun deathHandlers() {
        deadHandlers.removeIf { ent ->
            val deadStart = ent.deathTime == 1
            val deadExactly = ent.deathTime >= 18
            if (deadStart) {
                ent.deathTime++
                SoundCore.PARTICLES_DEATH.playAtEntity(ent)
            }

            if (deadExactly) {
                StaticRenderer.doSpawnDeathParticles(EventCore.mc, ent)
                return@removeIf true
            }
            false
        }
    }

    fun addDeadMob(ent: LivingEntity) {
        deadHandlers.add(ent)
    }

    fun guiInstance(e: GuiOpenEvent) {
        if (e.gui is IngameMenuScreen) {
            if (EventCore.mc.currentScreen !is CoreGUI<*>) {
                e.gui = IngameMenu()
            }
        } else if (e.gui is InventoryScreen && !OptionCore.DEFAULT_INVENTORY.isEnabled) {
            when {
                EventCore.mc.playerController!!.isInCreativeMode -> e.gui = CreativeScreen(EventCore.mc.player as PlayerEntity)
                else -> e.isCanceled = true
            }
        } /*else if (e.gui is DeathScreen && OptionCore.DEFAULT_DEATH_SCREEN.isEnabled) {
            if (EventCore.mc.currentScreen !is DeathGui) e.gui = DeathGui()
            else {
                e.isCanceled = true
            }
        }*/
    }

    fun renderEntity(e: RenderLivingEvent.Post<*, *>) {
        if (!OptionCore.UI_ONLY.isEnabled) {
            e.entity.getRenderData()?.update(e.partialRenderTick)
        }
    }

    fun mainMenuGUI(e: GuiOpenEvent) {
        // if (menuGUI)
        // if (e.gui is GuiMainMenu) {}
        // e.setGui(new MainMenuGUI());
    }
}
