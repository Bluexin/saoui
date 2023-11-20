package be.bluexin.mcui.forge.platform

import be.bluexin.mcui.config.Configuration
import be.bluexin.mcui.config.Property
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.common.ForgeConfigSpec

class ForgeConfiguration(
    private val namespace: ResourceLocation
) : Configuration {
    private val specBuilder = ForgeConfigSpec.Builder()
    private val spec by lazy(specBuilder::build) // Might be fine, we'll see :)
    private val props = mutableMapOf<ResourceLocation, Property>()

    override fun get(
        key: ResourceLocation,
        default: String,
        comment: String?,
        type: Property.Type
    ) = props.getOrPut(key) {
        specBuilder.push(key.path)
        comment?.let(specBuilder::comment)

        val prop = specBuilder
            .translation("$namespace.$key".replace(':', '.'))
            .define(key.path, default)
        specBuilder.pop()
        ForgeProperty(prop)
    }

    override fun save() {
        spec.save()
    }
}

class ForgeProperty(
    private val delegate: ForgeConfigSpec.ConfigValue<String>
) : Property {
    override val string: String
        get() = delegate.get()

    override fun set(value: String) = delegate.set(value)
}
