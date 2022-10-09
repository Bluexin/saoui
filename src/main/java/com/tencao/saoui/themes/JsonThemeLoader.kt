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

package com.tencao.saoui.themes

import com.google.gson.GsonBuilder
import com.tencao.saoui.SAOCore
import com.tencao.saoui.themes.elements.Hud
import net.minecraft.client.resources.IResourceManager
import net.minecraft.launchwrapper.Launch
import net.minecraftforge.client.resource.IResourceType
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener
import net.minecraftforge.fml.client.FMLFolderResourcePack
import org.apache.logging.log4j.LogManager
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.function.Predicate
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBException

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
object JsonThemeLoader : ISelectiveResourceReloadListener {

    private val logger by lazy { LogManager.getLogger(javaClass) }

    @JvmStatic
    fun main(args: Array<String>) {
        Launch.blackboard = mapOf("fml.deobfuscatedEnvironment" to true)
        load()
        exportTheme()
    }

    // TODO: tests
    // TODO: theme versions
    // TODO: loading reporter (amount of issues, details, missing keys, ..?)

    lateinit var HUD: Hud
    lateinit var themeList: List<String>
    lateinit var currentTheme: String

    @Throws(JAXBException::class)
    fun load() {
        val location = "themes/sao"

        val start = System.currentTimeMillis()
        val context = JAXBContext.newInstance(Hud::class.java)
        val um = context.createUnmarshaller()
        try {
            javaClass.classLoader.getResourceAsStream("assets/saoui/themes/sao/hud.xml")
                .use { HUD = um.unmarshal(it) as Hud }
//            Client.resourceManager.getResource(hudRL).inputStream.use { HUD = um.unmarshal(it) as Hud }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        HUD.setup()

//        StringNames.init()

        logger.info("Loaded theme and set it up in " + (System.currentTimeMillis() - start) + "ms.")
    }

    fun exportTheme() {
        val gson = GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create()

        FileWriter("theme.json").use {
            it.write(gson.toJson(HUD))
            it.flush()
        }

        val start = System.currentTimeMillis()
        val hud = FileReader("theme.json").use {
            gson.fromJson(it, HUD::class.java)
        }
        hud.setup()
        logger.info("Loaded theme and set it up in " + (System.currentTimeMillis() - start) + "ms.")
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager, types: Predicate<IResourceType>) {
        if (resourceManager is FMLFolderResourcePack && resourceManager.fmlContainer.modId == SAOCore.MODID) {
            // load()
        }
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager) {
        super.onResourceManagerReload(resourceManager)
        themeList = SAOCore.getFiles()
        load()
    }
}
