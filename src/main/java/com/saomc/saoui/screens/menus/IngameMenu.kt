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

package com.saomc.saoui.screens.menus

import be.bluexin.saomclib.message
import com.saomc.saoui.SoundCore
import com.saomc.saoui.api.elements.CategoryButton
import com.saomc.saoui.api.elements.ItemFilterElement
import com.saomc.saoui.api.elements.optionCategory
import com.saomc.saoui.api.items.IItemFilter
import com.saomc.saoui.api.items.ItemFilterRegister
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.events.EventCore
import com.saomc.saoui.play
import com.saomc.saoui.screens.CoreGUI
import com.saomc.saoui.screens.itemList
import com.saomc.saoui.screens.util.PopupYesNo
import com.saomc.saoui.util.IconCore
import com.saomc.saoui.util.UIUtil
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.gui.GuiIngameMenu
import net.minecraft.client.gui.GuiOptions
import net.minecraft.client.resources.I18n.format

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
class IngameMenu : CoreGUI<Unit>(Vec2d.ZERO) {

    private var loggingOut = false

    override var result: Unit
        get() = Unit
        set(_) {}

    val defaultElements by lazy {
        var index = 0
        arrayOf(
                tlCategory(IconCore.PROFILE, index++) {
                    ItemFilterRegister.tlFilters.forEach { baseFilter ->
                        addItemCategories(this, baseFilter)
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
                    profile(mc.player)
                },
                tlCategory(IconCore.SOCIAL, index++) {
                    category(IconCore.GUILD, format("sao.element.guild"))
                    partyMenu(mc.player)
                    category(IconCore.FRIEND, format("sao.element.friends"))
                },
                tlCategory(IconCore.MESSAGE, index++),
                tlCategory(IconCore.NAVIGATION, index++),
                tlCategory(IconCore.SETTINGS, index++) {
                    category(IconCore.OPTION, format("sao.element.options")) {
                        category(IconCore.OPTION, format("guiOptions")) {
                            onClick { _, _ ->
                                mc.displayGuiScreen(GuiOptions(this@IngameMenu, EventCore.mc.gameSettings))
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
                                loggingOut = true
                                true
                            } else false
                        }
                    }
                }
        )
    }

    override fun initGui() {
        elements.clear()
        defaultElements.forEach { elements.add(it) }
        updateInventory()

        pos = vec(width / 2.0 - 10, (height - elements.size * 20) / 2.0)
        destination = pos
        SoundCore.ORB_DROPDOWN.play()
    }

    override fun updateScreen() {
        if (loggingOut)
            UIUtil.closeGame()
        else super.updateScreen()
    }

    fun addItemCategories(button: CategoryButton, filter: IItemFilter){
        button.category(filter.icon, filter.displayName) {
            if (filter.isCategory) {
                if (filter.subFilters.isNotEmpty())
                    filter.subFilters.forEach { subFilter -> addItemCategories(this, subFilter) }
            }
            else itemList(mc.player.inventoryContainer, filter, filter.getValidSlots())
        }
    }

    fun updateInventory(){
        elements.asSequence().filter { it is ItemFilterElement }.forEach { (it as ItemFilterElement).updateInventory() }
    }

    override fun doesGuiPauseGame(): Boolean {
        return loggingOut || super.doesGuiPauseGame()
    }

}
