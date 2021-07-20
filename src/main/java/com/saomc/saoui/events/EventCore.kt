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

import com.saomc.saoui.capabilities.getRenderData
import com.saomc.saoui.effects.RenderDispatcher
import com.saomc.saoui.renders.StaticRenderer
import com.saomc.saoui.screens.CoreGUI
import com.saomc.saoui.screens.ingame.IngameGUI
import com.saomc.saoui.screens.menus.IngameMenu
import com.saomc.saoui.screens.util.NotificationAlert
import com.saomc.saoui.screens.util.Popup
import com.saomc.saoui.screens.util.PopupYesNo
import com.saomc.saoui.util.IconCore
import com.saomc.saoui.util.localize
import com.saomc.saoui.util.setIngameGUI
import com.teamwizardry.librarianlib.core.util.Client
import com.tencao.saomclib.events.PartyEvent
import com.tencao.saomclib.packets.PartyType
import com.tencao.saomclib.packets.Type
import com.tencao.saomclib.packets.to_server.updateServer
import com.tencao.saomclib.party.PlayerInfo
import net.minecraft.client.entity.player.ClientPlayerEntity
import net.minecraftforge.client.event.*
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import kotlin.math.pow

/**
 * This is the core for all event handlers, listening to events then passing on to the other events that need it.
 */
object EventCore {

    val notifications: MutableList<Popup<*>> = mutableListOf()

    /*
    @SubscribeEvent(receiveCanceled = true)
    fun playerTickListener(e: TickEvent.PlayerTickEvent) {
        if (CraftingUtil.craftReady) {
            CraftingUtil.getCraft()
        }
    }*/

    @SubscribeEvent
    fun clientTickListener(e: TickEvent.ClientTickEvent) {
        EventHandler.abilityCheck()
    }

    @SubscribeEvent
    fun renderTickListener(e: TickEvent.RenderTickEvent) {
        RenderHandler.deathHandlers()
        Client.player?.getRenderData()?.update(e.renderTickTime)
    }

    @SubscribeEvent
    fun onDeath(e: LivingDeathEvent) {
        if (e.entityLiving != null && e.entityLiving.world.isRemote)
            RenderHandler.addDeadMob(e.entityLiving)
    }

    @SubscribeEvent
    fun renderEntityListener(e: RenderLivingEvent.Post<*, *>) {
        RenderHandler.renderEntity(e)
    }

    @SubscribeEvent
    fun renderEntityListener(e: RenderLivingEvent.Pre<*, *>) {
        if (e.entity.getDistanceSq(mc.player as ClientPlayerEntity) > (mc.gameSettings.renderDistanceChunks * 16).toDouble().pow(2.0))
            e.isCanceled = true
    }

    @SubscribeEvent
    fun renderWorldListener(event: RenderWorldLastEvent) {
        RenderDispatcher.dispatch()
        if (mc.player != null) StaticRenderer.render()
    }

    @SubscribeEvent
    fun guiOpenListener(e: GuiOpenEvent) {
        RenderHandler.guiInstance(e)
        RenderHandler.mainMenuGUI(e)
    }

    @SubscribeEvent
    fun partyDisband(e: PartyEvent.Disbanded){
        NotificationAlert.new(IconCore.PARTY, "notificationPartyDisbandTitle".localize(), "")
    }

    @SubscribeEvent
    fun partyInviteCancelled(e: PartyEvent.InviteCanceled){
        val notification = notifications.firstOrNull { notification ->
            notification.text.any { line ->
                line.contains(e.partyData.leaderInfo.username, true)
            }
        }
        if (notification != null) {
            notifications.remove(notification)
            NotificationAlert.new(IconCore.PARTY, "notificationPartyInviteTimeoutTitle".localize(), "")
        }
        else {
            (mc.currentScreen as? CoreGUI<*>)?.getPopup?.text?.any { line ->
                line.contains(e.partyData.leaderInfo.username, true)
            }?.run {
                (mc.currentScreen as? CoreGUI<*>)?.getPopup?.onClose()
            }
        }
    }

