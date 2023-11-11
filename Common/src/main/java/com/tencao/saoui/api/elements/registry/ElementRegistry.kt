package com.tencao.saoui.api.elements.registry

import com.tencao.saomclib.Client
import com.tencao.saomclib.message
import com.tencao.saomclib.utils.math.vec
import com.tencao.saoui.api.elements.*
import com.tencao.saoui.api.events.MenuBuildingEvent
import com.tencao.saoui.api.items.IItemFilter
import com.tencao.saoui.api.items.ItemFilterRegister
import com.tencao.saoui.api.screens.IIcon
import com.tencao.saoui.api.scripting.JNLua
import com.tencao.saoui.config.OptionCategory
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.screens.menus.IngameMenu
import com.tencao.saoui.screens.util.PopupYesNo
import com.tencao.saoui.screens.util.itemList
import com.tencao.saoui.util.IconCore
import com.tencao.saoui.util.translate
import net.minecraft.client.gui.screen.IngameMenuScreen
import net.minecraft.client.gui.screen.OptionsScreen
import net.minecraft.client.resources.I18n
import net.minecraft.resources.IResourceManager
import net.minecraftforge.resource.IResourceType
import net.minecraftforge.resource.ISelectiveResourceReloadListener
import java.util.function.Predicate

object ElementRegistry : ISelectiveResourceReloadListener {

    /**
     * This is a fixed list of elements for the menu
     * that will be pulled everytime the menu is opened
     */
    val registeredElements = hashMapOf<Type, List<NeoElement>>()

    // TODO: this supports async stuff now
    override fun onResourceManagerReload(resourceManager: IResourceManager) {
        initRegistry()
    }

    override fun onResourceManagerReload(
        resourceManager: IResourceManager,
        resourcePredicate: Predicate<IResourceType>
    ) {
        initRegistry()
    }

    fun initRegistry() {
        registeredElements.clear()
        val event = MenuBuildingEvent(JNLua.loadIngameMenu())
//        MinecraftForge.EVENT_BUS.post(event)
        registeredElements[Type.INGAMEMENU] = event.elements
    }

    fun getDefaultElements(): MutableList<NeoElement> {
        var index = 0
        return arrayListOf(
            tlCategory(IconCore.PROFILE, index++) {
                ItemFilterRegister.tlFilters.forEach { baseFilter ->
                    addItemCategories(baseFilter)
                }
                category(IconCore.SKILLS, "sao.element.skills".translate()) {
                    setWip()
                    category(IconCore.SKILLS, "Test 1".translate()) {
                        category(IconCore.SKILLS, "1.1".translate()) {
                            for (i in 1..3) category(IconCore.SKILLS, "1.1.$i".translate())
                        }
                        category(IconCore.SKILLS, "1.2".translate()) {
                            for (i in 1..3) category(IconCore.SKILLS, "1.2.$i".translate())
                        }
                        category(IconCore.SKILLS, "1.3".translate()) {
                            for (i in 1..3) category(IconCore.SKILLS, "1.3.$i".translate())
                        }
                    }
                    category(IconCore.SKILLS, "解散".translate()) {
                        onClick { _, _ ->
                            highlighted = true
                            controllingGUI?.openGui(
                                PopupYesNo(
                                    "Disolve",
                                    "パーチイを解散しますか？",
                                    ""
                                )
                            )?.plusAssign {
                                Client.player?.message("Result: $it")
                                highlighted = false
                            }
                            true
                        }
                    }
                    category(IconCore.SKILLS, "3".translate()) {
                        category(IconCore.SKILLS, "3.1".translate()) {
                            for (i in 1..6) category(IconCore.SKILLS, "3.1.$i".translate())
                        }
                        category(IconCore.SKILLS, "3.2".translate()) {
                            for (i in 1..7) category(IconCore.SKILLS, "3.2.$i".translate())
                        }
                        category(IconCore.SKILLS, "3.3".translate()) {
                            for (i in 1..10) category(IconCore.SKILLS, "3.3.$i".translate())
                        }
                    }
                }
                crafting()
                Client.player?.let(::profile)
            },
            tlCategory(IconCore.SOCIAL, index++) {
                category(IconCore.GUILD, "sao.element.guild".translate()) {
                    setWip()
                    delegate.disabled = true //!SAOCore.isSAOMCLibServerSide
                }
                partyMenu()
                friendMenu()
            },
            tlCategory(IconCore.MESSAGE, index++) {
                setWip()
            },
            tlCategory(IconCore.NAVIGATION, index++) {
                setWip()
                /*
                category(IconCore.QUEST, "sao.element.quest".translate()) {
                    AdvancementUtil.getCategories().forEach {
                        advancementCategory(it)
                    }
                    Client.player.connection.advancements
                }
                recipes()*/
            },
            tlCategory(IconCore.SETTINGS, index) {
                category(IconCore.OPTION, "sao.element.options".translate()) {
                    category(IconCore.OPTION, "guiOptions".translate()) {
                        onClick { _, _ ->
                            Client.minecraft.setScreen(OptionsScreen(controllingGUI, Client.minecraft.options))
                            true
                        }
                    }
                    OptionCategory.tlOptionCategory.forEach {
                        +optionCategory(it)
                    }
                }
                category(IconCore.HELP, "sao.element.menu".translate()) {
                    onClick { _, _ ->
                        Client.minecraft.setScreen(IngameMenuScreen(true))
                        true
                    }
                }
                category(IconCore.LOGOUT, if (OptionCore.LOGOUT()) "sao.element.logout".translate() else "".translate()) {
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
        addDescription(I18n.get("saoui.wip"))
    }

    enum class Type {
        MAINMENU,
        INGAMEMENU;
    }
}

