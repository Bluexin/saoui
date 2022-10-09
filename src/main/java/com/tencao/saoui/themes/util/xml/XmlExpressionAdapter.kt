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

package com.tencao.saoui.themes.util.xml

import com.tencao.saoui.themes.util.*
import gnu.jel.CompilationException
import gnu.jel.CompiledExpression
import gnu.jel.Evaluator
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.PrintWriter
import java.io.StringWriter
import javax.xml.bind.annotation.adapters.XmlAdapter

/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
abstract class XmlExpressionAdapter<T> : XmlAdapter<ExpressionIntermediate, CValue<T>>(), ExpressionAdapter<T> {
    override val logger: Logger by lazy { LogManager.getLogger(javaClass) }

    @Throws(Exception::class)
    override fun unmarshal(v: ExpressionIntermediate) = compile(v)

    @Throws(Exception::class)
    override fun marshal(v: CValue<T>) = throw UnsupportedOperationException("marshalling xml expressions isn't allowed just yet.") // TODO: see Evaluator.compileBits(...)
}

/**
 * Adapts an expression that should return a int.
 */
class IntExpressionAdapter : XmlExpressionAdapter<Int>() {
    override fun value(c: CachedExpression<Int>) = CInt(c)

    override val type: Class<*> = Integer.TYPE

    override fun wrap(ce: CompiledExpression) = IntExpressionWrapper(ce)
}

/**
 * Adapts an expression that should return a double.
 */
class DoubleExpressionAdapter : XmlExpressionAdapter<Double>() {
    override fun value(c: CachedExpression<Double>) = CDouble(c)

    override val type: Class<*> = java.lang.Double.TYPE

    override fun wrap(ce: CompiledExpression) = DoubleExpressionWrapper(ce)
}

/**
 * Adapts an expression that should return a String.
 */
class StringExpressionAdapter : XmlExpressionAdapter<String>() {
    override fun value(c: CachedExpression<String>) = CString(c)

    override val type: Class<*> = String::class.java

    override fun wrap(ce: CompiledExpression) = StringExpressionWrapper(ce)
}

/**
 * Adapts an expression that should return a boolean.
 */
class BooleanExpressionAdapter : XmlExpressionAdapter<Boolean>() {
    override fun value(c: CachedExpression<Boolean>) = CBoolean(c)

    override val type: Class<*> = java.lang.Boolean.TYPE

    override fun wrap(ce: CompiledExpression) = BooleanExpressionWrapper(ce)
}

/**
 * Adapts an expression that should return [Unit] (aka void).
 */
class UnitExpressionAdapter : XmlExpressionAdapter<Unit>() {
    override val type: Class<*>? = null

    override fun value(c: CachedExpression<Unit>) = CUnit(c)

    override fun wrap(ce: CompiledExpression) = UnitExpressionWrapper(ce)
}
