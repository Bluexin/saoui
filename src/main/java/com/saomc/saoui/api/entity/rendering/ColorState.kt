package com.saomc.saoui.api.entity.rendering

/**
 * Part of saoui
 *
 *
 * Possible colors an entity can have.
 *
 * @author Bluexin
 */
enum class ColorState(var rgba: Int) {

    /**
     * Green, default color.
     * Used for passive mobs, friendly NPCs and nice players by default.
     */
    INNOCENT(0x93F43EFF.toInt()),

    /**
     * Orange.
     * Used for mobs not targeting you, and harmful players by default.
     */
    VIOLENT(0xF49B00FF.toInt()),

    /**
     * Red.
     * Used for mobs targeting you, and criminal players by default.
     */
    KILLER(0xB91111FF.toInt()),

    /**
     * Red.
     * Used for bosses by default.
     */
    BOSS(0xBD0000FF.toInt()),

    /**
     * Turquoise.
     * Used for creative players by default.
     */
    CREATIVE(0x4CEDC5FF),

    /**
     * Black.
     * Used for GM players by default (OP mode on servers).
     */
    OP(0x000000FF),

    /**
     * Grey.
     * Used for errors by default.
     */
    INVALID(0x8B8B8BFF.toInt()),

    /**
     * Purple.
     * Used for devs of the SAOUI by default.
     */
    GAMEMASTER(0x79139EFF);
}
