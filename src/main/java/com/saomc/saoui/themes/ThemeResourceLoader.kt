package com.saomc.saoui.themes

import com.saomc.saoui.SAOCore
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.client.resources.IResourcePack
import net.minecraft.client.resources.data.IMetadataSection
import net.minecraft.client.resources.data.MetadataSerializer
import net.minecraft.util.ResourceLocation
import org.apache.commons.io.filefilter.DirectoryFileFilter
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileFilter
import java.io.FileInputStream
import java.io.InputStream

object ThemeResourceLoader: IResourcePack {

    var debug = false
    val themeFolder = File(Minecraft().gameDir, "themes")

    override fun getInputStream(rl: ResourceLocation): InputStream? =
        if (!resourceExists(rl))
             null
        else {
            val file = File(File(themeFolder, rl.namespace), rl.path)

            val fileName = file.canonicalFile.name
            if (fileName == file.name){
                SAOCore.LOGGER.warn("Theme resource location $rl only matches the file $fileName due to the operating system not being case sensitive, please address this.")
            }
            FileInputStream(file)
        }


    override fun resourceExists(rl: ResourceLocation): Boolean {
        val requestedFile = File(File(themeFolder, rl.namespace), rl.path)

        if (debug && !requestedFile.isFile){
            SAOCore.LOGGER.debug("Cannot locate resource file at ${requestedFile.absolutePath}")
        }

        return requestedFile.isFile
    }

    override fun getResourceDomains(): MutableSet<String> {
        if (!themeFolder.exists()){
            themeFolder.mkdir()
        }
        val folders = hashSetOf<String>()

        themeFolder.listFiles(DirectoryFileFilter.DIRECTORY as FileFilter).forEach {resourceFolder ->
            if (resourceFolder.name == "debug"){
                debug = true
            }
            folders.add(resourceFolder.name)
        }
        return folders
    }

    override fun <T : IMetadataSection?> getPackMetadata(p0: MetadataSerializer, p1: String): T?  = null

    override fun getPackImage(): BufferedImage? = null

    override fun getPackName(): String = "Themes"
}