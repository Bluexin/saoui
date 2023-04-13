package com.tencao.saoui.themes

import net.minecraft.launchwrapper.Launch
import org.apache.logging.log4j.LogManager
import java.io.File

object ThemeConverter {

    private val logger by lazy { LogManager.getLogger(javaClass) }

    @JvmStatic
    fun main(args: Array<String>) {
        Launch.blackboard = mapOf("fml.deobfuscatedEnvironment" to true)
        if (args.size != 2) {
            logger.error(
                "Wrong arguments provided : {}, expected [xml theme path] [output file path]",
                args.joinToString(prefix = "[", postfix = "]")
            )
            return
        }
        val toRead = File(args[0])
        val toWrite = File(args[1])
        val hud = try {
            logger.info("Loading $toRead")
            XmlThemeLoader.loadHud(toRead)
                .also { it.setup(emptyMap()) }
        } catch (e: Exception) {
            val message = "Something went wrong reading $toRead"
            logger.error(message, e)
            AbstractThemeLoader.Reporter += message
            return
        }
        try {
            logger.info("Exporting to $toWrite")
            JsonThemeLoader.exportHud(hud, toWrite)
        } catch (e: Exception) {
            val message = "Something went wrong writing $toWrite"
            logger.error(message, e)
            AbstractThemeLoader.Reporter += message
            return
        }
        logger.info("Converted $toRead to $toWrite")
    }
}