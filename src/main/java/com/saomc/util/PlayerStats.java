package com.saomc.util;

import com.saomc.api.info.IPlayerStatsProvider;

/**
 * Part of saoui
 *
 * @author Bluexin
 */
public class PlayerStats {
    private static PlayerStats instance;
    private final IPlayerStatsProvider stats;

    private PlayerStats(IPlayerStatsProvider stats) {
        this.stats = stats;
    }

    public static void init(IPlayerStatsProvider provider) {
        if (instance != null) throw new IllegalStateException("PlayerStats already initialized!");
        instance = new PlayerStats(provider);
    }

    public static PlayerStats instance() {
        if (instance == null) throw new IllegalStateException("PlayerStats not initialized!");
        return instance;
    }

    public IPlayerStatsProvider getStats() {
        return this.stats;
    }
}
