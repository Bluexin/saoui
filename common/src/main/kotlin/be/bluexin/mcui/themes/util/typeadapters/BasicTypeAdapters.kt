package be.bluexin.mcui.themes.util.typeadapters

import be.bluexin.mcui.themes.AbstractThemeLoader
import be.bluexin.mcui.themes.util.*
import gnu.jel.CompilationException
import gnu.jel.CompiledExpression
import gnu.jel.Evaluator
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

sealed class BasicExpressionAdapter<CValueType: CValue<T>, T : Any> {
    private val logger: Logger by lazy { LogManager.getLogger(javaClass) }

    fun compile(v: ExpressionIntermediate): CValueType = try {
//        Constants.LOG.debug("Compiling ${v.expression}")
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
        default
    } catch (e: Exception) {
        val message = "An error occurred while compiling '${v.expression}'"
        logger.error(message, e)
        AbstractThemeLoader.Reporter += (e.message ?: "unknown error") + " in ${v.expression}"
        default
    }

    /**
     * The targeted class for this adapter's expression.

     * @return target
     */
    abstract val type: Class<*>?

    abstract fun value(c: CachedExpression<T>): CValueType

    /**
     * Wrap the expression using the appropriate [CompiledExpressionWrapper].

     * @param ce expression to wrap
     * *
     * @return wrapped expression
     */
    abstract fun wrap(ce: CompiledExpression): CompiledExpressionWrapper<T>

    abstract val jelType: JelType

    abstract val default: CValueType
}

/**
 * Adapts an expression that should return a int.
 */
object IntExpressionAdapter : BasicExpressionAdapter<CInt, Int>() {
    override fun value(c: CachedExpression<Int>) = CInt(c)

    override val type: Class<*> = Integer.TYPE

    override fun wrap(ce: CompiledExpression) = IntExpressionWrapper(ce)

    override val jelType: JelType by lazy { JelType.INT }

    override val default get() = CInt.ZERO
}

/**
 * Adapts an expression that should return a double.
 */
object DoubleExpressionAdapter : BasicExpressionAdapter<CDouble, Double>() {
    override fun value(c: CachedExpression<Double>) = CDouble(c)

    override val type: Class<*> = java.lang.Double.TYPE

    override fun wrap(ce: CompiledExpression) = DoubleExpressionWrapper(ce)

    override val jelType: JelType by lazy { JelType.DOUBLE }

    override val default get() = CDouble.ZERO
}

/**
 * Adapts an expression that should return a String.
 */
object StringExpressionAdapter : BasicExpressionAdapter<CString, String>() {
    override fun value(c: CachedExpression<String>) = CString(c)

    override val type: Class<*> = String::class.java

    override fun wrap(ce: CompiledExpression) = StringExpressionWrapper(ce)

    override val jelType: JelType by lazy { JelType.STRING }

    override val default get() = CString.EMPTY
}

/**
 * Adapts an expression that should return a boolean.
 */
object BooleanExpressionAdapter : BasicExpressionAdapter<CBoolean, Boolean>() {
    override fun value(c: CachedExpression<Boolean>) = CBoolean(c)

    override val type: Class<*> = java.lang.Boolean.TYPE

    override fun wrap(ce: CompiledExpression) = BooleanExpressionWrapper(ce)

    override val jelType: JelType by lazy { JelType.BOOLEAN }

    override val default get() = CBoolean.FALSE
}

/**
 * Adapts an expression that should return [Unit] (aka void).
 */
object UnitExpressionAdapter : BasicExpressionAdapter<CUnit, Unit>() {
    override val type: Class<*>? = null

    override fun value(c: CachedExpression<Unit>) = CUnit(c)

    override fun wrap(ce: CompiledExpression) = UnitExpressionWrapper(ce)

    override val jelType: JelType by lazy { JelType.UNIT }

    override val default get() = CUnit.UNIT
}
