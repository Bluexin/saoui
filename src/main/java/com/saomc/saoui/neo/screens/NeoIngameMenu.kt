/*
 * Copyright (C) 2016-2019 Arnaud 'Bluexin' Solé
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
        set(_) {}

    override fun initGui() {
        elements.clear()

        tlCategory(IconCore.PROFILE) {
            category(IconCore.EQUIPMENT, format("sao.element.equipment")) {
                category(IconCore.ARMOR, format("sao.element.armor")) {
                    itemList(mc.player.inventory, BaseFilters.EQUIPMENT, 36..39)
                }
                category(IconCore.EQUIPMENT, format("sao.element.weapons")) {
                    itemList(mc.player.inventory, BaseFilters.WEAPONS, 0..8)
                }
                category(IconCore.EQUIPMENT, format("sao.element.tools")) {
                    itemList(mc.player.inventory, BaseFilters.COMPATTOOLS, 0..8, 40..40)
                }
                category(IconCore.EQUIPMENT, format("sao.element.consumables")) {
                    itemList(mc.player.inventory, BaseFilters.CONSUMABLES, 0..8)
                }
                category(IconCore.EQUIPMENT, format("sao.element.shields")) {
                    itemList(mc.player.inventory, BaseFilters.SHIELDS, 40..40)
                }
                if (BaseFilters.baublesLoaded) {
                    category(IconCore.ACCESSORY, format("sao.element.accessories")) {
                        itemList(BaseFilters.getBaubles(mc.player)!!, BaseFilters.ACCESSORY, 0..10000)
                        itemList(mc.player.inventory, filter = BaseFilters.ACCESSORY)
                    }
                }
            }
            category(IconCore.ITEMS, format("sao.element.items")) {
                itemList(mc.player.inventory, BaseFilters.ITEMS, 0..8, 40..40)
            }
            category(IconCore.SKILLS, format("sao.element.skills")) {
                category(IconCore.SKILLS, "Test 1") {
                    category(IconCore.SKILLS, "1.1") {
                        for (i in 1..3) category(IconCore.SKILLS, "1.1.$i")
                    }
                    category(IconCore.SKILLS, "1.2") {
                        for (i in 1..3) category(IconCore.SKILLS, "1.2.$i")
                    }
                    category(IconCore.SKILLS, "1.3") {
                        for (i in 1..3) category(IconCore.SKILLS, "1.3.$i")
                    }
                }
                category(IconCore.SKILLS, "解散") {
                    onClick { _, _ ->
                        selected = true
                        openGui(PopupYesNo("Disolve", "パーチイを解散しますか？", "")) += {
                            mc.player.message("Result: $it")
                            selected = false
                        }
                        true
                    }
                }
                category(IconCore.SKILLS, "3") {
                    category(IconCore.SKILLS, "3.1") {
                        for (i in 1..6) category(IconCore.SKILLS, "3.1.$i")
                    }
                    category(IconCore.SKILLS, "3.2") {
                        for (i in 1..7) category(IconCore.SKILLS, "3.2.$i")
                    }
                    category(IconCore.SKILLS, "3.3") {
                        for (i in 1..10) category(IconCore.SKILLS, "3.3.$i")
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
                    onClick { _, _ ->
                        mc.displayGuiScreen(GuiOptions(this@NeoIngameMenu, EventCore.mc.gameSettings))
                        true
                    }
                }
                OptionCore.tlOptions.forEach {
                    +optionCategory(it)
                }
            }
            category(IconCore.HELP, format("sao.element.menu")) {
                onClick { _, _ ->
                    mc.displayGuiScreen(GuiIngameMenu())
                    true
                }
            }
            category(IconCore.LOGOUT, if (OptionCore.LOGOUT()) format("sao.element.logout") else "") {
                onClick { _, _ ->
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
