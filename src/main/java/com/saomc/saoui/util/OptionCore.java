package com.saomc.saoui.util;

import com.saomc.saoui.GLCore;
import com.saomc.saoui.events.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.stream.Stream;

@SideOnly(Side.CLIENT)
public enum OptionCore {

    //Main Categories
    VANILLA_OPTIONS(I18n.translateToLocal("guiOptions"), false, false, null, false),
    UI(I18n.translateToLocal("optCatUI"), false, true, null, false),
    THEME(I18n.translateToLocal("optTheme"), false, true, null, false),
    HEALTH_OPTIONS(I18n.translateToLocal("optCatHealth"), false, true, null, false),
    HOTBAR_OPTIONS(I18n.translateToLocal("optCatHotBar"), false, true, null, false),
    EFFECTS(I18n.translateToLocal("optCatEffects"), false, true, null, false),
    MISC(I18n.translateToLocal("optCatMisc"), false, true, null, false),
    //General UI Settings
    UI_ONLY(I18n.translateToLocal("optionUIOnly"), false, false, UI, false),
    DEFAULT_INVENTORY(I18n.translateToLocal("optionDefaultInv"), true, false, UI, false),
    DEFAULT_DEATH_SCREEN(I18n.translateToLocal("optionDefaultDeath"), false, false, UI, false),
    DEFAULT_DEBUG(I18n.translateToLocal("optionDefaultDebug"), false, false, UI, false),
    FORCE_HUD(I18n.translateToLocal("optionForceHud"), false, false, UI, false),
    LOGOUT(I18n.translateToLocal("optionLogout"), false, false, UI, false),
    GUI_PAUSE(I18n.translateToLocal("optionGuiPause"), true, false, UI, false),
    // Themes
    VANILLA_UI(I18n.translateToLocal("optionDefaultUI"), false, false, THEME, true),
    ALO_UI(I18n.translateToLocal("optionALOUI"), false, false, THEME, true),
    SAO_UI(I18n.translateToLocal("optionSAOUI"), true, false, THEME, true),
    // Health Options
    SMOOTH_HEALTH(I18n.translateToLocal("optionSmoothHealth"), true, false, HEALTH_OPTIONS, false),
    HEALTH_BARS(I18n.translateToLocal("optionHealthBars"), true, false, HEALTH_OPTIONS, false),
    REMOVE_HPXP(I18n.translateToLocal("optionLightHud"), false, false, HEALTH_OPTIONS, false),
    //DEFAULT_HEALTH(I18n.translateToLocal("optionDefaultHealth"), false, false, HEALTH_OPTIONS, false),
    ALT_ABSORB_POS(I18n.translateToLocal("optionAltAbsorbPos"), false, false, HEALTH_OPTIONS, false),
    //Hotbar
    DEFAULT_HOTBAR(I18n.translateToLocal("optionDefaultHotbar"), false, false, HOTBAR_OPTIONS, true),
    HOR_HOTBAR(I18n.translateToLocal("optionHorHotbar"), false, false, HOTBAR_OPTIONS, true),
    VER_HOTBAR(I18n.translateToLocal("optionVerHotbar"), false, false, HOTBAR_OPTIONS, true),
    //Effects
    CURSOR_TOGGLE(I18n.translateToLocal("optionCursorToggle"), true, false, EFFECTS, false),
    COLOR_CURSOR(I18n.translateToLocal("optionColorCursor"), true, false, EFFECTS, false),
    SPINNING_CRYSTALS(I18n.translateToLocal("optionSpinning"), true, false, EFFECTS, false),
    PARTICLES(I18n.translateToLocal("optionParticles"), true, false, EFFECTS, false),
    LESS_VISUALS(I18n.translateToLocal("optionLessVis"), false, false, EFFECTS, false),
    SOUND_EFFECTS(I18n.translateToLocal("optionSounds"), true, false, EFFECTS, false),
    //Misc
    CROSS_HAIR(I18n.translateToLocal("optionCrossHair"), false, false, MISC, false),
    AGGRO_SYSTEM(I18n.translateToLocal("optionAggro"), true, false, MISC, false),
    CLIENT_CHAT_PACKETS(I18n.translateToLocal("optionCliChatPacks"), true, false, MISC, false),
    MOUNT_STAT_VIEW(I18n.translateToLocal("optionMountStatView"), true, false, MISC, false),
    CUSTOM_FONT(I18n.translateToLocal("optionCustomFont"), false, false, MISC, false),
    DEBUG_MODE(I18n.translateToLocal("optionDebugMode"), false, false, MISC, false),
    COMPACT_INVENTORY(I18n.translateToLocal("optionCompatInv"), false, false, MISC, false),
    //Debug
    DISABLE_TICKS(I18n.translateToLocal("optionDisableTicks"), false, false, MISC, false);

    public final String name;
    public final boolean isCategory;
    public final OptionCore category;
    private boolean value;
    private boolean restricted;

    OptionCore(String optionName, boolean defaultValue, boolean isCat, OptionCore category, boolean onlyOne) {
        name = optionName;
        value = defaultValue;
        isCategory = isCat;
        this.category = category;
        restricted = onlyOne;
    }

    public static OptionCore fromString(String str) {
        return Stream.of(values()).filter(option -> option.toString().equals(str)).findAny().orElse(null);
    }

    @Override
    public final String toString() {
        return name;
    }

    public boolean flip() {
        this.value = !this.getValue();
        ConfigHandler.setOption(this);
        if (this == CUSTOM_FONT) GLCore.setFont(Minecraft.getMinecraft(), this.value);
        return this.value;
    }

    public boolean getValue() {
        return this.value;
    }

    public boolean isRestricted() {
        return this.restricted;
    }

    public OptionCore getCategory() {
        return this.category;
    }

    public void disable() {
        if (this.value) this.flip();
    }

    public void enable() {
        if (!this.value) this.flip();
    }
}
