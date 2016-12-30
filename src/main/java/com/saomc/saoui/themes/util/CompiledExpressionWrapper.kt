package com.saomc.saoui.themes.util

import com.saomc.saoui.themes.elements.HudDrawContext
import com.saomc.saoui.util.LogCore
import gnu.jel.CompiledExpression

/**
 * Typesafe wrappers for {@link CompiledExpression}
 *
 * @author Bluexin
 */
abstract class CompiledExpressionWrapper<out T>(val compiledExpression: CompiledExpression) {
    /**
     * Evaluates an expression.
     *
     * @param ctx Context to use when evaluating the expression.
     */
    abstract fun execute(ctx: HudDrawContext): T

    fun warn(e: Throwable) {
        LogCore.logWarn("An error occurred while executing an Expression.\n${e.message}\n${e.cause}")
        e.printStackTrace()
    }
}

class IntExpressionWrapper(compiledExpression: CompiledExpression) : CompiledExpressionWrapper<Int>(compiledExpression) {
    override fun execute(ctx: HudDrawContext): Int = try {
        compiledExpression.evaluate_int(arrayOf(ctx))
    } catch (e: Throwable) {
        warn(e)
        0
    }
}

class DoubleExpressionWrapper(compiledExpression: CompiledExpression) : CompiledExpressionWrapper<Double>(compiledExpression) {
    override fun execute(ctx: HudDrawContext): Double = try {
        compiledExpression.evaluate_double(arrayOf(ctx))
    } catch (e: Throwable) {
        warn(e)
        0.0
    }
}

class StringExpressionWrapper(compiledExpression: CompiledExpression) : CompiledExpressionWrapper<String>(compiledExpression) {
    override fun execute(ctx: HudDrawContext): String = try {
        compiledExpression.evaluate(arrayOf(ctx)).toString()
    } catch (e: Throwable) {
        warn(e)
        "--Error!"
    }
}
