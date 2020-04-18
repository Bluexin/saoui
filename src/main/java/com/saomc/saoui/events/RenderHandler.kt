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

package com.saomc.saoui.events

import com.saomc.saoui.SoundCore
import com.saomc.saoui.capabilities.getRenderData
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.playAtEntity
import com.saomc.saoui.renders.StaticRenderer
import com.saomc.saoui.screens.CoreGUI
import com.saomc.saoui.screens.DeathGui
import com.saomc.saoui.screens.menus.IngameMenu
import net.minecraft.client.gui.GuiGameOver
import net.minecraft.client.gui.GuiIngameMenu
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.inventory.GuiContainerCreative
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.client.event.RenderLivingEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

@SideOnly(Side.CLIENT)
internal object RenderHandler {

    private val deadHandlers = ArrayList<EntityLivingBase>()
    private var menuGUI = true

    fun deathHandlers() {
        deadHandlers.forEach { ent ->
            val deadStart = ent.deathTime == 1
            val deadExactly = ent.deathTime >= 18
            if (deadStart) {
                ent.deathTime++
                SoundCore.PARTICLES_DEATH.playAtEntity(ent)
            }

            if (deadExactly) {
                StaticRenderer.doSpawnDeathParticles(EventCore.mc, ent)
            }
        }
        deadHandlers.removeIf { ent -> ent.deathTime >= 18 }
    }

    fun addDeadMob(ent: EntityLivingBase) {
        deadHandlers.add(ent)
    }

    fun guiInstance(e: GuiOpenEvent) {
        if (e.gui is GuiIngameMenu) {
                if (EventCore.mc.currentScreen !is CoreGUI<*>) e.gui = IngameMenu()
        }
        else if (e.gui is GuiInventory && !OptionCore.DEFAULT_INVENTORY.isEnabled) {
            when {
                EventCore.mc.playerController.isInCreativeMode -> e.gui = GuiContainerCreative(EventCore.mc.player)
                else -> e.isCanceled = true
            }
        }
        else if (e.gui is GuiGameOver && !OptionCore.DEFAULT_DEATH_SCREEN.isEnabled) {
            if (EventCore.mc.currentScreen !is DeathGui) e.gui = DeathGui()
            else {
                e.isCanceled = true
            }
        }
    }


    fun renderEntity(e: RenderLivingEvent.Post<*>) {
        if (!OptionCore.UI_ONLY.isEnabled) {
            e.entity.getRenderData()?.update(e.partialRenderTick)
        }
    }

    fun mainMenuGUI(e: GuiOpenEvent) {
        if (menuGUI)
            if (e.gui is GuiMainMenu) {}
            //e.setGui(new MainMenuGUI());
    }

}
