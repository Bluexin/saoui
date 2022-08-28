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
import com.tencao.saoui.api.themes.IHudDrawContext
import gnu.jel.CompiledExpression

/**
 * Typesafe wrappers for {@link CompiledExpression}
 *
 * @author Bluexin
 */
abstract class CompiledExpressionWrapper<out T>(val compiledExpression: CompiledExpression) : Function1<IHudDrawContext, T> {
    protected fun warn(e: Throwable) {
        SAOCore.LOGGER.warn("An error occurred while executing an Expression.\n${e.message}\n${e.cause}")
        SAOCore.LOGGER.warn(e)
        // e.printStackTrace()
    }
}

class IntExpressionWrapper(compiledExpression: CompiledExpression) : CompiledExpressionWrapper<Int>(compiledExpression) {
    override fun invoke(ctx: IHudDrawContext): Int = try {
        compiledExpression.evaluate_int(arrayOf(ctx))
    } catch (e: Throwable) {
        warn(e)
        0
    }
}

class DoubleExpressionWrapper(compiledExpression: CompiledExpression) : CompiledExpressionWrapper<Double>(compiledExpression) {
    override fun invoke(ctx: IHudDrawContext): Double = try {
        compiledExpression.evaluate_double(arrayOf(ctx))
    } catch (e: Throwable) {
        warn(e)
        0.0
    }
}

class StringExpressionWrapper(compiledExpression: CompiledExpression) : CompiledExpressionWrapper<String>(compiledExpression) {
    override fun invoke(ctx: IHudDrawContext): String = try {
        compiledExpression.evaluate(arrayOf(ctx)).toString()
    } catch (e: Throwable) {
        warn(e)
        "--Error!"
    }
}

// @XmlJavaTypeAdapter(ExpressionAdapter.BooleanExpressionAdapter::class)
class BooleanExpressionWrapper(compiledExpression: CompiledExpression) : CompiledExpressionWrapper<Boolean>(compiledExpression) {
    override fun invoke(ctx: IHudDrawContext): Boolean = try {
        compiledExpression.evaluate_boolean(arrayOf(ctx))
    } catch (e: Throwable) {
        warn(e)
        false
    }
}

class UnitExpressionWrapper(compiledExpression: CompiledExpression) : CompiledExpressionWrapper<Unit>(compiledExpression) {
    override fun invoke(ctx: IHudDrawContext) = try {
        compiledExpression.evaluate_void(arrayOf(ctx))
    } catch (e: Throwable) {
        warn(e)
    }
}
