package com.tencao.saoui.api.elements

import com.tencao.saomclib.Client
import com.tencao.saomclib.capabilities.getPartyCapability
import com.tencao.saomclib.party.PlayerInfo
import com.tencao.saoui.util.PlayerIcon
import net.minecraft.client.resources.I18n

class PTMemberElement(val player: PlayerInfo) : IconLabelElement(PlayerIcon(player), if (party?.isInvited(player) == true) I18n.format("sao.party.player_invited", player.username) else player.username) {

    val invited = party?.isInvited(player) ?: false

    override fun update() {
        super.update()
        if ((invited && party?.isInvited(player) != true) || party?.isMember(player) != true) {
            (parent as? CategoryButton)?.elements?.remove(this)
        }
    }

    companion object {
        val party = Client.player?.getPartyCapability()?.partyData
        val invitedParty = Client.player?.getPartyCapability()?.inviteData
    }
}
