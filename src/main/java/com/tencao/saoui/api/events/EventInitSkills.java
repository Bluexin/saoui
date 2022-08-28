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

package com.tencao.saoui.api.events;

import com.tencao.saoui.api.entity.ISkill;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

/**
 * Part of saoui
 * Fired when the SAO UI initialises its Skill list.
 * <p>
 * Shouldn't be fired by other mods!! (unless you're really sure you know what you're doing)
 * <p>
 * Mods could use this event to get the same reference the SAO UI uses, to use the data however they want.
 *
 * @author Bluexin
 */
public class EventInitSkills extends Event {

    private final List<ISkill> skills;
    private boolean showRing = false;

    public EventInitSkills(List<ISkill> skills) {
        this.skills = skills;
    }

    /**
     * Gets the current list of skills.
     * One can edit this list however he wants.
     *
     * @return the current list of skills to be registered for use in the SAO UI
     */
    public List<ISkill> getSkills() {
        return this.skills;
    }

    /**
     * Gets whether skills can be shown on the Skills Ring around the player (instead of the hotbar to be shown there).
     *
     * @return whether skills can be shown on the Skills Ring
     */
    public boolean isRingShown() {
        return showRing;
    }

    /**
     * Enables or disables skills showing on the Skills Ring around the player (instead of the hotbar to be shown there).
     *
     * @param enabled whether to show the skills on the Skills Ring
     */
    public void setShowRing(boolean enabled) {
        this.showRing = enabled;
    }
}
