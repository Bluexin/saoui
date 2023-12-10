package be.bluexin.mcui.config

import be.bluexin.mcui.Constants
import be.bluexin.mcui.mixin.ModConfigMixin
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.config.ModConfig
import java.nio.file.Path

interface Configuration {
    fun register(key: ResourceLocation, default: String, comment: String?, type: Property.Type): Property
    fun get(key: ResourceLocation): Property?
    fun save()
    fun build(old: Configuration?)
    fun clear()
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
    protected lateinit var modConfig: ModConfig

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

    override fun build(old: Configuration?) {
        when (val oldConfig = (old as ConfigSpecBasedConfig?)?.modConfig) {
            null -> doRegister()
            is ModConfigMixin -> {
                oldConfig.`mcui$setSpec`(spec)
                val configData = oldConfig.handler.reader(basePath).apply(oldConfig)
                oldConfig.`mcui$setConfigData`(configData)
                oldConfig.save()
                modConfig = oldConfig
            }

            else -> Constants.LOG.error("Unknown $oldConfig - expected to implement ModConfigMixin")
        }
    }

    override fun clear() {
        modConfig.handler.unload(basePath, modConfig)
        modConfig.save()
    }

    protected abstract fun doRegister()
    protected abstract val basePath: Path

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
