package com.saomc.saoui.social.friends

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
class FriendRequest(private val friendName: String, var ticks: Int) {

    fun equals(name: String?): Boolean {
        return friendName == name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FriendRequest

        if (friendName != other.friendName) return false

        return true
    }

    override fun hashCode(): Int {
        return friendName.hashCode()
    }


}
