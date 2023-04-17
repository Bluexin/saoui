package be.bluexin.mcui.api.elements

import be.bluexin.mcui.util.Client
import com.tencao.saomclib.capabilities.getPartyCapability
import com.tencao.saomclib.packets.party.PartyType
import com.tencao.saomclib.packets.party.Type
import com.tencao.saomclib.packets.party.updateServer
import com.tencao.saomclib.party.PlayerInfo
import be.bluexin.mcui.screens.CoreGUI
import be.bluexin.mcui.screens.util.PopupPlayerInspect
import be.bluexin.mcui.social.friends.FriendCore
import be.bluexin.mcui.util.IconCore
import be.bluexin.mcui.util.PlayerIcon
import net.minecraft.client.resources.I18n

class FriendElement(override var parent: INeoParent?) : IconLabelElement(IconCore.FRIEND, I18n.format("sao.element.friends")) {

    private lateinit var addFriendButton: CategoryButton
    private val friendSocialButton = this

    private var dirty = false

    init {
        buildList()
    }

    fun buildList() {
        if (this.selected) {
            (this.parent as CategoryButton).close(true)
            (this.parent as CategoryButton).open(true)
        }
        elements.clear()
        +CategoryButton(IconLabelElement(IconCore.FRIEND, I18n.format("sao.element.add_friend")), this.tlParent)
        addFriendButton = elements.first() as CategoryButton
        Client.mc.connection?.playerInfoMap?.forEach {
            val playerInfo = PlayerInfo(it.gameProfile.id, it.gameProfile.name)
            if (!FriendCore.isFriend(playerInfo) && playerInfo.uuid != Client.mc.player.uniqueID) {
                addFriend(playerInfo)
            }
        }

        +CategoryButton(IconLabelElement(IconCore.LOGOUT, I18n.format("sao.element.offline_friends")), this.tlParent) {
            FriendCore.getOfflineList().sortedBy { it.username }.forEach { player ->
                removeFriend(player)
            }
        }

        FriendCore.getOnlineList().sortedBy { it.username }.forEach { player ->
            removeFriend(player)
        }

        if (!this.selected) elementsSequence.forEach { it.hide() }
        dirty = false
    }

    override fun update() {
        if (dirty) {
            buildList()
        }
    }

    private fun addFriend(player: PlayerInfo) {
        addFriendButton.category(PlayerIcon(player), player.username) {
            onClick { _, _ ->
                val party = Client.mc.player.getPartyCapability().partyData
                (tlParent as? CoreGUI<*>)?.openGui(
                    PopupPlayerInspect(
                        player,
                        listOf(
                            IconElement(IconCore.CONFIRM, description = mutableListOf("Add Friend")),
                            IconElement(IconCore.CANCEL, description = mutableListOf("Cancel"))
                        ).also {
                            if (party != null && party.isLeader(Client.mc.player)) IconElement(IconCore.PARTY, description = mutableListOf("Invite to party"))
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

    fun removeFriend(player: PlayerInfo) {
        +CategoryButton(IconLabelElement(PlayerIcon(player), player.username), tlParent) {
            highlighted = player.isOnline
            onClick { _, _ ->
                val party = Client.mc.player.getPartyCapability().partyData
                (tlParent as? CoreGUI<*>)?.openGui(
                    PopupPlayerInspect(
                        player,
                        listOf(
                            IconElement(IconCore.CONFIRM, description = mutableListOf("Remove Friend")),
                            IconElement(IconCore.CANCEL, description = mutableListOf("Cancel"))
                        ).also {
                            if (party != null && party.isLeader(Client.mc.player)) IconElement(IconCore.PARTY, description = mutableListOf("Invite to party"))
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
