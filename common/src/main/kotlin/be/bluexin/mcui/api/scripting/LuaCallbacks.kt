package be.bluexin.mcui.api.scripting

import be.bluexin.mcui.themes.JsonThemeLoader
import be.bluexin.mcui.themes.elements.Fragment
import net.minecraft.resources.ResourceLocation
import org.luaj.vm2.LuaFunction
import org.luaj.vm2.LuaValue

object LoadFragment : LuaFunction() {

    override fun call(arg: LuaValue) = call(arg.checkjstring())

    override fun call(arg: String): LuaValue {
        return LuaFragment(loadFragment(arg))
    }

    override fun call(arg1: LuaValue, arg2: LuaValue): LuaValue {
        return LuaFragment(loadFragment(ResourceLocation(arg1.checkjstring(), arg2.checkjstring())))
    }

    private fun loadFragment(
        rl: ResourceLocation
    ): Fragment {
        return JsonThemeLoader.loadFragment(rl)
    }

    private fun loadFragment(
        rl: String
    ) = loadFragment(ResourceLocation(rl))
}
