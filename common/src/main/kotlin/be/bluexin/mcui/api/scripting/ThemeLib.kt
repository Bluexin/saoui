package be.bluexin.mcui.api.scripting

import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.TwoArgFunction

object ThemeLib : TwoArgFunction() {
    override fun call(modname: LuaValue, env: LuaValue): LuaValue {
        val themeTable = LuaTable()
        themeTable["readFragment"] = ReadFragment
        themeTable["loadFragment"] = LoadFragment
        themeTable["readWidget"] = ReadWidget
        TODO("Not yet implemented")
        themeTable["loadWidget"] = ReadWidget
        themeTable["registerScreen"] = ReadWidget
        env["theme"] = themeTable
        if (!env["package"].isnil()) {
            env["package"]["loaded"]["theme"] = themeTable
        }

        return themeTable
    }
}