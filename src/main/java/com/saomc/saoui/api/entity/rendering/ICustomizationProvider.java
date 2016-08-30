package com.saomc.saoui.api.entity.rendering;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Part of saoui
 * <p>
 * Provides customization for the hp and crystal renderer.
 *
 * @author Bluexin
 */
public interface ICustomizationProvider {
    /**
     * @return offset along the X axis to use for rendering
     */
    double getXOffset();

    /**
     * @return offset along the Y axis to use for rendering
     */
    double getYOffset();

    /**
     * @return offset along the Z axis to use for rendering
     */
    double getZOffset();

    /**
     * @return scale to use for rendering
     */
    double getScale();

    /**
     * @return number of HP bars to render
     */
    int getBarCount();

    /**
     * Save any data to NBT format.
     * The implementation has to create his own sub-tag and to behave properly.
     * See {@link StaticCustomizationProvider#save(NBTTagCompound)} for an example.
     * This will be used for sync between client and server, and saving on world shutting down.
     *
     * @param tag the NBT tag to save to
     */
    default void save(NBTTagCompound tag) {
    }

    /**
     * Load any data from NBT format.
     * The implementation has to retrieve his own sub-tag and to behave properly.
     * See {@link StaticCustomizationProvider#load(NBTTagCompound)} for an example.
     * This will be used for sync between client and server, and loading on world starting up.
     *
     * @param tag the NBT tag to save to
     */
    default void load(NBTTagCompound tag) {
    }
}
