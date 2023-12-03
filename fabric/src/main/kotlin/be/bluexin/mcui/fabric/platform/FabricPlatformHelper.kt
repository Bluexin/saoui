package be.bluexin.mcui.fabric.platform

import be.bluexin.mcui.config.Configuration
import be.bluexin.mcui.config.Property
import be.bluexin.mcui.platform.services.PlatformHelper
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resources.ResourceLocation

class FabricPlatformHelper : PlatformHelper {
    override val platformName = "Fabric"

    override fun isModLoaded(modId: String) = FabricLoader.getInstance().isModLoaded(modId)

    override val isDevelopmentEnvironment: Boolean
        by lazy { FabricLoader.getInstance().isDevelopmentEnvironment }

    // FIXME tmp
    override fun config(namespace: ResourceLocation): Configuration = Config()
}

private class Config : Configuration {
    private val settings = mutableMapOf<ResourceLocation, Property>()

    override fun get(key: ResourceLocation, default: String, comment: String?, type: Property.Type): Property =
        settings.getOrPut(key) {
            Prop(default)
        }

    override fun save() = Unit

    private data class Prop(
        private var value: String
    ) : Property {
        override val string: String
            get() = value

        override fun set(value: String) {
            this.value = value
        }
    }
}
