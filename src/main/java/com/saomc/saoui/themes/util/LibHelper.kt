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

package com.saomc.saoui.themes.util

import com.saomc.saoui.SAOCore
import com.saomc.saoui.api.info.IOption
import com.saomc.saoui.api.themes.IHudDrawContext
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.effects.StatusEffects
import com.saomc.saoui.screens.util.HealthStep
import com.saomc.saoui.themes.elements.ElementParent
import com.saomc.saoui.util.ColorUtil
import cpw.mods.modlauncher.Launcher
import gnu.jel.CompilationException
import gnu.jel.Library
import net.minecraft.client.resources.I18n


/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
object LibHelper {
    val LIB: Library by lazy {
        val staticLib = arrayOf(Math::class.java, HealthStep::class.java, StatusEffects::class.java, OptionCore::class.java, I18n::class.java, ColorUtil::class.java)
        val dynLib = arrayOf(IHudDrawContext::class.java, ElementParent::class.java)
        val dotClasses = arrayOf(String::class.java, IOption::class.java, List::class.java, StatusEffects::class.java, HealthStep::class.java, ColorUtil::class.java)
        Library(staticLib, dynLib, dotClasses, null, null)
    }

    init {
        try {
            LIB.markStateDependent("random", null)
        } catch (e: CompilationException) {
            throw IllegalStateException(e)
        }
    }

    val obfuscated: Boolean by lazy {
        //TODO LOOK INTO
        //val obf = !(Launcher.INSTANCE.blackboard()["fml.deobfuscatedEnvironment"] as Boolean)
        //SAOCore.LOGGER.warn("Obfuscated: $obf")
        true
    }
}
