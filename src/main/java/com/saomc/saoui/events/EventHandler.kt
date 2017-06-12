package com.saomc.saoui.events

import be.bluexin.saouintw.communication.Command
import com.saomc.saoui.SoundCore
import net.minecraft.client.multiplayer.GuiConnecting
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

import com.saomc.saoui.events.EventCore.Companion.mc

@SideOnly(Side.CLIENT)
object EventHandler {

    var IS_SPRINTING = false
    var IS_SNEAKING = false

    internal fun nameNotification(e: ClientChatReceivedEvent) {
        if (mc.currentScreen !is GuiConnecting && e.message.unformattedText.contains(mc.player.getDisplayNameString()))
            SoundCore.play(mc, SoundCore.MESSAGE)
    }

    internal fun abilityCheck() {
        if (mc.player == null) {
            IS_SPRINTING = false
            IS_SNEAKING = false
        } else if (mc.inGameHasFocus) {
            if (IS_SPRINTING) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true)
            if (IS_SNEAKING) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true)
        }
    }

    internal fun chatCommand(evt: ClientChatReceivedEvent) {
        if (mc.currentScreen !is GuiConnecting && Command.processCommand(evt.message.unformattedText))
            evt.isCanceled = true// TODO: add pm feature and PT chat
    }

}

