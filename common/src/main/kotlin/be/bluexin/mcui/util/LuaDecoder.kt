package be.bluexin.mcui.util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.EmptySerializersModule
import org.luaj.vm2.LuaUserdata
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.CoerceLuaToJava
import java.util.*

@OptIn(ExperimentalSerializationApi::class)
sealed class AbstractLuaDecoder(
    protected var parent: AbstractLuaDecoder? = null
) : AbstractDecoder() {
    override val serializersModule = EmptySerializersModule()

    protected abstract val popValue: Any?

    override fun decodeValue(): Any = when (val value = popValue) {
        null -> error("Unexpected null value")
        is LuaValue -> CoerceLuaToJava.coerce(value, Any::class.java)
        else -> value
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        return when (descriptor.kind) {
            StructureKind.LIST -> LuaListDecoder(popValue as LuaUserdata, this)
            else -> {
                if (descriptor.elementsCount == 1 && descriptor.getElementDescriptor(0).kind == StructureKind.LIST) {
                    // we use a listDecoder directly when there is only 1 child, and it is a list
                    LuaListDecoder(popValue as LuaUserdata, this)
                } else when (val value = popValue) {
                    is LuaUserdata -> {
                        if (value.isuserdata(List::class.java)) LuaListDecoder(value, this)
                        else LuaDecoder(value, this, descriptor)
                    }

                    else -> LuaDirectDecoder(value, this)
                }
            }
        }
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor) = enumDescriptor.getElementIndex(popValue as String)

    class LuaDecoder(
        queue: Deque<Map.Entry<*, *>>,
        parent: AbstractLuaDecoder?,
        descriptor: SerialDescriptor?
    ) : AbstractLuaDecoder(parent) {
        constructor(
            luaUserdata: LuaUserdata,
            parent: AbstractLuaDecoder? = null,
            descriptor: SerialDescriptor? = null
        ) : this(
            (luaUserdata.checkuserdata(Map::class.java) as Map<*, *>).entries.let(::LinkedList),
            parent,
            descriptor
        )

        private val queue: Deque<Map.Entry<*, *>> = if (descriptor == null) queue else {
            queue.sortedBy { (key, _) ->
                descriptor.getElementAnnotations(descriptor.getElementIndex(key as String))
                    .firstNotNullOfOrNull { (it as? DeserializationOrder)?.order }
                    ?: Int.MAX_VALUE
            }.let(::LinkedList)
        }

        private val peekKey get() = queue.peek()?.key as? String
        override val popValue get() = queue.pop().value

        override fun decodeElementIndex(descriptor: SerialDescriptor) =
            peekKey?.let(descriptor::getElementIndex) ?: CompositeDecoder.DECODE_DONE

        override fun beginStructure(descriptor: SerialDescriptor) =
            // From the root decoder, we do not have a descriptor yet -- this will apply field ordering
            if (parent == null) LuaDecoder(queue, this, descriptor)
            else super.beginStructure(descriptor)

        override fun decodeNotNullMark() = queue.peek()?.value != null
    }

    class LuaListDecoder(data: List<*>, parent: AbstractLuaDecoder? = null) : AbstractLuaDecoder(parent) {
        constructor(
            luaUserdata: LuaUserdata,
            parent: AbstractLuaDecoder? = null
        ) : this(luaUserdata.checkuserdata(List::class.java) as List<*>, parent)

        private var queue = LinkedList(data)
        private var index = 0

        override val popValue get() = queue.pop()

        override fun decodeElementIndex(descriptor: SerialDescriptor) = when {
            queue.isEmpty() -> CompositeDecoder.DECODE_DONE
            else -> index++
        }

        override fun beginStructure(descriptor: SerialDescriptor) =
            // we use a listDecoder directly when there is only 1 child, and it is a list
            if (descriptor.kind == StructureKind.LIST) this
            else super.beginStructure(descriptor)

        override fun decodeCollectionSize(descriptor: SerialDescriptor) = queue.size

        override fun decodeSequentially() = true
    }

    class LuaDirectDecoder(private val data: Any?, parent: AbstractLuaDecoder? = null) : AbstractLuaDecoder(parent) {
        private var read = false

        override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
            if (!read) repeat(descriptor.elementsCount) {
                // Single non-optional can get serialized as direct value
                if (!descriptor.isElementOptional(it)) return it
            }
            return CompositeDecoder.DECODE_DONE
        }

        override val popValue: Any?
            get() = data.takeUnless { read }?.also { read = true }
    }
}