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
import com.tencao.saoui.renders.StaticRenderer
import com.tencao.saoui.screens.CoreGUI
import com.tencao.saoui.screens.menus.IngameMenu
import com.tencao.saoui.util.Client
import me.shedaniel.architectury.event.CompoundEventResult
import net.minecraft.client.gui.screens.PauseScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity

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

    fun guiInstance(screen: Screen): InteractionResultHolder<Screen> {
        if (screen is PauseScreen) {
            if (Client.minecraft.screen !is CoreGUI<*>) {
                return InteractionResultHolder.pass(IngameMenu())
            }
        }
        return InteractionResultHolder.pass(screen)
    }


    fun mainMenuGUI() {
        // if (menuGUI)
        // if (e.gui is GuiMainMenu) {}
        // e.setGui(new MainMenuGUI());
    }
}
