package com.saomc.saoui.events;

import com.saomc.saoui.SoundCore;
import com.saomc.saoui.api.events.ElementAction;
import com.saomc.saoui.api.screens.Actions;
import com.saomc.saoui.api.screens.ElementType;
import com.saomc.saoui.elements.ElementBuilder;
import com.saomc.saoui.screens.menu.IngameMenuGUI;
import com.saomc.saoui.util.LogCore;
import com.saomc.saoui.util.OptionCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptions;

import java.util.stream.Stream;

import static com.saomc.saoui.events.EventCore.mc;

/**
 * This handles and controls the default event, and our custom events for slots
 *
 * Created by Tencao on 18/08/2016.
 */
public class ElementHandler {

    protected static void defaultActions(ElementAction e){
        if (e.getAction() == Actions.LEFT_RELEASED) {
            LogCore.logDebug("Category - " + e.getCategory());
            LogCore.logDebug("Parent - " + e.getParent());
            LogCore.logDebug("isLocked - " + e.isLocked());
            LogCore.logDebug("isOpen - " + e.isOpen());
            if (e.getElementType() == ElementType.OPTION) optionAction(e);
            if (e.getElementType() == ElementType.SLOT) slotAction(e);
            if (e.isOpen()) {
                ElementBuilder.getInstance().disableChildElements(e.getCategory(), e.getParent(), e.getGui());
                SoundCore.play(Minecraft.getMinecraft().getSoundHandler(), SoundCore.DIALOG_CLOSE);
            } else if (!e.isOpen() && !e.isLocked()) {
                ElementBuilder.getInstance().enableChildElements(e.getCategory(), e.getParent(), e.getGui());
                SoundCore.play(Minecraft.getMinecraft().getSoundHandler(), SoundCore.MENU_POPUP);
            }
        }
    }

    private static void optionAction(ElementAction e){
        OptionCore option = OptionCore.fromString(e.getName());
        if (option.isRestricted()){
            if (!option.isEnabled()){
                Stream.of(OptionCore.values()).filter(opt -> opt.getCategory() == option.getCategory()).forEach(OptionCore::disable);
                option.enable();
            }
        } else option.flip();
    }

    private static void slotAction(ElementAction e){
        if (e.getCategory().equals("logout") && e.getParent().equals("settings") && OptionCore.LOGOUT.isEnabled()) logoutButton();
        if (e.getCategory().equals("menu") && e.getParent().equals("settings")) menuButton();
        if (e.getCategory().equals(OptionCore.VANILLA_OPTIONS.getName().toLowerCase()) && e.getParent().equals("settings")) vanillaOptions();
        if (e.getCategory().equals("prompt") && e.getParent().equals("profile")) promptButton(e);
    }

    private static void menuButton(){
        if (mc.currentScreen != null && mc.currentScreen instanceof IngameMenuGUI) {
            mc.currentScreen.onGuiClosed();
            mc.displayGuiScreen(new GuiIngameMenu());
        }
    }

    private static void vanillaOptions(){
        if (mc.currentScreen != null && mc.currentScreen instanceof IngameMenuGUI) {
            mc.currentScreen.onGuiClosed();
            mc.displayGuiScreen(new GuiOptions(mc.currentScreen, mc.gameSettings));
        }
    }

    private static void logoutButton(){
        if (mc.currentScreen != null && mc.currentScreen instanceof IngameMenuGUI) {
            mc.currentScreen.onGuiClosed();
            mc.theWorld.sendQuittingDisconnectingPacket();

            mc.loadWorld(null);
            mc.displayGuiScreen(new GuiMainMenu());
        }
    }

    private static void promptButton(ElementAction e){
        String message = "This is load simple test prompt testing the window and how it handles long strings of text. This test is using the single button display mode with an event firing once the button has been hit";
        //new GuiOpenEvent(new WindowView("Test Prompt", false, message, WindowAlign.HORIZONTAL_CENTER, WindowAlign.VERTICAL_CENTER, e.getParentElement()));
    }
}
