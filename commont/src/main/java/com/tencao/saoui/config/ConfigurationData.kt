package com.tencao.saoui.config

import net.minecraft.resources.ResourceLocation
import net.minecraftforge.common.ForgeConfigSpec

class ConfigurationData(
    private val namespace: ResourceLocation,
) : Configuration {
    override var options: MutableMap<ResourceLocation, ConfigProperty> = mutableMapOf()

    override fun get(key: ResourceLocation, default: String, comment: String?, type: Property.Type): Property? = options[key]


    override fun make(
        builder: ForgeConfigSpec.Builder,
        key: ResourceLocation,
        default: String,
        comment: String?,
        type: Property.Type
    ) = options.getOrPut(key){
        builder.push(key.namespace)
        val config = ConfigProperty(builder.comment(comment).translation("$namespace.$key".replace(':', '.')).define(key.path, default))
        builder.pop()
        config
    }

    override fun save() {
        options.values.forEach { it.save() }
    }
}

class ConfigProperty(
    private val delegate: ForgeConfigSpec.ConfigValue<String>
) : Property {
    override val string: String
        get() = delegate.get()

    override fun set(value: String) {
        delegate.set(value)
        delegate.save()
    }

    override fun save() {
        delegate.save()
    }
}
