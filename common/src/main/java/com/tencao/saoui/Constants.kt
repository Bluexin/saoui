package com.tencao.saoui

import me.shedaniel.architectury.platform.Platform
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File

object Constants {

    const val MODID = "saoui"
    const val NAME = "Sword Art Online UI"
    const val MOD_VERSION = "indev"
    @JvmField
    val LOGGER: Logger = LogManager.getLogger(MODID)
    val configDirectory: File = File(Platform.getConfigFolder().toUri())
}