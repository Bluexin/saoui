package com.tencao.saoui.events

import com.mojang.authlib.GameProfile
import com.mojang.brigadier.CommandDispatcher
import com.tencao.saoui.screens.CoreGUI
import com.tencao.saoui.screens.util.NotificationAlert
import com.tencao.saoui.screens.util.PopupYesNo
import com.tencao.saoui.util.Client
import com.tencao.saoui.util.IconCore
import com.tencao.saoui.util.localize
import dev.ftb.mods.ftbteams.FTBTeamsAPI
import dev.ftb.mods.ftbteams.data.ClientTeam
import dev.ftb.mods.ftbteams.data.TeamRank
import dev.ftb.mods.ftbteams.event.ClientTeamPropertiesChangedEvent
import dev.ftb.mods.ftbteams.event.TeamEvent
import net.minecraft.world.entity.player.Player

object PartyHandler {

    fun registerEvents(){
        TeamEvent.CLIENT_PROPERTIES_CHANGED.register(::partyChanged)
    }

    fun getPartyLeader(party: ClientTeam = FTBTeamsAPI.getClientManager().selfTeam): GameProfile {
        return Client.minecraft.connection!!.getPlayerInfo(
            party.members.firstOrNull {
                party.getHighestRank(it) == TeamRank.OWNER }
        )?.profile?: Client.player!!.gameProfile
    }

    fun partyChanged(event: ClientTeamPropertiesChangedEvent){
        val currentTeam = FTBTeamsAPI.getClientManager().selfTeam
        currentTeam.members.forEach { playerUUID ->
            val player = Client.minecraft.connection!!.getPlayerInfo(playerUUID)!!.profile
            if (currentTeam.getHighestRank(playerUUID) != event.team.getHighestRank(playerUUID)){
                if (currentTeam.getHighestRank(playerUUID) == TeamRank.INVITED){
                    when (event.team.getHighestRank(playerUUID)){
                        TeamRank.NONE -> partyInviteCancelled(player)
                        TeamRank.MEMBER -> partyJoin(player)
                        else -> {}
                    }
                }
                else if (currentTeam.getHighestRank(playerUUID) == TeamRank.MEMBER){
                    when (event.team.getHighestRank(playerUUID)){
                        TeamRank.NONE -> partyLeft(player)
                        TeamRank.OFFICER -> TODO()
                        TeamRank.OWNER -> partyLeaderChange(player)
                        else -> {}
                    }
                }
                else if (currentTeam.getHighestRank(playerUUID) == TeamRank.NONE){
                    when (event.team.getHighestRank(playerUUID)){
                        TeamRank.INVITED -> partyInvite(player, getPartyLeader(event.team), event.team)
                        TeamRank.ALLY -> TODO()
                        else -> {}
                    }
                }
            }
        }
        if (event.team.members.size == 1 && currentTeam.members.size > 1 || !event.team.members.contains(Client.player!!.uuid)){
            partyDisband()
        }
    }

    fun partyDisband() {
        NotificationAlert.new(IconCore.PARTY, "notificationPartyDisbandTitle".localize(), "")
    }

    fun partyInviteCancelled(player: GameProfile) {

        val notification = EventCore.notifications.firstOrNull { notification ->
            notification.text.any { line ->
                line.contains(player.name, true)
            }
        }
        if (notification != null) {
            EventCore.notifications.remove(notification)
            NotificationAlert.new(IconCore.PARTY, "notificationPartyInviteTimeoutTitle".localize(), "")
        } else {
            (EventCore.mc.screen as? CoreGUI<*>)?.getPopup?.text?.any { line ->
                line.contains(player.name, true)
            }?.run {
                (EventCore.mc.screen as? CoreGUI<*>)?.getPopup?.onClose()
            }
        }
    }

    fun partyJoin(player: GameProfile) {
        if (player == Client.player!!.gameProfile) {
            NotificationAlert.new(
                IconCore.PARTY, "notificationPartyJoinedTitle".localize(), "notificationPartyJoinedShortText".localize(
                getPartyLeader().name))
        } else {
            NotificationAlert.new(IconCore.PARTY, "notificationPartyAddedTitle".localize(), "notificationPartyAddedShortText".localize(player.name))
        }
    }

    fun partyLeft(player: GameProfile) {
        if (player == Client.player!!.gameProfile) {
            NotificationAlert.new(
                IconCore.PARTY, "notificationPartyLeftTitle".localize(), "notificationPartyLeftShortText".localize(
                getPartyLeader().name
            ))
        } else {
            NotificationAlert.new(IconCore.PARTY, "notificationPartyLeaveTitle".localize(), "notificationPartyLeaveShortText".localize(player.name))
        }
    }

    fun partyLeaderChange(player: GameProfile) {
        if (player == Client.player!!.gameProfile) {
            NotificationAlert.new(IconCore.PARTY, "notificationPartyLeaderTitle".localize(), "notificationPartyLeaderShortText".localize())
        } else  {
            NotificationAlert.new(IconCore.PARTY, "notificationPartyNewLeaderTitle".localize(), "notificationPartyNewLeaderShortText".localize(player.name))
        }
    }

    fun partyInvite(player: GameProfile, leader: GameProfile, party: ClientTeam) {
        if (player == Client.player!!.gameProfile) {
            val builder = StringBuilder()
            val members = party.members.filter { it != leader.id}.mapNotNull { Client.minecraft.connection?.getPlayerInfo(it)?.profile }
            builder.append("${"guiPartyInviteText".localize(leader.name)}\n\n")
            builder.append("Members: ${members.first().name}\n")

            members.filter { it != members.first() }.forEach { builder.append("            ${it.name}") }
            val partyNotification = PopupYesNo("guiPartyInviteTitle".localize(), builder.lines(), "")
            partyNotification.plusAssign {
                when (it) {
                    PopupYesNo.Result.YES -> {
                        CommandDispatcher<Player>().execute("/ftbteams join ${leader.name}", Client.player)
                    }
                    PopupYesNo.Result.NO -> {
                        CommandDispatcher<Player>().execute("/ftbteams deny_invite", Client.player)
                    }
                }
            }
            if (EventCore.mc.screen is CoreGUI<*>) {
                (EventCore.mc.screen as CoreGUI<*>).openGui(
                    partyNotification
                )
            } else {
                EventCore.notifications.add(partyNotification)
                NotificationAlert.new(IconCore.PARTY, "notificationPartyInviteTitle".localize(), "notificationPartyInviteShortText".localize(leader.name))
            }
        }
    }

}