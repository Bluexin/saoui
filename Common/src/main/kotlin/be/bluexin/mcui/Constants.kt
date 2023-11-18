package be.bluexin.mcui

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

object Constants {
    const val MOD_ID = "mcui"
    const val MOD_VERSION = "indev" // TODO
    @JvmField
    val LOG: Logger = LoggerFactory.getLogger(MOD_ID)
    lateinit var configDirectory: File
}