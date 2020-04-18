package com.saomc.saoui.elements.controllers

import be.bluexin.saomclib.capabilities.getPartyCapability
import be.bluexin.saomclib.events.PartyEvent
import be.bluexin.saomclib.packets.party.PartyType
import be.bluexin.saomclib.packets.party.Type
import be.bluexin.saomclib.packets.party.updateServer
import be.bluexin.saomclib.party.IPartyData
import be.bluexin.saomclib.party.PlayerInfo
import be.bluexin.saomclib.party.playerInfo
import com.saomc.saoui.api.elements.PTMemberElement
import com.saomc.saoui.elements.IElement
import com.saomc.saoui.elements.IconLabelElement
import com.saomc.saoui.screens.CoreGUIDsl
import com.saomc.saoui.util.IconCore
import com.saomc.saoui.util.PlayerIcon
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class PartyController(controller: IController): Controller(IconLabelElement(IconCore.PARTY, I18n.format("sao.element.party"), controller), controller) {

    init {
        MinecraftForge.EVENT_BUS.register(this)
        resetPartyData()
    }

    @SubscribeEvent
    fun partyRefresh(e: PartyEvent.Refresh){
        resetPartyData()
    }
    private fun resetPartyData(){
        // Only remove invalid entries, this will prevent menus
        // abruptly closing during refresh.
        if (this.selected) {
            controllingParent.reInit()
        }
        elements.clear()
        partyExtras()
        if (1 < Minecraft().integratedServer?.currentPlayerCount?: 2)
            partyList()
        if (!this.selected) elements.forEach { it.hide() }
    }

    fun partyList() {
        val party = partyCap.partyData

        if (party != null) {
            party.membersInfo.filter { !it.equals(player) }.forEach {ptmember ->
                +partyMemberButton(party, ptmember, player)
            }

            party.invitedInfo.mapNotNull { it.key }.forEach {ptmember ->
                +partyMemberButton(party, ptmember, player, true)
            }
        }
    }

    fun partyMemberButton(party: IPartyData, player: PlayerInfo, ourPlayer: EntityPlayer, invited: Boolean = false): IElement  = Controller(playerController(player, this), this){
            +IconLabelElement(IconCore.HELP, I18n.format("sao.element.inspect"), this)
            if (party.leaderInfo.equals(ourPlayer)) {
                +IconLabelElement(IconCore.CANCEL, I18n.format("sao.party.${if (invited) "cancel" else "kick"}"), this).onOpen {
                    if (invited) {
                        Type.CANCELINVITE.updateServer(player, PartyType.MAIN)
                    } else {
                        Type.KICK.updateServer(player, PartyType.MAIN)
                    }
                }
            }
        }

    fun playerController(player: PlayerInfo, controller: Controller) = IconLabelElement(PlayerIcon(player), if (PTMemberElement.party?.isInvited(player) == true) I18n.format("sao.party.player_invited", player.username) else player.username, controller)

    @CoreGUIDsl
    fun partyExtras() {
        val party = partyCap.partyData

        if ((party != null && party.isLeader(player)) || party == null) +Controller(IconLabelElement(IconCore.INVITE, I18n.format("sao.party.invite"), this), this) {
            Minecraft().connection?.playerInfoMap?.filter { it.gameProfile.id != player.uniqueID }?.forEach {
                +IconLabelElement(PlayerIcon(PlayerInfo(it.gameProfile.id, it.gameProfile.name)), it.gameProfile.name, this).onOpen {
                    Type.INVITE.updateServer(PlayerInfo(it.gameProfile.id, it.gameProfile.name), PartyType.MAIN)
                }
            }
        }

        if (party != null && party.size > 1) +IconLabelElement(IconCore.CANCEL, I18n.format("sao.party.leave"), this).onOpen {
            Type.LEAVE.updateServer(player.playerInfo(), PartyType.MAIN)
        }

        partyCap.inviteData.forEach {inviteParty ->
            +Controller(IconLabelElement(PlayerIcon(inviteParty.leaderInfo), I18n.format("sao.party.invited", inviteParty.leaderInfo.username), this), this) {
                +IconLabelElement(IconCore.CONFIRM, I18n.format("sao.misc.accept"), this).onOpen {
                    Type.ACCEPTINVITE.updateServer(player.playerInfo(), PartyType.INVITE)
                }
                +IconLabelElement(IconCore.CANCEL, I18n.format("sao.misc.decline"), this).onOpen {
                    Type.CANCELINVITE.updateServer(player.playerInfo(), PartyType.INVITE)
                }
            }
        }
    }

    companion object {
        //val partyElements = mutableListOf<PTMemberElement>()
        val partyCap by lazy { Minecraft().player.getPartyCapability() }

        val player: EntityPlayer = Minecraft().player
    }
}