package com.saomc.saoui.social.friends

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
class FriendRequest(private val friendName: String, var ticks: Int) {

    private fun equals(request: FriendRequest?): Boolean {
        return equals(request?.friendName)
    }

    fun equals(name: String?): Boolean {
        return friendName == name
    }

    override fun equals(`object`: Any?): Boolean {
        return if (`object` is FriendRequest) equals(`object` as FriendRequest?) else equals(`object`.toString())
    }

}
