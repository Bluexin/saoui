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
import com.tencao.saoui.config.Settings.NS_BUILTIN
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL
import java.io.File

/**
 * Part of SAOUI

 * @author Bluexin
 */
object ConfigHandler {
    val DEFAULT_THEME = ResourceLocation(SAOCore.MODID, "sao")

    private fun general(key: String) = ResourceLocation(CATEGORY_GENERAL, key)

    private val DEBUG_S = BooleanSetting(NS_BUILTIN, general("debug"), false).also(Settings::registerSetting)
    private val LAST_UPDATE =
        StringSetting(NS_BUILTIN, general("lastUpdate"), "nothing").also(Settings::registerSetting)
    private val IGNORE_UPDATE_S =
        BooleanSetting(NS_BUILTIN, general("ignoreUpdate"), true).also(Settings::registerSetting)
    private val DEBUG_FAKE_PARTY = IntSetting(
        NS_BUILTIN, general("debugFakePT"), 0, "Amount of fake party members, 0 to disable."
    ) { it in 0..10 }.also(Settings::registerSetting)
    private val LAST_THEME_USED = ResourceLocationSetting(
        NS_BUILTIN, general("lastThemeUsed"), DEFAULT_THEME,
        "The last used theme loaded. If invalid, defaults to sao's theme"
    ).also(Settings::registerSetting)

    var lastVersion by LAST_UPDATE
    var ignoreUpdate by IGNORE_UPDATE_S
    var enableDebug by DEBUG_S
    var debugFakePT by DEBUG_FAKE_PARTY
    var lastThemeUsed by LAST_THEME_USED
    var config: Configuration = Configuration(File(saoConfDir, "main.cfg"))
        private set
}
