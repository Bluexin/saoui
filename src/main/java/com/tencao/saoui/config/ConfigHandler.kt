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
import com.tencao.saoui.config.Settings.NS_BUILTIN
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL

/**
 * Part of SAOUI

 * @author Bluexin
 */
object ConfigHandler {
    val DEFAULT_THEME = ResourceLocation(SAOCore.MODID, "sao")

    private fun general(key: String) = ResourceLocation(CATEGORY_GENERAL, key)

    var lastVersion by StringSetting(NS_BUILTIN, general("lastUpdate"), "nothing").register()
    var ignoreUpdate by BooleanSetting(NS_BUILTIN, general("ignoreUpdate"), true).register()
    var enableDebug by BooleanSetting(NS_BUILTIN, general("debug"), false).register()
    var debugFakePT by IntSetting(
        NS_BUILTIN, general("debugFakePT"), 0, "Amount of fake party members, 0 to disable."
    ) { it in 0..10 }.register()
    var currentTheme by ResourceLocationSetting(
        NS_BUILTIN, general("currentTheme"), DEFAULT_THEME,
        "The currently selected theme. If invalid or unavailable, this will default to the builtin sao theme"
    ).register()
}
