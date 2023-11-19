package be.bluexin.mcui.api.scripting

import be.bluexin.mcui.themes.elements.Fragment
import org.luaj.vm2.LuaUserdata

class LuaFragment(
    val fragment: Fragment
) : LuaUserdata(fragment /* TODO: metatable ? */) {

}