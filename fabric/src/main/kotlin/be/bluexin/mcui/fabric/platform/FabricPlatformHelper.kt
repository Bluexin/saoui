package be.bluexin.mcui.fabric.platform

import be.bluexin.mcui.Constants
import be.bluexin.mcui.config.ConfigSpecBasedConfig
import be.bluexin.mcui.config.Configuration
import be.bluexin.mcui.platform.services.PlatformHelper
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.fml.config.ModConfig

class FabricPlatformHelper : PlatformHelper {
    override val platformName = "Fabric"

    override fun isModLoaded(modId: String) = FabricLoader.getInstance().isModLoaded(modId)

    override val isDevelopmentEnvironment: Boolean
        by lazy { FabricLoader.getInstance().isDevelopmentEnvironment }

    // FIXME tmp
    override fun config(namespace: ResourceLocation): Configuration = FabricConfig(namespace)
}

private class FabricConfig(
    namespace: ResourceLocation
) : ConfigSpecBasedConfig(namespace) {
    override fun build() {
        ForgeConfigRegistry.INSTANCE.register(
            Constants.MOD_ID, ModConfig.Type.CLIENT, spec, fileName
        )
    }
}
