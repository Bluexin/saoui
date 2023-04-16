package com.tencao.saoui.util

import com.tencao.saomclib.Client
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.realms.RealmsBridge

object UIUtil {

    val mc = Client.minecraft

    fun closeGame() {
        // This check needs to happen before disconnecting packet.
        val singlePlayerCheck = mc.isIntegratedServerRunning
        val realmsCheck = mc.isConnectedToRealms
        this.mc.currentScreen = null
        this.mc.world.sendQuittingDisconnectingPacket()
        this.mc.loadWorld(null)

        when {
            singlePlayerCheck -> {
                this.mc.displayGuiScreen(GuiMainMenu())
            }
            realmsCheck -> {
                RealmsBridge().switchToRealms(GuiMainMenu())
            }
            else -> {
                this.mc.displayGuiScreen(GuiMultiplayer(GuiMainMenu()))
            }
        }
    }
}
