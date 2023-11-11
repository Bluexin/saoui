package com.tencao.saoui.api.elements

import com.tencao.saomclib.Client
import com.tencao.saomclib.capabilities.getPartyCapability
import com.tencao.saomclib.events.PartyEvent
import com.tencao.saomclib.packets.PartyType
import com.tencao.saomclib.packets.Type
import com.tencao.saomclib.packets.toServer.updateServer
import com.tencao.saomclib.party.IPartyData
import com.tencao.saomclib.party.PlayerInfo
import com.tencao.saomclib.party.playerInfo
import com.tencao.saoui.util.IconCore
import com.tencao.saoui.util.PlayerIcon
import com.tencao.saoui.util.compat.Player
import net.minecraft.client.resources.I18n
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent

class PartyElement : IconLabelElement(IconCore.PARTY, I18n.get("sao.element.party")) {

    val player: Player
        get() = Client.player!!

    // val partyElements = mutableListOf<PTMemberElement>()
    val partyCap by lazy { player.getPartyCapability() }

    /**
     * Override the default element sequence to merge our party elements while keeping them separate.
     */
    // override val elementsSequence: Sequence<NeoElement> = elements.asSequence().filter(NeoElement::listed).plus(partyElements.asSequence().filter(NeoElement::listed))
    // override val otherElementsSequence: Sequence<NeoElement> = elements.asSequence().filter { !it.listed }.plus(partyElements.asSequence().filter { !it.listed })

    init {
        MinecraftForge.EVENT_BUS.register(this)
        resetPartyData()
    }

    @SubscribeEvent
    fun partyRefresh(e: PartyEvent.Refresh) {
        resetPartyData()
    }

    private fun resetPartyData() {
        // Only remove invalid entries, this will prevent menus
        // abruptly closing during refresh.
        if (this.selected) {
            (this.parent as CategoryButton).close(true)
            (this.parent as CategoryButton).open(true)
        }
        elements.clear()
        partyExtras()
        elements.removeIf {
            it is PTMemberElement && partyCap?.partyData?.isMember(it.player) != true &&
                partyCap?.partyData?.isInvited(it.player) != true
        }
        // elements[0].valid = !(partyCap.partyData?.isLeader(Minecraft().player) == false)
        // Don't bother with updating party functions if single player.
        if (1 < (Client.minecraft.singleplayerServer?.playerCount ?: 2)) {
            partyList()
        }
        if (!this.selected) elementsSequence.forEach { it.hide() }
    }

    fun partyList() {
        val party = partyCap?.partyData

        if (party != null) {
            party.getMembers().filter { !it.equals(player) }.forEach { ptmember ->
                if (elements.none { it is PTMemberElement && !it.equals(ptmember) }) {
                    +partyMemberButton(party, ptmember, player)
                }
            }

            party.getInvited().forEach { ptmember ->
                if (elements.none { it is PTMemberElement && !it.equals(ptmember) }) {
                    +partyMemberButton(party, ptmember, player, true)
                }
            }
        }
    }

    fun partyMemberButton(party: IPartyData, player: PlayerInfo, ourPlayer: Player, invited: Boolean = false): CategoryButton =
        CategoryButton(PTMemberElement(player), this.tlParent) {
            +CategoryButton(IconLabelElement(IconCore.HELP, I18n.get("sao.element.inspect")), this.tlParent)
            if (party.leaderInfo.equals(ourPlayer)) {
                +CategoryButton(IconLabelElement(IconCore.CANCEL, I18n.get("sao.party.${if (invited) "cancel" else "kick"}")), this.tlParent).onClick { _, _ ->
                    if (invited) {
                        Type.CANCELINVITE.updateServer(player, PartyType.MAIN)
                        true
                    } else {
                        Type.KICK.updateServer(player, PartyType.MAIN)
                        true
                    }
                }
            }
        }

    fun partyExtras() {
        val party = partyCap?.partyData

        if ((party != null && party.isLeader(player)) || party == null) +CategoryButton(IconLabelElement(IconCore.INVITE, I18n.get("sao.party.invite")), this.tlParent) {
            Client.minecraft.connection?.onlinePlayers?.filter { it.profile.id != player.uuid }?.forEach {
                +CategoryButton(IconLabelElement(PlayerIcon(PlayerInfo(it.profile.id, it.profile.name)), it.profile.name), this.tlParent).onClick { _, _ ->
                    Type.INVITE.updateServer(PlayerInfo(it.profile.id, it.profile.name), PartyType.MAIN)
                    true
                }
            }
        }
        if (party != null && party.size > 1) +CategoryButton(IconLabelElement(IconCore.CANCEL, I18n.get("sao.party.leave")), this.tlParent).onClick { _, _ ->
            Type.LEAVE.updateServer(player.playerInfo(), PartyType.MAIN)
            true
        }

        partyCap?.inviteData?.forEach { inviteParty ->
            +CategoryButton(IconLabelElement(PlayerIcon(inviteParty.leaderInfo), I18n.get("sao.party.invited", inviteParty.leaderInfo.username)), this.tlParent) {
                +CategoryButton(IconLabelElement(IconCore.CONFIRM, I18n.get("sao.misc.accept")), this.tlParent).onClick { _, _ ->
                    Type.ACCEPTINVITE.updateServer(player.playerInfo(), PartyType.INVITE)
                    true
                }
                +CategoryButton(IconLabelElement(IconCore.CANCEL, I18n.get("sao.misc.decline")), this.tlParent).onClick { _, _ ->
                    Type.CANCELINVITE.updateServer(player.playerInfo(), PartyType.INVITE)
                    true
                }
            }
        }
    }
}
