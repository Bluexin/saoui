package com.saomc.saoui.neo.screens

import com.saomc.saoui.api.elements.neo.category
import com.saomc.saoui.api.elements.neo.optionCategory
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.events.EventCore
import com.saomc.saoui.util.IconCore
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiOptions
import net.minecraft.client.resources.I18n.format

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
class NeoIngameMenu : NeoGui(0.0, 0.0) {

    override fun initGui() {
        elements.clear()

        tlCategory(IconCore.PROFILE) {
            +category(IconCore.EQUIPMENT, format("sao.element.equipment")) {
                +category(IconCore.EQUIPMENT, format("sao.element.equipment")) {
                    +category(IconCore.EQUIPMENT, format("sao.element.equipment"))
                    +category(IconCore.ITEMS, format("sao.element.items"))
                    +category(IconCore.SKILLS, format("sao.element.skills"))
                }
                +category(IconCore.ITEMS, format("sao.element.items")) {
                    +category(IconCore.EQUIPMENT, format("sao.element.equipment"))
                    +category(IconCore.ITEMS, format("sao.element.items"))
                    +category(IconCore.SKILLS, format("sao.element.skills"))
                }
                +category(IconCore.SKILLS, format("sao.element.skills")) {
                    +category(IconCore.EQUIPMENT, format("sao.element.equipment"))
                    +category(IconCore.ITEMS, format("sao.element.items"))
                    +category(IconCore.SKILLS, format("sao.element.skills"))
                }
            }
            +category(IconCore.ITEMS, format("sao.element.items")) {
                +category(IconCore.EQUIPMENT, format("sao.element.equipment")) {
                    +category(IconCore.EQUIPMENT, format("sao.element.equipment"))
                    +category(IconCore.ITEMS, format("sao.element.items"))
                    +category(IconCore.SKILLS, format("sao.element.skills"))
                }
                +category(IconCore.ITEMS, format("sao.element.items")) {
                    +category(IconCore.EQUIPMENT, format("sao.element.equipment"))
                    +category(IconCore.ITEMS, format("sao.element.items"))
                    +category(IconCore.SKILLS, format("sao.element.skills"))
                }
                +category(IconCore.SKILLS, format("sao.element.skills")) {
                    +category(IconCore.EQUIPMENT, format("sao.element.equipment"))
                    +category(IconCore.ITEMS, format("sao.element.items"))
                    +category(IconCore.SKILLS, format("sao.element.skills"))
                }
            }
            +category(IconCore.SKILLS, format("sao.element.skills")) {
                +category(IconCore.EQUIPMENT, format("sao.element.equipment")) {
                    +category(IconCore.EQUIPMENT, format("sao.element.equipment"))
                    +category(IconCore.ITEMS, format("sao.element.items"))
                    +category(IconCore.SKILLS, format("sao.element.skills"))
                }
                +category(IconCore.ITEMS, format("sao.element.items")) {
                    +category(IconCore.EQUIPMENT, format("sao.element.equipment"))
                    +category(IconCore.ITEMS, format("sao.element.items"))
                    +category(IconCore.SKILLS, format("sao.element.skills"))
                }
                +category(IconCore.SKILLS, format("sao.element.skills")) {
                    +category(IconCore.EQUIPMENT, format("sao.element.equipment"))
                    +category(IconCore.ITEMS, format("sao.element.items"))
                    +category(IconCore.SKILLS, format("sao.element.skills"))
                }
            }
        }
        tlCategory(IconCore.SOCIAL) {
            +category(IconCore.GUILD, format("sao.element.guild"))
            +category(IconCore.PARTY, format("sao.element.party"))
            +category(IconCore.FRIEND, format("sao.element.friends"))
        }
        tlCategory(IconCore.MESSAGE)
        tlCategory(IconCore.NAVIGATION)
        tlCategory(IconCore.SETTINGS) {
            +category(IconCore.OPTION, format("sao.element.options")) {
                +category(IconCore.OPTION, format("guiOptions")) {
                    onClick {
                        mc.displayGuiScreen(GuiOptions(EventCore.mc.currentScreen, EventCore.mc.gameSettings))
                        true
                    }
                }
                OptionCore.tlOptions.forEach {
                    +optionCategory(it)
                }
            }
            +category(IconCore.HELP, format("sao.element.menu"))
            +category(IconCore.LOGOUT, if (OptionCore.LOGOUT.isEnabled) format("sao.element.logout") else "") {
                onClick {
                    if (OptionCore.LOGOUT.isEnabled) {
                        EventCore.mc.currentScreen!!.onGuiClosed()
                        EventCore.mc.world.sendQuittingDisconnectingPacket()
                        EventCore.mc.loadWorld(null)
                        EventCore.mc.displayGuiScreen(GuiMainMenu())
                        true
                    } else false
                }
            }
        }

        // TODO: Gui should glide to the side when a category is opened to keep focus to the center as much as possible.
        x = width / 2.0 - 10
        y = (height - elements.size * 20) / 2.0
    }

}
