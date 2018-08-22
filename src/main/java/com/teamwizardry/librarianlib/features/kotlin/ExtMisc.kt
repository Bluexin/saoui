@file:JvmMultifileClass
@file:JvmName("CommonUtilMethods")

package com.teamwizardry.librarianlib.features.kotlin

import com.teamwizardry.librarianlib.features.math.Vec3d
import net.minecraft.entity.Entity
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import java.lang.reflect.Modifier
import java.lang.reflect.Parameter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*
import kotlin.reflect.KParameter
import kotlin.reflect.full.starProjectedType

fun Int.abs() = if (this < 0) -this else this

fun <K, V> MutableMap<K, V>.withRealDefault(default: (K) -> V): DefaultedMutableMap<K, V> {
    return when (this) {
        is RealDefaultImpl -> RealDefaultImpl(this.map, default)
        else -> RealDefaultImpl(this, default)
    }
}

interface DefaultedMutableMap<K, V> : MutableMap<K, V> {
    override fun get(key: K): V
}

private class RealDefaultImpl<K, V>(val map: MutableMap<K, V>, val default: (K) -> V) : DefaultedMutableMap<K, V>, MutableMap<K, V> by map {
    override fun get(key: K): V {
        return map.getOrPut(key) { default(key) }
    }
}

// Class ===============================================================================================================

fun <T> Class<T>.genericType(index: Int): Type? {
    val genericSuper = genericSuperclass
    return if (genericSuper is ParameterizedType) {
        val args = genericSuper.actualTypeArguments
        if (0 <= index && index < args.size)
            genericSuper.actualTypeArguments[index]
        else null
    } else null
}

fun <T> Class<T>.genericClass(index: Int): Class<*>? {
    val generic = genericType(index) ?: return null
    return if (generic is Class<*>) generic else null
}

@Suppress("UNCHECKED_CAST")
fun <T, O> Class<T>.genericClassTyped(index: Int) = genericClass(index) as Class<O>?

private val singletonMap = IdentityHashMap<Class<*>, Any?>()

/**
 * Searches for one of a few things:
 *
 * * If the class is a Kotlin object it will get the instance of it
 * * If the class has a static final field named `INSTANCE` with the same type as this class, gets the value of that field (if it is null, returns null)
 * * If the class has a zero-argument constructor, instantiates an instance of the class
 * * If none of these requirements was fulfilled or if the `INSTANCE` field contained null, returns null.
 *
 * After the first time this property is accessed for a class, its instance is cached for faster lookups.
 */
val <T : Any> Class<T>.singletonInstance: T?
    get() {
        @Suppress("UNCHECKED_CAST")
        if (this in singletonMap) return singletonMap[this] as T?

        val kt = this.kotlin.objectInstance
        if (kt != null) {
            singletonMap[this] = kt
            return kt
        }

        val field = this.declaredFields.find {
            Modifier.isStatic(it.modifiers) && Modifier.isFinal(it.modifiers) && it.name == "INSTANCE" && it.type == this
        }

        if (field != null) {
            val value = field.get(null)
            singletonMap[this] = value
            @Suppress("UNCHECKED_CAST")
            return value as T?
        }

        try {
            val constructor = this.getConstructor()
            val value = constructor.newInstance()
            singletonMap[this] = value
            return value
        } catch (e: NoSuchMethodException) {
            // NOOP
        }

        singletonMap[this] = null
        return null
    }

var Entity.motionVec: Vec3d
    get() = Vec3d(motionX, motionY, motionZ)
    set(value) {
        this.motionX = value.x
        this.motionY = value.y
        this.motionZ = value.z
    }

// String ==============================================================================================================

operator fun CharSequence.times(n: Int) = this.repeat(n)
operator fun Int.times(n: CharSequence) = n.repeat(this)

// Numbers =============================================================================================================

fun Int.clamp(min: Int, max: Int): Int = if (this < min) min else if (this > max) max else this
fun Short.clamp(min: Short, max: Short): Short = if (this < min) min else if (this > max) max else this
fun Long.clamp(min: Long, max: Long): Long = if (this < min) min else if (this > max) max else this
fun Byte.clamp(min: Byte, max: Byte): Byte = if (this < min) min else if (this > max) max else this
fun Char.clamp(min: Char, max: Char): Char = if (this < min) min else if (this > max) max else this
fun Float.clamp(min: Float, max: Float): Float = if (this < min) min else if (this > max) max else this
fun Double.clamp(min: Double, max: Double): Double = if (this < min) min else if (this > max) max else this

// listOf and mapOf ====================================================================================================

inline fun <reified K : Enum<K>, V> enumMapOf(): EnumMap<K, V> {
    return EnumMap(K::class.java)
}

inline fun <reified K : Enum<K>, V> enumMapOf(vararg pairs: Pair<K, V>): EnumMap<K, V> {
    val map = enumMapOf<K, V>()
    map.putAll(pairs)
    return map
}

// Collections =========================================================================================================

fun <T : Any, E : Any, R : Collection<T>, F : Collection<E>> R.instanceOf(collection: F): Boolean {
    return javaClass.isAssignableFrom(collection.javaClass) && (this.isNotEmpty() && collection.isNotEmpty() && elementAt(0).javaClass.isAssignableFrom(collection.elementAt(0).javaClass))
}

/**
 * Checks whether a [Parameter] [kotlin.Array] matches a [KParameter] [kotlin.collections.List]
 */
fun kotlin.Array<Parameter>.matches(other: kotlin.collections.List<KParameter>): Boolean {
    if (size != other.size) return false
    var ok = true
    this.forEachIndexed { i, it ->
        ok = other[i].type == it.type.kotlin.starProjectedType
        if (!ok) return@forEachIndexed
    }
    return ok
}

inline fun <T : NBTBase> NBTTagList(size: Int, generator: (Int) -> T): NBTTagList {
    val list = NBTTagList()
    for (i in 0 until size)
        list.appendTag(generator(i))
    return list
}

inline fun NBTTagCompound(size: Int, generator: (Int) -> Pair<String, NBTBase>): NBTTagCompound {
    val list = NBTTagCompound()
    for (i in 0 until size) {
        val (key, value) = generator(i)
        list.setTag(key, value)
    }
    return list
}

inline fun NBTTagCompound.forEach(code: (key: String, value: NBTBase) -> Unit) {
    @Suppress("UNCHECKED_CAST")
    for (key in keySet as Set<String>)
        code(key, getTag(key))
}
