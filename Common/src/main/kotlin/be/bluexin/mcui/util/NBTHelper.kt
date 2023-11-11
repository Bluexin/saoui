@file:JvmMultifileClass
@file:JvmName("CommonUtilMethods")

package be.bluexin.mcui.util

import net.minecraft.nbt.*
import net.minecraft.nbt.Tag.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import java.util.*
import java.util.function.Consumer

/**
 * @author WireSegal
 * Created at 12:23 PM on 11/21/18.
 */

// ===================================================================================================== Generic Helpers

private inline fun <T : Any, K, E> T?.getIf(key: K, predicate: T?.(K) -> Boolean, get: T.(K) -> E): E? =
    getIf(key, predicate, get, null)

private inline fun <T : Any, K, E> T?.getIf(key: K, predicate: T?.(K) -> Boolean, get: T.(K) -> E, default: E): E {
    if (this != null && predicate(key)) {
        return get(key)
    }
    return default
}

// ========================================================================================================= NBT Helpers

fun Class<out Tag>.idForClass() = when (this) {
    ByteTag::class.java -> TAG_BYTE
    ShortTag::class.java -> TAG_SHORT
    IntTag::class.java -> TAG_INT
    LongTag::class.java -> TAG_LONG
    FloatTag::class.java -> TAG_FLOAT
    DoubleTag::class.java -> TAG_DOUBLE
    ByteArrayTag::class.java -> TAG_BYTE_ARRAY
    StringTag::class.java -> TAG_STRING
    ListTag::class.java -> TAG_LIST
    CompoundTag::class.java -> TAG_COMPOUND
    IntArrayTag::class.java -> TAG_INT_ARRAY
    LongArrayTag::class.java -> TAG_LONG_ARRAY
    else -> throw IllegalArgumentException("Unknown NBT type: $this")
}

fun Byte.nbtClassForId(): Class<out Tag> {
    return when (this) {
        TAG_BYTE -> ByteTag::class.java
        TAG_SHORT -> ShortTag::class.java
        TAG_INT -> IntTag::class.java
        TAG_LONG -> LongTag::class.java
        TAG_FLOAT -> FloatTag::class.java
        TAG_DOUBLE -> DoubleTag::class.java
        TAG_BYTE_ARRAY -> ByteArrayTag::class.java
        TAG_STRING -> StringTag::class.java
        TAG_LIST -> ListTag::class.java
        TAG_COMPOUND -> CompoundTag::class.java
        TAG_INT_ARRAY -> IntArrayTag::class.java
        TAG_LONG_ARRAY -> LongArrayTag::class.java
        else -> throw IllegalArgumentException("Unknown NBT type: $this")
    }
}

inline fun <reified T : Tag> Tag.castOrDefault(): T = this.castOrDefault(T::class.java)

@Suppress("UNCHECKED_CAST")
fun <T : Tag> Tag.castOrDefault(clazz: Class<T>): T {
    return (
        when {
            clazz.isAssignableFrom(this.javaClass) -> this
            else -> clazz.defaultNBTValue()
        }
        ) as T
}

inline fun <reified T : Tag> defaultNBTValue(): T = T::class.java.defaultNBTValue()

@Suppress("UNCHECKED_CAST")
fun <T : Tag> Class<T>.defaultNBTValue(): T {
    return (
        when {
            NumericTag::class.java.isAssignableFrom(this) -> when (this) {
                LongTag::class.java -> LongTag.valueOf(0)
                IntTag::class.java -> IntTag.valueOf(0)
                ShortTag::class.java -> ShortTag.valueOf(0)
                DoubleTag::class.java -> DoubleTag.valueOf(0.0)
                FloatTag::class.java -> FloatTag.valueOf(0f)
                else -> ByteTag.valueOf(0)
            }
            this == ByteArrayTag::class.java -> ByteArrayTag(ByteArray(0))
            this == StringTag::class.java -> StringTag.valueOf("")
            this == ListTag::class.java -> ListTag()
            this == CompoundTag::class.java -> CompoundTag()
            this == IntArrayTag::class.java -> IntArrayTag(IntArray(0))
            this == LongArrayTag::class.java -> LongArrayTag(LongArray(0))
            else -> throw IllegalArgumentException("Unknown NBT type to produce: $this")
        }
        ) as T
}

// ====================================================================================================== Legacy Support

private fun ListTag.toUniqueId(): UUID? {
    if (size != 2 || get(0) !is NumericTag) return null
    return UUID((get(0) as NumericTag).asLong, (get(1) as NumericTag).asLong)
}

private fun CompoundTag.updateLegacy(tag: String): CompoundTag {
    if (hasKey(tag, TAG_LIST)) {
        val list = getList(tag, TAG_ANY_NUMERIC.toInt())
        val converted = list.toUniqueId()
        if (converted != null) {
            removeTag(tag)
            setUniqueId(tag, converted)
        }
    }

    return this
}

