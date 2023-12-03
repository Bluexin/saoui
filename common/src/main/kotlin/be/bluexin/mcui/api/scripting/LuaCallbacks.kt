package be.bluexin.mcui.api.scripting

import be.bluexin.mcui.Constants
import be.bluexin.mcui.themes.JsonThemeLoader
import be.bluexin.mcui.themes.elements.Fragment
import be.bluexin.mcui.themes.elements.FragmentReference
import be.bluexin.mcui.themes.util.Variables
import be.bluexin.mcui.util.AbstractLuaDecoder
import be.bluexin.mcui.util.AbstractLuaEncoder
import kotlinx.serialization.ExperimentalSerializationApi
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import org.luaj.vm2.LuaFunction
import org.luaj.vm2.LuaValue
import java.lang.ref.WeakReference
import java.util.*

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

    private fun generateId() = "generated:${UUID.randomUUID()}"

    override fun call(arg1: LuaValue, arg2: LuaValue): LuaValue {
        val (target, fragment) = internalLoad(arg1, arg2)
        val id = generateId()
        target.add(FragmentReference(id = id).also {
            it.setup(target, mapOf(ResourceLocation(id) to { fragment }))
        })

        return LuaValue.TRUE
    }

    override fun call(arg1: LuaValue, arg2: LuaValue, arg3: LuaValue): LuaValue {
        val (target, fragment) = internalLoad(arg1, arg2)
        val fragmentReference = try {
            val id = generateId()
            val serializer = Variables.serializer()
            @OptIn(ExperimentalSerializationApi::class) // TODO : move this special handling to the decoder
            AbstractLuaDecoder.LuaMapDecoder(arg3.checktable(), null, serializer.descriptor.getElementDescriptor(0))
                .decodeSerializableValue(serializer).let { variables ->
                    FragmentReference(id = id, serializedVariables = variables).also {
                        it.setup(target, mapOf(ResourceLocation(id) to { fragment }))
                    }
                }
        } catch (e: Throwable) {
            Constants.LOG.error("Could not parse variables", e)
            FragmentReference()
        }

        Minecraft.getInstance().tell {
            target.add(fragmentReference)
        }

        return LuaValue.TRUE
    }

    private fun internalLoad(arg1: LuaValue, arg2: LuaValue): Pair<Fragment, Fragment> {
        val target = find(arg1.checkjstring())
        requireNotNull(target) { "Couldn't find fragment $arg1" }
        return target to AbstractLuaDecoder.LuaDecoder(arg2.checktable())
            .decodeSerializableValue(Fragment.serializer())
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
