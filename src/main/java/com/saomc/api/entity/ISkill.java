package com.saomc.api.entity;

import com.saomc.api.screens.Actions;
import com.saomc.api.screens.IIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;

/**
 * Part of saoui
 * Skill to be shown in the "Skills" menu and/or in the skills ring if set so.
 *
 * @author Bluexin
 */
public interface ISkill {

    /**
     * Used when this skill is clicked on (with any mouse button).
     *
     * @param mc     The Minecraft instance
     * @param parent The parent gui
     */
    void activate(Minecraft mc, GuiInventory parent, Actions action);

    /**
     * Whether this skill should be visible to the player.
     *
     * @return should this skill be shown
     */
    default boolean visible() {
        return true;
    }

    /**
     * Whether this skill's button should highlight or not.
     *
     * @return whether it should be highlighted
     */
    boolean shouldHighlight();

    /**
     * Whether this skill should be shown on the "Skills" ring (around the character).
     * Only 12 skills can be shown on the ring.
     * For this to actually take effect, you need to specify it to {@link com.saomc.api.events.EventInitSkills}
     *
     * @return whether this skill should be listed on the "Skills" ring
     */
    default boolean shouldShowInRing() {
        return true;
    }

    /**
     * Sets whether this skill should be shown on the "Skills" ring.
     * It is highly recommended to not silently ignore this.
     */
    void setShowOnRing(boolean showOnRing);

    /**
     * Gets an end-user-friendly name for this skill.
     * Will get localized if a key exists for it.
     *
     * @return a name for this skill to display to the end-user
     */
    @Override
    String toString();

    /**
     * Gets an icon to be displayed with this skill.
     *
     * @return icon representing this skill
     */
    IIcon getIcon();
}
