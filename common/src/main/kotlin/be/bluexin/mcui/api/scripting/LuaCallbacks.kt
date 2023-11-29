package be.bluexin.mcui.api.scripting

import be.bluexin.mcui.themes.JsonThemeLoader
import be.bluexin.mcui.themes.elements.Fragment
import be.bluexin.mcui.util.AbstractLuaEncoder
import net.minecraft.resources.ResourceLocation
import org.luaj.vm2.LuaFunction
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.CoerceJavaToLua

object LoadFragment : LuaFunction() {

    override fun call(arg: LuaValue) = call(arg.checkjstring())

    override fun call(arg: String): LuaValue {
        return wrap(loadFragment(arg))
    }

    override fun call(arg1: LuaValue, arg2: LuaValue): LuaValue {
        return wrap(loadFragment(ResourceLocation(arg1.checkjstring(), arg2.checkjstring())))
    }

    private fun wrap(fragment: Fragment) = CoerceJavaToLua.coerce(
        AbstractLuaEncoder.LuaEncoder().apply {
            encodeSerializableValue(Fragment.serializer(), fragment)
        }.data
    )

    private fun loadFragment(
        rl: ResourceLocation
    ): Fragment {
        return JsonThemeLoader.loadFragment(rl)
    }

    private fun loadFragment(
        rl: String
    ) = loadFragment(ResourceLocation(rl))
}
