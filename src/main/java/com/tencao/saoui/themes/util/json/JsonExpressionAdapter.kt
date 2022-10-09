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

package com.tencao.saoui.themes.util.json

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.tencao.saoui.themes.util.*
import gnu.jel.CompiledExpression
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
abstract class JsonExpressionAdapter<T> : TypeAdapter<CValue<T>>(), ExpressionAdapter<T> {
    private val concatSpaces = Regex("  +")
    override val logger: Logger by lazy { LogManager.getLogger(javaClass) }

    override fun write(out: JsonWriter, value: CValue<T>) {
        val intermediate = value.value.expressionIntermediate
        when (intermediate.cacheType) {
            CacheType.STATIC -> out.value(intermediate.expression.replace(concatSpaces, " "))
            else -> {
                out.beginObject()
                out.name("cache")
                out.value(intermediate.cacheType.name)
                out.name("expression")
                out.value(intermediate.expression.replace(concatSpaces, " "))
                out.endObject()
            }
        }
    }

    override fun read(reader: JsonReader): CValue<T>? {
        return when (val nextToken = reader.peek()) {
            JsonToken.BEGIN_OBJECT -> {
                reader.beginObject()
                var cacheType: CacheType? = null
                var expression: String? = null
                repeat(2) {
                    when (reader.nextName()) {
                        "cache" -> cacheType = CacheType.valueOf(reader.nextString())
                        "expression" -> expression = reader.nextString()
                    }
                }
                reader.endObject()
                if (cacheType != null && expression != null) compile(ExpressionIntermediate().apply {
                    this.cacheType = cacheType!!
                    this.expression = expression!!
                }) else {
                    logger.warn("Unable to deserialize ${reader.path} : missing cache or expression")
                    null
                }
            }
            JsonToken.STRING -> compile(ExpressionIntermediate().apply {
                cacheType = CacheType.STATIC
                expression = reader.nextString()
            })
            else -> error("Unknown token $nextToken")
        }
    }
}

/**
 * Adapts an expression that should return a int.
 */
class JsonIntExpressionAdapter : JsonExpressionAdapter<Int>() {
    override fun value(c: CachedExpression<Int>) = CInt(c)

    override val type: Class<*> = Integer.TYPE

    override fun wrap(ce: CompiledExpression) = IntExpressionWrapper(ce)
}

/**
 * Adapts an expression that should return a double.
 */
class JsonDoubleExpressionAdapter : JsonExpressionAdapter<Double>() {
    override fun value(c: CachedExpression<Double>) = CDouble(c)

    override val type: Class<*> = java.lang.Double.TYPE

    override fun wrap(ce: CompiledExpression) = DoubleExpressionWrapper(ce)
}

/**
 * Adapts an expression that should return a String.
 */
class JsonStringExpressionAdapter : JsonExpressionAdapter<String>() {
    override fun value(c: CachedExpression<String>) = CString(c)

    override val type: Class<*> = String::class.java

    override fun wrap(ce: CompiledExpression) = StringExpressionWrapper(ce)
}

/**
 * Adapts an expression that should return a boolean.
 */
class JsonBooleanExpressionAdapter : JsonExpressionAdapter<Boolean>() {
    override fun value(c: CachedExpression<Boolean>) = CBoolean(c)

    override val type: Class<*> = java.lang.Boolean.TYPE

    override fun wrap(ce: CompiledExpression) = BooleanExpressionWrapper(ce)
}

/**
 * Adapts an expression that should return [Unit] (aka void).
 */
class JsonUnitExpressionAdapter : JsonExpressionAdapter<Unit>() {
    override val type: Class<*>? = null

    override fun value(c: CachedExpression<Unit>) = CUnit(c)

    override fun wrap(ce: CompiledExpression) = UnitExpressionWrapper(ce)
}
