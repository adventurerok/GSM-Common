package com.ithinkrok.util.math.expression;

import com.ithinkrok.util.math.Variables;

import java.math.MathContext;

public interface SimplifyMode {

    /**
     * Do not simplify Expressions. Simply returns the input expression as output.
     */
    SimplifyMode NONE = input -> input;

    /**
     * Simplify Expressions that are static into {@link NumberExpression} using {@link Expression#calculate(Variables)}.
     * Therefore, only double precision will be retained.
     */
    SimplifyMode DOUBLE = input -> input.isStatic() ? new NumberExpression(input.calculate(null)) : input;

    /**
     * Simplify Expressions that are static into {@link NumberExpression} using {@link Expression#calculateDecimal(Variables, MathContext)}
     *
     * @param mc The {@link MathContext} to use when calculating the value. Sets the precision of the BigDecimal used.
     *           Using {@link MathContext#UNLIMITED} can result in {@link ArithmeticException} if the decimal form
     *           of the number would go on for ever.
     * @return The specified SimplifyMode.
     */
    static SimplifyMode decimal(MathContext mc) {
        return input -> input.isStatic() ? new NumberExpression(input.calculateDecimal(null, mc)) : input;
    }


    /**
     * Simplifies Expressions. In most use cases, {@link Expression#isStatic()} should be checked to make sure
     * the input Expression is static (constant) and thus able to be simplified before simplification is done.
     *
     * @param input The {@link Expression} to simplify
     * @return The simplified Expression.
     */
    Expression simplify(Expression input);

}
