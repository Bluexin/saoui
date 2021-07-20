/*
 * Copyright (C) 2016-2019 Arnaud 'Bluexin' Solé
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.saomc.saoui

import com.saomc.saoui.api.events.EventInitStatsProvider
import com.saomc.saoui.capabilities.RenderCapability
import com.saomc.saoui.config.ConfigHandler
import com.saomc.saoui.config.FriendData
import com.saomc.saoui.events.EventCore
import com.saomc.saoui.screens.CoreGUI
import com.saomc.saoui.themes.ThemeLoader
import com.saomc.saoui.util.DefaultStatsProvider
import com.saomc.saoui.util.PlayerStats
import com.tencao.saomclib.SAOMCLib
import com.tencao.saomclib.capabilities.CapabilitiesHandler
import net.minecraft.client.Minecraft
import net.minecraft.entity.LivingEntity
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.EventBus
import net.minecraftforge.fml.ModContainer
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import net.minecraftforge.fml.loading.FMLEnvironment
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import java.io.File
import java.net.URI
import java.nio.file.FileSystems
import java.util.concurrent.ConcurrentHashMap

@Mod(SAOCore.MODID)
object SAOCore {
    const val MODID = "saoui"
    const val NAME = "Sword Art Online UI"
    const val VERSION = "2.1.4"
    const val DEPS = "required-after:saomclib@[1.4.8,);after:mantle;required-after:librarianlib@[4.22,)"

    val mc: Minecraft
        get() = Minecraft.getInstance()

    val MOD_CONTAINER: ModContainer = ModLoadingContext.get().activeContainer

    val saoConfDir: File = confDir(File(mc.gameDir, "config"))
    val isSAOMCLibServerSide: Boolean
        get() = SAOMCLib.proxy.isServerSideLoaded

    val LOGGER: Logger = LogManager.getLogger(MODID)

    init {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            LOGGER.info("Disabled because not running on the client.")
        }
        else {
            MOD_BUS.addListener(::init)
            MOD_BUS.addListener(::loadComplete)
            //eventBus.register(this)
            MinecraftForge.EVENT_BUS.register(this)
        }
    }

    @Throws(Exception::class)
    fun init(event: FMLClientSetupEvent) {
        MOD_BUS.register(EventCore)
        MOD_BUS.register(SoundCore)
        val s = EventInitStatsProvider(DefaultStatsProvider())
        MOD_BUS.post(s)
        PlayerStats.init(s.implementation)
        ConfigHandler.preInit()
        FriendData.preInit()


        CapabilitiesHandler.registerEntityCapability(RenderCapability::class.java, RenderCapability.Storage()) { `object`: Any -> `object` is LivingEntity }

        //TODO Check this
        ThemeLoader.themeFolder =  getFile()

        CoreGUI.animator // Let's force things to init early

        if (ModList.get().isLoaded("mantle")) {
            LOGGER.info("Unregistering Mantle health renderer.")
            val f = EventBus::class.java.getDeclaredField("listeners")
            f.isAccessible = true
            val listeners = f.get(MinecraftForge.EVENT_BUS) as ConcurrentHashMap<*, *>
            val handler = listeners.keys.firstOrNull { it.javaClass.canonicalName == "slimeknights.mantle.client.ExtraHeartRenderHandler" }
            if (handler == null) LOGGER.warn("Unable to unregister Mantle health renderer!")
            else MinecraftForge.EVENT_BUS.unregister(handler)
        }
    }

    fun configLoad(event: ModConfig.ModConfigEvent){
    }

    fun loadComplete(event: FMLLoadCompleteEvent){
        //AdvancementUtil.generateList()
        ThemeLoader.load()
    }

    private fun confDir(genDir: File): File {
        return File(genDir, MODID)
    }

    fun getFile(): File {
        val uri: URI = SAOCore::class.java.getResource("/assets/saoui/themes/")!!.toURI()
        val fileSystem = FileSystems.newFileSystem(uri, emptyMap<String, Any?>())
        val path = fileSystem.getPath("/assets/saoui/themes/")
        return path.toFile()
    }

}
