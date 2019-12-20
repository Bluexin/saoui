package com.saomc.saoui.api.elements

import be.bluexin.saomclib.capabilities.getPartyCapability
import be.bluexin.saomclib.packets.party.PartyType
import be.bluexin.saomclib.packets.party.Type
import be.bluexin.saomclib.packets.party.updateServer
import be.bluexin.saomclib.party.IPartyData
import be.bluexin.saomclib.party.PlayerInfo
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.screens.CoreGUIDsl
import com.saomc.saoui.util.IconCore
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer

class PartyElement(val player: EntityPlayer): IconLabelElement(IconCore.PARTY, I18n.format("sao.element.party")) {

    var invalidate = false

    //val partyElements = mutableListOf<PTMemberElement>()
    val partyCap by lazy { Minecraft().player.getPartyCapability() }

    /**
     * Override the default element sequence to merge our party elements while keeping them separate.
     */
    //override val elementsSequence: Sequence<NeoElement> = elements.asSequence().filter(NeoElement::listed).plus(partyElements.asSequence().filter(NeoElement::listed))
    //override val otherElementsSequence: Sequence<NeoElement> = elements.asSequence().filter { !it.listed }.plus(partyElements.asSequence().filter { !it.listed })


    override fun update() {
        if (invalidate)
            resetPartyData()
        //partyElements.forEach(NeoElement::update)
        super.update()
    }

    private fun resetPartyData(){
        // Only remove invalid entries, this will prevent menus
        // abruptly closing during refresh.
        elements.removeIf { it is PTMemberElement && partyCap.partyData?.isMember(it.player) != true &&
                                    partyCap.partyData?.isInvited(it.player) != true}
        elements[0].valid = !(partyCap.partyData?.isLeader(Minecraft().player) == false)
        invalidate = false
        // Don't bother with updating party functions if single player.
        if (1 >= Minecraft().integratedServer?.currentPlayerCount?: 2) return
        partyList(player)
    }

    fun category(icon: IIcon, label: String, body: (CategoryButton.() -> Unit)? = null): CategoryButton {
        val cat = CategoryButton(IconLabelElement(icon, label), this, body)
        +cat
        return cat
    }

    companion object {

        @CoreGUIDsl
        fun PartyElement.partyList(player: EntityPlayer) {
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
        fun PartyElement.partyMemberButton(party: IPartyData, player: PlayerInfo, ourPlayer: EntityPlayer, invited: Boolean = false): CategoryButton =
                CategoryButton(PTMemberElement(player), this) {
                    +IconLabelElement(IconCore.HELP, I18n.format("sao.element.inspect"))
                    if (party.leaderInfo.equals(ourPlayer)) {
                        +IconLabelElement(IconCore.CANCEL, I18n.format("sao.party.${if (invited) "cancel" else "kick"}")).onClick { _, _ ->
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

    }

    /**
     * Used to mark the current party list as
     * invalid and rebuild the list on the next
     * draw()
     */
    fun invalidate(){
        invalidate = true
    }
}