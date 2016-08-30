package com.saomc.saoui.api.entity.rendering;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Part of saoui
 * <p>
 * Handles the changing of color states for an entity.
 *
 * @author Bluexin
 */
@FunctionalInterface
public interface IColorStateHandler {

    /**
     * @return the color state the entity should be showing.
     */
    ColorState getColorState();

    /**
     * Called every tick.
     * Use this to handle anything special.
     */
    default void tick() {
    }

    /**
     * Save any data to NBT format.
     * The implementation has to create his own sub-tag and to behave properly.
     * See {@link PlayerColorStateHandler#save(NBTTagCompound)} for an example.
     * This will be used for sync between client and server, and saving on world shutting down.
     *
     * @param tag the NBT tag to save to
     */
    default void save(NBTTagCompound tag) {
    }

    /**
     * Load any data from NBT format.
     * The implementation has to retrieve his own sub-tag and to behave properly.
     * See {@link PlayerColorStateHandler#load(NBTTagCompound)} for an example.
     * This will be used for sync between client and server, and loading on world starting up.
     *
     * @param tag the NBT tag to save to
     */
    default void load(NBTTagCompound tag) {
    }
}
