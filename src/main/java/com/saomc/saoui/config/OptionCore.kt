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
import com.saomc.saoui.api.info.IOption
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
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
    HEALTH_BARS(I18n.format("optionHealthBars"), true, false, HEALTH_OPTIONS, false),
    REMOVE_HPXP(I18n.format("optionLightHud"), false, false, HEALTH_OPTIONS, false),
    //DEFAULT_HEALTH(I18n.format("optionDefaultHealth"), false, false, HEALTH_OPTIONS, false),
    ALT_ABSORB_POS(I18n.format("optionAltAbsorbPos"), false, false, HEALTH_OPTIONS, false),
    MOB_HEALTH(I18n.format("optionMobHealth"), true, false, HEALTH_OPTIONS, false),
    //Hotbar
    DEFAULT_HOTBAR(I18n.format("optionDefaultHotbar"), false, false, HOTBAR_OPTIONS, true),
    HOR_HOTBAR(I18n.format("optionHorHotbar"), false, false, HOTBAR_OPTIONS, true),
    VER_HOTBAR(I18n.format("optionVerHotbar"), true, false, HOTBAR_OPTIONS, true),
    //Effects
    CURSOR_TOGGLE(I18n.format("optionCursorToggle"), true, false, EFFECTS, false),
    COLOR_CURSOR(I18n.format("optionColorCursor"), true, false, EFFECTS, false),
    SPINNING_CRYSTALS(I18n.format("optionSpinning"), true, false, EFFECTS, false),
    PARTICLES(I18n.format("optionParticles"), true, false, EFFECTS, false),
    LESS_VISUALS(I18n.format("optionLessVis"), false, false, EFFECTS, false),
    SOUND_EFFECTS(I18n.format("optionSounds"), true, false, EFFECTS, false),
    //Misc
    AGGRO_SYSTEM(I18n.format("optionAggro"), true, false, MISC, false),
    CLIENT_CHAT_PACKETS(I18n.format("optionCliChatPacks"), true, false, MISC, false),
    MOUNT_STAT_VIEW(I18n.format("optionMountStatView"), true, false, MISC, false),
    CUSTOM_FONT(I18n.format("optionCustomFont"), false, false, MISC, false),
    DEBUG_MODE(I18n.format("optionDebugMode"), false, false, MISC, false),
    COMPACT_INVENTORY(I18n.format("optionCompatInv"), false, false, MISC, false),
    TEXT_SHADOW(I18n.format("optionTextShadow"), true, false, MISC, false),
    //Debug
    DISABLE_TICKS(I18n.format("optionDisableTicks"), false, false, MISC, false),
    BUGGY_MENU(I18n.format("optionEnableMenus"), true, false, MISC, false),
    NOTICE(I18n.format("optionNotice"), true, false, MISC, false);

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
