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
import java.io.File
import java.io.FileWriter
import java.io.InputStream

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
class JsonThemeLoader : AbstractThemeLoader(ThemeFormat.JSON) {

    override fun InputStream.loadHud(): Hud = use {
        GsonBuilder()
            .create()
            .fromJson(it.reader(), Hud::class.java)
    }

    fun exportHud(hud: Hud, toFile: File) {
        FileWriter(toFile).use {
            GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create().toJson(hud, it)
            it.flush()
        }

        val start = System.currentTimeMillis()
        val newHud = loadHud(toFile)
        newHud.setup()
        SAOCore.LOGGER.info("Loaded theme and set it up in " + (System.currentTimeMillis() - start) + "ms.")
    }
}
