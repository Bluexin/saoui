package com.tencao.saoui.util

import com.tencao.saoui.config.ConfigHandler
import dev.ftb.mods.ftbteams.FTBTeamsAPI
import net.minecraft.client.multiplayer.PlayerInfo

object PartyUtil {

    fun getPartyInfo(): List<PlayerInfo> = FTBTeamsAPI.getClientManager().selfTeam.members.mapNotNull { Client.minecraft.connection?.getPlayerInfo(it) }


    fun getFakePartyInfo(): List<PlayerInfo> {
        val members: MutableList<PlayerInfo> = mutableListOf()
        for (i in 0 until ConfigHandler.debugFakePT) members.add(Client.minecraft.connection!!.getPlayerInfo(Client.player!!.uuid)!!)
        return members
    }

}