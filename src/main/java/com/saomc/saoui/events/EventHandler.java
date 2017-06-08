package com.saomc.saoui.events;

import be.bluexin.saouintw.communication.Command;
import com.saomc.saoui.SoundCore;
import com.saomc.saoui.elements.ElementBuilder;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.saomc.saoui.events.EventCore.mc;

@SideOnly(Side.CLIENT)
public class EventHandler {

    public static boolean IS_SPRINTING = false;
    public static boolean IS_SNEAKING = false;

    static void nameNotification(ClientChatReceivedEvent e) {
        if (!(mc.currentScreen instanceof GuiConnecting) && e.getMessage().getUnformattedText().contains(mc.player.getDisplayNameString()))
            SoundCore.play(mc, SoundCore.MESSAGE);
    }

    static void abilityCheck() {
        if (mc.player == null) {
            IS_SPRINTING = false;
            IS_SNEAKING = false;
        } else if (mc.inGameHasFocus) {
            if (IS_SPRINTING) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
            if (IS_SNEAKING) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
        }
    }

    static void chatCommand(ClientChatReceivedEvent evt) {
        if (!(mc.currentScreen instanceof GuiConnecting) && Command.processCommand(evt.getMessage().getUnformattedText()))
            evt.setCanceled(true);// TODO: add pm feature and PT chat
    }

    static void cleanTempElements(){
        ElementBuilder.Companion.getInstance().cleanTempElements();
    }
}

