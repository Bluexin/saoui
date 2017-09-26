package com.saomc.saoui.api.themes;

import com.saomc.saoui.effects.StatusEffects;
import com.saomc.saoui.screens.ingame.HealthStep;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

/**
 * Getters to use in JEL (for access in xml themes).
 * <p>
 * These are accessible in the HUD xml.
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

    float getZ();

    FontRenderer getFontRenderer();

    RenderItem getItemRenderer();

    EntityPlayer getPlayer();

    float getPartialTicks();

    float horsejump();

    void setI(int i);

    int i();

    String ptName(int index);

    float ptHp(int index);

    float ptMaxHp(int index);

    float ptHpPct(int index);

    int ptSize();

    HealthStep ptHealthStep(int index);

    float foodLevel();

    default float foodMax() {
        return 20.0f;
    }

    default float foodPct() {
        return Math.min(foodLevel() / foodMax(), 1.0f);
    }

    float saturationLevel();

    default float saturationMax() {
        return 20.0f;
    }

    default float saturationPct() {
        return Math.min(saturationLevel() / saturationMax(), 1.0f);
    }

    List<StatusEffects> statusEffects();

    default StatusEffects statusEffect(int i) {
        return statusEffects().get(i);
    }
}
