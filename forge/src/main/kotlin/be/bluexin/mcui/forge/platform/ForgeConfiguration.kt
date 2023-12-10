package be.bluexin.mcui.forge.platform

import be.bluexin.mcui.config.ConfigSpecBasedConfig
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.config.ModConfig

class ForgeConfiguration(
    namespace: ResourceLocation
) : ConfigSpecBasedConfig(namespace) {

    override fun build() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, spec, fileName)
    }
}
