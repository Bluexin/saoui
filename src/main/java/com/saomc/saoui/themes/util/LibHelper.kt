package com.saomc.saoui.themes.util

import com.saomc.saoui.api.info.IOption
import com.saomc.saoui.api.themes.IHudDrawContext
import com.saomc.saoui.config.OptionCore
import com.saomc.saoui.effects.StatusEffects
import com.saomc.saoui.screens.ingame.HealthStep
import com.saomc.saoui.themes.elements.ElementParent
import gnu.jel.CompilationException
import gnu.jel.Library
import net.minecraft.client.resources.I18n
import net.minecraft.launchwrapper.LaunchClassLoader

/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
object LibHelper {
    val LIB: Library by lazy {
        val staticLib = arrayOf(Math::class.java, HealthStep::class.java, OptionCore::class.java, I18n::class.java)
        val dynLib = arrayOf(IHudDrawContext::class.java, ElementParent::class.java)
        val dotClasses = arrayOf(String::class.java, IOption::class.java, List::class.java, StatusEffects::class.java)
        Library(staticLib, dynLib, dotClasses, null, null)
    }

    init {
        try {
            LIB.markStateDependent("random", null)
        } catch (e: CompilationException) {
            e.printStackTrace()
        }
    }

    val obfuscated: Boolean by lazy {
        try {
            val bytes = (LibHelper::class.java.classLoader as LaunchClassLoader).getClassBytes("net.minecraft.world.World")
            bytes == null
        } catch (e: Exception) {
            true
        }
    }
}
