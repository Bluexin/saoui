package com.saomc.saoui.api.entity.rendering;

/**
 * Part of saoui
 * <p>
 * Possible colors an entity can have.
 *
 * @author Bluexin
 */
public enum ColorState {

    /**
     * Green, default color.
     * Used for passive mobs, friendly NPCs and nice players by default.
     */
    INNOCENT(0x93F43EFF),

    /**
     * Orange.
     * Used for mobs not targeting you, and harmful players by default.
     */
    VIOLENT(0xF49B00FF),

    /**
     * Red.
     * Used for mobs targeting you, and criminal players by default.
     */
    KILLER(0xBD0000FF),

    /**
     * Red.
     * Used for bosses by default.
     */
    BOSS(0xBD0000FF),

    /**
     * Turquoise.
     * Used for creative players by default.
     */
    CREATIVE(0x4CEDC5FF),

    /**
     * Black.
     * Used for GM players by default (OP mode on servers).
     */
    OP(0xFFFFFFFF),

    /**
     * Grey.
     * Used for errors by default.
     */
    INVALID(0x8B8B8BFF),

    /**
     * Purple.
     * Used for devs of the SAOUI by default.
     */
    GAMEMASTER(0x79139EFF);

    private final int color;

    ColorState(int argb) {
        color = argb;
    }

    /**
     * Gets an integer representing the RGBA for this color state (in form 0xRRGGBBAA).
     *
     * @return rgba of this color state
     */
    public int rgba() {
        return this.color;
    }

}
