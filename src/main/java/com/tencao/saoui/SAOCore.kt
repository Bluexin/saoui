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

package com.tencao.saoui

import com.tencao.saomclib.Client
import com.tencao.saomclib.SAOMCLib
import com.tencao.saomclib.capabilities.CapabilitiesHandler
import com.tencao.saoui.api.events.EventInitStatsProvider
import com.tencao.saoui.capabilities.RenderCapability
import com.tencao.saoui.config.ConfigHandler
import com.tencao.saoui.config.FriendData
import com.tencao.saoui.events.EventCore
import com.tencao.saoui.screens.CoreGUI
import com.tencao.saoui.themes.ThemeLoader
import com.tencao.saoui.themes.elements.Hud
import com.tencao.saoui.util.AdvancementUtil
import com.tencao.saoui.util.DefaultStatsProvider
import com.tencao.saoui.util.PlayerStats
import com.tencao.saoui.util.append
import net.minecraft.client.resources.SimpleReloadableResourceManager
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.ResourceLocation
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
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap
import java.util.function.BiPredicate
import java.util.stream.Stream
import java.util.zip.ZipFile
import javax.xml.bind.JAXBContext
import kotlin.streams.toList

@Mod(
    modid = SAOCore.MODID,
    name = SAOCore.NAME,
    version = SAOCore.VERSION,
    clientSideOnly = true,
    dependencies = SAOCore.DEPS,
    acceptableSaveVersions = "*",
    canBeDeactivated = true
)
object SAOCore {
    const val MODID = "saoui"
    const val NAME = "Sword Art Online UI"
    const val VERSION = "2.1.7"
    const val DEPS = "required-after:saomclib@[1.4.8,);after:mantle"

    val mc = Client.minecraft

    val saoConfDir: File = confDir(File(Client.minecraft.mcDataDir, "config"))
    val isSAOMCLibServerSide: Boolean
        get() = SAOMCLib.proxy.isServerSideLoaded

    lateinit var modFile: File

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
        modFile = event.sourceFile

        CapabilitiesHandler.registerEntityCapability(
            RenderCapability::class.java,
            RenderCapability.Storage()
        ) { `object`: Any -> `object` is EntityLivingBase }

        (Client.resourceManager as SimpleReloadableResourceManager).registerReloadListener(ThemeLoader)
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
            val handler =
                listeners.keys.firstOrNull { it.javaClass.canonicalName == "slimeknights.mantle.client.ExtraHeartRenderHandler" }
            if (handler == null) LOGGER.warn("Unable to unregister Mantle health renderer!")
            else MinecraftForge.EVENT_BUS.unregister(handler)
        }
    }

    @Mod.EventHandler
    fun loadComplete(event: FMLLoadCompleteEvent) {
        AdvancementUtil.generateList()
        ThemeLoader.load()
        // ElementRegistry.initRegistry()
    }

    private fun confDir(genDir: File): File {
        return File(genDir, MODID)
    }

    /**
     * Returns a map of path to display name for themes
     */
    fun getFiles(): Map<ResourceLocation, ThemeMetadata> {
        val candidates = mutableMapOf<ResourceLocation, ThemeMetadata>()

        // Built-in
        candidates += modFile.findThemePaths().extractThemesMetadata(MODID)

        // Loaded resource packs
        Client.minecraft.resourcePackRepository.repositoryEntries.map {
            it.resourcePackName to Client.minecraft.resourcePackRepository.dirResourcepacks.resolve(it.resourcePackName)
        }.forEach { (name, location) ->
            candidates += location.findThemePaths().extractThemesMetadata(name)
        }

        val context = JAXBContext.newInstance(Hud::class.java)
        val um = context.createUnmarshaller()
        return candidates.filterValues {
            try {
                Client.resourceManager.getResource(it.themeRoot.append("/hud.xml")).inputStream.use { um.unmarshal(it) as Hud }
                true
            } catch (e: IOException) {
                LOGGER.warn("Could not load HUD for $it !", e)
                false
            }
        }
    }

    /**
     * Find `hud.xml` files defined in given [File], which can be a zip/jar or folder
     */
    private fun File.findThemePaths(): List<Path> {
        LOGGER.info("Analyzing $this")
        return when {
            // jar
            this.isFile -> ZipFile(this).use { zip ->
                zip.entries().asSequence()
                    .filter { it.name.endsWith("hud.xml") && !it.isDirectory }
                    .map { Paths.get(it.name) }
                    .toList()
            }
            // folder
            this.isDirectory -> {
                val thisAsPath = this.toPath()
                Files.find(thisAsPath, 5, BiPredicate { path, fileAttributes ->
                    fileAttributes.isRegularFile && path.endsWith("hud.xml")
                }).map { thisAsPath.relativize(it) }.use(Stream<Path>::toList)
            }
            // unknown
            else -> {
                LOGGER.warn("Unknown file type : $this")
                emptyList()
            }
        }
    }

    /**
     * Transform a list of paths into their respective metadata, indexed by id
     * @param fallbackName Name to fall back to for legacy themes
     */
    private fun List<Path>.extractThemesMetadata(fallbackName: String): Map<ResourceLocation, ThemeMetadata> =
        this.associate { path ->
            val domain = path.subpath(1, 2).toString()
            val name = path.parent.fileName.toString().let {
                if (it != "themes") it else {
                    LOGGER.warn("Theme pack $fallbackName is using the old theme structure !")
                    fallbackName
                }
            }
            val id = ResourceLocation(domain, name)
            LOGGER.info("Read $id from $fallbackName at $path")
            id to ThemeMetadata(
                id = id,
                themeRoot = ResourceLocation(domain, path.parent.toString().removePrefix("assets/$domain/")),
                name = name.removeSuffix(".zip")
            )
        }
}

data class ThemeMetadata(val id: ResourceLocation, val themeRoot: ResourceLocation, val name: String)

/*
.map { path ->
    val domain = path.subpath(1, 2).toString()
    val name = path.parent.fileName.toString().takeUnless { it == "themes" } ?: domain
    ResourceLocation(MODID, "themes/$name/hud.xml") to name
    Triple(
        ResourceLocation(domain, name),
        ResourceLocation(domain, path.parent.toString()),
        name
    )
}.asSequence().associateBy(Triple<*, *, *>::first)
 */