// ===================================================================================================== CompoundTag?

fun CompoundTag?.removeTag(tag: String) = this?.remove(tag)

fun CompoundTag?.hasNumericKey(tag: String) = this.hasKey(tag, TAG_ANY_NUMERIC)
fun CompoundTag?.hasKey(tag: String) = this != null && tag in this
fun CompoundTag?.hasString(tag: String) = this != null && this.hasKey(tag, TAG_STRING)
fun CompoundTag?.hasIntArray(tag: String) = this != null && this.hasKey(tag, TAG_INT_ARRAY)
fun CompoundTag?.hasByteArray(tag: String) = this != null && this.hasKey(tag, TAG_BYTE_ARRAY)
fun CompoundTag?.hasKey(tag: String, type: Class<out Tag>) = this.hasKey(tag, type.idForClass())
fun CompoundTag?.hasKey(tag: String, id: Byte) = this != null && this.contains(tag, id.toInt())
fun CompoundTag?.hasUniqueId(tag: String) = this != null && this.hasUUID(tag)

fun CompoundTag?.setBoolean(tag: String, value: Boolean) = this?.putBoolean(tag, value)
fun CompoundTag?.setByte(tag: String, value: Byte) = this?.putByte(tag, value)
fun CompoundTag?.setShort(tag: String, value: Short) = this?.putShort(tag, value)
fun CompoundTag?.setInteger(tag: String, value: Int) = this?.putInt(tag, value)
fun CompoundTag?.setIntArray(tag: String, value: IntArray) = this?.putIntArray(tag, value)
fun CompoundTag?.setByteArray(tag: String, value: ByteArray) = this?.putByteArray(tag, value)
fun CompoundTag?.setLong(tag: String, value: Long) = this?.putLong(tag, value)
fun CompoundTag?.setFloat(tag: String, value: Float) = this?.putFloat(tag, value)
fun CompoundTag?.setDouble(tag: String, value: Double) = this?.putDouble(tag, value)
fun CompoundTag?.setCompoundTag(tag: String, value: CompoundTag) = setTag(tag, value)
fun CompoundTag?.setString(tag: String, value: String) = this?.putString(tag, value)
fun CompoundTag?.setTagList(tag: String, value: ListTag) = setTag(tag, value)
fun CompoundTag?.setUniqueId(tag: String, value: UUID) = this?.putUUID(tag, value)
fun CompoundTag?.setTag(tag: String, value: Tag) = this?.put(tag, value)

@JvmOverloads
fun CompoundTag?.getBoolean(tag: String, defaultExpected: Boolean = false) = getIf(tag, CompoundTag?::hasNumericKey, CompoundTag::getBoolean, defaultExpected)

@JvmOverloads
fun CompoundTag?.getByte(tag: String, defaultExpected: Byte = 0) = getIf(tag, CompoundTag?::hasNumericKey, CompoundTag::getByte, defaultExpected)

@JvmOverloads
fun CompoundTag?.getShort(tag: String, defaultExpected: Short = 0) = getIf(tag, CompoundTag?::hasNumericKey, CompoundTag::getShort, defaultExpected)

@JvmOverloads
fun CompoundTag?.getInteger(tag: String, defaultExpected: Int = 0) = getIf(tag, CompoundTag?::hasNumericKey, CompoundTag::getInt, defaultExpected)
fun CompoundTag?.getIntArray(tag: String) = getIf(tag, CompoundTag?::hasIntArray, CompoundTag::getIntArray)
fun CompoundTag?.getByteArray(tag: String) = getIf(tag, CompoundTag?::hasByteArray, CompoundTag::getByteArray)

@JvmOverloads
fun CompoundTag?.getLong(tag: String, defaultExpected: Long = 0) = getIf(tag, CompoundTag?::hasNumericKey, CompoundTag::getLong, defaultExpected)

@JvmOverloads
fun CompoundTag?.getFloat(tag: String, defaultExpected: Float = 0f) = getIf(tag, CompoundTag?::hasNumericKey, CompoundTag::getFloat, defaultExpected)

@JvmOverloads
fun CompoundTag?.getDouble(tag: String, defaultExpected: Double = 0.0) = getIf(tag, CompoundTag?::hasNumericKey, CompoundTag::getDouble, defaultExpected)
fun CompoundTag?.getCompoundTag(tag: String): CompoundTag? = getIf(tag, CompoundTag?::hasKey, CompoundTag::getCompoundTag)
fun CompoundTag?.getString(tag: String) = getIf(tag, CompoundTag?::hasKey, CompoundTag::getString)
fun CompoundTag?.getTagList(tag: String, type: Class<out Tag>) = getTagList(tag, type.idForClass())
fun CompoundTag?.getTagList(tag: String, objType: Byte) = getTagList(tag, objType.toInt())
fun CompoundTag?.getTagList(tag: String, objType: Int) = getIf(tag, { this.hasKey(it, TAG_LIST) }) { getList(it, objType) }
fun CompoundTag?.getUniqueId(tag: String) = getIf(tag, CompoundTag?::hasUniqueId, CompoundTag::getUUID)
fun CompoundTag?.getTag(tag: String) = getIf(tag, CompoundTag?::hasKey, CompoundTag::get)

