package com.saomc.saoui.elements.controllers

import be.bluexin.saomclib.capabilities.getPartyCapability
import be.bluexin.saomclib.party.PlayerInfo
import com.saomc.saoui.elements.IconLabelElement
import com.saomc.saoui.social.friends.FriendCore
import com.saomc.saoui.util.IconCore
import com.saomc.saoui.util.PlayerIcon
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.client.resources.I18n

class FriendController(controller: IController): Controller(IconLabelElement(IconCore.FRIEND, I18n.format("sao.element.friends"), controller), controller) {

    private val addFriendButton: Controller = Controller(IconLabelElement(IconCore.FRIEND, I18n.format("sao.element.add_friend"), this), this)

    private var dirty = false

    init {
        buildList()
    }
    fun buildList(){
        if (this.selected) {
            controllingParent.reInit()
        }
        elements.clear()
        +addFriendButton
        Minecraft().connection?.playerInfoMap?.forEach {
            val playerInfo = PlayerInfo(it.gameProfile.id, it.gameProfile.name)
            if (!FriendCore.isFriend(playerInfo) && playerInfo.uuid != Minecraft().player.uniqueID){
                addFriend(playerInfo)
            }
        }


        val friendList = FriendCore.getFriendList()
        friendList.sortedBy { it.username }
        friendList.filter { it.isOnline }.forEach {player ->
            removeFriend(player)
        }
        friendList.filter { !it.isOnline }.forEach {player ->
            removeFriend(player)
        }
        if (!this.selected) elements.forEach { it.hide() }
        dirty = false
    }

    override fun update() {
        if (dirty)
            buildList()
    }

    private fun addFriend(player: PlayerInfo){
        addFriendButton.let {
            +IconLabelElement(PlayerIcon(player), player.username, it).onOpen {
                val party = Minecraft().player.getPartyCapability().partyData/*
                (tlController as? CoreGUI<*>)?.openGui(
                        PopupPlayerInspect(
                                player) {
                            buttons = mapOf(
                                    Element(IconCore.CONFIRM, this, description = mutableListOf("Add Friend")) to 0,
                                    Element(IconCore.CANCEL, this, description = mutableListOf("Cancel")) to 1
                            ).also {
                                if (party != null && party.isLeader(Minecraft().player)) Element(IconCore.PARTY, this, description = mutableListOf("Invite to party")) to 2 }
                        }
                )?.plusAssign { id ->
                    when (id) {
                        0 -> {
                            FriendCore.addFriend(player)
                            dirty = true
                        }
                        2 -> {
                            Type.INVITE.updateServer(player, PartyType.MAIN)
                        }
                        else -> {
                        }
                    }
                }*/

            }
        }
    }

    fun removeFriend(player: PlayerInfo){
        +IconLabelElement(PlayerIcon(player), player.username, this){
            highlighted = player.isOnline
            onOpen {
                val party = Minecraft().player.getPartyCapability().partyData/*
                (tlController as? CoreGUI<*>)?.openGui(
                        PopupPlayerInspect(
                                player
                        ){
                            buttons = mapOf(
                                    Element(IconCore.CONFIRM, this, description = mutableListOf("Remove Friend")) to 0,
                                    Element(IconCore.CANCEL, this, description = mutableListOf("Cancel")) to 1
                            ).also {
                                if (party != null && party.isLeader(Minecraft().player)) Element(IconCore.PARTY, this, description = mutableListOf("Invite to party")) to 2}
                        }
                )?.plusAssign { id ->
                    when (id) {
                        0 -> {
                            FriendCore.removeFriend(player)
                            dirty = true
                        }
                        2 -> {
                            Type.INVITE.updateServer(player, PartyType.MAIN)
                        }
                        else -> {
                        }
                    }
                }*/
            }
        }
    }
}