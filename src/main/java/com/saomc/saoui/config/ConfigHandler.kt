package com.saomc.saoui.config

import com.saomc.saoui.SAOCore
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

import java.io.File
import java.util.stream.Stream

/**
 * Part of SAOUI

 * @author Bluexin
 */
object ConfigHandler {
    var lastVersion: String = ""
    var _IGNORE_UPATE: Boolean = false
    var DEBUG = false
    var debugFakePT: Int = 0
    private var config: Configuration? = null
    var saoConfDir: File? = null
        private set

    fun preInit(event: FMLPreInitializationEvent) {
        saoConfDir = confDir(event.modConfigurationDirectory)
        config = Configuration(File(saoConfDir, "main.cfg"))
        config!!.load()

        DEBUG = config!!.get(Configuration.CATEGORY_GENERAL, "debug", DEBUG).boolean

        lastVersion = config!!.get(Configuration.CATEGORY_GENERAL, "lastUpdate", "nothing").string
        _IGNORE_UPATE = config!!.get(Configuration.CATEGORY_GENERAL, "ignoreUpdate", false).boolean

        OptionCore.values().filter{ it.isCategory }
                .forEach { c ->
                    Stream.of(*OptionCore.values()).filter { o -> o.category === c }
                            .forEach { o ->
                                if (config!!.get(c.name.toLowerCase(), o.name.toLowerCase(), o.isEnabled).boolean)
                                    o.enable()
                                else
                                    o.disable()
                            }
                }

        OptionCore.values().filter { o -> !o.isCategory && o.category == null }.forEach { o ->
            if (config!!.get(Configuration.CATEGORY_GENERAL, o.name.toLowerCase(), o.isEnabled).boolean)
                o.enable()
            else
                o.disable()
        }

        debugFakePT = config!!.getInt("debugFakePT", Configuration.CATEGORY_GENERAL, 0, 0, 10, "Amount of fake party members, 0 to disable.")

        config!!.save()
    }

    fun setOption(option: OptionCore) {
        config!!.get(option.category?.name?.toLowerCase() ?: Configuration.CATEGORY_GENERAL, option.name.toLowerCase(), option.isEnabled).set(option.isEnabled)
        saveAllOptions()
    }

    private fun saveAllOptions() {
        config!!.save()
    }

    fun saveVersion(version: String) {
        config!!.get(Configuration.CATEGORY_GENERAL, "last.update", lastVersion).set(version)
        config!!.save()
    }

    fun setIgnoreVersion(value: Boolean) {
        config!!.get(Configuration.CATEGORY_GENERAL, "ignore.update", ignoreVersion()).set(value)
        config!!.save()
    }

    fun ignoreVersion(): Boolean {
        return _IGNORE_UPATE
    }

    private fun confDir(genDir: File): File {
        return File(genDir, SAOCore.MODID)
    }
}
