package com.saomc.saoui.themes.util

import com.saomc.saoui.SAOCore
import com.saomc.saoui.api.themes.IHudDrawContext
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

//@XmlJavaTypeAdapter(ExpressionAdapter.BooleanExpressionAdapter::class)
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
