package com.tencao.saoui.config

import net.minecraft.util.ResourceLocation

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
}

class StringSetting(
    namespace: ResourceLocation,
    key: ResourceLocation,
    defaultValue: String,
    comment: String?,
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
    comment: String?
) : Setting<Boolean>(
    namespace, key,
    defaultValue, comment,
    { it.toBooleanStrictOrNull() },
    { it.toString() }, { true }
)

class IntSetting(
    namespace: ResourceLocation,
    key: ResourceLocation,
    defaultValue: Int,
    comment: String?,
    validate: (Int) -> Boolean = { true }
) : Setting<Int>(
    namespace, key,
    defaultValue, comment,
    { it.toIntOrNull() },
    { it.toString() }, validate
)

class ChoiceSetting(
    namespace: ResourceLocation,
    key: ResourceLocation,
    defaultValue: String,
    comment: String?,
    values: Set<String>
) : Setting<String>(
    namespace, key,
    defaultValue, comment,
    { it }, { it }, { it in values }
)
