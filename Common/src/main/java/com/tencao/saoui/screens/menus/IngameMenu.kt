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

package com.tencao.saoui.screens.menus

import com.tencao.saomclib.Client
import com.tencao.saomclib.message
import com.tencao.saomclib.utils.math.Vec2d
import com.tencao.saomclib.utils.math.vec
import com.tencao.saoui.SAOCore
import com.tencao.saoui.SoundCore
import com.tencao.saoui.api.elements.CategoryButton
import com.tencao.saoui.api.elements.IconElement
import com.tencao.saoui.api.elements.NeoElement
import com.tencao.saoui.api.elements.optionCategory
import com.tencao.saoui.api.events.MenuBuildingEvent
import com.tencao.saoui.api.items.IItemFilter
import com.tencao.saoui.api.items.ItemFilterRegister
import com.tencao.saoui.api.screens.IIcon
import com.tencao.saoui.config.OptionCategory
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.events.EventCore
import com.tencao.saoui.play
import com.tencao.saoui.screens.CoreGUI
import com.tencao.saoui.screens.util.PopupNotice
import com.tencao.saoui.screens.util.PopupYesNo
import com.tencao.saoui.screens.util.itemList
import com.tencao.saoui.util.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screen.IngameMenuScreen
import net.minecraft.client.gui.screen.OptionsScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraftforge.common.MinecraftForge

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
class IngameMenu(elements: MutableList<NeoElement> = mutableListOf()) : CoreGUI<Unit>("Ingame Menu", Vec2d.ZERO, elements = elements) {

    var loggingOut = false

    override var result: Unit
        get() = Unit
        set(_) {}

    override fun init(minecraft: Minecraft, width: Int, height: Int) {
        super.init(minecraft, width, height)
        elements.clear()
        val defaultList = getDefaultElements()
        val event = MenuBuildingEvent(defaultList)
        MinecraftForge.EVENT_BUS.post(event)
        event.elements.forEach { it.parent = this }
        elements.addAll(event.elements)

        pos = vec(width / 2.0 - 10, (height - elements.size * 20) / 2.0)
        destination = pos
        SoundCore.ORB_DROPDOWN.play()
    }

    override fun tick() {
        if (loggingOut) {
            UIUtil.closeGame()
        } else {
            // CraftingUtil.updateItemHelper()
            super.tick()
        }
        if (!hasChecked) {
            if (!ClientUtil.isSAOMCLibServerSide) {
                openGui(PopupNotice("notificationSAOMCLibTitle".localize(), listOf("notificationSAOMCLibDesc".localize(), "notificationSAOMCLibDesc2".localize()), ""))
            }
            hasChecked = true
        }
    }

    fun getDefaultElements(): ArrayList<NeoElement> {
        var index = 0
        return arrayListOf(
            Companion.tlCategory(IconCore.PROFILE, index++) {
                ItemFilterRegister.tlFilters.forEach { baseFilter ->
                    addItemCategories(this, baseFilter)
                }
                category(IconCore.SKILLS, "sao.element.skills".translate()) {
                    addDescription("saoui.wip".localize())
                    category(IconCore.SKILLS, "Test 1".toTextComponent()) {
                        category(IconCore.SKILLS, "1.1".toTextComponent()) {
                            for (i in 1..3) category(IconCore.SKILLS, "1.1.$i".toTextComponent())
                        }
                        category(IconCore.SKILLS, "1.2".toTextComponent()) {
                            for (i in 1..3) category(IconCore.SKILLS, "1.2.$i".toTextComponent())
                        }
                        category(IconCore.SKILLS, "1.3".toTextComponent()) {
                            for (i in 1..3) category(IconCore.SKILLS, "1.3.$i".toTextComponent())
                        }
                    }
                    category(IconCore.SKILLS, "解散".toTextComponent()) {
                        onClick { _, _ ->
                            highlighted = true
                            controllingGUI?.openGui(
                                PopupYesNo(
                                    "Disolve",
                                    "パーチイを解散しますか？",
                                    ""
                                )
                            )?.plusAssign {
                                mc.player?.message("Result: $it")
                                highlighted = false
                            }
                            true
                        }
                    }
                    category(IconCore.SKILLS, "3".toTextComponent()) {
                        category(IconCore.SKILLS, "3.1".toTextComponent()) {
                            for (i in 1..6) category(IconCore.SKILLS, "3.1.$i".toTextComponent())
                        }
                        category(IconCore.SKILLS, "3.2".toTextComponent()) {
                            for (i in 1..7) category(IconCore.SKILLS, "3.2.$i".toTextComponent())
                        }
                        category(IconCore.SKILLS, "3.3".toTextComponent()) {
                            for (i in 1..10) category(IconCore.SKILLS, "3.3.$i".toTextComponent())
                        }
                    }

                    disabled = true
                }
                // crafting()
                profile()
            },
            Companion.tlCategory(IconCore.SOCIAL, index++) {
                category(IconCore.GUILD, "sao.element.guild".translate()) {
                    addDescription("saoui.wip".localize())
                    delegate.disabled = !SAOCore.isSAOMCLibServerSide
                    disabled = true
                }
                partyMenu()
                friendMenu()
            },
            Companion.tlCategory(IconCore.MESSAGE, index++) {
                disabled = true
                addDescription("saoui.wip".localize())
            },
            Companion.tlCategory(IconCore.NAVIGATION, index++) {
                addDescription("saoui.wip".localize())
                category(IconCore.QUEST, "sao.element.quest".translate()) {
                    /*
                    AdvancementUtil.getCategories().forEach {
                        advancementCategory(it)
                    }*/
                }
                // recipes()
                disabled = true
            },
            Companion.tlCategory(IconCore.SETTINGS, index) {
                category(IconCore.OPTION, "sao.element.options".translate()) {
                    category(IconCore.OPTION, "guiOptions".translate()) {
                        onClick { _, _ ->
                            val gui = OptionsScreen(controllingGUI as? Screen, EventCore.mc.options)
                            true
                        }
                        disabled = true
                    }
                    OptionCategory.tlOptionCategory.forEach {
                        +optionCategory(it)
                    }
                }
                category(IconCore.HELP, "sao.element.menu".translate()) {
                    onClick { _, _ ->
                        mc.setScreen(IngameMenuScreen(true))
                        true
                    }
                }
                category(IconCore.LOGOUT, if (OptionCore.LOGOUT()) "sao.element.logout".translate() else "".toTextComponent()) {
                    onClick { _, _ ->
                        if (OptionCore.LOGOUT()) {
                            (controllingGUI as? IngameMenu)?.loggingOut = true
                            true
                        } else false
                    }
                }
            }
        )
    }

    fun addItemCategories(button: CategoryButton, filter: IItemFilter) {
        button.category(filter.icon, filter.displayName.translate()) {
            if (filter.isCategory) {
                if (filter.subFilters.isNotEmpty()) {
                    filter.subFilters.forEach { subFilter -> addItemCategories(this, subFilter) }
                }
            } else itemList(Client.player!!.inventoryMenu, filter)
        }
    }

    override fun isPauseScreen(): Boolean {
        return loggingOut || super.isPauseScreen()
    }

    companion object {
        val mc = Client.minecraft
        var hasChecked: Boolean = false
        fun tlCategory(icon: IIcon, index: Int, description: MutableList<String> = mutableListOf(), body: (CategoryButton.() -> Unit)? = null): CategoryButton {
            return CategoryButton(IconElement(icon, vec(0, 25 * index), description = description), null, body)
        }
    }
}
