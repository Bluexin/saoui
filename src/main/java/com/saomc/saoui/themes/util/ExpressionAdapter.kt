package com.saomc.saoui.themes.util

import com.saomc.saoui.util.LogCore
import gnu.jel.CompilationException
import gnu.jel.CompiledExpression
import gnu.jel.Evaluator
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
        value(v.cacheType.cacheExpression(wrap(Evaluator.compile(v.expression, LibHelper.LIB, type))))
    } catch (ce: CompilationException) {
        val sb = StringBuilder("An error occurred during theme loading. See more info below.\n")
                .append("–––COMPILATION ERROR :\n")
                .append(ce.message).append('\n')
                .append("                       ")
                .append(v.expression).append('\n')
        val column = ce.column // Column, where error was found
        for (i in 0..column + 23 - 1 - 1) sb.append(' ')
        sb.append('^')
        val w = StringWriter()
        ce.printStackTrace(PrintWriter(w))
        sb.append('\n').append(w)
        LogCore.logFatal(sb.toString())
        null
    } catch (e: Exception) {
        val w = StringWriter()
        e.printStackTrace(PrintWriter(w))
        LogCore.logFatal("An error occurred while compiling '${v.expression}'.\n$w")
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
