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

import be.bluexin.saomclib.events.PartyEvent
import be.bluexin.saomclib.packets.PTC2SPacket
import be.bluexin.saomclib.packets.PacketPipeline
import com.saomc.saoui.effects.RenderDispatcher
import com.saomc.saoui.neo.screens.PopupYesNo
import com.saomc.saoui.screens.ingame.IngameGUI
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.GameSettings
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.client.event.RenderLivingEvent
import net.minecraftforge.client.event.RenderPlayerEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent

/**
 * This is the core for all event handlers, listening to events then passing on to the other events that need it.
 */
object EventCore {

    @SubscribeEvent
    fun clientTickListener(e: TickEvent.ClientTickEvent) {
        EventHandler.abilityCheck()
    }

    @SubscribeEvent
    fun renderTickListener(e: TickEvent.RenderTickEvent) {
        RenderHandler.deathHandlers()
    }

    @SubscribeEvent
    fun onDeath(e: LivingDeathEvent) {
        if (e.entityLiving != null && e.entityLiving.world.isRemote)
            RenderHandler.addDeadMob(e.entityLiving)
    }

    @SubscribeEvent
    fun renderPlayerListener(e: RenderPlayerEvent.Post) {
        RenderHandler.renderPlayer(e)
    }

    @SubscribeEvent
    fun renderEntityListener(e: RenderLivingEvent.Post<*>) {
        RenderHandler.renderEntity(e)
    }

    @SubscribeEvent
    fun renderEntityListener(e: RenderLivingEvent.Pre<*>) {
        if (e.entity.getDistanceSq(mc.player) > Math.pow((mc.gameSettings.getOptionFloatValue(GameSettings.Options.RENDER_DISTANCE) * 16).toDouble(), 2.0))
            e.isCanceled = true
    }

    @SubscribeEvent
    fun renderWorldListener(event: RenderWorldLastEvent) {
        RenderDispatcher.dispatch()
    }

    @SubscribeEvent
    fun guiOpenListener(e: GuiOpenEvent) {
        RenderHandler.guiInstance(e)
        RenderHandler.mainMenuGUI(e)
    }

    @SubscribeEvent
    fun partyInvite(e: PartyEvent.Invited) {
        PopupYesNo("Party Invite", "Would you like to join ${e.party!!.leader}'s party?") += {
            if (it == PopupYesNo.Result.YES)
                PacketPipeline.sendToServer(PTC2SPacket(PTC2SPacket.Type.JOIN, e.player))
            else PacketPipeline.sendToServer(PTC2SPacket(PTC2SPacket.Type.CANCEL, e.player))
        }
        //if (e.party?.leader != mc.player) mc.ingameGUI.setRecordPlayingMessage(I18n.format("saoui.invited", e.party?.leader?.displayName))
    }

    @SubscribeEvent
    fun onWorldLoad(e: FMLNetworkEvent.ClientConnectedToServerEvent){
        mc.ingameGUI = IngameGUI(mc)
    }

    internal val mc = Minecraft.getMinecraft()
}
