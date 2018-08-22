package com.saomc.saoui.api.events;

import com.saomc.saoui.api.entity.ISkill;
import cpw.mods.fml.common.eventhandler.Event;

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
