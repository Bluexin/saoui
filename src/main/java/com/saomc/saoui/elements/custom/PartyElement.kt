package com.saomc.saoui.elements.custom

import be.bluexin.saomclib.capabilities.getPartyCapability
import be.bluexin.saomclib.events.PartyEvent
import be.bluexin.saomclib.packets.party.PartyType
import be.bluexin.saomclib.packets.party.Type
import be.bluexin.saomclib.packets.party.updateServer
import be.bluexin.saomclib.party.IPartyData
import be.bluexin.saomclib.party.PlayerInfo
import be.bluexin.saomclib.party.playerInfo
import com.saomc.saoui.elements.IElement
import com.saomc.saoui.elements.IconLabelElement
import com.saomc.saoui.elements.gui.CoreGUIDsl
import com.saomc.saoui.util.IconCore
import com.saomc.saoui.util.PlayerIcon
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class PartyElement(override var parent: IElement?) : IconLabelElement(IconCore.PARTY) {

    //val partyElements = mutableListOf<PTMemberElement>()
    val partyCap
        get() = Minecraft().player.getPartyCapability()

    val player: EntityPlayer
        get() = Minecraft().player

    /**
     * Override the default element sequence to merge our party elements while keeping them separate.
     */
    //override val elementsSequence: Sequence<NeoElement> = elements.asSequence().filter(NeoElement::listed).plus(partyElements.asSequence().filter(NeoElement::listed))
    //override val otherElementsSequence: Sequence<NeoElement> = elements.asSequence().filter { !it.listed }.plus(partyElements.asSequence().filter { !it.listed })

    init {
    }

    override fun open(reInit: Boolean) {
        super.open(reInit)
        if (!reInit){
            MinecraftForge.EVENT_BUS.register(this)
        }
        elements.clear()
        resetPartyData()
    }

    override fun close(reInit: Boolean) {
        super.close(reInit)
        if (!reInit)
            MinecraftForge.EVENT_BUS.unregister(this)
    }

    @SubscribeEvent
    fun partyRefresh(e: PartyEvent.Refresh) {
        resetPartyData()
    }

    private fun resetPartyData() {
        // Only remove invalid entries, this will prevent menus
        // abruptly closing during refresh.
        if (this.selected) {
            parent?.close(true)
            parent?.open(true)
        }
        elements.clear()
        partyExtras()
        elements.removeIf {
            it is PTMemberElement && partyCap.partyData?.isMember(it.player) != true &&
                    partyCap.partyData?.isInvited(it.player) != true
        }
        //elements[0].valid = !(partyCap.partyData?.isLeader(Minecraft().player) == false)
        // Don't bother with updating party functions if single player.
        if (1 < Minecraft().integratedServer?.currentPlayerCount ?: 2)
            partyList()
        if (!this.selected) elements.forEach { it.hide() }
    }

    @CoreGUIDsl
    fun partyList() {
        val party = partyCap.partyData

        if (party != null) {
            party.membersInfo.filter { !it.equals(player) }.forEach { ptmember ->
                if (elements.none { it is PTMemberElement && !it.equals(ptmember) })
                    +partyMemberButton(party, ptmember, player)
            }

            party.invitedInfo.mapNotNull { it.key }.forEach { ptmember ->
                if (elements.none { it is PTMemberElement && !it.equals(ptmember) })
                    +partyMemberButton(party, ptmember, player, true)
            }
        }
    }

    @CoreGUIDsl
    fun partyMemberButton(party: IPartyData, player: PlayerInfo, ourPlayer: EntityPlayer, invited: Boolean = false): IElement =
            PTMemberElement(player, this) {
                +IconLabelElement(IconCore.HELP, I18n.format("sao.element.inspect"), this)
                if (party.leaderInfo.equals(ourPlayer)) {
                    +IconLabelElement(IconCore.CANCEL, I18n.format("sao.party.${if (invited) "cancel" else "kick"}"), this).onClick { _, _ ->
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


    @CoreGUIDsl
    fun partyExtras() {
        val party = partyCap.partyData

        if ((party != null && party.isLeader(player)) || party == null) +IconLabelElement(IconCore.INVITE, I18n.format("sao.party.invite"), this) {
            Minecraft().connection?.playerInfoMap?.filter { it.gameProfile.id != player.uniqueID }?.forEach {
                +IconLabelElement(PlayerIcon(PlayerInfo(it.gameProfile.id, it.gameProfile.name)), it.gameProfile.name, this).onClick { _, _ ->
                    Type.INVITE.updateServer(PlayerInfo(it.gameProfile.id, it.gameProfile.name), PartyType.MAIN)
                    true
                }
            }
        }
        if (party != null && party.size > 1) +IconLabelElement(IconCore.CANCEL, I18n.format("sao.party.leave"), this).onClick { _, _ ->
            Type.LEAVE.updateServer(player.playerInfo(), PartyType.MAIN)
            true
        }

        partyCap.inviteData.forEach { inviteParty ->
            +IconLabelElement(PlayerIcon(inviteParty.leaderInfo), I18n.format("sao.party.invited", inviteParty.leaderInfo.username), this) {
                +IconLabelElement(IconCore.CONFIRM, I18n.format("sao.misc.accept"), this).onClick { _, _ ->
                    Type.ACCEPTINVITE.updateServer(player.playerInfo(), PartyType.INVITE)
                    true
                }
                +IconLabelElement(IconCore.CANCEL, I18n.format("sao.misc.decline"), this).onClick { _, _ ->
                    Type.CANCELINVITE.updateServer(player.playerInfo(), PartyType.INVITE)
                    true
                }
            }
        }
    }

}