package com.saomc.saoui.util

import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.realms.RealmsBridge


object UIUtil {

    val mc = Minecraft()

    fun closeGame(){
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
