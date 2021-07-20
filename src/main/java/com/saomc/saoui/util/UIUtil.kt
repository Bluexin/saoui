package com.saomc.saoui.util

import com.teamwizardry.librarianlib.core.util.Client
import net.minecraft.client.gui.screen.MainMenuScreen
import net.minecraft.client.gui.screen.MultiplayerScreen
import net.minecraft.realms.RealmsBridgeScreen


object UIUtil {

    val mc = Client.minecraft

    fun closeGame(){
        // This check needs to happen before disconnecting packet.
        val singlePlayerCheck = mc.isIntegratedServerRunning
        val realmsCheck = mc.isConnectedToRealms
        this.mc.currentScreen = null
        this.mc.world?.sendQuittingDisconnectingPacket()
        this.mc.unloadWorld()

        when {
            singlePlayerCheck -> {
                this.mc.displayGuiScreen(MainMenuScreen())
            }
            realmsCheck -> {
                RealmsBridgeScreen().func_231394_a_(MainMenuScreen())
            }
            else -> {
                this.mc.displayGuiScreen(MultiplayerScreen(MainMenuScreen()))
            }
        }
    }
}
