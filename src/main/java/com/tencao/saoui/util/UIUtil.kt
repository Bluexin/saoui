package com.tencao.saoui.util

import com.tencao.saomclib.Client
import net.minecraft.client.gui.screen.MainMenuScreen
import net.minecraft.client.gui.screen.MultiplayerScreen
import net.minecraft.realms.RealmsBridgeScreen

object UIUtil {

    fun closeGame() {
        // This check needs to happen before disconnecting packet.
        val singlePlayerCheck = Client.minecraft.isIntegratedServerRunning
        val realmsCheck = Client.minecraft.isConnectedToRealms
        Client.minecraft.currentScreen = null
        Client.minecraft.world?.sendQuittingDisconnectingPacket()
        Client.minecraft.unloadWorld()

        when {
            singlePlayerCheck -> {
                Client.minecraft.displayGuiScreen(MainMenuScreen())
            }
            realmsCheck -> {
                RealmsBridgeScreen().func_231394_a_(MainMenuScreen())
            }
            else -> {
                Client.minecraft.displayGuiScreen(MultiplayerScreen(MainMenuScreen()))
            }
        }
    }

    fun resetMouse() {
        if (!Client.minecraft.mouseHelper.mouseGrabbed) {
            Client.minecraft.mouseHelper.mouseGrabbed = true
            Client.minecraft.mouseHelper.ungrabMouse()
        }
    }
}
