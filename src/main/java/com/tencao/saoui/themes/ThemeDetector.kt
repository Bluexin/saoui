package com.tencao.saoui.themes

import com.tencao.saomclib.Client
import com.tencao.saoui.SAOCore
import com.tencao.saoui.themes.elements.Hud
import com.tencao.saoui.util.append
import net.minecraft.util.ResourceLocation
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.BiPredicate
import java.util.stream.Stream
import java.util.zip.ZipFile
import javax.xml.bind.JAXBContext
import kotlin.streams.toList

object ThemeDetector {
    /**
     * Returns a map of path to display name for themes
     */
    fun listThemes(): Map<ResourceLocation, ThemeMetadata> {
        val candidates = mutableMapOf<ResourceLocation, ThemeMetadata>()

        // Built-in
        candidates += SAOCore.modFile.findThemePaths().extractThemesMetadata(SAOCore.MODID)

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
                SAOCore.LOGGER.warn("Could not load HUD for $it !", e)
                false
            }
        }
    }

    /**
     * Find `hud.xml` files defined in given [File], which can be a zip/jar or folder.
     *
     * This returns a List and not a lazy Sequence to be able to close opened resources (see usages of [use]) within
     * the scope of this method.
     */
    private fun File.findThemePaths(): List<Path> {
        SAOCore.LOGGER.debug("Analyzing {}", this)
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
                SAOCore.LOGGER.warn("Unknown file type : {}", this)
                emptyList()
            }
        }
    }

    /**
     * Transform a list of paths into their respective metadata, indexed by id
     * @param themeDomain Domain name for the theme
     */
    private fun List<Path>.extractThemesMetadata(themeDomain: String): Map<ResourceLocation, ThemeMetadata> =
        this.associate { path ->
            val domain = path.subpath(1, 2).toString()
            val name = path.parent.fileName.toString().let {
                if (it != "themes") it else {
                    SAOCore.LOGGER.warn("Theme pack {} is using the old theme structure !", themeDomain)
                    themeDomain.removeSuffix(".zip")
                }
            }
            val id = ResourceLocation(themeDomain, name)
            SAOCore.LOGGER.debug("Found candidate theme {} from {} at {}", id, themeDomain, path)
            id to ThemeMetadata(
                id = id,
                themeRoot = ResourceLocation(domain, path.parent.toString().removePrefix("assets/$domain/")),
                name = name
            )
        }
}