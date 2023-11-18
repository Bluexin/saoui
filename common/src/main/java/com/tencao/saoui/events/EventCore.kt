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

import com.mojang.blaze3d.vertex.PoseStack
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.effects.RenderDispatcher
import com.tencao.saoui.events.RenderHandler.guiInstance
import com.tencao.saoui.renders.StaticRenderer
import com.tencao.saoui.screens.CoreGUI
import com.tencao.saoui.screens.ingame.IngameGUI
import com.tencao.saoui.screens.util.HealthStep
import com.tencao.saoui.screens.util.Popup
import com.tencao.saoui.util.Client
import com.tencao.saoui.util.ClientUtil
import me.shedaniel.architectury.event.events.EntityEvent
import me.shedaniel.architectury.event.events.GuiEvent
import me.shedaniel.architectury.event.events.client.ClientPlayerEvent
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

/**
 * This is the core for all event handlers, listening to events then passing on to the other events that need it.
 */
object EventCore {

    val notifications: MutableList<Popup<*>> = mutableListOf()

    fun registerEvents(){
        GuiEvent.SET_SCREEN.register(::guiInstance)
        GuiEvent.RENDER_HUD.register(::clientRenderEvent)
        WorldRenderEvents.AFTER_ENTITIES.register(::renderEntityListener)
        EntityEvent.LIVING_DEATH.register(::onDeath)
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(::onWorldLoad)
        if (ClientUtil.isFTBTeamsLoaded) PartyHandler.registerEvents()
    }

    /*
    @SubscribeEvent(receiveCanceled = true)
    fun playerTickListener(e: TickEvent.PlayerTickEvent) {
        if (CraftingUtil.craftReady) {
            CraftingUtil.getCraft()
        }
    }*/
    fun onDeath(entity: LivingEntity, source: DamageSource): InteractionResult {
        if (entity.level.isClientSide && entity.isDeadOrDying) {
            RenderHandler.addDeadMob(entity)
        }
        return InteractionResult.SUCCESS
    }

    fun renderEntityListener(context: WorldRenderContext) {
        if (!OptionCore.UI_ONLY.isEnabled) {
            context.gameRenderer().resetData()
            StaticRenderer.render(context.matrixStack())
        }
        RenderHandler.deathHandlers()
        RenderDispatcher.dispatch()
        HealthStep.updateHealthSmooth(context.tickDelta())
    }

    fun clientRenderEvent(poseStack: PoseStack, partialTicks: Float) {
        val screen = Client.minecraft.screen
        if (screen is CoreGUI<*>) {
            if (notifications.isNotEmpty()) {
                if (screen.subGui == null) {
                    screen.openGui(notifications.first())
                    notifications.removeAt(0)
                }
            }
        }
    }


    fun onWorldLoad(player: Player) {
        Client.minecraft.gui = IngameGUI(mc)
    }

    internal val mc
        get() = Client.minecraft

}
