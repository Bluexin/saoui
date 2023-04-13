package com.tencao.saoui.themes.util

import com.tencao.saoui.themes.AbstractThemeLoader
import gnu.jel.CompilationException
import gnu.jel.CompiledExpression
import gnu.jel.Evaluator
import org.apache.logging.log4j.Logger
import java.io.PrintWriter
import java.io.StringWriter

interface ExpressionAdapter<T> {
    val logger: Logger

    fun compile(v: ExpressionIntermediate) = try {
        value(v.cacheType.cacheExpression(wrap(Evaluator.compile(v.expression, LibHelper.LIB, type)), v))
    } catch (ce: CompilationException) {
        val sb = StringBuilder("An error occurred during theme loading. See more info below.\n")
            .append("–––COMPILATION ERROR :\n")
            .append(ce.message).append('\n')
            .append("  ")
            .append(v.expression).append('\n')
        val column = ce.column // Column, where error was found
        for (i in 0 until column + 1) sb.append(' ')
        sb.append('^')
        /*val w = StringWriter()
        ce.printStackTrace(PrintWriter(w))
        sb.append('\n').append(w)*/
        val message = sb.toString()
        logger.error(message)
        AbstractThemeLoader.Reporter += message.substringAfterLast("–––COMPILATION ERROR :\n")
        null
    } catch (e: Exception) {
        val message = "An error occurred while compiling '${v.expression}'"
        logger.error(message, e)
        AbstractThemeLoader.Reporter += (e.message ?: "unknown error") + " in ${v.expression}"
        null
    }

    /**
     * The targeted class for this adapter's expression.

     * @return target
     */
    val type: Class<*>?

    fun value(c: CachedExpression<T>): CValue<T>

    /**
     * Wrap the expression using the appropriate [CompiledExpressionWrapper].

     * @param ce expression to wrap
     * *
     * @return wrapped expression
     */
    fun wrap(ce: CompiledExpression): CompiledExpressionWrapper<T>
}