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

package be.bluexin.mcui.themes

import com.google.gson.GsonBuilder
import be.bluexin.mcui.themes.elements.ElementParent
import be.bluexin.mcui.themes.elements.Fragment
import be.bluexin.mcui.themes.elements.Hud
import be.bluexin.mcui.themes.util.json.AfterUnmarshalAdapterFactory
import java.io.File
import java.io.FileWriter
import java.io.InputStream

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
object JsonThemeLoader : AbstractThemeLoader(ThemeFormat.JSON) {

    private val gson by lazy {
        GsonBuilder()
            .registerTypeAdapterFactory(AfterUnmarshalAdapterFactory())
            .create()
    }

    override fun InputStream.loadHud(): Hud = use {
        gson.fromJson(it.reader(), Hud::class.java)
    }

    override fun InputStream.loadFragment(): Fragment = use {
        gson.fromJson(it.reader(), Fragment::class.java)
    }

    fun export(what: ElementParent, toFile: File) {
        FileWriter(toFile).use {
            GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create().toJson(what, it)
            it.flush()
        }
    }
}
