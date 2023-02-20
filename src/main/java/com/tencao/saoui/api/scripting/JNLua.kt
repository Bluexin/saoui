package com.tencao.saoui.api.scripting

import com.tencao.saomclib.message
import com.tencao.saoui.SAOCore
import com.tencao.saoui.api.elements.CategoryButton
import com.tencao.saoui.api.elements.INeoParent
import com.tencao.saoui.api.elements.optionCategory
import com.tencao.saoui.api.elements.registry.ElementRegistry
import com.tencao.saoui.api.items.IItemFilter
import com.tencao.saoui.api.items.ItemFilterRegister
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.screens.menus.IngameMenu
import com.tencao.saoui.util.IconCore
import li.cil.repack.com.naef.jnlua.LuaState
import li.cil.repack.com.naef.jnlua.NamedJavaFunction
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.resources.I18n
import org.intellij.lang.annotations.Language
import java.io.File

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
        val state = JNLuaStateFactory.Lua53.createState()
        this.state = state
        if (state != null) {
            try {
                state.register(TLCategoryF())
                state.register(OpenGuiF())

                state.pushJavaObject(IngameMenu.mc)
                state.setGlobal("mc")

                state.registerModule("i18n", 1, arrayOf(I18nFormatF()))

                state.pushEnum(
                    "OptionCore", OptionCore.values(), mapOf(
                        "tlOptions" to OptionCore.tlOptions
                    )
                )

                state.pushJavaObject(ItemFilterRegister.tlFilters)
                state.setGlobal("ItemFilters")

                state.pushJavaFunction {
                    with(ElementRegistry) { it.checkJavaObject(-1, CategoryButton::class.java).setWip() }
                    1
                }
                state.setGlobal("SetWip")

                state.pushJavaFunction {
                    val button = it.checkJavaObject(1, CategoryButton::class.java)
                    val filter = it.checkJavaObject(2, IItemFilter::class.java)
                    with(ElementRegistry) { button.addItemCategories(filter) }
                    1
                }
                state.setGlobal("AddItemCategories")

                state.pushJavaFunction {
                    val parent = it.checkJavaObject(1, INeoParent::class.java)
                    val option = it.checkJavaObject(2, OptionCore::class.java)
                    it.pushJavaObject(parent.optionCategory(option))
                    1
                }
                state.setGlobal("OptionCategory")

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
                p0.catching(Unit) {
                    p0.pushJavaObject(this)
                    p0.call(1, 0)
                }
            }
        } else null
        p0.pushJavaObject(ElementRegistry.tlCategory(icon, index, body))

        return 1
    }

    override fun getName() = "Category"
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

inline fun <T> LuaState.catching(default: T, body: () -> T): T {
    return try {
        body()
    } catch (e: Exception) {
        val stack = (1..this.top).map { "$it: ${type(it)}" }
        val message = "Evaluation failed. Stack : ${stack.joinToString()}"
        SAOCore.LOGGER.warn(message, e)
        IngameMenu.mc.player?.apply {
            message(message)
            e.message?.let { message(it) }
        }
        default
    }
}

private fun LuaState.pushEnum(name: String, values: Array<out Enum<*>>, extra: Map<String, Any?> = emptyMap()) {
    newTable()
    (values.associateBy(Enum<*>::name) + extra).forEach { (key, value) ->
        pushString(key)
        pushJavaObject(value)
        setTable(-3)
    }
    setGlobal(name)
}
