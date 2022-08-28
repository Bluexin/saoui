package com.tencao.saoui.api.elements.registry

import com.tencao.saomclib.Client
import com.tencao.saomclib.message
import com.tencao.saomclib.utils.math.vec
import com.tencao.saoui.SAOCore
import com.tencao.saoui.api.elements.CategoryButton
import com.tencao.saoui.api.elements.IconElement
import com.tencao.saoui.api.elements.NeoElement
import com.tencao.saoui.api.elements.optionCategory
import com.tencao.saoui.api.events.MenuBuildingEvent
import com.tencao.saoui.api.items.IItemFilter
import com.tencao.saoui.api.items.ItemFilterRegister
import com.tencao.saoui.api.screens.IIcon
import com.tencao.saoui.config.OptionCategory.Companion.tlOptionCategory
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.events.EventCore
import com.tencao.saoui.screens.menus.IngameMenu
import com.tencao.saoui.screens.util.PopupYesNo
import com.tencao.saoui.screens.util.itemList
import com.tencao.saoui.util.IconCore
import net.minecraft.client.gui.screen.IngameMenuScreen
import net.minecraft.client.gui.screen.OptionsScreen
import net.minecraft.client.resources.I18n
import net.minecraftforge.common.MinecraftForge

object ElementRegistry {

    /**
     * This is a fixed list of elements for the menu
     * that will be pulled everytime the menu is opened
     */
    val registeredElements = hashMapOf<Type, ArrayList<NeoElement>>()

    fun initRegistry() {
        registeredElements.clear()
        val event = MenuBuildingEvent(getDefaultElements())
        MinecraftForge.EVENT_BUS.post(event)
        registeredElements[Type.INGAMEMENU] = event.elements
    }

    fun getDefaultElements(): ArrayList<NeoElement> {
        var index = 0
        return arrayListOf(
            tlCategory(IconCore.PROFILE, index++) {
                ItemFilterRegister.tlFilters.forEach { baseFilter ->
                    addItemCategories(this, baseFilter)
                }
                category(IconCore.SKILLS, I18n.format("sao.element.skills")) {
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
                            controllingGUI?.openGui(
                                PopupYesNo(
                                    "Disolve",
                                    "パーチイを解散しますか？",
                                    ""
                                )
                            )?.plusAssign {
                                IngameMenu.mc.player?.message("Result: $it")
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
                }
                // crafting()
                profile(IngameMenu.mc.player!!)
            },
            tlCategory(IconCore.SOCIAL, index++) {
                category(IconCore.GUILD, I18n.format("sao.element.guild")) {
                    delegate.disabled = !SAOCore.isSAOMCLibServerSide
                }
                partyMenu()
                friendMenu()
            },
            tlCategory(IconCore.MESSAGE, index++),
            tlCategory(IconCore.NAVIGATION, index++) {
                category(IconCore.QUEST, I18n.format("sao.element.quest")) {
                    /*
                    AdvancementUtil.getCategories().forEach {
                        advancementCategory(it)
                    }*/
                }
                // TODO Redo this
                // recipes()
            },
            tlCategory(IconCore.SETTINGS, index) {
                category(IconCore.OPTION, I18n.format("sao.element.options")) {
                    category(IconCore.OPTION, I18n.format("guiOptions")) {
                        onClick { _, _ ->
                            IngameMenu.mc.displayGuiScreen(OptionsScreen(controllingGUI, EventCore.mc.gameSettings))
                            true
                        }
                    }
                    tlOptionCategory.forEach {
                        +optionCategory(it)
                    }
                }
                category(IconCore.HELP, I18n.format("sao.element.menu")) {
                    onClick { _, _ ->
                        IngameMenu.mc.displayGuiScreen(IngameMenuScreen(true))
                        true
                    }
                }
                category(IconCore.LOGOUT, if (OptionCore.LOGOUT()) I18n.format("sao.element.logout") else "") {
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
        button.category(filter.icon, filter.displayName) {
            if (filter.isCategory) {
                if (filter.subFilters.isNotEmpty()) {
                    filter.subFilters.forEach { subFilter -> addItemCategories(this, subFilter) }
                }
            } else itemList(Client.player!!.container, filter)
        }
    }

    fun tlCategory(icon: IIcon, index: Int, body: (CategoryButton.() -> Unit)? = null): CategoryButton {
        return CategoryButton(IconElement(icon, vec(0, 25 * index)), null, body)
    }

    enum class Type {
        MAINMENU,
        INGAMEMENU;
    }
}
