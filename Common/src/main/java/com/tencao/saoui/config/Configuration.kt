package com.tencao.saoui.config

import net.minecraft.util.ResourceLocation

interface Configuration {
    fun get(key: ResourceLocation, default: String, comment: String?, type: Property.Type): Property
    fun save()
}

interface Property {
    enum class Type {
        STRING,
        BOOLEAN,
        INTEGER,
        DOUBLE,
    }

    val string: String
    fun set(value: String)
}
