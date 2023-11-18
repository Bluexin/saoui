package com.tencao.saoui.api.scripting

import com.tencao.saoui.Constants
import com.tencao.saoui.api.elements.CategoryButton
import com.tencao.saoui.api.elements.INeoParent
import com.tencao.saoui.api.elements.optionCategory
import com.tencao.saoui.api.elements.registry.ElementRegistry
import com.tencao.saoui.api.items.IItemFilter
import com.tencao.saoui.api.items.ItemFilterRegister
import com.tencao.saoui.config.OptionCategory
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.util.*
import li.cil.repack.com.naef.jnlua.LuaState
import li.cil.repack.com.naef.jnlua.NamedJavaFunction
import net.minecraft.client.gui.screens.Screen
import java.io.File

object JNLua {

    private fun LuaState.registerModule(name: String, version: Long, functions: Array<NamedJavaFunction>) {
        register(name, functions, true)
        // Set a field 'VERSION' with the value 1
        pushInteger(version)
        setField(-2, "VERSION")
        // Pop the module table
        pop(1)
    }

    private var state: LuaState? = null

    fun loadIngameMenu(): MutableList<CategoryButton> {
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
                state.pushGlobalObject("mc", Client.minecraft)
                state.registerModule("i18n", 1, arrayOf(I18nFormatF()))
                state.pushEnum("OptionCore", OptionCore.values(), mapOf("tlOptions" to OptionCore.values()))
                state.pushGlobalObject("ItemFilters", ItemFilterRegister.tlFilters)

                state.pushGlobalFunction("SetWip") {
                    with(ElementRegistry) { it.checkJavaObject(-1, CategoryButton::class.java).setWip() }
                    1
                }

                state.pushGlobalFunction("AddItemCategories") {
                    val button = it.checkJavaObject(1, CategoryButton::class.java)
                    val filter = it.checkJavaObject(2, IItemFilter::class.java)
                    with(ElementRegistry) { button.addItemCategories(filter) }
                    1
                }

                //TODO Seperate options to allow users to define their own
                state.pushGlobalFunction("OptionCategory") {
                    val parent = it.checkJavaObject(1, INeoParent::class.java)
                    val optionCategory = it.checkJavaObject(2, OptionCategory::class.java)
                    it.pushJavaObject(parent.optionCategory(optionCategory))
                    1
                }

                state.load(script, "=igmenu")
                // Evaluate the chunk, thus defining the tlCats
                state.call(0, 0)
                state.getGlobal("tlCats")

                // Get and print result
                val tableSize = state.tableSize(-1)
                Constants.LOGGER.debug("Found $tableSize categories from Lua")

                repeat(tableSize) {
                    state.pushInteger(it.toLong() + 1)
                    state.getTable(-2)

                    // Get and print result
                    if (state.isString(-1)) {
                        val result = state.checkString(-1)
                        Constants.LOGGER.debug("Found $result from Lua")
                    } else if (state.isJavaObject(-1, CategoryButton::class.java)) {
                        val category = state.checkJavaObject(-1, CategoryButton::class.java)
                        tlCategories += category
                        Constants.LOGGER.debug("Foound Category {}", category)
                    } else Constants.LOGGER.debug("Found unknown ({}) in Lua", state.type(-1))
                    state.pop(1)
                }

                state.pop(1)
            } catch (e: Exception) {
                Constants.LOGGER.warn("Something went wrong evaluating Lua script", e)
                Client.player?.displayClientMessage("Evaluating igmenu.lua failed : ${e.message}".translate(), false)
            } finally {
//                state.close() // TODO : figure out proper state lifecycle. Closing it prevents lazy callbacks
            }
        } else Constants.LOGGER.error("Unable to create state")

        return tlCategories
    }
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
        val gui = p0.checkJavaObject(-1, Screen::class.java)
        Client.minecraft.setScreen(gui)

        return 1
    }

    override fun getName() = "OpenGui"
}

private class I18nFormatF : NamedJavaFunction {
    override fun invoke(p0: LuaState): Int {
        val label = p0.checkString(1)
        val top = p0.top
        val extra = if (top > 1) Array<Any>(top - 1) { p0.toJavaObjectRaw(it + 2) } else emptyArray<Any>()
        p0.pushJavaObject(label.translate(*extra))

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
        Constants.LOGGER.warn(message, e)
        Client.player?.apply {
            displayClientMessage(message.toTextComponent(), false)
            e.message?.let { displayClientMessage(it.toTextComponent(), false) }
        }
        default
    }
}

private fun LuaState.pushEnum(name: String, values: Array<out Enum<*>>, extra: Map<String, Any?> = emptyMap()) {
    newTable(0, values.size + extra.size)
    (values.associateBy(Enum<*>::name) + extra).forEach { (key, value) ->
        pushString(key)
        pushJavaObject(value)
        setTable(-3)
    }
    setGlobal(name)
}

private fun LuaState.pushGlobalObject(name: String, global: Any?) {
    pushJavaObject(global)
    setGlobal(name)
}

private fun LuaState.pushGlobalFunction(name: String, global: (LuaState) -> Int) {
    pushJavaFunction(global)
    setGlobal(name)
}
