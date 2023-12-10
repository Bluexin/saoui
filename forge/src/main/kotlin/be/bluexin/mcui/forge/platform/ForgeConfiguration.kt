package be.bluexin.mcui.forge.platform

import be.bluexin.mcui.config.ConfigSpecBasedConfig
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.config.ModConfigEvent
import net.minecraftforge.fml.loading.FMLPaths
import thedarkcolour.kotlinforforge.KotlinModLoadingContext
import java.nio.file.Path

class ForgeConfiguration(
    namespace: ResourceLocation
) : ConfigSpecBasedConfig(namespace) {

    override fun doRegister() {
        val fileName = fileName
        // TODO : not sure this works, not sure if we'll be able to hot reload Settings definitions on Forge
        // as config loading is split into phases there
        KotlinModLoadingContext.get().getKEventBus().addListener<ModConfigEvent.Loading> {
            if (it.config.fileName == fileName) modConfig = it.config
        }
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, spec, fileName)
    }

    override val basePath: Path
        get() = FMLPaths.CONFIGDIR.get()
}
