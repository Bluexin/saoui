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
        val unformattedName: String,
        val displayName: String = I18n.format(unformattedName),
        val description: List<String> = listOf( I18n.format("$unformattedName.desc")),
        private var value: Boolean,
        /**
         * @return Returns true if this is a Category or not
         */
        val isCategory: Boolean,
        private val category: OptionCore?,
        private val restricted: Boolean
) : IOption {

    //Main Categories
    UI("optCatUI", value = false, isCategory = true, category = null, restricted = false),
    THEME("optTheme", value = false, isCategory = true, category = null, restricted = false),
    ENTITIES("optTheme", value = false, isCategory = true, category = null, restricted = false),
    HEALTH_OPTIONS("optCatHealth", value = false, isCategory = true, category = null, restricted = false),
    HOTBAR_OPTIONS("optCatHotBar", value = false, isCategory = true, category = null, restricted = false),
    EFFECTS("optCatEffects", value = false, isCategory = true, category = null, restricted = false),
    MISC("optCatMisc", value = false, isCategory = true, category = null, restricted = false),
    //General UI Settings
    UI_ONLY("optionUIOnly", value = false, isCategory = false, category = UI, restricted = false),
    DEFAULT_INVENTORY("optionDefaultInv", value = true, isCategory = false, category = UI, restricted = false),
    DEFAULT_DEATH_SCREEN("optionDefaultDeath", value = false, isCategory = false, category = UI, restricted = false),
    DEFAULT_DEBUG("optionDefaultDebug", value = false, isCategory = false, category = UI, restricted = false),
    FORCE_HUD("optionForceHud", value = true, isCategory = false, category = UI, restricted = false),
    LOGOUT("optionLogout", value = false, isCategory = false, category = UI, restricted = false),
    GUI_PAUSE("optionGuiPause", value = false, isCategory = false, category = UI, restricted = false),
    UI_MOVEMENT("optionUIMovement", value = true, isCategory = false, category = UI, restricted = false),
    // Themes
    @Deprecated("Themes are a thing now")
    VANILLA_UI("optionDefaultUI", value = false, isCategory = false, category = THEME, restricted = true),
    @Deprecated("Themes are a thing now")
    SAO_UI("optionSAOUI", value = true, isCategory = false, category = THEME, restricted = true),
    // Entity Options
    ENTITY_HEALTH_BARS("optionEntityHealthBars", value = false, isCategory = true, category = ENTITIES, restricted = false),
    ENTITY_CRYSTALS("optionEntityCrystals", value = false, isCategory = true, category = ENTITIES, restricted = false),
    // Health Options
    SMOOTH_HEALTH("optionSmoothHealth", value = true, isCategory = false, category = HEALTH_OPTIONS, restricted = false),
    REMOVE_HPXP("optionLightHud", value = false, isCategory = false, category = HEALTH_OPTIONS, restricted = false),
    ALT_ABSORB_POS("optionAltAbsorbPos", value = false, isCategory = false, category = HEALTH_OPTIONS, restricted = false),
    HIDE_OFFLINE_PARTY("optionHideOfflineParty", value = false, isCategory = false, category = HEALTH_OPTIONS, restricted = false),
    // Health Bars
    INNOCENT_HEALTH("optionInnocentHealthBars", value = true, isCategory = false, category = ENTITY_HEALTH_BARS, restricted = false),
    VIOLENT_HEALTH("optionViolentHealthBars", value = true, isCategory = false, category = ENTITY_HEALTH_BARS, restricted = false),
    KILLER_HEALTH("optionKillerHealthBars", value = true, isCategory = false, category = ENTITY_HEALTH_BARS, restricted = false),
    BOSS_HEALTH("optionBossHealthBars", value = true, isCategory = false, category = ENTITY_HEALTH_BARS, restricted = false),
    CREATIVE_HEALTH("optionCreativeHealthBars", value = true, isCategory = false, category = ENTITY_HEALTH_BARS, restricted = false),
    OP_HEALTH("optionOPHealthBars", value = true, isCategory = false, category = ENTITY_HEALTH_BARS, restricted = false),
    INVALID_HEALTH("optionInvalidHealthBars", value = true, isCategory = false, category = ENTITY_HEALTH_BARS, restricted = false),
    DEV_HEALTH("optionDevHealthBars", value = true, isCategory = false, category = ENTITY_HEALTH_BARS, restricted = false),
    //CRYSTALS
    INNOCENT_CRYSTAL("optionInnocentCrystal", value = true, isCategory = false, category = ENTITY_CRYSTALS, restricted = false),
    VIOLENT_CRYSTAL("optionViolentCrystal", value = true, isCategory = false, category = ENTITY_CRYSTALS, restricted = false),
    KILLER_CRYSTAL("optionKillerCrystal", value = true, isCategory = false, category = ENTITY_CRYSTALS, restricted = false),
    BOSS_CRYSTAL("optionBossCrystal", value = true, isCategory = false, category = ENTITY_CRYSTALS, restricted = false),
    CREATIVE_CRYSTAL("optionCreativeCrystal", value = true, isCategory = false, category = ENTITY_CRYSTALS, restricted = false),
    OP_CRYSTAL("optionOPCrystal", value = true, isCategory = false, category = ENTITY_CRYSTALS, restricted = false),
    INVALID_CRYSTAL("optionInvalidCrystal", value = true, isCategory = false, category = ENTITY_CRYSTALS, restricted = false),
    DEV_CRYSTAL("optionDevCrystal", value = true, isCategory = false, category = ENTITY_CRYSTALS, restricted = false),
    //Hotbar
    DEFAULT_HOTBAR("optionDefaultHotbar", value = false, isCategory = false, category = HOTBAR_OPTIONS, restricted = true),
    HOR_HOTBAR("optionHorHotbar", value = false, isCategory = false, category = HOTBAR_OPTIONS, restricted = true),
    VER_HOTBAR("optionVerHotbar", value = true, isCategory = false, category = HOTBAR_OPTIONS, restricted = true),
    //Effects
    SPINNING_CRYSTALS("optionSpinning", value = true, isCategory = false, category = EFFECTS, restricted = false),
    PARTICLES("optionParticles", value = true, isCategory = false, category = EFFECTS, restricted = false),
    SOUND_EFFECTS("optionSounds", value = true, isCategory = false, category = EFFECTS, restricted = false),
    MOUSE_OVER_EFFECT("optionMouseOver", value = true, isCategory = false, category = EFFECTS, restricted = false),
    //Misc
    AGGRO_SYSTEM("optionAggro", value = true, isCategory = false, category = MISC, restricted = false),
    MOUNT_STAT_VIEW("optionMountStatView", value = true, isCategory = false, category = MISC, restricted = false),
    CUSTOM_FONT("optionCustomFont", value = false, isCategory = false, category = MISC, restricted = false),
    TEXT_SHADOW("optionTextShadow", value = true, isCategory = false, category = MISC, restricted = false);
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
