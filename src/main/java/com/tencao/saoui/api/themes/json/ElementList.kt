package com.tencao.saoui.api.themes.json

enum class ElementList {

    /**
     * Key for the HudPart rendering the HP box on-screen.
     */
    HEALTH_BOX,

    /**
     * Key for the HudPart rendering the hotbar parts
     */
    HOTBAR,

    /**
     * Key for the HudPart rendering the XP box on-screen.
     */
    EXPERIENCE,

    /**
     * Key for the HudPart rendering the cross-hair.
     */
    CROSS_HAIR,

    /**
     * Key for the HudPart rendering the armor.
     */
    ARMOR,

    /**
     * Key for the HudPart rendering the horse jump bar.
     */
    JUMP_BAR,

    /**
     * Key for the HudPart rendering the party.
     */
    PARTY,

    /**
     * Key for the HudPart rendering food.
     */
    FOOD,

    /**
     * Key for the HudPart rendering potion effects.
     */
    EFFECTS,

    /**
     * Key for the HudPart rendering air bubbles.
     */
    AIR,

    /**
     * Key for the HudPart rendering mount health.
     */
    MOUNT_HEALTH,

    /**
     * Key for the HudPart rendering entity health on the hud.
     */
    ENTITY_HEALTH_HUD,

    /**
     * For anything else the theme wants, best to set their own conditions for rendering.
     */
    CUSTOM;
}
