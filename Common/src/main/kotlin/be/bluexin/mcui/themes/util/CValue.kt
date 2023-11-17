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

package be.bluexin.mcui.themes.util

import be.bluexin.mcui.api.themes.IHudDrawContext
import be.bluexin.mcui.themes.util.serialization.*
import kotlinx.serialization.Serializable

/**
 * Wraps around custom types implementation for XML loading and value caching.
 *
 * @author Bluexin
 */
sealed class CValue<out T : Any>(val value: (IHudDrawContext) -> T) : (IHudDrawContext) -> T {
    override fun invoke(ctx: IHudDrawContext) = value(ctx)
}

/**
 * Custom Int type.
 */
@Serializable(CIntSerializer::class)
class CInt(value: (IHudDrawContext) -> Int) : CValue<Int>(value)

/**
 * Custom Double type.
 */
@Serializable(CDoubleSerializer::class)
class CDouble(value: (IHudDrawContext) -> Double) : CValue<Double>(value)

/**
 * Custom String type.
 */
@Serializable(CStringSerializer::class)
class CString(value: (IHudDrawContext) -> String) : CValue<String>(value)

/**
 * Custom Boolean type.
 */
@Serializable(CBooleanSerializer::class)
class CBoolean(value: (IHudDrawContext) -> Boolean) : CValue<Boolean>(value)

/**
 * Custom Unit/Void type.
 */
@Serializable(CUnitSerializer::class)
class CUnit(value: (IHudDrawContext) -> Unit) : CValue<Unit>(value)

val ((IHudDrawContext) -> Any).expressionIntermediate: ExpressionIntermediate? get() = (this as? CachedExpression<*>)?.expressionIntermediate
val ((IHudDrawContext) -> Any).expression: String? get() = this.expressionIntermediate?.expression
