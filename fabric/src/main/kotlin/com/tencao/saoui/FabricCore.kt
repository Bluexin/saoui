package com.tencao.saoui

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.api.ModInitializer
import net.minecraftforge.api.fml.event.config.ModConfigEvent

@Suppress("unused")
@Environment(EnvType.CLIENT)
object FabricCore : ModInitializer {

    override fun onInitialize() {
        SAOCore.setup()
        SoundRegistry.register()
    }
}
