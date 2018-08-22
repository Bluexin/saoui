package com.saomc.saoui

import be.bluexin.saomclib.capabilities.CapabilitiesHandler
import com.saomc.saoui.api.events.EventInitStatsProvider
import com.saomc.saoui.config.ConfigHandler
import com.saomc.saoui.events.EventCore
import com.saomc.saoui.neo.screens.NeoGui
import com.saomc.saoui.themes.ThemeLoader
import com.saomc.saoui.util.DefaultStatsProvider
import com.saomc.saoui.util.PlayerStats
import cpw.mods.fml.common.Loader
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.eventhandler.EventBus
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraftforge.common.MinecraftForge
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.concurrent.ConcurrentHashMap
import javax.xml.bind.JAXBException

@Mod(modid = SAOCore.MODID, name = SAOCore.NAME, version = SAOCore.VERSION, /*clientSideOnly = true, */dependencies = SAOCore.DEPS, acceptableSaveVersions = "*", canBeDeactivated = true)
object SAOCore {
    const val MODID = "saoui"
    const val NAME = "Sword Art Online UI"
    const val VERSION = "2.0.0.10"
    const val DEPS = "required-before:" + MODID + "ntw;required-after:saomclib@[1.2.1,);after:mantle"

    // TODO: investigate toasts -> net.minecraft.client.gui.toasts

    @JvmStatic
    @Mod.InstanceFactory
    fun shenanigan() = this

    const val UNKNOWN_TIME_DELAY = -1f
    val LOGGER: Logger = LogManager.getLogger(MODID)

    @Mod.EventHandler
    @Throws(Exception::class)
    fun preInit(event: FMLPreInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(EventCore)
        ConfigHandler.preInit(event)

        (Minecraft.getMinecraft().resourceManager as IReloadableResourceManager).registerReloadListener { _ ->
            try {
                ThemeLoader.load()
            } catch (e: JAXBException) {
                e.printStackTrace()
            }
        }
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        val s = EventInitStatsProvider(DefaultStatsProvider())
        MinecraftForge.EVENT_BUS.post(s)
        PlayerStats.init(s.implementation)
        NeoGui.animator // Let's force things to init early
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        // Attempt to fix 1st menu opening freeze, doesn't seem enough :<
        /*GLCore.glBindTexture(StringNames.gui)
        GLCore.glBindTexture(StringNames.slot)
        GLCore.glBindTexture(StringNames.entities)
        GLCore.glBindTexture(StringNames.entitiesCustom)
        GLCore.glBindTexture(StringNames.particleLarge)
        IconCore.values().forEach { if (it.rl != null) GLCore.glBindTexture(it.rl!!) }
        NeoIngameMenu()//.initGui()*/

        if (Loader.isModLoaded("mantle")) {
            LOGGER.info("Unregistering Mantle health renderer.")
            val f = EventBus::class.java.getDeclaredField("listeners")
            f.isAccessible = true
            val listeners = f.get(MinecraftForge.EVENT_BUS) as ConcurrentHashMap<*, *>
            val handler = listeners.keys.firstOrNull { it.javaClass.canonicalName == "slimeknights.mantle.client.ExtraHeartRenderHandler" }
            if (handler == null) LOGGER.warn("Unable to unregister Mantle health renderer!")
            else MinecraftForge.EVENT_BUS.unregister(handler)
        }
    }

}
