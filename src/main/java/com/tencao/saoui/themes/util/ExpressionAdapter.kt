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
import gnu.jel.CompilationException
import gnu.jel.CompiledExpression
import java.io.PrintWriter
import java.io.StringWriter
import javax.xml.bind.annotation.adapters.XmlAdapter

/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
abstract class ExpressionAdapter<T> : XmlAdapter<ExpressionIntermediate, CValue<T>>() {

    @Throws(Exception::class)
    override fun unmarshal(v: ExpressionIntermediate) = try {
        value(v.cacheType.cacheExpression(wrap(LibHelper.compile(v.expression, LibHelper.LIB, type)!!)))
    } catch (ce: CompilationException) {
        val sb = StringBuilder("An error occurred during theme loading. See more info below.\n")
            .append("–––COMPILATION ERROR :\n")
            .append(ce.message).append('\n')
            .append("                       ")
            .append(v.expression).append('\n')
        val column = ce.column // Column, where error was found
        for (i in 0 until column + 23 - 1) sb.append(' ')
        sb.append('^')
        val w = StringWriter()
        ce.printStackTrace(PrintWriter(w))
        sb.append('\n').append(w)
        SAOCore.LOGGER.fatal(sb.toString())
        null
    } catch (e: Exception) {
        val w = StringWriter()
        e.printStackTrace(PrintWriter(w))
        SAOCore.LOGGER.fatal("An error occurred while compiling '${v.expression}'.\n$w")
        null
    }

    @Throws(Exception::class)
    override fun marshal(v: CValue<T>) = throw UnsupportedOperationException("marshalling xml expressions isn't allowed just yet.") // TODO: see Evaluator.compileBits(...)

    /**
     * The targeted class for this adapter's expression.

     * @return target
     */
    protected abstract val type: Class<*>?

    protected abstract fun value(c: CachedExpression<T>): CValue<T>

    /**
     * Wrap the expression using the appropriate [CompiledExpressionWrapper].

     * @param ce expression to wrap
     * *
     * @return wrapped expression
     */
    protected abstract fun wrap(ce: CompiledExpression): CompiledExpressionWrapper<T>
}

/**
 * Adapts an expression that should return a int.
 */
class IntExpressionAdapter : ExpressionAdapter<Int>() {
    override fun value(c: CachedExpression<Int>) = CInt(c)

    override val type: Class<*> = Integer.TYPE

    override fun wrap(ce: CompiledExpression) = IntExpressionWrapper(ce)
}

/**
 * Adapts an expression that should return a double.
 */
class DoubleExpressionAdapter : ExpressionAdapter<Double>() {
    override fun value(c: CachedExpression<Double>) = CDouble(c)

    override val type: Class<*> = java.lang.Double.TYPE

    override fun wrap(ce: CompiledExpression) = DoubleExpressionWrapper(ce)
}

/**
 * Adapts an expression that should return a String.
 */
class StringExpressionAdapter : ExpressionAdapter<String>() {
    override fun value(c: CachedExpression<String>) = CString(c)

    override val type: Class<*> = String::class.java

    override fun wrap(ce: CompiledExpression) = StringExpressionWrapper(ce)
}

/**
 * Adapts an expression that should return a boolean.
 */
class BooleanExpressionAdapter : ExpressionAdapter<Boolean>() {
    override fun value(c: CachedExpression<Boolean>) = CBoolean(c)

    override val type: Class<*> = java.lang.Boolean.TYPE

    override fun wrap(ce: CompiledExpression) = BooleanExpressionWrapper(ce)
}

/**
 * Adapts an expression that should return [Unit] (aka void).
 */
class UnitExpressionAdapter : ExpressionAdapter<Unit>() {
    override val type: Class<*>? = null

    override fun value(c: CachedExpression<Unit>) = CUnit(c)

    override fun wrap(ce: CompiledExpression) = UnitExpressionWrapper(ce)
}
