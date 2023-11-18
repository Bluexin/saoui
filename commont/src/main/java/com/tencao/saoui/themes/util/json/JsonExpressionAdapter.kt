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
import com.tencao.saoui.themes.AbstractThemeLoader
import com.tencao.saoui.themes.util.CValue
import com.tencao.saoui.themes.util.CacheType
import com.tencao.saoui.themes.util.ExpressionIntermediate
import com.tencao.saoui.themes.util.typeadapters.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
abstract class JsonExpressionAdapter<T: Any>(
    private val expressionAdapter: BasicExpressionAdapter<T>
) : TypeAdapter<CValue<T>>() {
    private val concatSpaces = Regex("  +")
    private val logger: Logger by lazy { LogManager.getLogger(javaClass) }

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

    override fun read(reader: JsonReader): CValue<T>? = when (val nextToken = reader.peek()) {
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
            if (cacheType != null && expression != null) expressionAdapter.compile(ExpressionIntermediate().apply {
                this.cacheType = cacheType!!
                this.expression = expression!!
            }) else {
                val message = "Unable to deserialize ${reader.path} : missing cache or expression"
                logger.warn(message)
                AbstractThemeLoader.Reporter += message
                null
            }
        }
        JsonToken.STRING -> expressionAdapter.compile(ExpressionIntermediate().apply {
            cacheType = CacheType.STATIC
            expression = reader.nextString()
        })
        else -> error("Unknown token $nextToken")
    }
}

/**
 * Adapts an expression that should return a int.
 */
class JsonIntExpressionAdapter : JsonExpressionAdapter<Int>(IntExpressionAdapter)

/**
 * Adapts an expression that should return a double.
 */
class JsonDoubleExpressionAdapter : JsonExpressionAdapter<Double>(DoubleExpressionAdapter)

/**
 * Adapts an expression that should return a String.
 */
class JsonStringExpressionAdapter : JsonExpressionAdapter<String>(StringExpressionAdapter)

/**
 * Adapts an expression that should return a boolean.
 */
class JsonBooleanExpressionAdapter : JsonExpressionAdapter<Boolean>(BooleanExpressionAdapter)

/**
 * Adapts an expression that should return [Unit] (aka void).
 */
class JsonUnitExpressionAdapter : JsonExpressionAdapter<Unit>(UnitExpressionAdapter)
