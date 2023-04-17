package be.bluexin.mcui.api.elements.registry

import be.bluexin.mcui.util.Client
import com.tencao.saomclib.message
import com.tencao.saomclib.utils.math.vec
import be.bluexin.mcui.SAOCore
import be.bluexin.mcui.api.elements.*
import be.bluexin.mcui.api.events.MenuBuildingEvent
import be.bluexin.mcui.api.items.IItemFilter
import be.bluexin.mcui.api.items.ItemFilterRegister
import be.bluexin.mcui.api.screens.IIcon
import be.bluexin.mcui.api.scripting.JNLua
import be.bluexin.mcui.config.OptionCore
import be.bluexin.mcui.events.EventCore
import be.bluexin.mcui.screens.menus.IngameMenu
import be.bluexin.mcui.screens.util.PopupYesNo
import be.bluexin.mcui.screens.util.itemList
import be.bluexin.mcui.util.AdvancementUtil
import be.bluexin.mcui.util.IconCore
import net.minecraft.client.gui.GuiIngameMenu
import net.minecraft.client.gui.GuiOptions
import net.minecraft.client.resources.I18n
import net.minecraft.client.resources.IResourceManager
import net.minecraftforge.client.resource.IResourceType
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener
import net.minecraftforge.client.resource.VanillaResourceType
import net.minecraftforge.common.MinecraftForge
import java.util.function.Predicate

object ElementRegistry : ISelectiveResourceReloadListener {

    /**
     * This is a fixed list of elements for the menu
     * that will be pulled everytime the menu is opened
     */
    val registeredElements = hashMapOf<Type, List<NeoElement>>()

    override fun onResourceManagerReload(
        resourceManager: IResourceManager,
        resourcePredicate: Predicate<IResourceType>
    ) {
        if (resourcePredicate.test(VanillaResourceType.TEXTURES)) initRegistry()
    }

    fun initRegistry() {
        registeredElements.clear()
        val event = MenuBuildingEvent(JNLua.loadIngameMenu())
        MinecraftForge.EVENT_BUS.post(event)
        registeredElements[Type.INGAMEMENU] = event.elements
    }

    fun getDefaultElements(): MutableList<NeoElement> {
        var index = 0
        return arrayListOf(
            tlCategory(IconCore.PROFILE, index++) {
                ItemFilterRegister.tlFilters.forEach { baseFilter ->
                    addItemCategories(baseFilter)
                }
                category(IconCore.SKILLS, I18n.format("sao.element.skills")) {
                    setWip()
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
                                IngameMenu.mc.player.message("Result: $it")
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
                crafting()
                profile(IngameMenu.mc.player)
            },
            tlCategory(IconCore.SOCIAL, index++) {
                category(IconCore.GUILD, I18n.format("sao.element.guild")) {
                    setWip()
                    delegate.disabled = !SAOCore.isSAOMCLibServerSide
                }
                partyMenu()
                friendMenu()
            },
            tlCategory(IconCore.MESSAGE, index++) {
                setWip()
            },
            tlCategory(IconCore.NAVIGATION, index++) {
                setWip()
                category(IconCore.QUEST, I18n.format("sao.element.quest")) {
                    AdvancementUtil.getCategories().forEach {
                        advancementCategory(it)
                    }
                }
                recipes()
            },
            tlCategory(IconCore.SETTINGS, index) {
                category(IconCore.OPTION, I18n.format("sao.element.options")) {
                    category(IconCore.OPTION, I18n.format("guiOptions")) {
                        onClick { _, _ ->
                            IngameMenu.mc.displayGuiScreen(GuiOptions(controllingGUI, EventCore.mc.gameSettings))
                            true
                        }
                    }
                    OptionCore.tlOptions.forEach {
                        +optionCategory(it)
                    }
                }
                category(IconCore.HELP, I18n.format("sao.element.menu")) {
                    onClick { _, _ ->
                        IngameMenu.mc.displayGuiScreen(GuiIngameMenu())
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

    fun CategoryButton.addItemCategories(filter: IItemFilter) {
        if (filter.isCategory) {
            category(filter.icon, filter.displayName) {
                filter.subFilters.forEach { subFilter -> addItemCategories(subFilter) }
            }
        } else {
            +object : CategoryButton(IconLabelElement(filter.icon, filter.displayName), this, null) {
                override fun show() {
                    super.show()
                    this.elements.clear()
                    itemList(Client.mc.player.inventoryContainer, filter)
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
        addDescription(I18n.format("saoui.wip"))
    }

    enum class Type {
        MAINMENU,
        INGAMEMENU;
    }
}

