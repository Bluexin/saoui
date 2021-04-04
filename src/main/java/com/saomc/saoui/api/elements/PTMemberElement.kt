package com.saomc.saoui.api.elements

import be.bluexin.saomclib.capabilities.getPartyCapability
import be.bluexin.saomclib.party.PlayerInfo
import com.saomc.saoui.util.PlayerIcon
import com.teamwizardry.librarianlib.features.kotlin.Client
import net.minecraft.client.resources.I18n

class PTMemberElement(val player: PlayerInfo): IconLabelElement(PlayerIcon(player), if (party?.isInvited(player) == true) I18n.format("sao.party.player_invited", player.username) else player.username) {

    val invited = party?.isInvited(player)?: false

    override fun update() {
        super.update()
        if ((invited && party?.isInvited(player) != true) || party?.isMember(player) != true)
            (parent as? CategoryButton)?.elements?.remove(this)
    }

    companion object{
        val party = Client.minecraft.player.getPartyCapability().partyData
        val invitedParty = Client.minecraft.player.getPartyCapability().inviteData
    }
}