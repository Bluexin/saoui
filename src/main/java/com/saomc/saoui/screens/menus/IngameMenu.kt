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
import com.saomc.saoui.SAOCore
import com.saomc.saoui.SoundCore
import com.saomc.saoui.api.elements.CategoryButton
import com.saomc.saoui.api.elements.IconElement
import com.saomc.saoui.api.elements.NeoElement
import com.saomc.saoui.api.elements.optionCategory
import com.saomc.saoui.api.events.MenuBuildingEvent
import com.saomc.saoui.api.items.IItemFilter
import com.saomc.saoui.api.items.ItemFilterRegister
import com.saomc.saoui.api.screens.IIcon
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.events.EventCore
import com.saomc.saoui.play
import com.saomc.saoui.screens.CoreGUI
import com.saomc.saoui.screens.util.PopupNotice
import com.saomc.saoui.screens.util.PopupYesNo
import com.saomc.saoui.screens.util.itemList
import com.saomc.saoui.themes.ThemeResourceLoader
import com.saomc.saoui.util.AdvancementUtil
import com.saomc.saoui.util.CraftingUtil
import com.saomc.saoui.util.IconCore
import com.saomc.saoui.util.UIUtil
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.gui.GuiIngameMenu
import net.minecraft.client.gui.GuiOptions
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.resources.I18n.format
import net.minecraft.client.resources.SimpleReloadableResourceManager
import net.minecraftforge.common.MinecraftForge

/**
 * Part of saoui by Bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
class IngameMenu(elements: MutableList<NeoElement> = mutableListOf()) : CoreGUI<Unit>(Vec2d.ZERO, elements = elements) {

    var loggingOut = false

    override var result: Unit
        get() = Unit
        set(_) {}

    override fun initGui() {
        elements.clear()
        val defaultList = getDefaultElements()
        val event = MenuBuildingEvent(defaultList)
        MinecraftForge.EVENT_BUS.post(event)
        event.elements.forEach { it.parent = this }
        elements.addAll(event.elements)

        pos = vec(width / 2.0 - 10, (height - elements.size * 20) / 2.0)
        destination = pos
        SoundCore.ORB_DROPDOWN.play()

        if (!hasChecked){
            if (!SAOCore.isSAOMCLibServerSide){
                openGui(PopupNotice(format("notificationSAOMCLibTitle"), listOf(format("notificationSAOMCLibDesc"), format("notificationSAOMCLibDesc2")), ""))
            }
            hasChecked = true
        }
    }

    override fun updateScreen() {
        if (loggingOut)
            UIUtil.closeGame()
        else {
            CraftingUtil.updateItemHelper()
            super.updateScreen()
        }
    }

    fun getDefaultElements() = {
        var index = 0
        arrayListOf<NeoElement>(
                Companion.tlCategory(IconCore.PROFILE, index++) {
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
                                highlighted = true
                                controllingGUI?.openGui(PopupYesNo("Disolve", "パーチイを解散しますか？", ""))?.plusAssign {
                                    Companion.mc.player.message("Result: $it")
                                    highlighted = false
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

                        disabled = true
                    }
                    crafting()
                    profile(Companion.mc.player)
                },
                Companion.tlCategory(IconCore.SOCIAL, index++) {
                    category(IconCore.GUILD, format("sao.element.guild")) {
                        this.delegate.disabled = !SAOCore.isSAOMCLibServerSide
                        disabled = true
                    }
                    partyMenu()
                    friendMenu()
                },
                Companion.tlCategory(IconCore.MESSAGE, index++),
                Companion.tlCategory(IconCore.NAVIGATION, index++) {
                    category(IconCore.QUEST, format("sao.element.quest")) {
                        AdvancementUtil.getCategories().forEach {
                            advancementCategory(it)
                        }
                    }
                    recipes()
                    disabled = true
                },
                Companion.tlCategory(IconCore.SETTINGS, index) {
                    category(IconCore.OPTION, format("sao.element.options")) {
                        category(IconCore.OPTION, format("guiOptions")) {
                            /*
                            onClick { _, _ ->
                                Companion.mc.displayGuiScreen(GuiOptions(controllingGUI, EventCore.mc.gameSettings))
                                true
                            }*/

                            onClick { _, _ ->
                                val gui = GuiOptions(controllingGUI as? GuiScreen, EventCore.mc.gameSettings)
                                true
                            }
                            disabled = true
                        }
                        OptionCore.tlOptions.forEach {
                            +optionCategory(it)
                        }
                    }
                    category(IconCore.HELP, format("sao.element.menu")) {
                        onClick { _, _ ->
                            Companion.mc.displayGuiScreen(GuiIngameMenu())
                            true
                        }
                    }
                    category(IconCore.OPTION, format("Reload Theme")) {
                        onClick { _, _ ->
                            (Minecraft().resourceManager as SimpleReloadableResourceManager).reloadResourcePack(ThemeResourceLoader)
                            true
                        }
                        disabled = true
                    }
                    category(IconCore.LOGOUT, if (OptionCore.LOGOUT()) format("sao.element.logout") else "") {
                        onClick { _, _ ->
                            if (OptionCore.LOGOUT()) {
                                (controllingGUI as? IngameMenu)?.loggingOut = true
                                true
                            } else false
                        }
                    }
                }
        )
    }.invoke()

    fun addItemCategories(button: CategoryButton, filter: IItemFilter){
        button.category(filter.icon, filter.displayName) {
            if (filter.isCategory) {
                if (filter.subFilters.isNotEmpty())
                    filter.subFilters.forEach { subFilter -> addItemCategories(this, subFilter) }
            }
            else itemList(Minecraft().player.inventoryContainer, filter)
        }
    }

    override fun doesGuiPauseGame(): Boolean {
        return loggingOut || super.doesGuiPauseGame()
    }

    companion object {
        val mc = Minecraft()
        var hasChecked: Boolean = false
        fun tlCategory(icon: IIcon, index: Int, body: (CategoryButton.() -> Unit)? = null): CategoryButton {
            return CategoryButton(IconElement(icon, vec(0, 25 * index)), null, body)
        }

    }
}
