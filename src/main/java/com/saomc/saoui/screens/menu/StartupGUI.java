package com.saomc.saoui.screens.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.saomc.saoui.SAOCore;
import com.saomc.saoui.config.OptionCore;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class StartupGUI extends GuiScreen {

    private static boolean isDev() {
        return SAOCore.VERSION.toLowerCase().contains("dev") && OptionCore.NOTICE.isEnabled();
    }

    private static boolean hasUpdate() {
        return false; //UpdateChecker.hasUpdate();
    }

    public static boolean shouldShow() {
        return isDev() || hasUpdate();
    }

    //This is used for dev builds, as dev builds are constant
    private static void handleGuiText(String text, FontRenderer fontRenderer, Gui gui, int width, int height) {

        int heightLoc = 85;

        String[] lines = text.split("\n");
        for (String s : lines) {

            java.util.List<String> info = fontRenderer.listFormattedStringToWidth(s, width - 40);
            for (String infoCut : info) {
                gui.drawCenteredString(fontRenderer, infoCut, width / 2, height / 2 - heightLoc, 0xFFFFFF);
                heightLoc = heightLoc - 12;
            }
        }
    }

    //This is used for changelogs, as changelogs have a varying amount
    private static void handleGuiText(List<String> text, FontRenderer fontRenderer, Gui gui, int width, int height) {

        int heightLoc = 85;

        for (String s : text) {

            if (s == null) text.spliterator().trySplit();
            else {
                java.util.List<String> info = fontRenderer.listFormattedStringToWidth(s, width - 40);
                for (String infoCut : info) {
                    gui.drawCenteredString(fontRenderer, infoCut, width / 2, height / 2 - heightLoc, 0xFFFFFF);
                    heightLoc = heightLoc - 12;
                }
            }
        }
    }

    @Override
    public void initGui() {
        if (isDev())
            this.buttonList.add(new GuiButton(0, this.width / 2 - 144, this.height / 2 + 96, 288, 20, "Accept"));
        else {
            this.buttonList.add(new GuiButton(0, this.width / 2 - 154, this.height / 2 + 96, 144, 20, "Ignore"));
            this.buttonList.add(new GuiButton(1, this.width / 2 + 10, this.height / 2 + 96, 144, 20, "Continue"));
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        drawDefaultBackground();
        drawCenteredString(this.fontRendererObj, TextFormatting.YELLOW + "NOTICE" + TextFormatting.RESET, this.width / 2, this.height / 2 - 100, 0xFFFFFF);

        if (isDev())
            handleGuiText(getDevText(), fontRendererObj, this, this.width, this.height);
//        else if (hasUpdate())
//            handleGuiText(UpdateChecker.fetchChangeLog(), fontRendererObj, this, this.width, this.height);

        super.drawScreen(par1, par2, par3);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (isDev())
            switch (button.id) {
                case 0: {
                    //noinspection ControlFlowStatementWithoutBraces
                    ((List<GuiButton>) buttonList).forEach(b -> b.enabled = false);
                    this.mc.displayGuiScreen(null);
                    break;
                }
            }
        /*else if (UpdateChecker.hasUpdate())
            switch (button.id) {
                case 0: {
                    buttonList.forEach(b -> b.enabled = false);
                    ConfigHandler.setIgnoreVersion(true);
                    this.mc.displayGuiScreen(null);
                    break;
                }
                case 1: {
                    buttonList.forEach(b -> b.enabled = false);
                    this.mc.displayGuiScreen(null);
                    break;
                }
            }*/
    }

    @Override
    protected void keyTyped(char par1, int par2) {
    }

    private String getDevText() {
        return "This is a development build of the SAO UI mod\n\n" +
                "Bugs are to be expected, as features may be incomplete, code may be unfinished, and things may just simply not work.\n" +
                "Please report all bugs in the discord group, and don't distribute or modify this build without permission.";
    }

    /**
     * Yes, this is boldly copied over from 1.12.2.
     */
    public enum TextFormatting {
        BLACK("BLACK", '0', 0),
        DARK_BLUE("DARK_BLUE", '1', 1),
        DARK_GREEN("DARK_GREEN", '2', 2),
        DARK_AQUA("DARK_AQUA", '3', 3),
        DARK_RED("DARK_RED", '4', 4),
        DARK_PURPLE("DARK_PURPLE", '5', 5),
        GOLD("GOLD", '6', 6),
        GRAY("GRAY", '7', 7),
        DARK_GRAY("DARK_GRAY", '8', 8),
        BLUE("BLUE", '9', 9),
        GREEN("GREEN", 'a', 10),
        AQUA("AQUA", 'b', 11),
        RED("RED", 'c', 12),
        LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13),
        YELLOW("YELLOW", 'e', 14),
        WHITE("WHITE", 'f', 15),
        OBFUSCATED("OBFUSCATED", 'k', true),
        BOLD("BOLD", 'l', true),
        STRIKETHROUGH("STRIKETHROUGH", 'm', true),
        UNDERLINE("UNDERLINE", 'n', true),
        ITALIC("ITALIC", 'o', true),
        RESET("RESET", 'r', -1);


        /** Maps a name (e.g., 'underline') to its corresponding enum value (e.g., UNDERLINE). */
        private static final Map<String, TextFormatting> NAME_MAPPING = Maps.<String, TextFormatting>newHashMap();
        /**
         * Matches formatting codes that indicate that the client should treat the following text as bold, recolored,
         * obfuscated, etc.
         */
        private static final Pattern FORMATTING_CODE_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");
        /** The name of this color/formatting */
        private final String name;
        /** The formatting code that produces this format. */
        private final char formattingCode;
        private final boolean fancyStyling;
        /**
         * The control string (section sign + formatting code) that can be inserted into client-side text to display
         * subsequent text in this format.
         */
        private final String controlString;
        /** The numerical index that represents this color */
        private final int colorIndex;

        private static String lowercaseAlpha(String p_175745_0_)
        {
            return p_175745_0_.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
        }

        private TextFormatting(String formattingName, char formattingCodeIn, int colorIndex)
        {
            this(formattingName, formattingCodeIn, false, colorIndex);
        }

        private TextFormatting(String formattingName, char formattingCodeIn, boolean fancyStylingIn)
        {
            this(formattingName, formattingCodeIn, fancyStylingIn, -1);
        }

        private TextFormatting(String formattingName, char formattingCodeIn, boolean fancyStylingIn, int colorIndex)
        {
            this.name = formattingName;
            this.formattingCode = formattingCodeIn;
            this.fancyStyling = fancyStylingIn;
            this.colorIndex = colorIndex;
            this.controlString = "\u00a7" + formattingCodeIn;
        }

        /**
         * Returns the numerical color index that represents this formatting
         */
        public int getColorIndex()
        {
            return this.colorIndex;
        }

        /**
         * False if this is just changing the color or resetting; true otherwise.
         */
        public boolean isFancyStyling()
        {
            return this.fancyStyling;
        }

        /**
         * Checks if this is a color code.
         */
        public boolean isColor()
        {
            return !this.fancyStyling && this != RESET;
        }

        /**
         * Gets the friendly name of this value.
         */
        public String getFriendlyName()
        {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public String toString()
        {
            return this.controlString;
        }

        /**
         * Returns a copy of the given string, with formatting codes stripped away.
         */
        @Nullable
        public static String getTextWithoutFormattingCodes(@Nullable String text)
        {
            return text == null ? null : FORMATTING_CODE_PATTERN.matcher(text).replaceAll("");
        }

        /**
         * Gets a value by its friendly name; null if the given name does not map to a defined value.
         */
        @Nullable
        public static TextFormatting getValueByName(@Nullable String friendlyName)
        {
            return friendlyName == null ? null : (TextFormatting)NAME_MAPPING.get(lowercaseAlpha(friendlyName));
        }

        /**
         * Get a TextFormatting from it's color index
         */
        @Nullable
        public static TextFormatting fromColorIndex(int index)
        {
            if (index < 0)
            {
                return RESET;
            }
            else
            {
                for (TextFormatting textformatting : values())
                {
                    if (textformatting.getColorIndex() == index)
                    {
                        return textformatting;
                    }
                }

                return null;
            }
        }

        /**
         * Gets all the valid values.
         */
        public static Collection<String> getValidValues(boolean p_96296_0_, boolean p_96296_1_)
        {
            List<String> list = Lists.<String>newArrayList();

            for (TextFormatting textformatting : values())
            {
                if ((!textformatting.isColor() || p_96296_0_) && (!textformatting.isFancyStyling() || p_96296_1_))
                {
                    list.add(textformatting.getFriendlyName());
                }
            }

            return list;
        }

        static
        {
            for (TextFormatting textformatting : values())
            {
                NAME_MAPPING.put(lowercaseAlpha(textformatting.name), textformatting);
            }
        }
    }

}
