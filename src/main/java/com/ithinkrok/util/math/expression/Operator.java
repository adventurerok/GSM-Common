package com.ithinkrok.util.math.expression;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Created by paul on 20/01/16.
 */
public class Operator {
    private final String name;
    private final Executor executor;
    private final DecimalExecutor decimalExecutor;
    private final boolean isFunction;
    private final boolean isDynamic;
    private final int maxArguments;
    private final int minArguments;
    private final int precedence;


    public Operator(String name,
                    Executor executor,
                    boolean isFunction, boolean isDynamic,
                    int precedence, int minArguments, int maxArguments) {

        this(name, executor, decimalWrapper(executor), isFunction, isDynamic, precedence, minArguments, maxArguments);
    }

    public Operator(String name,
                    Executor executor, DecimalExecutor decimalExecutor,
                    boolean isFunction, boolean isDynamic,
                    int precedence, int minArguments, int maxArguments) {

        this.name = name;
        this.executor = executor;
        this.decimalExecutor = decimalExecutor;
        this.isFunction = isFunction;
        this.isDynamic = isDynamic;
        this.maxArguments = maxArguments;
        this.minArguments = minArguments;
        this.precedence = precedence;
    }

    private static DecimalExecutor decimalWrapper(Executor executor) {
        return (mc, numbers) -> {
            double[] values = new double[numbers.length];

            for (int index = 0; index < numbers.length; ++index) {
                values[index] = numbers[index].doubleValue();
            }

            return BigDecimal.valueOf(executor.operate(values));
        };
    }

    public String getName() {
        return name;
    }

    public Executor getExecutor() {
        return executor;
    }

    public DecimalExecutor getDecimalExecutor() {
        return decimalExecutor;
    }

    public boolean isFunction() {
        return isFunction;
    }

    public boolean isDynamic() {
        return isDynamic;
    }

    public int getMaxArguments() {
        return maxArguments;
    }

    public int getMinArguments() {
        return minArguments;
    }

    public int getPrecedence() {
        return precedence;
    }

    /**
     * Created by paul on 20/01/16.
     */
    public interface Executor {
        double operate(double... numbers);
    }

    public interface DecimalExecutor {
        BigDecimal operate(MathContext mc, BigDecimal... numbers);
    }
}
