package com.saomc.saoui.themes.util

import com.saomc.saoui.api.themes.IHudDrawContext
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
