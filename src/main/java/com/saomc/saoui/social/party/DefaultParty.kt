package com.saomc.saoui.social.party

import com.saomc.saoui.api.social.party.IParty
import net.minecraft.entity.player.EntityPlayer

import java.util.ArrayList

/**
 * Part of saoui
 * Default implementation of [IParty].

 * @author Bluexin
 */
class DefaultParty : IParty {
    private val members = ArrayList<EntityPlayer>() // TODO: check if this can't cause a mem leak (using direct refs instead of WeakReferences)

    override fun addMember(member: EntityPlayer?): Boolean {
        return member != null && !this.members.contains(member) && this.members.add(member)
    }

    override fun removeMember(member: EntityPlayer): Boolean {
        val flag = this.members.remove(member)
        if (this.size() <= 1) this.dissolve()
        return flag
    }

    override fun getMembers(): List<EntityPlayer> {
        return this.members
    }

    override fun getLeader(): EntityPlayer? {
        return if (this.size() > 0) this.members[0] else null
    }

    override fun dissolve() {
        this.members.clear()
    }

    override fun size(): Int {
        return this.members.size
    }

    override fun isParty(): Boolean {
        return this.size() > 1
    }

    override fun isInParty(player: EntityPlayer): Boolean {
        return this.members.contains(player)
    }
}
