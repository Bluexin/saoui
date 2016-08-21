package com.saomc.saoui.screens.menu;

import com.saomc.saoui.SAOCore;
import com.saomc.saoui.events.ConfigHandler;
import com.saomc.saoui.util.UpdateChecker;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class StartupGUI extends GuiScreen {

    private static boolean isDev() {
        return SAOCore.VERSION.contains("Dev");
    }

    private static boolean hasUpdate() {
        return UpdateChecker.hasUpdate();
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
        GlStateManager.disableTexture2D();
        GlStateManager.enableTexture2D();

        drawDefaultBackground();
        drawCenteredString(this.fontRendererObj, TextFormatting.YELLOW + "NOTICE" + TextFormatting.RESET, this.width / 2, this.height / 2 - 100, 0xFFFFFF);

        if (isDev())
            handleGuiText(getDevText(), fontRendererObj, this, this.width, this.height);
        else if (hasUpdate())
            handleGuiText(UpdateChecker.fetchChangeLog(), fontRendererObj, this, this.width, this.height);

        super.drawScreen(par1, par2, par3);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (isDev())
            switch (button.id) {
                case 0: {
                    buttonList.forEach(b -> b.enabled = false);
                    this.mc.displayGuiScreen(null);
                    break;
                }
            }
        else if (UpdateChecker.hasUpdate())
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
            }
    }

    @Override
    protected void keyTyped(char par1, int par2) {
    }

    private String getDevText() {
        return "This is a development build of the SAO UI mod\n\n" +
                "Bugs are to be expected, as features may be incomplete, code may be unfinished, and things may just simply not work.\n" +
                "Please report all bugs in the discord group, and don't distribute or modify this build without permission.";
    }

}
