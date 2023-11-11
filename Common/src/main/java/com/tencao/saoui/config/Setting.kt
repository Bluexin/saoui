package com.tencao.saoui.config

import com.google.gson.annotations.JsonAdapter
import com.tencao.saoui.themes.util.json.JsonSettingAdapterFactory
import net.minecraft.util.ResourceLocation
import kotlin.reflect.KProperty


@JsonAdapter(JsonSettingAdapterFactory::class)
sealed class Setting<T : Any>(
    val namespace: ResourceLocation,
    val key: ResourceLocation,
    val defaultValue: T,
    val comment: String?
) {
    operator fun component1() = namespace
    operator fun component2() = key
    operator fun component3() = defaultValue
    operator fun component4() = comment
    operator fun component5(): (serialized: String) -> T? = ::read
    operator fun component6(): (value: T) -> String = ::write
    operator fun component7(): (value: T) -> Boolean = ::validate
    operator fun component8() = type

    operator fun getValue(caller: Any, property: KProperty<*>): T = Settings[this]

    operator fun setValue(caller: Any, property: KProperty<*>, value: T) = Settings.set(this, value)

    open val type get() = Property.Type.STRING

    fun register() = this.also(Settings::register)

    abstract fun read(serialized: String): T?
    abstract fun write(value: T): String
    abstract fun validate(value: T): Boolean
}

open class StringSetting(
    namespace: ResourceLocation,
    key: ResourceLocation,
    defaultValue: String,
    comment: String? = null,
    private val validate: ((String) -> Boolean)? = null
) : Setting<String>(
    namespace, key,
    defaultValue, comment
) {
    override fun read(serialized: String) = serialized
    override fun write(value: String) = value
    override fun validate(value: String) = validate?.invoke(value) ?: true
}

class BooleanSetting(
    namespace: ResourceLocation,
    key: ResourceLocation,
    defaultValue: Boolean,
    comment: String? = null
) : Setting<Boolean>(
    namespace, key,
    defaultValue, comment
) {
    override val type get() = Property.Type.BOOLEAN

    override fun read(serialized: String) = serialized.toBooleanStrictOrNull()
    override fun write(value: Boolean) = value.toString()
    override fun validate(value: Boolean): Boolean = true
}

class IntSetting(
    namespace: ResourceLocation,
    key: ResourceLocation,
    defaultValue: Int,
    comment: String? = null,
    private val validate: ((Int) -> Boolean)? = null
) : Setting<Int>(
    namespace, key,
    defaultValue, comment
) {
    override val type get() = Property.Type.INTEGER

    override fun read(serialized: String): Int? = serialized.toIntOrNull()
    override fun write(value: Int): String = value.toString()
    override fun validate(value: Int): Boolean = validate?.invoke(value) ?: true
}

class DoubleSetting(
    namespace: ResourceLocation,
    key: ResourceLocation,
    defaultValue: Double,
    comment: String? = null,
    private val validate: ((Double) -> Boolean)? = null
) : Setting<Double>(
    namespace, key,
    defaultValue, comment
) {
    override val type get() = Property.Type.DOUBLE

    override fun read(serialized: String): Double? = serialized.toDoubleOrNull()
    override fun write(value: Double): String = value.toString()
    override fun validate(value: Double): Boolean = validate?.invoke(value) ?: true
}

class ChoiceSetting(
    namespace: ResourceLocation,
    key: ResourceLocation,
    defaultValue: String,
    comment: String? = null,
    values: Set<String>
) : StringSetting(
    namespace, key,
    defaultValue, comment,
    values::contains
)

class ResourceLocationSetting(
    namespace: ResourceLocation,
    key: ResourceLocation,
    defaultValue: ResourceLocation,
    comment: String? = null,
    private val validate: ((ResourceLocation) -> Boolean)? = null
) : Setting<ResourceLocation>(
    namespace, key,
    defaultValue, comment
) {
    override fun read(serialized: String) = if (serialized.contains(':')) ResourceLocation(serialized) else null
    override fun write(value: ResourceLocation) = value.toString()
    override fun validate(value: ResourceLocation) = validate?.invoke(value) ?: true
}
