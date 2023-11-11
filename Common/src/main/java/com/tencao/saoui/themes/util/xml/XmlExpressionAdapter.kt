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

import com.tencao.saoui.themes.util.CValue
import com.tencao.saoui.themes.util.ExpressionIntermediate
import com.tencao.saoui.themes.util.typeadapters.*
import jakarta.xml.bind.annotation.adapters.XmlAdapter

/**
 * Part of saoui by Bluexin.

 * @author Bluexin
 */
abstract class XmlExpressionAdapter<T: Any>(
    private val expressionAdapter: BasicExpressionAdapter<T>
) : XmlAdapter<ExpressionIntermediate, CValue<T>>() {

    @Throws(Exception::class)
    override fun unmarshal(v: ExpressionIntermediate) = expressionAdapter.compile(v)

    @Throws(Exception::class)
    override fun marshal(v: CValue<T>) = v.value.expressionIntermediate
}

/**
 * Adapts an expression that should return a int.
 */
class XmlIntExpressionAdapter : XmlExpressionAdapter<Int>(IntExpressionAdapter)

/**
 * Adapts an expression that should return a double.
 */
class XmlDoubleExpressionAdapter : XmlExpressionAdapter<Double>(DoubleExpressionAdapter)

/**
 * Adapts an expression that should return a String.
 */
class XmlStringExpressionAdapter : XmlExpressionAdapter<String>(StringExpressionAdapter)

/**
 * Adapts an expression that should return a boolean.
 */
class XmlBooleanExpressionAdapter : XmlExpressionAdapter<Boolean>(BooleanExpressionAdapter)

/**
 * Adapts an expression that should return [Unit] (aka void).
 */
class XmlUnitExpressionAdapter : XmlExpressionAdapter<Unit>(UnitExpressionAdapter)
