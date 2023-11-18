package com.tencao.saoui.api.elements

import com.mojang.authlib.GameProfile
import com.mojang.brigadier.CommandDispatcher
import com.tencao.saoui.util.Client
import com.tencao.saoui.util.IconCore
import com.tencao.saoui.util.PlayerIcon
import com.tencao.saoui.util.translate
import dev.ftb.mods.ftbteams.FTBTeamsAPI
import dev.ftb.mods.ftbteams.data.ClientTeam
import dev.ftb.mods.ftbteams.data.FTBTUtils
import dev.ftb.mods.ftbteams.event.ClientTeamPropertiesChangedEvent
import dev.ftb.mods.ftbteams.event.TeamEvent
import net.minecraft.world.entity.player.Player


class PartyElement : IconLabelElement(IconCore.PARTY, "sao.element.party".translate()) {

    val player: Player
        get() = Client.player!!

    // val partyElements = mutableListOf<PTMemberElement>()
    var party : ClientTeam = FTBTeamsAPI.getClientManager().selfTeam
    val invited
        get() = FTBTeamsAPI.getClientManager().teamMap.filter { it.value.isInvited(Client.player!!.uuid) }.toMap()

    /**
     * Override the default element sequence to merge our party elements while keeping them separate.
     */
    // override val elementsSequence: Sequence<NeoElement> = elements.asSequence().filter(NeoElement::listed).plus(partyElements.asSequence().filter(NeoElement::listed))
    // override val otherElementsSequence: Sequence<NeoElement> = elements.asSequence().filter { !it.listed }.plus(partyElements.asSequence().filter { !it.listed })

    init {
        registerEvent()
        resetPartyData()
    }

    fun registerEvent(){
        TeamEvent.CLIENT_PROPERTIES_CHANGED.register(::teamChanged)
    }

    fun teamChanged(event: ClientTeamPropertiesChangedEvent){
        party = event.team
        resetPartyData()
    }

    fun resetPartyData() {
        // Only remove invalid entries, this will prevent menus
        // abruptly closing during refresh.
        if (this.selected) {
            (this.parent as CategoryButton).close(true)
            (this.parent as CategoryButton).open(true)
        }
        elements.clear()
        partyExtras()
        elements.removeIf {
            it is PTMemberElement && !party.isMember(it.player.id) && !party.isInvited(it.player.id)
        }
        // elements[0].valid = !(partyCap.partyData?.isLeader(Minecraft().player) == false)
        // Don't bother with updating party functions if single player.
        if (1 < (Client.minecraft.singleplayerServer?.playerCount ?: 2)) {
            partyList()
        }
        if (!this.selected) elementsSequence.forEach { it.hide() }
    }

    fun partyList() {
        party.members.filter {  it != Client.player!!.uuid && !party.isInvited(it) }
            .map { Client.minecraft.connection?.getPlayerInfo(it)?.profile ?: FTBTUtils.NO_PROFILE }
            .forEach { ptmember ->
                if (elements.none { it is PTMemberElement && it != ptmember }) {
                    +partyMemberButton(ptmember)
                }
            }

        party.members.filter { it != Client.player!!.uuid && party.isInvited(it) }
            .map { Client.minecraft.connection?.getPlayerInfo(it)?.profile ?: FTBTUtils.NO_PROFILE }
            .forEach { ptmember ->
                if (ptmember != FTBTUtils.NO_PROFILE) {
                    if (elements.none { it is PTMemberElement && it != ptmember }) {
                        +partyMemberButton(ptmember, true)
                    }
                }
            }

    }

    fun partyMemberButton(player: GameProfile, invited: Boolean = false): CategoryButton =
        CategoryButton(PTMemberElement(player), this.tlParent) {
            +CategoryButton(IconLabelElement(IconCore.HELP, "sao.element.inspect".translate()), this.tlParent)
            if (party.isOfficer(Client.player!!.uuid)) {
                +CategoryButton(IconLabelElement(IconCore.CANCEL, "sao.party.${if (invited) "cancel" else "kick"}".translate()), this.tlParent).onClick { _, _ ->
                    if (invited) {
                        CommandDispatcher<Player>().execute("/ftbteams kick ${player.name}", Client.player)
                        true
                    } else {
                        CommandDispatcher<Player>().execute("/ftbteams kick ${player.name}", Client.player)
                        true
                    }
                }
            }
        }

    fun partyExtras() {
        if ((party.isOfficer(player.uuid)) || party.isFreeToJoin) +CategoryButton(IconLabelElement(IconCore.INVITE, "sao.party.invite".translate()), this.tlParent) {
            Client.minecraft.connection?.onlinePlayers?.filter { it.profile.id != player.uuid }?.forEach {
                +CategoryButton(IconLabelElement(PlayerIcon(it.profile), it.profile.name), this.tlParent).onClick { _, _ ->
                    CommandDispatcher<Player>().execute("/ftbteams invite ${it.profile.name}", Client.player)
                    true
                }
            }
        }
        if (party.members.size > 1) +CategoryButton(IconLabelElement(IconCore.CANCEL, "sao.party.leave".translate()), this.tlParent).onClick { _, _ ->
            CommandDispatcher<Player>().execute("/ftbteams leave", Client.player)
            true
        }

        invited.forEach {
            val leader = Client.minecraft.connection?.getPlayerInfo(it.key)?.profile?: FTBTUtils.NO_PROFILE
            +CategoryButton(IconLabelElement(PlayerIcon(leader), "sao.party.invited".translate(leader.name)), this.tlParent) {
                +CategoryButton(IconLabelElement(IconCore.CONFIRM, "sao.misc.accept".translate()), this.tlParent).onClick { _, _ ->
                    CommandDispatcher<Player>().execute("/ftbteams join ${it.value.name}", Client.player)
                    true
                }
                +CategoryButton(IconLabelElement(IconCore.CANCEL, "sao.misc.decline".translate()), this.tlParent).onClick { _, _ ->
                    CommandDispatcher<Player>().execute("/ftbteams deny_invite", Client.player)
                    true
                }
            }
        }

    }
}
