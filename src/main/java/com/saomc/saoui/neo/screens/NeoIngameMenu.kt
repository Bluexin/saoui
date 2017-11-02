package com.saomc.saoui.neo.screens

import com.saomc.saoui.events.EventCore
import com.saomc.saoui.util.IconCore
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.resources.I18n

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
class NeoIngameMenu : NeoGui(0.0, 0.0) {

    override fun initGui() {
        elements.clear()

        tlCategory(IconCore.PROFILE) {
            category(IconCore.EQUIPMENT, I18n.format("sao.element.equipment"))
            category(IconCore.ITEMS, I18n.format("sao.element.items"))
            category(IconCore.SKILLS, I18n.format("sao.element.skills"))
        }
        tlCategory(IconCore.SOCIAL) {
            category(IconCore.GUILD, I18n.format("sao.element.guild"))
            category(IconCore.PARTY, I18n.format("sao.element.party"))
            category(IconCore.FRIEND, I18n.format("sao.element.friends"))
        }
        tlCategory(IconCore.MESSAGE)
        tlCategory(IconCore.NAVIGATION)
        tlCategory(IconCore.SETTINGS) {
            category(IconCore.OPTION, I18n.format("sao.element.options"))
            category(IconCore.HELP, I18n.format("sao.element.menu"))
            category(IconCore.LOGOUT, I18n.format("sao.element.logout")) {
                onClick {
                    EventCore.mc.currentScreen!!.onGuiClosed()
                    EventCore.mc.world.sendQuittingDisconnectingPacket()
                    EventCore.mc.loadWorld(null)
                    EventCore.mc.displayGuiScreen(GuiMainMenu())
                    true
                }
            }
        }

        // TODO: centered by default. Use the amount of Top Level Categories (tlc) to define a center.
        // TODO: Gui should glide to the side when a category is opened to keep focus to the center as much as possible.
        x = width / 2.0
        y = height / 2.0
    }

}
