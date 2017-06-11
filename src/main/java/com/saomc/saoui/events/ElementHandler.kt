package com.saomc.saoui.events

import com.saomc.saoui.SAOCore
import com.saomc.saoui.SoundCore
import com.saomc.saoui.api.elements.CategoryEnum
import com.saomc.saoui.api.events.ElementAction
import com.saomc.saoui.api.screens.Actions
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.events.EventCore.mc
import com.saomc.saoui.neo.screens.IngameMenuGUI
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiIngameMenu
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiOptions
import java.lang.IllegalArgumentException
import java.util.stream.Stream

/**
 * This handles and controls the default event, and our custom events for slots

 * Created by Tencao on 18/08/2016.
 */
object ElementHandler {

    fun defaultActions(e: ElementAction) {
        if (e.action == Actions.LEFT_RELEASED) {
            if (e.isCategory) {
                try {
                    val category = CategoryEnum.valueOf(e.name.toUpperCase())
                    if (e.isOpen) {
                        e.parent.closeCategory(category)
                        SoundCore.play(Minecraft.getMinecraft().soundHandler, SoundCore.DIALOG_CLOSE)
                    } else if (!e.isOpen && !e.isLocked) {
                        e.parent.openCategory(category)
                        SoundCore.play(Minecraft.getMinecraft().soundHandler, SoundCore.MENU_POPUP)
                    }
                } catch (error: IllegalArgumentException){
                    SAOCore.LOGGER.fatal("Element: " + e.name + " incorrectly set isCategory with no matching categories")
                }
            }
        }

    }

    private fun optionAction(e: ElementAction) {
        val option = OptionCore.fromString(e.name)
        if (option!!.isRestricted) {
            if (!option.isEnabled) {
                Stream.of(*OptionCore.values()).filter { opt -> opt.category == option.category }.forEach({ it.disable() })
                option.enable()
            }
        } else
            option.flip()
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