// =========================================================================================================== ItemStack

fun ItemStack.getOrCreateNBT(): CompoundTag {
    val compound = this.tag ?: CompoundTag()
    this.tag = compound
    return compound
}

fun ItemStack.removeNBTEntry(tag: String) = getTag().removeTag(tag)

fun ItemStack.hasNBTEntry(tag: String) = getTag().hasKey(tag)
fun ItemStack.hasNBTUniqueIdEntry(tag: String) = getTag()?.updateLegacy(tag).hasUniqueId(tag)

@JvmName("setBoolean")
fun ItemStack.setNBTBoolean(tag: String, value: Boolean) = getOrCreateNBT().setBoolean(tag, value)

@JvmName("setByte")
fun ItemStack.setNBTByte(tag: String, value: Byte) = getOrCreateNBT().setByte(tag, value)

@JvmName("setShort")
fun ItemStack.setNBTShort(tag: String, value: Short) = getOrCreateNBT().setShort(tag, value)

@JvmName("setInt")
fun ItemStack.setNBTInt(tag: String, value: Int) = getOrCreateNBT().setInteger(tag, value)

@JvmName("setIntArray")
fun ItemStack.setNBTIntArray(tag: String, value: IntArray) = getOrCreateNBT().setIntArray(tag, value)

@JvmName("setByteArray")
fun ItemStack.setNBTByteArray(tag: String, value: ByteArray) = getOrCreateNBT().setByteArray(tag, value)

@JvmName("setLong")
fun ItemStack.setNBTLong(tag: String, value: Long) = getOrCreateNBT().setLong(tag, value)

@JvmName("setFloat")
fun ItemStack.setNBTFloat(tag: String, value: Float) = getOrCreateNBT().setFloat(tag, value)

@JvmName("setDouble")
fun ItemStack.setNBTDouble(tag: String, value: Double) = getOrCreateNBT().setDouble(tag, value)

@JvmName("setCompound")
fun ItemStack.setNBTCompound(tag: String, value: CompoundTag) = setNBTTag(tag, value)

@JvmName("setString")
fun ItemStack.setNBTString(tag: String, value: String) = getOrCreateNBT().setString(tag, value)

@JvmName("setList")
fun ItemStack.setNBTList(tag: String, value: ListTag) = setNBTTag(tag, value)

@JvmName("setUniqueId")
fun ItemStack.setNBTUniqueId(tag: String, value: UUID) = getOrCreateNBT().setUniqueId(tag, value)

@JvmName("setTag")
fun ItemStack.setNBTTag(tag: String, value: Tag) = getOrCreateNBT().setTag(tag, value)

@JvmOverloads
@JvmName("getBoolean")
fun ItemStack.getNBTBoolean(tag: String, defaultExpected: Boolean = false) = this.tag.getBoolean(tag, defaultExpected)

@JvmOverloads
@JvmName("getByte")
fun ItemStack.getNBTByte(tag: String, defaultExpected: Byte = 0) = this.tag.getByte(tag, defaultExpected)

@JvmOverloads
@JvmName("getShort")
fun ItemStack.getNBTShort(tag: String, defaultExpected: Short = 0) = this.tag.getShort(tag, defaultExpected)

@JvmOverloads
@JvmName("getInt")
fun ItemStack.getNBTInt(tag: String, defaultExpected: Int = 0) = this.tag.getInteger(tag, defaultExpected)

@JvmName("getIntArray")
fun ItemStack.getNBTIntArray(tag: String) = this.tag.getIntArray(tag)

@JvmName("getByteArray")
fun ItemStack.getNBTByteArray(tag: String) = this.tag.getByteArray(tag)

@JvmOverloads
@JvmName("getLong")
fun ItemStack.getNBTLong(tag: String, defaultExpected: Long = 0) = this.tag.getLong(tag, defaultExpected)

@JvmOverloads
@JvmName("getFloat")
fun ItemStack.getNBTFloat(tag: String, defaultExpected: Float = 0f) = this.tag.getFloat(tag, defaultExpected)

@JvmOverloads
@JvmName("getDouble")
fun ItemStack.getNBTDouble(tag: String, defaultExpected: Double = 0.0) = this.tag.getDouble(tag, defaultExpected)

