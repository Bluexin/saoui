package com.saomc.saoui.themes.elements;

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
enum RenderingProperty {

    /**
     * Marks the element as offset to the right by the username's length.
     */
    USERNAME_OFFSET_POS,

    /**
     * Marks the element as offset to the left by the username's length.
     */
    USERNAME_OFFSET_NEG,

    /**
     * Marks the {@link GLString} to use the player's username as text, or the {@link GLRectangle} to use it's width.
     */
    USERNAME
}
