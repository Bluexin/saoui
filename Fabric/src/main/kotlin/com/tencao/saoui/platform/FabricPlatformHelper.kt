package com.tencao.saoui.platform

import be.bluexin.mcui.config.Configuration
import be.bluexin.mcui.platform.services.PlatformHelper
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resources.ResourceLocation

class FabricPlatformHelper : PlatformHelper {
    override val platformName = "Fabric"

    override fun isModLoaded(modId: String) = FabricLoader.getInstance().isModLoaded(modId)

    override val isDevelopmentEnvironment: Boolean
        by lazy { FabricLoader.getInstance().isDevelopmentEnvironment }

    override fun config(namespace: ResourceLocation): Configuration {
        TODO("Not yet implemented")
    }
}
