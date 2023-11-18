package com.tencao.saoui.api.elements.registry

import com.tencao.saoui.SAOCore
import com.tencao.saoui.api.elements.*
import com.tencao.saoui.api.items.IItemFilter
import com.tencao.saoui.api.items.ItemFilterRegister
import com.tencao.saoui.api.screens.IIcon
import com.tencao.saoui.config.OptionCategory
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.screens.menus.IngameMenu
import com.tencao.saoui.screens.util.PopupYesNo
import com.tencao.saoui.screens.util.itemList
import com.tencao.saoui.util.*
import com.tencao.saoui.util.math.vec
import net.minecraft.client.gui.screens.OptionsScreen
import net.minecraft.client.gui.screens.PauseScreen
import net.minecraft.client.gui.screens.Screen

object ElementRegistry {

    /**
     * This is a fixed list of elements for the menu
     * that will be pulled everytime the menu is opened
     */
    val registeredElements = hashMapOf<Type, ArrayList<NeoElement>>()

    fun initRegistry() {
        registeredElements.clear()
    }

    fun getDefaultElements(): ArrayList<NeoElement> {
        var index = 0
        return arrayListOf(
            IngameMenu.tlCategory(IconCore.PROFILE, index++) {
                ItemFilterRegister.tlFilters.forEach { baseFilter ->
                    addItemCategories(baseFilter)
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
                                IngameMenu.mc.player?.displayClientMessage("Result: $it".translate(), false)
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
            IngameMenu.tlCategory(IconCore.SOCIAL, index++) {
                category(IconCore.GUILD, "sao.element.guild".translate()) {
                    addDescription("saoui.wip".localize())
                    delegate.disabled = !SAOCore.isSAOMCLibServerSide
                    disabled = true
                }
                partyMenu()
                friendMenu()
            },
            IngameMenu.tlCategory(IconCore.MESSAGE, index++) {
                disabled = true
                addDescription("saoui.wip".localize())
            },
            IngameMenu.tlCategory(IconCore.NAVIGATION, index++) {
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
            IngameMenu.tlCategory(IconCore.SETTINGS, index) {
                category(IconCore.OPTION, "sao.element.options".translate()) {
                    category(IconCore.OPTION, "guiOptions".translate()) {
                        onClick { _, _ ->
                            val gui = OptionsScreen(controllingGUI as? Screen, Client.minecraft.options)
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
                        IngameMenu.mc.setScreen(PauseScreen(true))
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

    fun CategoryButton.addItemCategories(filter: IItemFilter) {
        if (filter.isCategory) {
            category(filter.icon, filter.displayName.translate()) {
                filter.subFilters.forEach { subFilter -> addItemCategories(subFilter) }
            }
        } else {
            +object : CategoryButton(IconLabelElement(filter.icon, filter.displayName), this, null) {
                override fun show() {
                    super.show()
                    this.elements.clear()
                    itemList(Client.player!!.inventoryMenu, filter)
                }

                override fun hide() {
                    super.hide()
                    elements.clear()
                }
            }
        }
    }


    fun tlCategory(icon: IIcon, index: Int, body: (CategoryButton.() -> Unit)? = null): CategoryButton {
        return CategoryButton(IconElement(icon, vec(0, 25 * index)), null, body)
    }

    fun CategoryButton.setWip() {
        disabled = true
        addDescription("saoui.wip".translate())
    }

    enum class Type {
        MAINMENU,
        INGAMEMENU;
    }
}