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

package be.bluexin.mcui.themes.util

import be.bluexin.mcui.api.info.IOption
import be.bluexin.mcui.api.themes.IHudDrawContext
import be.bluexin.mcui.config.OptionCore
import be.bluexin.mcui.config.Settings
import be.bluexin.mcui.effects.StatusEffects
import be.bluexin.mcui.util.HealthStep
import be.bluexin.mcui.themes.util.typeadapters.*
import be.bluexin.mcui.util.ColorUtil
import gnu.jel.CompilationException
import gnu.jel.DVMap
import gnu.jel.Library
import net.minecraft.client.resources.I18n
import net.minecraft.launchwrapper.Launch

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
object LibHelper {
    private val contextResolver = ContextAwareDVMap()

    val LIB: Library by lazy {
        val staticLib = arrayOf(Math::class.java, HealthStep::class.java, StatusEffects::class.java, OptionCore::class.java, Settings.JelWrappers::class.java, I18n::class.java, ColorUtil::class.java)
        val dynLib = arrayOf(IHudDrawContext::class.java)
        val dotClasses = arrayOf(String::class.java, IOption::class.java, List::class.java, StatusEffects::class.java, HealthStep::class.java, ColorUtil::class.java)
        Library(staticLib, dynLib, dotClasses, contextResolver, null)
    }

    init {
        try {
            LIB.markStateDependent("random", null)
        } catch (e: CompilationException) {
            throw IllegalStateException(e)
        }
    }

    val obfuscated: Boolean by lazy {
        val obf = !(Launch.blackboard["fml.deobfuscatedEnvironment"] as Boolean)
        Constants.LOG.debug("Obfuscated: $obf")
        obf
    }

    fun pushContext(context: Map<String, JelType>) {
        contextResolver.context = context
    }

    fun popContext() {
        contextResolver.context = emptyMap()
    }
}

private class ContextAwareDVMap : DVMap() {
    var context: Map<String, JelType> = emptyMap()
    override fun getTypeName(name: String): String? = context[name]?.typeName
}
