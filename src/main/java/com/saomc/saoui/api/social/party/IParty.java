package com.saomc.saoui.api.social.party;

import com.saomc.saoui.api.events.EventInitParty;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

/**
 * Part of saoui.
 * Stores a party and everything related to using it.
 * The SAO UI will only ever use one instance throughout the whole game session.
 * <p>
 * If you need it, you can implement this interface and provide
 * an instance of your implementation to this mod (trough {@link EventInitParty}.
 *
 * @author Bluexin
 */
public interface IParty {

    /**
     * Adds a member to this party.
     * Reaction to already present member is up to implementation details.
     *
     * @param member the member to add
     * @return whether the operation was successful (typically whether the player was already present, or following hypothetical size limit)
     */
    boolean addMember(EntityPlayer member);

    /**
     * Removes a member from this party.
     *
     * @param member the member to remove
     * @return whether the operation was successful
     */
    boolean removeMember(EntityPlayer member);

    /**
     * Gets a list containing all the members of this party.
     * Returned list safety (concurrency, mutability, ...) is up to implementation details.
     *
     * @return all the members in this party
     */
    List<EntityPlayer> getMembers();

    /**
     * Gets the leader of this party.
     * Returned reference safety (concurrency, mutability, ...) is up to implementation details.
     *
     * @return the leader of this party.
     */
    EntityPlayer getLeader();

    /**
     * Dissolves this party, aka removing all the members.
     */
    void dissolve();

    /**
     * Gets the size of this party, aka the amount of members.
     *
     * @return the amount of members in this party
     */
    int size();

    /**
     * Gets whether or not this is a valid party, typically denoted by having more than one member.
     * This is here so implementation can do as pleased, and should be the only reliable
     * source. Checking for size won't always work, depending on implementation.
     *
     * @return whether the party is valid
     */
    boolean isParty();

    /**
     * Returns whether the provided player is in this party.
     *
     * @param player the player to check for
     * @return whether the provided player is in this party
     */
    boolean isInParty(EntityPlayer player);
}
