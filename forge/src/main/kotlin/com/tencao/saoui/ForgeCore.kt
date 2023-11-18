package com.tencao.saoui

import com.tencao.saoui.api.elements.animator.Animator
import com.tencao.saoui.api.events.EventInitStatsProvider
import com.tencao.saoui.effects.particles.ModParticles
import com.tencao.saoui.events.EventCore
import com.tencao.saoui.util.DefaultStatsProvider
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(Constants.MODID)
object ForgeCore {
    init {
        DistExecutor.safeRunWhenOn(Dist.CLIENT) {
            DistExecutor.SafeRunnable {
                //LOADING_CONTEXT.registerConfig(ModConfig.Type.CLIENT, ConfigHandler.config)
                FORGE_BUS.register(this)
                FORGE_BUS.register(SoundRegistry)
                MOD_BUS.addListener(::onClientSetup)
            }
        }
    }

    // @JvmSynthetic
    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        SAOCore.setup()
    }

}