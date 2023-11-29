package be.bluexin.mcui.api.scripting

import be.bluexin.mcui.themes.elements.Fragment
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import org.luaj.vm2.LuaUserdata

class LuaFragment(
    val fragment: Fragment
) : LuaUserdata(fragment /* TODO: metatable ? */) {
}

@OptIn(ExperimentalSerializationApi::class)
class LuaExpose<Data : Any, Serializer : KSerializer<Data>>(
    val data: Data,
    val serializer: Serializer
) : LuaUserdata(data) {
    // TODO : shared cache ?

    /*override fun get(key: LuaValue): LuaValue {
        val desc = descriptors[key.checkjstring()]
            ?: return super.get(key)


    }*/

    companion object {
        inline operator fun <reified Data : Any> invoke(data: Data) = LuaExpose(data, serializer())
    }
}
