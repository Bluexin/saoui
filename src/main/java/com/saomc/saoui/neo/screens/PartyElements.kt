package com.saomc.saoui.neo.screens

import be.bluexin.saomclib.capabilities.getPartyCapability
import be.bluexin.saomclib.events.PartyEvent
import be.bluexin.saomclib.party.IParty
import com.saomc.saoui.api.elements.neo.NeoCategoryButton
import com.saomc.saoui.api.elements.neo.NeoIconLabelElement
import com.saomc.saoui.util.IconCore
import net.minecraft.client.resources.I18n.format
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.FakePlayer
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

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

    MinecraftForge.EVENT_BUS.register(object {
        @SubscribeEvent
        fun onInvited(invitation: PartyEvent.Invited) {
            MinecraftForge.EVENT_BUS.unregister(this)
            performLater {
                this@partyList.init()
            }
        }

        @SubscribeEvent
        fun onCanceled(invitation: PartyEvent.InviteCanceled) {
            MinecraftForge.EVENT_BUS.unregister(this)
            performLater {
                this@partyList.init()
            }
        }
    })
}

@NeoGuiDsl
fun NeoCategoryButton.partyExtras(player: EntityPlayer) {
    val partyCapability = player.getPartyCapability()
    val party = partyCapability.getOrCreatePT()

    category(IconCore.PARTY, format("sao.party.invite")) {
        player.world.playerEntities.asSequence().filter { it != player && it !is FakePlayer && !party.isMember(it) }.forEach { player ->
            val b = NeoIconLabelElement(IconCore.INVITE, player.displayNameString)
            b.onClick { _, _ ->
                party.invite(player)
                true
            }
            +b
        }
    }
    if (partyCapability.invitedTo != null) {
        category(IconCore.PARTY, format("sao.party.invited", partyCapability.invitedTo?.leader)) {
            val accept = NeoIconLabelElement(IconCore.CONFIRM, format("sao.accept"))
            accept.onClick { _, _ ->
                partyCapability.invitedTo?.acceptInvite(player)
                true
            }
            +accept
            val decline = NeoIconLabelElement(IconCore.CANCEL, format("sao.decline"))
            decline.onClick { _, _ ->
                partyCapability.invitedTo?.cancel(player)
                true
            }
            +decline
        }
    }
}

@NeoGuiDsl
fun NeoCategoryButton.partyMemberButton(party: IParty, player: EntityPlayer, ourPlayer: EntityPlayer, invited: Boolean = false): NeoCategoryButton =
        NeoCategoryButton(NeoIconLabelElement(IconCore.FRIEND, player.displayNameString), this, {
            +NeoIconLabelElement(IconCore.HELP, format("sao."))
            if (party.leader == ourPlayer) {
                val kickButton = NeoIconLabelElement(IconCore.CANCEL, format("sao.party.kick"))
                kickButton.onClick { _, _ ->
                    if (invited) party.removeMember(player)
                    else party.cancel(player)
                }
                +kickButton
            }
        })
