package com.tencao.saoui.util

import com.tencao.saomclib.Client
import net.minecraft.client.gui.screen.MainMenuScreen
import net.minecraft.client.gui.screen.MultiplayerScreen
import net.minecraft.realms.RealmsBridgeScreen

object UIUtil {

    fun closeGame() {
        // This check needs to happen before disconnecting packet.
        val singlePlayerCheck = Client.minecraft.isLocalServer
        val realmsCheck = Client.minecraft.isConnectedToRealms
        Client.minecraft.screen = null
        Client.minecraft.level?.disconnect()
        Client.minecraft.clearLevel()

        when {
            singlePlayerCheck -> {
                Client.minecraft.setScreen(MainMenuScreen())
            }
            realmsCheck -> {
                RealmsBridgeScreen().switchToRealms(MainMenuScreen())
            }
            else -> {
                Client.minecraft.setScreen(MultiplayerScreen(MainMenuScreen()))
            }
        }
    }

    fun resetMouse() {
        if (!Client.minecraft.mouseHandler.mouseGrabbed) {
            Client.minecraft.mouseHandler.mouseGrabbed = true
            Client.minecraft.mouseHandler.releaseMouse()
        }
    }
}
