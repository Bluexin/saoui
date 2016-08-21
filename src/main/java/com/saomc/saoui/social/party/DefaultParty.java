package com.saomc.saoui.social.party;

import com.saomc.saoui.api.social.party.IParty;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Part of saoui
 * Default implementation of {@link IParty}.
 *
 * @author Bluexin
 */
public class DefaultParty implements IParty {
    private final List<EntityPlayer> members = new ArrayList<>(); // TODO: check if this can't cause a mem leak (using direct refs instead of WeakReferences)

    @Override
    public boolean addMember(EntityPlayer member) {
        return member != null && !this.members.contains(member) && this.members.add(member);
    }

    @Override
    public boolean removeMember(EntityPlayer member) {
        boolean flag = this.members.remove(member);
        if (this.size() <= 1) this.dissolve();
        return flag;
    }

    @Override
    public List<EntityPlayer> getMembers() {
        return this.members;
    }

    @Override
    public EntityPlayer getLeader() {
        return this.size() > 0 ? this.members.get(0) : null;
    }

    @Override
    public void dissolve() {
        this.members.clear();
    }

    @Override
    public int size() {
        return this.members.size();
    }

    @Override
    public boolean isParty() {
        return this.size() > 1;
    }

    @Override
    public boolean isInParty(EntityPlayer player) {
        return this.members.contains(player);
    }
}
