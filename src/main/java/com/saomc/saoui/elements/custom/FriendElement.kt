package com.saomc.saoui.elements.custom

import be.bluexin.saomclib.capabilities.getPartyCapability
import be.bluexin.saomclib.packets.party.PartyType
import be.bluexin.saomclib.packets.party.Type
import be.bluexin.saomclib.packets.party.updateServer
import be.bluexin.saomclib.party.PlayerInfo
import com.saomc.saoui.elements.IElement
import com.saomc.saoui.elements.IconElement
import com.saomc.saoui.elements.IconLabelElement
import com.saomc.saoui.elements.gui.CoreGUIDsl
import com.saomc.saoui.elements.gui.PopupPlayerInspect
import com.saomc.saoui.social.friends.FriendCore
import com.saomc.saoui.util.IconCore
import com.saomc.saoui.util.PlayerIcon
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.client.resources.I18n

class FriendElement(override var parent: IElement?) : IconLabelElement(IconCore.FRIEND, I18n.format("sao.element.friends")) {

    private lateinit var addFriendButton: IElement
    private val friendSocialButton = this

    private var dirty = false

    init {
        buildList()
    }


    fun buildList() {
        if (this.selected) {
            parent?.close(true)
            parent?.open(true)
        }
        elements.clear()
        +IconLabelElement(IconCore.FRIEND, I18n.format("sao.element.add_friend"), this)
        addFriendButton = elements.first()
        Minecraft().connection?.playerInfoMap?.forEach {
            val playerInfo = PlayerInfo(it.gameProfile.id, it.gameProfile.name)
            if (!FriendCore.isFriend(playerInfo) && playerInfo.uuid != Minecraft().player.uniqueID) {
                addFriend(playerInfo)
            }
        }

        +IconLabelElement(IconCore.LOGOUT, I18n.format("sao.element.offline_friends"), this) {
            FriendCore.getOfflineList().sortedBy { it.username }.forEach { player ->
                removeFriend(player)
            }
        }

        FriendCore.getOnlineList().sortedBy { it.username }.forEach { player ->
            removeFriend(player)
        }

        if (!this.selected) elements.forEach { it.hide() }
        dirty = false
    }

    override fun update() {
        if (dirty)
            buildList()
    }


    @CoreGUIDsl
    private fun addFriend(player: PlayerInfo) {
        addFriendButton.category(PlayerIcon(player), player.username) {
            onClick { _, _ ->
                val party = Minecraft().player.getPartyCapability().partyData
                controllingGUI?.openGui(
                        PopupPlayerInspect(
                                player,
                                listOf(
                                        IconElement(IconCore.CONFIRM, description = mutableListOf("Add Friend")),
                                        IconElement(IconCore.CANCEL, description = mutableListOf("Cancel"))
                                ).also {
                                    if (party != null && party.isLeader(Minecraft().player)) IconElement(IconCore.PARTY, description = mutableListOf("Invite to party"))
                                }
                        )
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
                }

                true
            }
        }
    }

    @CoreGUIDsl
    fun removeFriend(player: PlayerInfo) {
        +IconLabelElement(PlayerIcon(player), player.username, this) {
            highlighted = player.isOnline
            onClick { _, _ ->
                val party = Minecraft().player.getPartyCapability().partyData
                controllingGUI?.openGui(
                        PopupPlayerInspect(
                                player,
                                listOf(
                                        IconElement(IconCore.CONFIRM, description = mutableListOf("Remove Friend")),
                                        IconElement(IconCore.CANCEL, description = mutableListOf("Cancel"))
                                ).also {
                                    if (party != null && party.isLeader(Minecraft().player)) IconElement(IconCore.PARTY, description = mutableListOf("Invite to party"))
                                }
                        )
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
                }

                true
            }
        }
    }
}