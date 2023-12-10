package be.bluexin.mcui.config

import be.bluexin.mcui.Constants
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.common.ForgeConfigSpec

interface Configuration {
    fun register(key: ResourceLocation, default: String, comment: String?, type: Property.Type): Property
    fun get(key: ResourceLocation): Property?
    fun save()
    fun build()
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

abstract class ConfigSpecBasedConfig(
    private val namespace: ResourceLocation
) : Configuration {
    private val specBuilder = ForgeConfigSpec.Builder()
    protected val spec: ForgeConfigSpec by lazy(specBuilder::build) // Might be fine, we'll see :)
    private val props = mutableMapOf<ResourceLocation, Property>()

    override fun register(
        key: ResourceLocation, default: String, comment: String?, type: Property.Type
    ) = props.getOrPut(key) {
        specBuilder.push(key.namespace)
        comment?.let(specBuilder::comment)

        val prop = specBuilder
            .translation("$namespace.$key".replace(':', '.'))
            .define(key.path, default)
        specBuilder.pop()
        ConfigSpecProperty(prop)
    }

    override fun get(key: ResourceLocation) = props[key]

    override fun save() = spec.save()

    protected val fileName: String by lazy {
        buildString {
            append(Constants.MOD_ID)
            append('/')
            if (namespace != Settings.NS_BUILTIN) {
                append(namespace.namespace)
                append('/')
                append(namespace.path)
            } else append("main")
            append(".toml")
        }
    }

    private class ConfigSpecProperty(
        private val delegate: ForgeConfigSpec.ConfigValue<String>
    ) : Property {
        override val string: String
            get() = delegate.get()

        override fun set(value: String) = delegate.set(value)
    }
}
