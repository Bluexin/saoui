package com.tencao.saoui.api.elements

import com.mojang.authlib.GameProfile
import com.tencao.saoui.util.Client
import com.tencao.saoui.util.PlayerIcon
import com.tencao.saoui.util.toTextComponent
import com.tencao.saoui.util.translate
import dev.ftb.mods.ftbteams.data.ClientTeamManager

class PTMemberElement(val player: GameProfile) : IconLabelElement(PlayerIcon(player), if (party?.isInvited(player.id) == true) "sao.party.player_invited".translate(player.name) else player.name.toTextComponent()) {

    val invited = party?.isInvited(player.id) ?: false

    override fun update() {
        super.update()
        if ((invited && party?.isInvited(player.id) != true) || party?.isMember(player.id) != true) {
            (parent as? CategoryButton)?.elements?.remove(this)
        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other is GameProfile){
            player == other
        } else super.equals(other)
    }

    override fun hashCode(): Int {
        var result = player.hashCode()
        result = 31 * result + invited.hashCode()
        return result
    }

    companion object {
        val party = ClientTeamManager.INSTANCE.getTeam(Client.player?.uuid)
    }
}
