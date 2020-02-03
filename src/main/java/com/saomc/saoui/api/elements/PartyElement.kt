package com.saomc.saoui.api.elements

import be.bluexin.saomclib.capabilities.getPartyCapability
import be.bluexin.saomclib.events.PartyEvent
import be.bluexin.saomclib.packets.party.PartyType
import be.bluexin.saomclib.packets.party.Type
import be.bluexin.saomclib.packets.party.updateServer
import be.bluexin.saomclib.party.IPartyData
import be.bluexin.saomclib.party.PlayerInfo
import com.saomc.saoui.screens.CoreGUIDsl
import com.saomc.saoui.util.IconCore
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.FakePlayer
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class PartyElement(val player: EntityPlayer, override var parent: INeoParent?): IconLabelElement(IconCore.PARTY, I18n.format("sao.element.party")) {

    //val partyElements = mutableListOf<PTMemberElement>()
    val partyCap by lazy { Minecraft().player.getPartyCapability() }

    /**
     * Override the default element sequence to merge our party elements while keeping them separate.
     */
    //override val elementsSequence: Sequence<NeoElement> = elements.asSequence().filter(NeoElement::listed).plus(partyElements.asSequence().filter(NeoElement::listed))
    //override val otherElementsSequence: Sequence<NeoElement> = elements.asSequence().filter { !it.listed }.plus(partyElements.asSequence().filter { !it.listed })

    init {
        MinecraftForge.EVENT_BUS.register(this)
        resetPartyData()
    }

    override fun update() {
        super.update()
    }

    @SubscribeEvent
    fun partyRefresh(e: PartyEvent.Refresh){
        resetPartyData()
    }

    private fun resetPartyData(){
        // Only remove invalid entries, this will prevent menus
        // abruptly closing during refresh.
        if (this.selected) {
            (this.parent as CategoryButton).close(true)
            (this.parent as CategoryButton).open(true)
        }
        elements.clear()
        partyExtras(player)
        elements.removeIf { it is PTMemberElement && partyCap.partyData?.isMember(it.player) != true &&
                                    partyCap.partyData?.isInvited(it.player) != true}
        //elements[0].valid = !(partyCap.partyData?.isLeader(Minecraft().player) == false)
        // Don't bother with updating party functions if single player.
        if (1 < Minecraft().integratedServer?.currentPlayerCount?: 2)
            partyList(player)
        if (!this.selected) elements.forEach { it.hide() }
    }

    @CoreGUIDsl
    fun partyList(player: EntityPlayer) {
        val partyCapability = player.getPartyCapability()
        val party = partyCapability.partyData

        if (party != null) {
            party.membersInfo.filter { !it.equals(player) }.forEach {ptmember ->
                if (elements.none { it is PTMemberElement && !it.equals(ptmember) })
                    +partyMemberButton(party, ptmember, player)
            }

            party.invitedInfo.mapNotNull { it.key }.forEach {ptmember ->
                if (elements.none { it is PTMemberElement && !it.equals(ptmember) })
                    +partyMemberButton(party, ptmember, player, true)
            }
        }
    }

    @CoreGUIDsl
    fun partyMemberButton(party: IPartyData, player: PlayerInfo, ourPlayer: EntityPlayer, invited: Boolean = false): CategoryButton =
            CategoryButton(PTMemberElement(player), this.tlParent) {
                +CategoryButton(IconLabelElement(IconCore.HELP, I18n.format("sao.element.inspect")), this.tlParent)
                if (party.leaderInfo.equals(ourPlayer)) {
                    +CategoryButton(IconLabelElement(IconCore.CANCEL, I18n.format("sao.party.${if (invited) "cancel" else "kick"}")), this.tlParent).onClick { _, _ ->
                        if (invited) {
                            Type.CANCELINVITE.updateServer(player.player, PartyType.MAIN)
                            true
                        } else {
                            Type.KICK.updateServer(player.player, PartyType.MAIN)
                            true
                        }
                    }
                }
            }

    @CoreGUIDsl
    fun partyExtras(player: EntityPlayer) {
        val partyCapability = player.getPartyCapability()
        val party = partyCapability.partyData

        if ((party != null && party.isLeader(player)) || party == null) +CategoryButton(IconLabelElement(IconCore.PARTY, I18n.format("sao.party.invite")), this.tlParent) {
            @Suppress("UNCHECKED_CAST")
            (player.world.playerEntities as List<EntityPlayer>).asSequence().filter { it != player && it !is FakePlayer && party?.isMember(it) != true }.forEach { player ->
                +CategoryButton(IconLabelElement(IconCore.INVITE, player.displayNameString), this.tlParent).onClick { _, _ ->
                    Type.INVITE.updateServer(player, PartyType.MAIN)
                    true
                }
            }
        }
        if (party != null && party.size > 1) +CategoryButton(IconLabelElement(IconCore.CANCEL, I18n.format("sao.party.leave")), this.tlParent).onClick { _, _ ->
            Type.LEAVE.updateServer(player, PartyType.MAIN)
            true
        }

        val invitedTo = partyCapability.inviteData
        if (invitedTo != null) {
            +CategoryButton(IconLabelElement(IconCore.PARTY, I18n.format("sao.party.invited", invitedTo.leaderInfo.username)), this.tlParent) {
                +CategoryButton(IconLabelElement(IconCore.CONFIRM, I18n.format("sao.misc.accept")), this.tlParent).onClick { _, _ ->
                    Type.ACCEPTINVITE.updateServer(player, PartyType.INVITE)
                    true
                }
                +CategoryButton(IconLabelElement(IconCore.CANCEL, I18n.format("sao.misc.decline")), this.tlParent).onClick { _, _ ->
                    Type.CANCELINVITE.updateServer(player, PartyType.INVITE)
                    true
                }
            }
        }
    }

}