package com.saomc.saoui.events

import com.saomc.saoui.SAOCore
import com.saomc.saoui.SoundCore
import com.saomc.saoui.api.elements.CategoryEnum
import com.saomc.saoui.api.elements.ElementDefEnum
import com.saomc.saoui.api.events.ElementAction
import com.saomc.saoui.api.screens.Actions
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.events.EventCore.mc
import com.saomc.saoui.neo.screens.IngameMenuGUI
import com.saomc.saoui.social.StaticPlayerHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiIngameMenu
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiOptions
import net.minecraft.entity.player.EntityPlayer

/**
 * This handles and controls the default event, and our custom events for slots

 * Created by Tencao on 18/08/2016.
 */
object ElementHandler {

    fun defaultActions(e: ElementAction) {
        if (e.action == Actions.LEFT_RELEASED) {
            when (e.elementType) {
                ElementDefEnum.CATEGORY -> {
                    try {
                        val category = CategoryEnum.valueOf(e.name.toUpperCase())
                        if (e.isOpen) {
                            e.parent.closeCategory(category)
                            SoundCore.play(Minecraft.getMinecraft().soundHandler, SoundCore.DIALOG_CLOSE)
                        } else if (!e.isOpen && !e.isLocked) {
                            e.parent.openCategory(category)
                            SoundCore.play(Minecraft.getMinecraft().soundHandler, SoundCore.MENU_POPUP)
                        }
                    } catch (error: IllegalArgumentException) {
                        SAOCore.LOGGER.fatal("Element: " + e.name + " incorrectly set isCategory with no matching categories")
                    }
                }
                ElementDefEnum.OPTION -> {
                    try {
                        OptionCore.valueOf(e.name).flip()
                    } catch (ignored: IllegalArgumentException) {
                    }
                }
                ElementDefEnum.BUTTON -> optionAction(e)
                ElementDefEnum.PLAYER -> {
                    val player: EntityPlayer? = Minecraft.getMinecraft().world.getPlayerEntityByName(e.name)
                    if (player != null) {
                        val pt = StaticPlayerHelper.getIParty()
                        val check = pt.invite(player)
                        if (!!check && pt.isMember(player)) {
                            pt.removeMember(player)
                            SAOCore.LOGGER.info("Removing " + player.name + " from party")
                        }
                        else {
                            SAOCore.LOGGER.info("Sending party invite to " + player.name)
                        }
                    }
                }
                else -> return
            }
        }

    }

    private fun optionAction(e: ElementAction) {
        if (e.name.equals("Vanilla_Options", true)) mc.displayGuiScreen(GuiOptions(mc.currentScreen, mc.gameSettings))
        if (e.name.equals("Menu", true)) mc.displayGuiScreen(GuiIngameMenu())
        if (e.name.equals("Logout", true)) {
            mc.currentScreen!!.onGuiClosed()
            mc.world.sendQuittingDisconnectingPacket()

            mc.loadWorld(null)
            mc.displayGuiScreen(GuiMainMenu())
        }
        if (e.name.equals("Dissolve", true)) StaticPlayerHelper.getIParty().dissolve()
    }

    private fun slotAction(e: ElementAction) {}

    private fun menuButton() {
        if (mc.currentScreen != null && mc.currentScreen is IngameMenuGUI) {
            mc.currentScreen!!.onGuiClosed()
            mc.displayGuiScreen(GuiIngameMenu())
        }
    }

    private fun vanillaOptions() {
        if (mc.currentScreen != null && mc.currentScreen is IngameMenuGUI) {
            mc.currentScreen!!.onGuiClosed()
            mc.displayGuiScreen(GuiOptions(mc.currentScreen!!, mc.gameSettings))
        }
    }

    private fun logoutButton() {
        if (mc.currentScreen != null && mc.currentScreen is IngameMenuGUI) {
            mc.currentScreen!!.onGuiClosed()
            mc.world.sendQuittingDisconnectingPacket()

            mc.loadWorld(null)
            mc.displayGuiScreen(GuiMainMenu())
        }
    }

    private fun promptButton(e: ElementAction) {
        val message = "This is load simple test prompt testing the window and how it handles long strings of text. This test is using the single button display mode with an event firing once the button has been hit"
        //new GuiOpenEvent(new WindowView("Test Prompt", false, message, WindowAlign.HORIZONTAL_CENTER, WindowAlign.VERTICAL_CENTER, e.getParentElement()));
    }
}
