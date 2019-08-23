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
import be.bluexin.saomclib.events.PartyEvent
import be.bluexin.saomclib.party.IParty
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
    val party = partyCapability.getOrCreatePT()

    party.members.filter { it != player }.forEach {
        +partyMemberButton(party, it, player)
    }

    party.invited.forEach {
        +partyMemberButton(party, it, player, true)
    }

    val ref = WeakReference(this)
    MinecraftForge.EVENT_BUS.register(object {
        @SubscribeEvent
        fun onPartyEvent(event: PartyEvent) {
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
    val party = partyCapability.getOrCreatePT()

    if (party.isLeader(player)) category(IconCore.PARTY, format("sao.party.invite")) {
        @Suppress("UNCHECKED_CAST")
        (player.world.playerEntities as List<EntityPlayer>).asSequence().filter { it != player && it !is FakePlayer && !party.isMember(it) }.forEach { player ->
            +NeoIconLabelElement(IconCore.INVITE, player.displayNameString).onClick { _, _ ->
                party.invite(player)
                true
            }
        }
    }
    if (party.isParty) +NeoIconLabelElement(IconCore.CANCEL, format("sao.party.leave")).onClick { _, _ ->
        party.removeMember(player)
    }
    if (partyCapability.invitedTo != null) {
        category(IconCore.PARTY, format("sao.party.invited", partyCapability.invitedTo?.leader?.displayNameString)) {
            +NeoIconLabelElement(IconCore.CONFIRM, format("sao.misc.accept")).onClick { _, _ ->
                partyCapability.invitedTo?.acceptInvite(player)
                true
            }
            +NeoIconLabelElement(IconCore.CANCEL, format("sao.misc.decline")).onClick { _, _ ->
                partyCapability.invitedTo?.cancel(player)
                true
            }
        }
    }
}

@NeoGuiDsl
fun NeoCategoryButton.partyMemberButton(party: IParty, player: EntityPlayer, ourPlayer: EntityPlayer, invited: Boolean = false): NeoCategoryButton =
        NeoCategoryButton(NeoIconLabelElement(IconCore.FRIEND, if (invited) format("sao.party.player_invited", player.displayNameString) else player.displayNameString), this) {
            +NeoIconLabelElement(IconCore.HELP, format("sao.element.inspect"))
            if (party.leader == ourPlayer) {
                +NeoIconLabelElement(IconCore.CANCEL, format("sao.party.${if (invited) "cancel" else "kick"}")).onClick { _, _ ->
                    if (invited) party.cancel(player)
                    else party.removeMember(player)
                }
            }
        }
