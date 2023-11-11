package com.tencao.saoui

import com.tencao.saoui.api.elements.animator.Animator
import com.tencao.saoui.api.events.EventInitStatsProvider
import com.tencao.saoui.capabilities.RenderCapability
import com.tencao.saoui.effects.particles.ModParticles
import com.tencao.saoui.events.EventCore
import com.tencao.saoui.themes.ThemeManager
import com.tencao.saoui.util.DefaultStatsProvider
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.apache.logging.log4j.Level
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(Constants.MODID)
object ForgeCore {
    init {
        DistExecutor.safeRunWhenOn(Dist.CLIENT) {
            DistExecutor.SafeRunnable {
                //LOADING_CONTEXT.registerConfig(ModConfig.Type.CLIENT, ConfigHandler.config)
                FORGE_BUS.register(this)
                FORGE_BUS.register(EventCore)
                FORGE_BUS.register(SoundCore)
                FORGE_BUS.register(Animator)
                FORGE_BUS.register(ModParticles)
                // MOD_BUS.addListener(::addReloadEvent)
                MOD_BUS.register(ModParticles)
                MOD_BUS.addListener(::onClientSetup)
                MOD_BUS.addListener(::loadComplete)
                val s = EventInitStatsProvider(DefaultStatsProvider())
                MOD_BUS.post(s)
            }
        }

        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        Constants.LOGGER.info("Hello Kotlin Forge world!")
    }

    // @JvmSynthetic
    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        Constants.LOGGER.log(Level.INFO, "Initializing client...")
        CapabilitiesHandler.registerEntityCapability(RenderCapability::class.java, RenderCapability.Storage()) { `object`: Any -> `object` is LivingEntity }

        (Client.resourceManager as SimpleReloadableResourceManager).registerReloadListener(ThemeManager)
        // TODO Check this
        // ThemeManager.themeList = getFiles("/assets/saoui/themes")
    }
}