@JvmName("getCompound")
fun ItemStack.getNBTCompound(tag: String): CompoundTag? = this.tag.getCompoundTag(tag)

@JvmName("getString")
fun ItemStack.getNBTString(tag: String) = this.tag.getString(tag)

@JvmName("getList")
fun ItemStack.getNBTList(tag: String, type: Class<out Tag>) = getNBTList(tag, type.idForClass().toInt())

@JvmName("getList")
fun ItemStack.getNBTList(tag: String, objType: Int) = this.tag.getTagList(tag, objType)

@JvmName("getUniqueId")
fun ItemStack.getNBTUniqueId(tag: String) = this.tag?.updateLegacy(tag).getUniqueId(tag)

@JvmName("getTag")
fun ItemStack.getNBTTag(tag: String) = this.tag.getTag(tag)

// ========================================================================================================== Extensions

operator fun CompoundTag?.contains(key: String) = hasKey(key)

// ListTag ==========================================================================================================

val ListTag.indices: IntRange
    get() = 0 until size

inline fun <reified T : Tag> ListTag.forEach(run: (T) -> Unit) {
    for (tag in this)
        run(tag.castOrDefault())
}

inline fun <reified T : Tag> ListTag.forEachIndexed(run: (Int, T) -> Unit) {
    for ((i, tag) in this.withIndex())
        run(i, tag.castOrDefault())
}

class NBTWrapper(val contained: ItemStack) {
    operator fun set(s: String, tag: Any?) {
        if (tag == null) {
            contained.removeNBTEntry(s)
        } else contained.setNBTTag(s, convertNBT(tag)!!)
    }

    operator fun get(s: String): Tag? {
        return contained.getNBTTag(s)
    }
}

val ItemStack.nbt: NBTWrapper
    get() = NBTWrapper(this)

// CompoundTag ======================================================================================================

operator fun CompoundTag.iterator(): Iterator<Pair<String, Tag>> {
    return object : Iterator<Pair<String, Tag>> {
        val keys = this@iterator.allKeys.iterator()
        override fun hasNext() = keys.hasNext()
        override fun next(): Pair<String, Tag> {
            val next = keys.next()
            return next to this@iterator[next]!!
        }
    }
}

operator fun CompoundTag.get(key: String): Tag? = this.getTag(key)

@JvmName("create")
fun tagCompound(lambda: NbtDsl.() -> Unit) = NbtDsl().apply(lambda).root

fun <T> list(vararg args: T): ListTag {
    val list = ListTag()
    args.forEach { list.add(convertNBT(it)) }
    return list
}

fun compound(vararg args: Pair<String, *>): CompoundTag {
    val comp = CompoundTag()
    args.forEach { convertNBT(it.second)?.let { tag -> comp.setTag(it.first, tag) } }
    return comp
}

class NbtDsl(val root: CompoundTag = CompoundTag()) {
    operator fun String.invoke(lambda: NbtDsl.() -> Unit) {
        root[this] = tagCompound(lambda)
    }

    infix fun String.to(lambda: NbtDsl.() -> Unit) = this(lambda)

    @JvmName("append")
    operator fun String.invoke(lambda: Consumer<NbtDsl>) = this { lambda.accept(this) }

    @JvmName("append")
    operator fun String.invoke(vararg values: Any?) {
        root[this] = if (values.size == 1) convertNBT(values.first())!! else convertNBT(values)!!
    }

    infix fun String.to(value: Any?) = this(value)
}

operator fun CompoundTag.set(key: String, value: Tag) = setTag(key, value)

fun convertNBT(value: Any?): Tag? = when (value) {
    is Tag -> value

    null -> ByteTag.valueOf(0)
    is Boolean -> ByteTag.valueOf(if (value) 1 else 0)
    is Byte -> ByteTag.valueOf(value)
    is Char -> ShortTag.valueOf(value.code.toShort())
    is Short -> ShortTag.valueOf(value)
    is Int -> IntTag.valueOf(value)
    is Long -> LongTag.valueOf(value)
    is Float -> FloatTag.valueOf(value)
    is Double -> DoubleTag.valueOf(value)
    is ByteArray -> ByteArrayTag(value)
    is String -> StringTag.valueOf(value)
    is IntArray -> IntArrayTag(value)
    is UUID -> ListTag().apply {
        add(LongTag.valueOf(value.leastSignificantBits))
        add(LongTag.valueOf(value.mostSignificantBits))
    }
    is Array<*> -> list(*value)
    is Collection<*> -> list(*value.toTypedArray())
    is Map<*, *> -> compound(*value.toList().map { it.first.toString() to it.second }.toTypedArray())
    is ResourceLocation -> StringTag.valueOf(value.toString())

//    is INBTSerializable<*> -> value.serializeNBT()
//    is IStringSerializable -> StringTag(value.name)

    else -> null
}
