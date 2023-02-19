package com.tencao.saoui.api.elements.registry

import com.tencao.saomclib.Client
import com.tencao.saomclib.message
import com.tencao.saomclib.utils.math.vec
import com.tencao.saoui.SAOCore
import com.tencao.saoui.api.elements.*
import com.tencao.saoui.api.events.MenuBuildingEvent
import com.tencao.saoui.api.items.IItemFilter
import com.tencao.saoui.api.items.ItemFilterRegister
import com.tencao.saoui.api.screens.IIcon
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.events.EventCore
import com.tencao.saoui.screens.menus.IngameMenu
import com.tencao.saoui.screens.util.PopupYesNo
import com.tencao.saoui.screens.util.itemList
import com.tencao.saoui.util.AdvancementUtil
import com.tencao.saoui.util.IconCore
import li.cil.repack.com.naef.jnlua.LuaState
import li.cil.repack.com.naef.jnlua.NamedJavaFunction
import net.minecraft.client.gui.GuiIngameMenu
import net.minecraft.client.gui.GuiOptions
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.resources.I18n
import net.minecraft.client.resources.IResourceManager
import net.minecraftforge.client.resource.IResourceType
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener
import net.minecraftforge.client.resource.VanillaResourceType
import net.minecraftforge.common.MinecraftForge
import org.intellij.lang.annotations.Language
import java.io.File
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
        val event = MenuBuildingEvent(Lua53.test())
        MinecraftForge.EVENT_BUS.post(event)
        registeredElements[Type.INGAMEMENU] = event.elements
    }

    fun getDefaultElements(): MutableList<NeoElement> {
        var index = 0
        return arrayListOf(
            tlCategory(IconCore.PROFILE, index++) {
                ItemFilterRegister.tlFilters.forEach { baseFilter ->
                    addItemCategories(this, baseFilter)
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

    fun addItemCategories(button: CategoryButton, filter: IItemFilter) {
        if (filter.isCategory) {
            button.category(filter.icon, filter.displayName) {
                if (filter.subFilters.isNotEmpty()) {
                    filter.subFilters.forEach { subFilter -> addItemCategories(this, subFilter) }
                }
            }
        } else {
            button += object : CategoryButton(IconLabelElement(filter.icon, filter.displayName), button, null) {
                override fun show() {
                    super.show()
                    itemList(Client.minecraft.player.inventoryContainer, filter)
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

object Lua53 {
    @JvmStatic
    fun main(args: Array<String>) {
        test()
    }

    private fun LuaState.registerModule(name: String, version: Long, functions: Array<NamedJavaFunction>) {
        register(name, functions, true)
        // Set a field 'VERSION' with the value 1
        pushInteger(version)
        setField(-2, "VERSION")
        // Pop the module table
        pop(1)
    }

    private var state: LuaState? = null

    fun test(): MutableList<CategoryButton> {
        this.state?.close()
        val tlCategories = mutableListOf<CategoryButton>()
        // TODO : this is probs not the best long-term, look into getting JNLua to load the script directly
        val script = File("../igmenu.lua").readText()
        if (script.isEmpty()) return tlCategories
        val state = LuaStateFactory.Lua53.createState()
        this.state = state
        if (state != null) {
            try {
                state.register(TLCategoryF())
                state.register(OpenGuiF())
                state.register(GetMcF())
                state.registerModule("i18n", 1, arrayOf(I18nFormatF()))

                state.load(script, "=igmenu")
                // Evaluate the chunk, thus defining the function
                state.call(0, 0)
                state.getGlobal("tlCats")

                // Get and print result
                val tableSize = state.tableSize(-1)
                println("Found $tableSize categories from Lua")

                repeat(tableSize) {
                    state.pushInteger(it.toLong() + 1)
                    state.getTable(-2)

                    // Get and print result
                    if (state.isString(-1)) {
                        val result = state.checkString(-1)
                        println("Found $result from Lua")
                    } else if (state.isJavaObject(-1, CategoryButton::class.java)) {
                        val category = state.checkJavaObject(-1, CategoryButton::class.java)
                        tlCategories += category
                        println("Foound Category $category")
                    } else println("Found unknown (${state.type(-1)}) in Lua")
                    state.pop(1)
                }

                state.pop(1)
            } catch (e: Exception) {
                SAOCore.LOGGER.warn("Something went wrong evaluating Lua script", e)
                IngameMenu.mc.player?.message("Evaluating igmenu.lua failed : ${e.message}")
            } finally {
//                state.close() // TODO : figure out proper state lifecycle. Closing it prevents lazy callbacks
            }
        } else System.err.println("Unable to create state")

        return tlCategories
    }

    private fun LuaState.load(@Language("lua") code: String) = load(code, "=test")
}

private class TLCategoryF : NamedJavaFunction {
    override fun invoke(p0: LuaState): Int {
        val icon = IconCore.valueOf(p0.checkString(1))
        val index = p0.checkInt32(2)
        val body: (CategoryButton.() -> Unit)? = if (p0.top > 2 && p0.isFunction(3)) {
            {
                p0.pushJavaObject(this)
                p0.call(1, 0)
            }
        } else null
        p0.pushJavaObject(ElementRegistry.tlCategory(icon, index, body))

        return 1
    }

    override fun getName() = "tlCategory"
}

private class OpenGuiF : NamedJavaFunction {
    override fun invoke(p0: LuaState): Int {
        val gui = p0.checkJavaObject(-1, GuiScreen::class.java)
        IngameMenu.mc.displayGuiScreen(gui)

        return 1
    }

    override fun getName() = "openGui"
}

private class I18nFormatF : NamedJavaFunction {
    override fun invoke(p0: LuaState): Int {
        val label = p0.checkString(1)
        p0.pushJavaObject(I18n.format(label))

        return 1
    }

    override fun getName() = "format"
}

private class GetMcF : NamedJavaFunction {
    override fun invoke(p0: LuaState): Int {
        p0.pushJavaObject(IngameMenu.mc)

        return 1
    }

    override fun getName() = "mc"
}

inline fun <T> LuaState.catching(default: T, body: () -> T): T {
    return try {
        body()
    } catch (e: Exception) {
        val stack = (1..this.top).map { "$it: ${type(it)}" }
        SAOCore.LOGGER.warn("Evaluation failed. Stack : ${stack.joinToString()}", e)
        default
    }
}
