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

package com.saomc.saoui.neo.screens.util

import be.bluexin.saomclib.capabilities.getPartyCapability
import be.bluexin.saomclib.events.PartyEvent3
import be.bluexin.saomclib.packets.party.PartyType
import be.bluexin.saomclib.packets.party.Type
import be.bluexin.saomclib.packets.party.updateServer
import be.bluexin.saomclib.party.IPartyData
import be.bluexin.saomclib.party.PlayerInfo
import com.saomc.saoui.api.elements.neo.NeoCategoryButton
import com.saomc.saoui.api.elements.neo.NeoIconLabelElement
import com.saomc.saoui.neo.screens.NeoGuiDsl
import com.saomc.saoui.util.IconCore
import net.minecraft.client.resources.I18n.format
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.FakePlayer
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.lang.ref.WeakReference

@NeoGuiDsl
fun NeoCategoryButton.partyMenu(player: EntityPlayer) {
    category(IconCore.PARTY, format("sao.element.party")) {
        partyList(player)
        partyExtras(player)
    }
}

@NeoGuiDsl
fun NeoCategoryButton.partyList(player: EntityPlayer) {
    val partyCapability = player.getPartyCapability()
    val party = partyCapability.partyData

    if (party != null) {
        party.membersInfo.mapNotNull(PlayerInfo::player).filter { it != player }.forEach {
            +partyMemberButton(party, it, player)
        }

        party.invitedInfo.mapNotNull { it.key.player }.forEach {
            +partyMemberButton(party, it, player, true)
        }
    }

    val ref = WeakReference(this)
    MinecraftForge.EVENT_BUS.register(object {
        @SubscribeEvent
        fun onPartyEvent(event: PartyEvent3) {
            MinecraftForge.EVENT_BUS.unregister(this)
            ref.get()?.performLater {
                ref.get()?.reInit()
            }
        }
    })
}

@NeoGuiDsl
fun NeoCategoryButton.partyExtras(player: EntityPlayer) {
    val partyCapability = player.getPartyCapability()
    val party = partyCapability.partyData

    if ((party != null && party.isLeader(player)) || party == null) category(IconCore.PARTY, format("sao.party.invite")) {
        @Suppress("UNCHECKED_CAST")
        (player.world.playerEntities as List<EntityPlayer>).asSequence().filter { it != player && it !is FakePlayer && party?.isMember(it) != true }.forEach { player ->
            +NeoIconLabelElement(IconCore.INVITE, player.displayNameString).onClick { _, _ ->
                Type.INVITE.updateServer(player, PartyType.MAIN)
                true
            }
        }
    }
    if (party != null && party.isParty) +NeoIconLabelElement(IconCore.CANCEL, format("sao.party.leave")).onClick { _, _ ->
        Type.LEAVE.updateServer(player, PartyType.MAIN)
        true
    }

    val invitedTo = partyCapability.inviteData
    if (invitedTo != null) {
        category(IconCore.PARTY, format("sao.party.invited", invitedTo.leaderInfo.username)) {
            +NeoIconLabelElement(IconCore.CONFIRM, format("sao.misc.accept")).onClick { _, _ ->
                Type.ACCEPTINVITE.updateServer(player, PartyType.INVITE)
                true
            }
            +NeoIconLabelElement(IconCore.CANCEL, format("sao.misc.decline")).onClick { _, _ ->
                Type.CANCELINVITE.updateServer(player, PartyType.INVITE)
                true
            }
        }
    }
}

@NeoGuiDsl
fun NeoCategoryButton.partyMemberButton(party: IPartyData, player: EntityPlayer, ourPlayer: EntityPlayer, invited: Boolean = false): NeoCategoryButton =
        NeoCategoryButton(NeoIconLabelElement(IconCore.FRIEND, if (invited) format("sao.party.player_invited", player.displayNameString) else player.displayNameString), this) {
            +NeoIconLabelElement(IconCore.HELP, format("sao.element.inspect"))
            if (party.leaderInfo.equals(ourPlayer)) {
                +NeoIconLabelElement(IconCore.CANCEL, format("sao.party.${if (invited) "cancel" else "kick"}")).onClick { _, _ ->
                    if (invited) {
                        Type.CANCELINVITE.updateServer(player, PartyType.MAIN)
                        true
                    }
                    else {
                        Type.KICK.updateServer(player, PartyType.MAIN)
                        true
                    }
                }
            }
        }
