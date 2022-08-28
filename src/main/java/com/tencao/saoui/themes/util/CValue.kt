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

import com.tencao.saoui.api.themes.IHudDrawContext
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

/**
 * Wraps around custom types implementation for XML loading and value caching.
 *
 * @author Bluexin
 */
abstract class CValue<out T>(val value: CachedExpression<T>) : (IHudDrawContext) -> T {
    override fun invoke(ctx: IHudDrawContext) = value(ctx)
}

/**
 * Custom Int type.
 */
@XmlJavaTypeAdapter(IntExpressionAdapter::class)
class CInt(value: CachedExpression<Int>) : CValue<Int>(value)

/**
 * Custom Double type.
 */
@XmlJavaTypeAdapter(DoubleExpressionAdapter::class)
class CDouble(value: CachedExpression<Double>) : CValue<Double>(value)

/**
 * Custom String type.
 */
@XmlJavaTypeAdapter(StringExpressionAdapter::class)
class CString(value: CachedExpression<String>) : CValue<String>(value)

/**
 * Custom Boolean type.
 */
@XmlJavaTypeAdapter(BooleanExpressionAdapter::class)
class CBoolean(value: CachedExpression<Boolean>) : CValue<Boolean>(value)

/**
 * Custom Unit/Void type.
 */
@XmlJavaTypeAdapter(UnitExpressionAdapter::class)
class CUnit(value: CachedExpression<Unit>) : CValue<Unit>(value)
