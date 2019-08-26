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

package com.saomc.saoui.api.themes;

import com.saomc.saoui.effects.StatusEffects;
import com.saomc.saoui.screens.ingame.HealthStep;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Getters to use in JEL (for access in xml themes).
 * <p>
 * These are accessible in the HUD xml.
 *
 * Everywhere "percent" or "scale of 1" is mentioned, it means the value will be a decimal ranging from 0.0 (included)
 * to 1.0 (included).
 *
 * @author Bluexin
 */
@SuppressWarnings("unused") // Used to get access in JEL
public interface IHudDrawContext {

    /**
     * @return the player's username
     */
    String username();

    /**
     * @return the width of the player's username (in pixel, with the current font)
     */
    double usernamewidth();

    /**
     * @return the current hp of the player, in percentage of 1
     */
    double hpPct();

    /**
     * @return the current hp of the player (1 heart = 2 HP)
     */
    float hp();

    /**
     * @return the current maximum hp of the player (1 heart = 2 HP)
     */
    float maxHp();

    /**
     * @return the health step the player is currently at
     */
    HealthStep healthStep();

    /**
     * @return the id of the hotbar slot the player is using (from 0 to 8)
     */
    int selectedslot();

    /**
     * @return screen width, scaled
     */
    int scaledwidth();

    /**
     * @return screen heigth, scaled
     */
    int scaledheight();

    /**
     * Used to know whether the specified offhand slot is empty.
     * Currently there is only 1 offhand slot.
     *
     * @param slot offhand slot to query
     * @return whether the specified offhand slot is empty
     */
    boolean offhandEmpty(int slot);

    /**
     * Used to get the width in pixels of the string with the current fontrenderer.
     *
     * @param s the string to query the width of
     * @return width in pixels of the provided string with current fontrenderer
     */
    int strWidth(String s);

    /**
     * @return the current absorption amount the player has
     */
    float absorption();

    /**
     * @return the current experience level of the player
     */
    int level();

    /**
     * @return the current experience percent of the player (scale of 1)
     */
    float experience();

    /**
     * @return z value. Shouldn't be needed in theme
     */
    float getZ();

    /**
     * @return current font renderer. Useless in themes for now
     */
    FontRenderer getFontRenderer();

    /**
     * @return current item renderer. Useless in themes for now
     */
    RenderItem getItemRenderer();

    /**
     * @return current player. Useless in themes for now
     */
    EntityPlayer getPlayer();

    /**
     * @return partial ticks
     */
    float getPartialTicks();

    /**
     * @return horse jump value (on a scale of 1)
     */
    float horsejump();

    /**
     * Internal
     */
    void setI(int i);

    /**
     * @return index in repetition groups
     */
    int i();

    /**
     * @param index the index of the party member to check
     * @return username of the party member at given index
     */
    String ptName(int index);

    /**
     * @param index the index of the party member to check
     * @return hp of the party member at given index
     */
    float ptHp(int index);

    /**
     * @param index the index of the party member to check
     * @return max hp of the party member at given index
     */
    float ptMaxHp(int index);

    /**
     * @param index the index of the party member to check
     * @return hp percent of the party member at given index
     */
    float ptHpPct(int index);

    /**
     * @param index the index of the party member to check
     * @return health step of the party member at given index
     */
    HealthStep ptHealthStep(int index);

    /**
     * @param index the index of the party member to check
     * @return whether we have data about the member (didn't get GC for example)
     */
    boolean ptPresent(int index);

    /**
     * @return current party size
     */
    int ptSize();

    /**
     * @return player food level
     */
    float foodLevel();

    /**
     * @return player food max value
     */
    default float foodMax() {
        return 20.0f;
    }

    /**
     * @return player food percentage
     */
    default float foodPct() {
        return Math.min(foodLevel() / foodMax(), 1.0f);
    }

    /**
     * @return player saturation level
     */
    float saturationLevel();

    /**
     * @return player saturation max value
     */
    default float saturationMax() {
        return 20.0f;
    }

    /**
     * @return player saturation percentage
     */
    default float saturationPct() {
        return Math.min(saturationLevel() / saturationMax(), 1.0f);
    }

    /**
     * @return player's current status effects
     */
    List<StatusEffects> statusEffects();

    /**
     * @param i index to check
     * @return status effect at given index
     */
    default StatusEffects statusEffect(final int i) {
        return statusEffects().get(i);
    }

    /**
     * @return whether the current player is riding an entity
     */
    boolean hasMount();

    /**
     * @return current mount hp (or 0 if none)
     */
    float mountHp();

    /**
     * @return mount max hp (or 1 if none)
     */
    float mountMaxHp();

    /**
     * @return mount hp percentage (or 1 if none)
     */
    default float mountHpPct() {
        return hasMount() ? Math.min(mountHp() / mountMaxHp(), 1.0f) : 1.0f;
    }

    /**
     * @return whether the player is under water
     */
    boolean inWater();

    /**
     * @return current air level
     */
    int air();

    /**
     * @return max air level
     */
    default int airMax() {
        return 300;
    }

    /**
     * @return air level percentage
     */
    default float airPct() {
        return Math.min(air() / (float) airMax(), 1.0f);
    }

    /**
     * @return armor value
     */
    int armor();

    /**
     * @return current active mod extension, if any
     */
    @Nullable HudContextExtension ext();

    /**
     * @param key of the mod extension to look for
     * @param version version of the extension
     * @return mod extension, if any
     */
    @Nullable HudContextExtension ext(@Nonnull final String key, @Nonnull final String version);

    /**
     * Set the current active mod extension by key
     *
     * @param key mod extension to set by key
     * @param version version of the extension
     * @return previously set active extension, if any
     */
    @Nullable HudContextExtension setExt(@Nullable final String key, @Nonnull final String version);

    /**
     * Set the current active mod extension
     *
     * @param ext mod extension to set
     * @return previously set active extension, if any
     */
    @Nullable HudContextExtension setExt(@Nullable final HudContextExtension ext);
}
