package com.saomc.saoui.util;

import net.minecraft.entity.player.EntityPlayer;

public final class PlayerString implements Strings {

    private final EntityPlayer player;

    public PlayerString(EntityPlayer entityPlayer) {
        player = entityPlayer;
    }

    public final String toString() {
        return PlayerStats.Companion.instance().getStats().getStatsString(player);
    }

}
