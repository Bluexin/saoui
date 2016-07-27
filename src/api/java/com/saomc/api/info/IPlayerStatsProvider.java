package com.saomc.api.info;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Part of saoui
 * Used to retrieve information about a player.
 * The SAO UI will only ever use one instance throughout the whole game session.
 * <p>
 * If you need it, you can implement this interface and provide
 * an instance of your implementation to this mod (trough {@link com.saomc.api.events.EventInitStatsProvider}.
 *
 * @author Bluexin
 */
public interface IPlayerStatsProvider {

    /**
     * Getter for the player's level.
     *
     * @param player the player to get the level of
     * @return the level of given player
     */
    default int getLevel(EntityPlayer player) {
        return player.experienceLevel;
    }

    /**
     * Getter for the player's experience percentage (range is [0.0, 1.0[).
     *
     * @param player the player to get the exp of
     * @return the experience % of given player
     */
    default float getExpPct(EntityPlayer player) {
        return player.experience;
    }

    /**
     * Getter for a "stats string" (to be displayed in the menu).
     *
     * @param player the player to get the info about
     * @return the stats of given player
     */
    String getStatsString(EntityPlayer player);
}
