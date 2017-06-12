package com.saomc.saoui

import com.saomc.saoui.api.events.EventInitStatsProvider
import com.saomc.saoui.config.ConfigHandler
import com.saomc.saoui.events.EventCore
import com.saomc.saoui.themes.ThemeLoader
import com.saomc.saoui.util.DefaultStatsProvider
import com.saomc.saoui.util.PlayerStats
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.xml.bind.JAXBException

@Mod(modid = SAOCore.MODID, name = SAOCore.NAME, version = SAOCore.VERSION, clientSideOnly = true, dependencies = SAOCore.DEPS, acceptableSaveVersions = "*", canBeDeactivated = true)
class SAOCore {

    @Mod.EventHandler
    @Throws(Exception::class)
    fun preInit(event: FMLPreInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(EventCore())
        ConfigHandler.preInit(event)

        (Minecraft.getMinecraft().resourceManager as IReloadableResourceManager).registerReloadListener { resourceManager ->
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
    }

    fun t() {

    }

    companion object {
        const val MODID = "saoui"
        const val NAME = "Sword Art Online UI"
        const val VERSION = "2.0-lite-dev"
        const val DEPS = "required-before:" + MODID + "ntw;required-after:saomclib@[1.1,)"
        val UNKNOWN_TIME_DELAY = -1f
        val LOGGER: Logger = LogManager.getLogger(MODID)
        // TODO: optimize things, ie remove public and static!

        @Mod.Instance(MODID)
        var instance: SAOCore? = null
    }

}
