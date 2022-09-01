/*
 * Copyright (C) 2020-2021 Tencao
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

package com.tencao.saoui

import com.tencao.saomclib.Client
import com.tencao.saomclib.SAOMCLib
import com.tencao.saomclib.capabilities.CapabilitiesHandler
import com.tencao.saoui.api.elements.animator.Animator
import com.tencao.saoui.api.events.EventInitStatsProvider
import com.tencao.saoui.capabilities.RenderCapability
import com.tencao.saoui.config.ConfigHandler
import com.tencao.saoui.effects.particles.ModParticles
import com.tencao.saoui.events.EventCore
import com.tencao.saoui.screens.CoreGUI
import com.tencao.saoui.themes.ThemeLoader
import com.tencao.saoui.themes.elements.Hud
import com.tencao.saoui.util.DefaultStatsProvider
import com.tencao.saoui.util.PlayerStats
import net.minecraft.entity.LivingEntity
import net.minecraft.resources.SimpleReloadableResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.LOADING_CONTEXT
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.zip.ZipFile
import javax.xml.bind.JAXBContext

@Mod(SAOCore.MODID)
object SAOCore {
    const val MODID = "saoui"
    const val NAME = "Sword Art Online UI"

    // val saoConfDir: File get() = confDir(File(Client.minecraft.gameDir, "config"))
    val isSAOMCLibServerSide: Boolean
        get() = SAOMCLib.proxy.isServerSideLoaded

    val LOGGER: Logger = LogManager.getLogger(MODID)

    init {
        DistExecutor.safeRunWhenOn(Dist.CLIENT) {
            DistExecutor.SafeRunnable {
                LOADING_CONTEXT.registerConfig(ModConfig.Type.CLIENT, ConfigHandler.config)
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
                PlayerStats.init(s.implementation)
            }
        }
    }

    // @JvmSynthetic
    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        LOGGER.log(Level.INFO, "Initializing client...")
        CapabilitiesHandler.registerEntityCapability(RenderCapability::class.java, RenderCapability.Storage()) { `object`: Any -> `object` is LivingEntity }

        (Client.resourceManager as SimpleReloadableResourceManager).addReloadListener(ThemeLoader)
        // TODO Check this
        ThemeLoader.themeList = getFiles("/assets/saoui/themes")
    }

    // @JvmSynthetic
    @SubscribeEvent
    fun loadComplete(event: FMLLoadCompleteEvent) {
        CoreGUI.animator
        ThemeLoader.load()
        // AdvancementUtil.generateList()
    }

    fun configLoad(event: ModConfig.ModConfigEvent) {
        val config = event.config
        if (config.modId.equals(MODID)) {}
        // ConfigHandler.config.afterReload()
    }

    private fun confDir(genDir: File): File {
        return File(genDir, MODID)
    }

    fun getFiles(location: String): List<String> {
        val list = mutableListOf<String>()
        val modFile = ModList.get().getModFileById(MODID).file
        var file = File(modFile.filePath.toFile(), location)

        // JAR
        if (!file.exists()) {
            val walk = Files.walk(modFile.locator.findPath(modFile, location), 1)
            walk.iterator().forEachRemaining {
                var name = it.toString()
                // Don't process the base directory
                if (location.length != name.length) {
                    if (!name.endsWith(".xsd")) {
                        LOGGER.info(name)
                        name = name.substring(location.length + 1, name.length - 1)
                        if (name.isNotEmpty()) {
                            list.add(name)
                        }
                    }
                }
            }
        }
        // IDE
        else {
            file.list { dir, name ->
                !name.endsWith(".xsd")
            }?.let {
                list.addAll(
                    it
                )
            }
        }

        // Added Resources
        Client.minecraft.fileResourcePacks.listFiles().forEach { extraFile ->
            // Make sure the resource pack is actually loaded first
            if (Client.minecraft.resourcePackList.enabledPacks.any { it.name.contains(extraFile.name.substringAfterLast("\\")) }) {
                // Process Zips
                if (extraFile.name.endsWith(".zip")) {
                    val zipFile = ZipFile(extraFile)
                    zipFile.entries().asSequence().forEach { entry ->
                        if (entry.name.endsWith("hud.xml")) {
                            var name = entry.name.substring(0, entry.name.length - 8)
                            name = name.substringAfterLast("/")
                            list.add(name)
                        }
                    }
                }
                // Process Folders
                else {
                    extraFile.listFiles { dir, name -> name.equals("hud.xml") }.forEach {
                        list.add(it.parent)
                    }
                }
            }
        }

        val themeList = mutableListOf<String>()
        val context = JAXBContext.newInstance(Hud::class.java)
        val um = context.createUnmarshaller()
        list.forEach { name ->
            val hudRL = ResourceLocation(MODID, "themes/$name/hud.xml")
            try {
                Client.resourceManager.getResource(hudRL).inputStream.use { ThemeLoader.HUD = um.unmarshal(it) as Hud }
                // If passes without exception, add
                themeList.add(name)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return themeList
    }

    /*
    fun getFiles(location: String): List<String> {
        val list = mutableListOf<String>()
        val modFile = ModList.get().getModFileById(MODID).file
        val file = File(modFile.filePath.toFile(), location)
        // JAR
        if (!file.exists()) {
            val walk = Files.walk(modFile.locator.findPath(modFile, location), 1)
            walk.iterator().forEachRemaining {
                var name = it.toString()
                // Don't process the base directory
                if (location.length != name.length) {
                    LOGGER.error(name)
                    name = name.substring(location.length + 1, name.length - 1)
                    if (name.isNotEmpty())
                        list.add(name)
                }
            }
        }
        // IDE
        else {
            list.addAll(file.list()!!)
        }
        return list
    }

    @SubscribeEvent
    fun addReloadEvent(event: TextureStitchEvent.Pre){
        ThemeLoader.themeList = getFiles("/assets/saoui/themes")
        ThemeLoader.load()
        //Files.walk(Client.minecraft.fileResourcePacks.path, 5)
    }*/
}
