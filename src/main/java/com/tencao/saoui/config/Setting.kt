package com.tencao.saoui.config

import net.minecraft.util.ResourceLocation
import kotlin.reflect.KProperty

abstract class Setting<T : Any>(
    val namespace: ResourceLocation,
    val key: ResourceLocation,
    val defaultValue: T,
    val comment: String?,
    val read: (String) -> T?,
    val write: (T) -> String,
    val validate: (T) -> Boolean
) {
    operator fun component1() = namespace
    operator fun component2() = key
    operator fun component3() = defaultValue
    operator fun component4() = comment
    operator fun component5() = read
    operator fun component6() = write
    operator fun component7() = validate

    operator fun getValue(caller: Any, property: KProperty<*>): T = Settings[this]

    operator fun setValue(caller: Any, property: KProperty<*>, value: T) = Settings.set(this, value)
}

open class StringSetting(
    namespace: ResourceLocation,
    key: ResourceLocation,
    defaultValue: String,
    comment: String? = null,
    validate: (String) -> Boolean = { true }
) : Setting<String>(
    namespace, key,
    defaultValue, comment,
    { it }, { it }, validate
)

class BooleanSetting(
    namespace: ResourceLocation,
    key: ResourceLocation,
    defaultValue: Boolean,
    comment: String? = null
) : Setting<Boolean>(
    namespace, key,
    defaultValue, comment,
    String::toBooleanStrictOrNull,
    Boolean::toString, { true }
)

class IntSetting(
    namespace: ResourceLocation,
    key: ResourceLocation,
    defaultValue: Int,
    comment: String? = null,
    validate: (Int) -> Boolean = { true }
) : Setting<Int>(
    namespace, key,
    defaultValue, comment,
    String::toIntOrNull,
    Int::toString, validate
)

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
) : Setting<ResourceLocation>(
    namespace, key,
    defaultValue, comment,
    { if (it.contains(':')) ResourceLocation(it) else null },
    ResourceLocation::toString, { true }
)
