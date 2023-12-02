package be.bluexin.mcui.api.scripting

import be.bluexin.mcui.themes.JsonThemeLoader
import be.bluexin.mcui.themes.elements.Fragment
import be.bluexin.mcui.util.AbstractLuaEncoder
import net.minecraft.resources.ResourceLocation
import org.luaj.vm2.LuaFunction
import org.luaj.vm2.LuaValue
import java.lang.ref.WeakReference

object ReadFragment : LuaFunction() {

    override fun call(arg: LuaValue) = call(arg.checkjstring())

    override fun call(arg: String): LuaValue {
        return wrap(loadFragment(arg))
    }

    override fun call(arg1: LuaValue, arg2: LuaValue): LuaValue {
        return wrap(loadFragment(ResourceLocation(arg1.checkjstring(), arg2.checkjstring())))
    }

    private fun wrap(fragment: Fragment) = AbstractLuaEncoder.LuaEncoder().apply {
        encodeSerializableValue(Fragment.serializer(), fragment)
    }.data

    private fun loadFragment(
        rl: ResourceLocation
    ): Fragment {
        return JsonThemeLoader.loadFragment(rl)
    }

    private fun loadFragment(
        rl: String
    ) = loadFragment(ResourceLocation(rl))
}

object LoadFragment : LuaFunction() {

    override fun call(arg1: LuaValue, arg2: LuaValue): LuaValue {
        val targetId = arg1.checkjstring()
        val fragmentData = arg2.checkuserdata(Map::class.java)

        return LuaValue.TRUE
    }

    private val roots = mutableMapOf<String, WeakReference<Fragment>>()

    // TODO : id should be a resource location
    operator fun set(id: String, fragment: Fragment) {
        roots.remove(id)?.clear()
        roots[id] = WeakReference(fragment)
    }

    private fun find(id: String) = roots[id]?.let {
        val fragment = it.get()
        if (fragment == null) roots.remove(id)
        fragment
    }

    fun clear(id: String) {
        roots.remove(id)?.clear()
    }
}
