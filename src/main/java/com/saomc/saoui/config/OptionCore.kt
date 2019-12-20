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

package com.saomc.saoui.config

import com.saomc.saoui.GLCore
import com.saomc.saoui.api.events.OptionTriggerEvent
import com.saomc.saoui.api.info.IOption
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
enum class OptionCore(
        /**
         * @return Returns the Option name in String format
         *
         * Deprecated:
         * @see OptionCore.toString
         */
        val displayName: String,
        private var value: Boolean,
        /**
         * @return Returns true if this is a Category or not
         */
        val isCategory: Boolean,
        private val category: OptionCore?,
        private val restricted: Boolean
) : IOption {

    //Main Categories
    UI(I18n.format("optCatUI"), false, true, null, false),
    THEME(I18n.format("optTheme"), false, true, null, false),
    HEALTH_OPTIONS(I18n.format("optCatHealth"), false, true, null, false),
    HOTBAR_OPTIONS(I18n.format("optCatHotBar"), false, true, null, false),
    EFFECTS(I18n.format("optCatEffects"), false, true, null, false),
    MISC(I18n.format("optCatMisc"), false, true, null, false),
    //General UI Settings
    UI_ONLY(I18n.format("optionUIOnly"), false, false, UI, false),
    DEFAULT_INVENTORY(I18n.format("optionDefaultInv"), true, false, UI, false),
    DEFAULT_DEATH_SCREEN(I18n.format("optionDefaultDeath"), false, false, UI, false),
    DEFAULT_DEBUG(I18n.format("optionDefaultDebug"), false, false, UI, false),
    FORCE_HUD(I18n.format("optionForceHud"), true, false, UI, false),
    LOGOUT(I18n.format("optionLogout"), false, false, UI, false),
    GUI_PAUSE(I18n.format("optionGuiPause"), false, false, UI, false),
    // Themes
    @Deprecated("Themes are a thing now")
    VANILLA_UI(I18n.format("optionDefaultUI"), false, false, THEME, true),
    @Deprecated("Themes are a thing now")
    SAO_UI(I18n.format("optionSAOUI"), true, false, THEME, true),
    // Health Options
    SMOOTH_HEALTH(I18n.format("optionSmoothHealth"), true, false, HEALTH_OPTIONS, false),
    HEALTH_BARS(I18n.format("optionHealthBars"), false, true, HEALTH_OPTIONS, false),
    REMOVE_HPXP(I18n.format("optionLightHud"), false, false, HEALTH_OPTIONS, false),
    ALT_ABSORB_POS(I18n.format("optionAltAbsorbPos"), false, false, HEALTH_OPTIONS, false),
    // Health Bars
    INNOCENT_HEALTH(I18n.format("optionInnocentHealthBars"), true, false, HEALTH_BARS, false),
    VIOLENT_HEALTH(I18n.format("optionViolentHealthBars"), true, false, HEALTH_BARS, false),
    KILLER_HEALTH(I18n.format("optionKillerHealthBars"), true, false, HEALTH_BARS, false),
    BOSS_HEALTH(I18n.format("optionBossHealthBars"), true, false, HEALTH_BARS, false),
    /*
    CREATIVE_HEALTH(I18n.format("optionCreativeHealthBars"), true, false, HEALTH_BARS, false),
    OP_HEALTH(I18n.format("optionOPHealthBars"), true, false, HEALTH_BARS, false),
    INVALID_HEALTH(I18n.format("optionInvalidHealthBars"), true, false, HEALTH_BARS, false),
    DEV_HEALTH(I18n.format("optionDevHealthBars"), true, false, HEALTH_BARS, false),*/
    //Hotbar
    DEFAULT_HOTBAR(I18n.format("optionDefaultHotbar"), false, false, HOTBAR_OPTIONS, true),
    HOR_HOTBAR(I18n.format("optionHorHotbar"), false, false, HOTBAR_OPTIONS, true),
    VER_HOTBAR(I18n.format("optionVerHotbar"), true, false, HOTBAR_OPTIONS, true),
    //Effects
    SPINNING_CRYSTALS(I18n.format("optionSpinning"), true, false, EFFECTS, false),
    PARTICLES(I18n.format("optionParticles"), true, false, EFFECTS, false),
    SOUND_EFFECTS(I18n.format("optionSounds"), true, false, EFFECTS, false),
    MOUSE_OVER_EFFECT(I18n.format("optionMouseOver"), true, false, EFFECTS, false),
    CRYSTALS(I18n.format("optionCursor"), false, true, EFFECTS, false),
    //CRYSTALS
    INNOCENT_CRYSTAL(I18n.format("optionInnocentCrystal"), true, false, CRYSTALS, false),
    VIOLENT_CRYSTAL(I18n.format("optionViolentCrystal"), true, false, CRYSTALS, false),
    KILLER_CRYSTAL(I18n.format("optionKillerCrystal"), true, false, CRYSTALS, false),
    BOSS_CRYSTAL(I18n.format("optionBossCrystal"), true, false, CRYSTALS, false),
    /*
    CREATIVE_CRYSTAL(I18n.format("optionCreativeCrystal"), true, false, CRYSTALS, false),
    OP_CRYSTAL(I18n.format("optionOPCrystal"), true, false, CRYSTALS, false),
    INVALID_CRYSTAL(I18n.format("optionInvalidCrystal"), true, false, CRYSTALS, false),
    DEV_CRYSTAL(I18n.format("optionDevCrystal"), true, false, CRYSTALS, false),*/

    //Misc
    AGGRO_SYSTEM(I18n.format("optionAggro"), true, false, MISC, false),
    MOUNT_STAT_VIEW(I18n.format("optionMountStatView"), true, false, MISC, false),
    CUSTOM_FONT(I18n.format("optionCustomFont"), false, false, MISC, false),
    COMPACT_INVENTORY(I18n.format("optionCompatInv"), false, false, MISC, false),
    TEXT_SHADOW(I18n.format("optionTextShadow"), true, false, MISC, false);
    // TODO: make a way for themes to register custom options?

    override fun toString() = name

    /**
     * This will flip the enabled state of the option and return the new value.
     * For category restrictions, this will always enable the option.

     * @return Returns the newly set value
     */
    fun flip(): Boolean {
        if (this.isRestricted) {
            values().filter { it.category == this.category }.forEach {
                it.value = false
                ConfigHandler.setOption(it) // TODO: transaction
            }
            this.value = true
        } else {
            this.value = !this.isEnabled
            if (this == CUSTOM_FONT) GLCore.setFont(Minecraft.getMinecraft(), this.value)
        }
        ConfigHandler.setOption(this)
        MinecraftForge.EVENT_BUS.post(OptionTriggerEvent(this))
        return this.value
    }

    /**
     * @return Returns true if the Option is selected/enabled
     */
    override fun isEnabled(): Boolean {
        return this.value
    }

    /**
     * This checks if the Option is restricted or not.
     * Restricted Options can only have one option enabled
     * in their Category.

     * @return Returns true if restricted
     */
    override fun isRestricted(): Boolean {
        return this.restricted
    }

    /**
     * @return Returns the Category
     */
    override fun getCategory(): OptionCore? {
        return this.category
    }

    /**
     * @return Returns the Category
     */
    fun getCategoryName(): String {
        return this.category?.name ?: "Options"
    }

    /**
     * This will disable the Option when called
     */
    fun disable() {
        if (this.value) this.flip()
    }

    /**
     * This will enable the Option when called
     */
    fun enable() {
        if (!this.value) this.flip()
    }

    val subOptions
        get() = values().filter { it.category == this }

    operator fun invoke() = isEnabled

    companion object {

        fun fromString(str: String): OptionCore? {
            return valueOf(str)
        }

        /**
         * Easy downcaster to use with JEL.

         * @param o the option to downcast
         * *
         * @return downcasted option
         */
        operator fun get(o: OptionCore): IOption {
            return o
        }

        /**
         * Easy getter to use with JEL.

         * @param o the option to get
         * *
         * @return whether the option is enabled
         */
        @JvmStatic
        fun isEnabled(o: OptionCore): Boolean {
            return o.isEnabled
        }

        val tlOptions
            get() = values().filter { it.category == null }
    }
}
