package be.bluexin.mcui.util

import be.bluexin.mcui.util.Client
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.realms.RealmsBridge

object UIUtil {

    val mc = Client.mc

    fun closeGame() {
        // This check needs to happen before disconnecting packet.
        val singlePlayerCheck = mc.isIntegratedServerRunning
        val realmsCheck = mc.isConnectedToRealms
        this.mc.currentScreen = null
        this.mc.world.sendQuittingDisconnectingPacket()
        this.mc.loadWorld(null)

        when {
            singlePlayerCheck -> {
                this.mc.setScreen(GuiMainMenu())
            }
            realmsCheck -> {
                RealmsBridge().switchToRealms(GuiMainMenu())
            }
            else -> {
                this.mc.setScreen(GuiMultiplayer(GuiMainMenu()))
            }
        }
    }
}
