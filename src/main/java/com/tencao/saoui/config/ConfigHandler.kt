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

package com.tencao.saoui.config

import com.tencao.saoui.SAOCore
import com.tencao.saoui.SAOCore.saoConfDir
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.config.Configuration
import java.io.File
import java.util.stream.Stream

/**
 * Part of SAOUI

 * @author Bluexin
 */
object ConfigHandler {
    val DEFAULT_THEME = ResourceLocation(SAOCore.MODID, "sao")

    var lastVersion: String = "1.0"
    var IGNORE_UPDATE: Boolean = false
    var DEBUG = false
    var debugFakePT: Int = 0
    var lastThemeUsed: ResourceLocation = DEFAULT_THEME
    var config: Configuration = Configuration(File(saoConfDir, "main.cfg"))
        private set

    fun preInit() {
        // saoConfDir = confDir(event.modConfigurationDirectory)
        // config = Configuration(File(saoConfDir, "main.cfg"))
        config.load()

        DEBUG = config.get(Configuration.CATEGORY_GENERAL, "debug", DEBUG).boolean

        lastVersion = config.get(Configuration.CATEGORY_GENERAL, "lastUpdate", "nothing").string
        IGNORE_UPDATE = config.get(Configuration.CATEGORY_GENERAL, "ignoreUpdate", false).boolean

        OptionCore.values().filter { it.isCategory }
            .forEach { c ->
                Stream.of(*OptionCore.values()).filter { o -> o.category === c }
                    .forEach { o ->
                        if (config.get(c.name.toLowerCase(), o.name.toLowerCase(), o.isEnabled).boolean) {
                            o.enable()
                        } else {
                            o.disable()
                        }
                    }
            }

        OptionCore.values().filter { o -> !o.isCategory && o.category == null }.forEach { o ->
            if (config.get(Configuration.CATEGORY_GENERAL, o.name.toLowerCase(), o.isEnabled).boolean) {
                o.enable()
            } else {
                o.disable()
            }
        }

        debugFakePT = config.getInt("debugFakePT", Configuration.CATEGORY_GENERAL, 0, 0, 10, "Amount of fake party members, 0 to disable.")

        lastThemeUsed = config.getString("lastThemeUsed", Configuration.CATEGORY_GENERAL, lastThemeUsed.toString(), "The last used theme loaded. If invalid, defaults to sao's theme")
            .let {
                if (it.contains(':')) ResourceLocation(it)
                else DEFAULT_THEME // fallback for old configs
            }

        config.save()
    }

    fun setOption(option: OptionCore) {
        config.get(option.category?.name?.toLowerCase() ?: Configuration.CATEGORY_GENERAL, option.name.toLowerCase(), option.isEnabled).set(option.isEnabled)
        saveAllOptions()
    }

    fun saveAllOptions() {
        config.save()
    }

    fun saveTheme(theme: ResourceLocation) {
        config.get(Configuration.CATEGORY_GENERAL, "lastThemeUsed", lastThemeUsed.toString()).set(theme.toString())
        lastThemeUsed = theme
        config.save()
    }

    fun saveVersion(version: String) {
        config.get(Configuration.CATEGORY_GENERAL, "last.update", lastVersion).set(version)
        config.save()
    }

    fun setIgnoreVersion(value: Boolean) {
        config.get(Configuration.CATEGORY_GENERAL, "ignore.update", ignoreVersion()).set(value)
        config.save()
    }

    fun ignoreVersion(): Boolean {
        return IGNORE_UPDATE
    }
}
