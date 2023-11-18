package com.tencao.saoui.config

import net.minecraft.resources.ResourceLocation
import net.minecraftforge.common.ForgeConfigSpec


interface Configuration {
    fun get(key: ResourceLocation, default: String, comment: String?, type: Property.Type): Property
    fun make(builder: ForgeConfigSpec.Builder, key: ResourceLocation, default: String, comment: String?, type: Property.Type): Property
    fun save()

    val options: MutableMap<ResourceLocation, ConfigProperty>
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

    fun save()
}
