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

import be.bluexin.saomclib.events.PartyEvent3
import be.bluexin.saomclib.packets.party.PartyType
import be.bluexin.saomclib.packets.party.Type
import be.bluexin.saomclib.packets.party.updateServer
import com.saomc.saoui.effects.RenderDispatcher
import com.saomc.saoui.screens.ingame.IngameGUI
import com.saomc.saoui.screens.util.PopupYesNo
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.localize
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
import kotlin.math.pow

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
        if (e.entity.getDistanceSq(mc.player) > (mc.gameSettings.getOptionFloatValue(GameSettings.Options.RENDER_DISTANCE) * 16).toDouble().pow(2.0))
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
    fun partyInvite(e: PartyEvent3.Invited) {
        val p = e.partyData
        if (e.player.equals(Minecraft().player)) {
            PopupYesNo("guiPartyInviteTitle".localize(), "guiPartyInviteText".localize(p.leaderInfo.username), "") += {
                if (it == PopupYesNo.Result.YES) Type.ACCEPTINVITE.updateServer(Minecraft().player, PartyType.INVITE)
                else Type.CANCELINVITE.updateServer(Minecraft().player, PartyType.INVITE)
            }
        }
    }

    @SubscribeEvent
    fun onWorldLoad(e: FMLNetworkEvent.ClientConnectedToServerEvent){
        mc.ingameGUI = IngameGUI(mc)
    }

    internal val mc = Minecraft.getMinecraft()
}
