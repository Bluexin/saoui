package com.saomc.saoui.themes.util;

import com.saomc.saoui.util.LogCore;
import gnu.jel.CompilationException;
import gnu.jel.CompiledExpression;
import gnu.jel.Evaluator;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Part of saoui by Bluexin.
 *
 * @author Bluexin
 */
public abstract class ExpressionAdapter<T extends CompiledExpressionWrapper> extends XmlAdapter<String, T> {

    @Override
    public T unmarshal(String v) throws Exception {
        try {
            return wrap(Evaluator.compile(v, LibHelper.lib, getType()));
        } catch (CompilationException ce) {
            StringBuilder sb = new StringBuilder("An error occurred during theme loading. See more info below.\n")
                    .append("–––COMPILATION ERROR :\n")
                    .append(ce.getMessage()).append('\n')
                    .append("                       ")
                    .append(v);
            int column = ce.getColumn(); // Column, where error was found
            for (int i = 0; i < column + 23 - 1; i++) sb.append(' ');
            sb.append('\n').append('^');
            LogCore.logWarn(sb.toString());
        }

        return null;
    }

    @Override
    public String marshal(T v) throws Exception {
        throw new UnsupportedOperationException("marshalling xml expressions isn't allowed just yet.");
    } // TODO: see Evaluator.compileBits(...)

    /**
     * The targeted class for this adapter's expression.
     *
     * @return target
     */
    protected abstract Class getType();

    /**
     * Wrap the expression using the appropriate {@link CompiledExpressionWrapper}.
     *
     * @param ce expression to wrap
     * @return wrapped expression
     */
    protected abstract T wrap(CompiledExpression ce);

    /**
     * Adapts an expression that should return a int.
     */
    public static class IntExpressionAdapter extends ExpressionAdapter<IntExpressionWrapper> {
        @Override
        protected Class getType() {
            return Integer.TYPE;
        }

        @Override
        protected IntExpressionWrapper wrap(CompiledExpression ce) {
            return new IntExpressionWrapper(ce);
        }
    }

    /**
     * Adapts an expression that should return a double.
     */
    public static class DoubleExpressionAdapter extends ExpressionAdapter<DoubleExpressionWrapper> {
        @Override
        protected Class getType() {
            return Double.TYPE;
        }

        @Override
        protected DoubleExpressionWrapper wrap(CompiledExpression ce) {
            return new DoubleExpressionWrapper(ce);
        }
    }

    /**
     * Adapts an expression that should return a String.
     */
    public static class StringExpressionAdapter extends ExpressionAdapter<StringExpressionWrapper> {
        @Override
        protected Class getType() {
            return String.class;
        }

        @Override
        protected StringExpressionWrapper wrap(CompiledExpression ce) {
            return new StringExpressionWrapper(ce);
        }
    }
}
