package com.saomc.saoui.neo.screens

import be.bluexin.saomclib.message
import com.saomc.saoui.api.elements.neo.optionCategory
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.events.EventCore
import com.saomc.saoui.screens.inventory.BaseFilters
import com.saomc.saoui.util.IconCore
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.gui.GuiIngameMenu
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiOptions
import net.minecraft.client.resources.I18n.format

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
class NeoIngameMenu : NeoGui<Unit>(Vec2d.ZERO) {

    override var result: Unit
        get() = Unit
        set(value) {}

    override fun initGui() {
        elements.clear()

        tlCategory(IconCore.PROFILE) {
            category(IconCore.EQUIPMENT, format("sao.element.equipment")) {
                category(IconCore.ARMOR, format("sao.element.armor")) {
                    itemList(mc.player.inventory, { BaseFilters.EQUIPMENT(it, false) })
                }
            }
            category(IconCore.ITEMS, format("sao.element.items"))
            category(IconCore.SKILLS, format("sao.element.skills")) {
                category(IconCore.SKILLS, "Test 1") {
                    category(IconCore.SKILLS, "1.1") {
                        category(IconCore.SKILLS, "1.1.1")
                        category(IconCore.SKILLS, "1.1.2")
                        category(IconCore.SKILLS, "1.1.3")
                    }
                    category(IconCore.SKILLS, "1.2") {
                        category(IconCore.SKILLS, "1.2.1")
                        category(IconCore.SKILLS, "1.2.2")
                        category(IconCore.SKILLS, "1.2.3")
                    }
                    category(IconCore.SKILLS, "1.3") {
                        category(IconCore.SKILLS, "1.3.1")
                        category(IconCore.SKILLS, "1.3.2")
                        category(IconCore.SKILLS, "1.3.3")
                    }
                }
                category(IconCore.SKILLS, "解散") {
                    onClick {
                        selected = true
                        openGui(PopupYesNo("Disolve", "パーチイを解散しますか？")) += {
                            mc.player.message("Result: $it")
                            selected = false
                        }
                        true
                    }
                }
                category(IconCore.SKILLS, "3") {
                    category(IconCore.SKILLS, "3.1") {
                        category(IconCore.SKILLS, "3.1.1")
                        category(IconCore.SKILLS, "3.1.2")
                        category(IconCore.SKILLS, "3.1.3")
                    }
                    category(IconCore.SKILLS, "3.2") {
                        category(IconCore.SKILLS, "3.2.1")
                        category(IconCore.SKILLS, "3.2.2")
                        category(IconCore.SKILLS, "3.2.3")
                    }
                    category(IconCore.SKILLS, "3.3") {
                        category(IconCore.SKILLS, "3.3.1")
                        category(IconCore.SKILLS, "3.3.2")
                        category(IconCore.SKILLS, "3.3.3")
                    }
                }
            }
        }
        tlCategory(IconCore.SOCIAL) {
            category(IconCore.GUILD, format("sao.element.guild"))
            partyMenu(mc.player)
            category(IconCore.FRIEND, format("sao.element.friends"))
        }
        tlCategory(IconCore.MESSAGE)
        tlCategory(IconCore.NAVIGATION)
        tlCategory(IconCore.SETTINGS) {
            category(IconCore.OPTION, format("sao.element.options")) {
                category(IconCore.OPTION, format("guiOptions")) {
                    onClick {
                        mc.displayGuiScreen(GuiOptions(this@NeoIngameMenu, EventCore.mc.gameSettings))
                        true
                    }
                }
                OptionCore.tlOptions.forEach {
                    +optionCategory(it)
                }
            }
            category(IconCore.HELP, format("sao.element.menu")) {
                onClick {
                    mc.displayGuiScreen(GuiIngameMenu())
                    true
                }
            }
            category(IconCore.LOGOUT, if (OptionCore.LOGOUT()) format("sao.element.logout") else "") {
                onClick {
                    if (OptionCore.LOGOUT()) {
                        EventCore.mc.currentScreen!!.onGuiClosed()
                        EventCore.mc.world.sendQuittingDisconnectingPacket()
                        EventCore.mc.loadWorld(null)
                        EventCore.mc.displayGuiScreen(GuiMainMenu())
                        true
                    } else false
                }
            }
        }

        pos = vec(width / 2.0 - 10, (height - elements.size * 20) / 2.0)
        destination = pos
    }
}
