package com.tencao.saoui

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File

object Constants {

    const val MODID = "saoui"
    const val NAME = "Sword Art Online UI"
    @JvmField
    val LOGGER: Logger = LogManager.getLogger(MODID)
    lateinit var configDirectory: File
}