    @SubscribeEvent
    fun partyJoin(e: PartyEvent.Join){
        if (e.player == player){
            NotificationAlert.new(IconCore.PARTY, "notificationPartyJoinedTitle".localize(), "notificationPartyJoinedShortText".localize(e.partyData.leaderInfo.username))
        }
        else if (e.partyData.isMember(player))
            NotificationAlert.new(IconCore.PARTY, "notificationPartyAddedTitle".localize(), "notificationPartyAddedShortText".localize(e.player.username))
    }

    @SubscribeEvent
    fun partyLeft(e: PartyEvent.Leave){
        if (e.player == player){
            NotificationAlert.new(IconCore.PARTY, "notificationPartyLeftTitle".localize(), "notificationPartyLeftShortText".localize(e.partyData.leaderInfo.username))
        }
        else if (e.partyData.isMember(player))
            NotificationAlert.new(IconCore.PARTY, "notificationPartyLeaveTitle".localize(), "notificationPartyLeaveShortText".localize(e.player.username))
    }

    @SubscribeEvent
    fun partyLeaderChange(e: PartyEvent.LeaderChanged){
        if (e.newLeader == player){
            NotificationAlert.new(IconCore.PARTY, "notificationPartyLeaderTitle".localize(), "notificationPartyLeaderShortText".localize())
        }
        else if (e.partyData.isMember(player))
            NotificationAlert.new(IconCore.PARTY, "notificationPartyNewLeaderTitle".localize(), "notificationPartyNewLeaderShortText".localize(e.newLeader.username))
    }

    @SubscribeEvent
    fun partyInvite(e: PartyEvent.Invited) {
        val p = e.partyData
        if (e.player == player){
            val builder = StringBuilder()
            builder.append("${"guiPartyInviteText".localize(p.leaderInfo.username)}\n\n")
            builder.append("Members: ${p.getMembers().first().username}\n")
            p.getMembers().filter { it != p.getMembers().first() }.forEach { builder.append("            ${it.username}") }
            val partyNotification = PopupYesNo("guiPartyInviteTitle".localize(), builder.lines(), "")
            partyNotification.plusAssign {
                when (it) {
                    PopupYesNo.Result.YES -> {
                        Type.ACCEPTINVITE.updateServer(player, PartyType.INVITE)
                    }
                    PopupYesNo.Result.NO -> {
                        Type.CANCELINVITE.updateServer(player, PartyType.INVITE)
                    }
                }
            }
            if (mc.currentScreen is CoreGUI<*>)
                (mc.currentScreen as CoreGUI<*>).openGui(
                        partyNotification
                )
            else {
                notifications.add(partyNotification)
                NotificationAlert.new(IconCore.PARTY, "notificationPartyInviteTitle".localize(), "notificationPartyInviteShortText".localize(p.leaderInfo.username))
            }
        }
    }

    @SubscribeEvent
    fun clientRenderEvent(e: GuiScreenEvent.DrawScreenEvent){
        if (e.gui is CoreGUI<*>)
            if (notifications.isNotEmpty()){
                if ((e.gui as CoreGUI<*>).subGui == null){
                    (e.gui as CoreGUI<*>).openGui(notifications.first())
                    notifications.removeAt(0)
                }
            }
    }

    @SubscribeEvent
    fun chatEvent(e: ClientChatReceivedEvent){
        //TODO chat system
    }

    @SubscribeEvent
    fun onWorldLoad(e: ClientPlayerNetworkEvent.LoggedInEvent){
        mc.setIngameGUI(IngameGUI(mc))
    }

    @SubscribeEvent
    fun itemPickupEvent(e: PlayerEvent.ItemPickupEvent){
        inventoryUpdate()
    }

    @SubscribeEvent
    fun itemCraftedEvent(e: PlayerEvent.ItemCraftedEvent){
        inventoryUpdate()
    }

    @SubscribeEvent
    fun itemSmeltedEvent(e: PlayerEvent.ItemSmeltedEvent){
        inventoryUpdate()
    }

    fun inventoryUpdate(){
        //TODO Fix me
    }

    @SubscribeEvent
    fun onDisconnect(e: ClientPlayerNetworkEvent.LoggedOutEvent){
        IngameMenu.hasChecked = false
    }

    internal val mc = Client.minecraft

    // Safe reference to player
    internal val player = PlayerInfo(mc.session.profile)
}
