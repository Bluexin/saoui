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

package com.tencao.saoui.themes.util

import com.tencao.saoui.SAOCore
import com.tencao.saoui.api.info.IOption
import com.tencao.saoui.api.themes.IHudDrawContext
import com.tencao.saoui.config.OptionCore
import com.tencao.saoui.effects.StatusEffects
import com.tencao.saoui.screens.util.HealthStep
import com.tencao.saoui.themes.elements.ElementParent
import com.tencao.saoui.util.ColorUtil
import gnu.jel.CompilationException
import gnu.jel.CompiledExpression
import gnu.jel.Evaluator
import gnu.jel.Library
import net.minecraft.client.resources.I18n
import net.minecraftforge.fml.loading.FMLLoader

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
        val obf = !FMLLoader.getNameFunction("srg").isPresent
        if (!obf) {
            SAOCore.LOGGER.warn("Obfuscated: $obf")
        }
        obf
    }

    /**
     * Compiles expression, resolving the function names in the library.
     * @param expression is the expression to compile. i.e. "sin(666)" .
     * @param lib Library of functions exported for use in expression.
     * @param resultType identifies the type result should be converted to. Can
     * be null, in this case the result type is not fixed.
     * @return Instance of the CompiledExpression subclass, implementing
     * the specified expression evaluation.
     * @exception CompilationException if the expression is not
     * syntactically or semantically correct.
     * @see CompiledExpression
     */
    @Throws(CompilationException::class)
    fun compile(
        expression: String,
        library: Library,
        resultType: Class<*>?
    ): CompiledExpression? {
        val image = Evaluator.compileBits(expression, library, resultType)
        return try {
            ImageLoader.load(image).newInstance() as CompiledExpression
        } catch (exc: Exception) {
            SAOCore.LOGGER.fatal("Unable to compile expression '$expression'")
            SAOCore.LOGGER.fatal("With resultType: ${resultType?.canonicalName}")
            exc.printStackTrace()
            null
        }
    }
}
