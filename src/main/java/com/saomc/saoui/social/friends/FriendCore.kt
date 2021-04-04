package com.saomc.saoui.social.friends

import be.bluexin.saomclib.party.PlayerInfo
import com.saomc.saoui.config.FriendData
import com.teamwizardry.librarianlib.features.kotlin.Client
import java.util.*

object FriendCore {

    private val friendList: LinkedHashSet<PlayerInfo>
        get() = FriendData.friendDataList

    val friendIterable = friendList.iterator()

    fun getFriendList(): Iterable<PlayerInfo>{
        return friendList
    }

    fun addFriend(playerInfo: PlayerInfo){
        friendList.add(playerInfo)
        FriendData.save()
    }

    fun removeFriend(playerInfo: PlayerInfo){
        friendList.remove(playerInfo)
        FriendData.save()
    }

    fun isFriend(player: PlayerInfo): Boolean{
        friendIterable.forEach { if (it.equals(player))  return true }
        return false
    }

    fun isFriend(player: UUID): Boolean{
        friendIterable.forEach { if (it.equals(player))  return true }
        return false
    }

    fun isFriendOnline(playerInfo: PlayerInfo): Boolean{
        return Client.minecraft.connection?.playerInfoMap?.any { it.displayName?.formattedText?.equals(playerInfo.username, true)?: false } ?: false
    }

    fun getOnlineList(): List<PlayerInfo>{
        return friendList.filter { isFriendOnline(it) }
    }

    fun getOfflineList(): List<PlayerInfo>{
        return friendList.filter { !isFriendOnline(it) }
    }

}