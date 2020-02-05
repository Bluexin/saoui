package com.saomc.saoui.util

import com.teamwizardry.librarianlib.features.helpers.setNBTTag
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagString
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

    fun getCustomHead(playerName: String): ItemStack {
        val customHead = ItemStack(Items.SKULL, 1, 3)
        customHead.setNBTTag("SkullOwner", NBTTagString(playerName))
        return customHead
    }
}