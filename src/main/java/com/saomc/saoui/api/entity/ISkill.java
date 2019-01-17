/*
 * Copyright (C) 2016-2019 Arnaud 'Bluexin' Solé
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.saomc.saoui.api.entity;

import com.saomc.saoui.api.events.EventInitSkills;
import com.saomc.saoui.api.screens.Actions;
import com.saomc.saoui.api.screens.IIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;

/**
 * Part of saoui
 * <p>
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
     * For this to actually take effect, you need to specify it to {@link EventInitSkills}
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
