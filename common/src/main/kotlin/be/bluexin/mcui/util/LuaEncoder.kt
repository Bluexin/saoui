package be.bluexin.mcui.util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import org.luaj.vm2.LuaUserdata
import org.luaj.vm2.lib.jse.CoerceJavaToLua

@OptIn(ExperimentalSerializationApi::class)
sealed class AbstractLuaEncoder(protected val parent: AbstractLuaEncoder? = null) : AbstractEncoder() {

    override val serializersModule = EmptySerializersModule()

    abstract fun endChild(value: Any?)

    override fun beginStructure(descriptor: SerialDescriptor) = when (descriptor.kind) {
        StructureKind.LIST -> LuaListEncoder(this)
        StructureKind.MAP, is PolymorphicKind -> LuaEncoder(this)
        else -> LuaEncoder(this)
    }

    override fun shouldEncodeElementDefault(descriptor: SerialDescriptor, index: Int) = false

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) =
        encodeValue(enumDescriptor.getElementName(index))

    class LuaEncoder(parent: AbstractLuaEncoder? = null) : AbstractLuaEncoder(parent) {

        private var currentName: String? = null
        private val cn get() = currentName ?: error("currentName should not be null")
        private val properties = mutableMapOf<String, Any?>()
        val data: LuaUserdata get() = CoerceJavaToLua.coerce(properties.toMap()) as LuaUserdata

        override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
            currentName = descriptor.getElementName(index)
            return true
        }

        override fun endChild(value: Any?) {
            properties[cn] = value
            currentName = null
        }

        override fun beginStructure(descriptor: SerialDescriptor) =
            if (currentName == null) this
            else super.beginStructure(descriptor)

        override fun endStructure(descriptor: SerialDescriptor) {
            parent?.let {
                when {
                    properties.size > 1 -> it.endChild(CoerceJavaToLua.coerce(properties))
                    // Single non-optional can get serialized as direct value
                    properties.size == 1 -> it.endChild(CoerceJavaToLua.coerce(properties.values.single()))
                    else -> it.endChild(CoerceJavaToLua.coerce(emptyMap<String, Any?>()))
                }
            }
        }

        override fun encodeValue(value: Any) = endChild(value)
        override fun encodeNull() = endChild(null)
    }

    class LuaListEncoder(parent: AbstractLuaEncoder? = null) : AbstractLuaEncoder(parent) {

        private val values = mutableListOf<Any?>()

        override fun encodeValue(value: Any) = endChild(value)

        override fun endStructure(descriptor: SerialDescriptor) {
            parent?.endChild(CoerceJavaToLua.coerce(values))
        }

        override fun endChild(value: Any?) {
            values += value
        }
    }
}

