package com.ithinkrok.util.math.expression;

import java.math.MathContext;

public interface SimplifyMode {

    SimplifyMode NONE = input -> input;
    SimplifyMode DOUBLE = input -> input.isStatic() ? new NumberExpression(input.calculate(null)) : input;

    static SimplifyMode decimal(MathContext mc) {
        return input -> input.isStatic() ? new NumberExpression(input.calculateDecimal(null, mc)) : input;
    }

    Expression simplify(Expression input);

}
