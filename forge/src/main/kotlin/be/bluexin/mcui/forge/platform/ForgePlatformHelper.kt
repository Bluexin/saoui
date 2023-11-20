package be.bluexin.mcui.forge.platform

import be.bluexin.mcui.platform.services.PlatformHelper
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.loading.FMLLoader

class ForgePlatformHelper : PlatformHelper {

    override val platformName = "Forge"

    override fun isModLoaded(modId: String) = ModList.get().isLoaded(modId)

    override val isDevelopmentEnvironment: Boolean
        by lazy { !FMLLoader.isProduction() }

    override fun config(namespace: ResourceLocation) = ForgeConfiguration(namespace)
}