package be.bluexin.mcui.util

import be.bluexin.mcui.Constants
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import org.luaj.vm2.lib.jse.CoerceJavaToLua

@OptIn(ExperimentalSerializationApi::class)
sealed class AbstractLuaEncoder(protected val parent: AbstractLuaEncoder? = null) : AbstractEncoder() {

    abstract fun endChild(value: Any?)

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        Constants.LOG.info("Start structure $descriptor")
        return when (descriptor.kind) {
            StructureKind.LIST -> LuaListEncoder(this)
            StructureKind.MAP, is PolymorphicKind -> LuaEncoder(this)
            else -> LuaEncoder(this)
        }
    }

    override fun shouldEncodeElementDefault(descriptor: SerialDescriptor, index: Int) = false

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) =
        encodeValue(enumDescriptor.getElementName(index))

    class LuaEncoder(parent: AbstractLuaEncoder? = null) : AbstractLuaEncoder(parent) {
        override val serializersModule = EmptySerializersModule()

        private var currentName: String? = null
        private val cn get() = currentName ?: error("currentName should not be null")
        private val properties = mutableMapOf<String, Any?>()
        val data get() = properties.toMap()

        override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
            currentName = descriptor.getElementName(index)
            Constants.LOG.info("Encoding $currentName at $index in ${descriptor.serialName}")
            return true
        }

        override fun endChild(value: Any?) {
            Constants.LOG.info("Encoding $cn : $value")
            properties[cn] = value
            currentName = null
        }

        override fun beginStructure(descriptor: SerialDescriptor) =
            if (currentName == null) this
            else super.beginStructure(descriptor)

        override fun endStructure(descriptor: SerialDescriptor) {
            Constants.LOG.info("End structure $descriptor")
            parent?.let {
                when {
                    properties.size > 1 -> it.endChild(CoerceJavaToLua.coerce(properties))
                    properties.size == 1 -> it.endChild(CoerceJavaToLua.coerce(properties.values.single()))
                    else -> it.endChild(CoerceJavaToLua.coerce(emptyMap<String, Any?>()))
                }
            }
        }

        override fun encodeValue(value: Any) = endChild(value)
        override fun encodeNull() = endChild(null)
    }

    class LuaListEncoder(parent: AbstractLuaEncoder? = null) : AbstractLuaEncoder(parent) {
        override val serializersModule = EmptySerializersModule()

        private val values = mutableListOf<Any?>()

        override fun encodeValue(value: Any) = endChild(value)

        override fun endStructure(descriptor: SerialDescriptor) {
            Constants.LOG.info("End list $descriptor")
            parent?.endChild(CoerceJavaToLua.coerce(values))
        }

        override fun endChild(value: Any?) {
            values += value
        }
    }
}

