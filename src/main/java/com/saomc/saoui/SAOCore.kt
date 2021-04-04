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

import be.bluexin.saomclib.SAOMCLib
import be.bluexin.saomclib.capabilities.CapabilitiesHandler
import com.saomc.saoui.api.events.EventInitStatsProvider
import com.saomc.saoui.capabilities.RenderCapability
import com.saomc.saoui.config.ConfigHandler
import com.saomc.saoui.config.FriendData
import com.saomc.saoui.events.EventCore
import com.saomc.saoui.screens.CoreGUI
import com.saomc.saoui.themes.ThemeLoader
import com.saomc.saoui.util.AdvancementUtil
import com.saomc.saoui.util.DefaultStatsProvider
import com.saomc.saoui.util.PlayerStats
import com.teamwizardry.librarianlib.features.kotlin.Client
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.EventBus
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.util.concurrent.ConcurrentHashMap


@Mod(modid = SAOCore.MODID, name = SAOCore.NAME, version = SAOCore.VERSION, clientSideOnly = true, dependencies = SAOCore.DEPS, acceptableSaveVersions = "*", canBeDeactivated = true)
object SAOCore {
    const val MODID = "saoui"
    const val NAME = "Sword Art Online UI"
    const val VERSION = "2.1.4"
    const val DEPS = "required-after:saomclib@[1.4.8,);after:mantle;required-after:librarianlib@[4.22,)"

    val mc = Client.minecraft

    val saoConfDir: File = confDir(File(Client.minecraft.gameDir, "config"))
    val isSAOMCLibServerSide: Boolean
        get() = SAOMCLib.proxy.isServerSideLoaded

    @JvmStatic
    @Mod.InstanceFactory
    fun shenanigan() = this

    val LOGGER: Logger = LogManager.getLogger(MODID)

    @Mod.EventHandler
    @Throws(Exception::class)
    fun preInit(event: FMLPreInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(EventCore)
        MinecraftForge.EVENT_BUS.register(SoundCore)
        ConfigHandler.preInit()
        FriendData.preInit()

        CapabilitiesHandler.registerEntityCapability(RenderCapability::class.java, RenderCapability.Storage()) { `object`: Any -> `object` is EntityLivingBase }

        ThemeLoader.themeFolder =  File(event.sourceFile, "assets/saoui/themes")
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        val s = EventInitStatsProvider(DefaultStatsProvider())
        MinecraftForge.EVENT_BUS.post(s)
        PlayerStats.init(s.implementation)
        CoreGUI.animator // Let's force things to init early
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
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

    @Mod.EventHandler
    fun loadComplete(event: FMLLoadCompleteEvent){
        AdvancementUtil.generateList()
        ThemeLoader.load()
        //ElementRegistry.initRegistry()
    }

    private fun confDir(genDir: File): File {
        return File(genDir, MODID)
    }


}
