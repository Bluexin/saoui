package com.saomc.saoui.api.elements

import be.bluexin.saomclib.party.PlayerInfo
import com.saomc.saoui.screens.CoreGUIDsl
import com.saomc.saoui.social.friends.FriendCore
import com.saomc.saoui.util.IconCore
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.client.resources.I18n

class FriendElement(override var parent: INeoParent?): IconLabelElement(IconCore.FRIEND, I18n.format("sao.element.friends")) {

    private lateinit var addFriendButton: CategoryButton
    private val friendSocialButton = this

    private var dirty = false

    init {
        buildList()
    }


    fun buildList(){
        if (this.selected) {
            (this.parent as CategoryButton).close(true)
            (this.parent as CategoryButton).open(true)
        }
        elements.clear()
        +CategoryButton(IconLabelElement(IconCore.FRIEND, I18n.format("sao.element.add_friend")), this.tlParent)
        addFriendButton = elements.first() as CategoryButton
        Minecraft().connection?.playerInfoMap?.forEach {
            val playerInfo = PlayerInfo(it.gameProfile.id, it.gameProfile.name)
            if (!FriendCore.isFriend(playerInfo) && playerInfo.uuid != Minecraft().player.uniqueID){
                addFriend(playerInfo)
            }
        }

        FriendCore.getFriendList().forEach { player ->
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
    private fun addFriend(player: PlayerInfo){
        addFriendButton.category(/*ItemIcon{UIUtil.getCustomHead(it.gameProfile.name)}*/ IconCore.NONE, player.username){
            category(IconCore.NONE, I18n.format("sao.element.add")){
                onClick { _, _ ->
                    FriendCore.addFriend(player)
                    dirty = true
                    true
                }
            }
            category(IconCore.PROFILE, I18n.format("sao.element.inspect"))
        }
    }

    @CoreGUIDsl
    fun removeFriend(player: PlayerInfo){
        +CategoryButton(IconLabelElement(/*ItemIcon{UIUtil.getCustomHead(player.username)}*/ IconCore.NONE, player.username), tlParent){
            category(IconCore.NONE, I18n.format("sao.element.remove_friend")){
                onClick { _, _ ->
                    FriendCore.removeFriend(player)
                    dirty = true
                    true
                }
            }
        }
    }
}