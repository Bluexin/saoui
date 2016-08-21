package com.saomc.saoui.events;

import com.saomc.saoui.SoundCore;
import com.saomc.saoui.communication.Command;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EventHandler {

    public static boolean IS_SPRINTING = false;
    public static boolean IS_SNEAKING = false;

    static void nameNotification(ClientChatReceivedEvent e) {
        if (!(EventCore.mc.currentScreen instanceof GuiConnecting) && e.getMessage().getUnformattedText().contains(EventCore.mc.thePlayer.getDisplayNameString()))
            SoundCore.play(EventCore.mc, SoundCore.MESSAGE);
    }

    static void abilityCheck() {
        if (EventCore.mc.thePlayer == null) {
            IS_SPRINTING = false;
            IS_SNEAKING = false;
        } else if (EventCore.mc.inGameHasFocus) {
            if (IS_SPRINTING) KeyBinding.setKeyBindState(EventCore.mc.gameSettings.keyBindSprint.getKeyCode(), true);
            if (IS_SNEAKING) KeyBinding.setKeyBindState(EventCore.mc.gameSettings.keyBindSneak.getKeyCode(), true);
        }
    }

    static void chatCommand(ClientChatReceivedEvent evt) {
        if (!(EventCore.mc.currentScreen instanceof GuiConnecting) && Command.processCommand(evt.getMessage().getUnformattedText()))
            evt.setCanceled(true);// TODO: add pm feature and PT chat
    }

}

