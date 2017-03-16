package com.saomc.saoui.events;

import be.bluexin.saouintw.communication.Command;
import com.saomc.saoui.SoundCore;
import com.saomc.saoui.elements.ElementBuilder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import static com.saomc.saoui.events.EventCore.mc;

@SideOnly(Side.CLIENT)
public class EventHandler {

    public static boolean IS_SPRINTING = false;
    public static boolean IS_SNEAKING = false;

    static void nameNotification(ClientChatReceivedEvent e) {
        if (!(mc.currentScreen instanceof GuiConnecting) && e.message.getUnformattedText().contains(mc.thePlayer.getDisplayName()))
            SoundCore.play(mc, SoundCore.MESSAGE);
    }

    static void abilityCheck() {
        if (mc.thePlayer == null) {
            IS_SPRINTING = false;
            IS_SNEAKING = false;
        } else if (mc.inGameHasFocus) {
            if (IS_SPRINTING) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
            if (IS_SNEAKING) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
        }
    }

    static void chatCommand(ClientChatReceivedEvent evt) {
        if (!(mc.currentScreen instanceof GuiConnecting) && Command.processCommand(evt.message.getUnformattedText()))
            evt.setCanceled(true);// TODO: add pm feature and PT chat
    }

    static void cleanTempElements(){
        ElementBuilder.getInstance().cleanTempElements();
    }
}

