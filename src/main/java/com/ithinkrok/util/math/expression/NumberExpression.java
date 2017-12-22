package com.ithinkrok.util.math.expression;

import com.ithinkrok.util.math.Variables;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Created by paul on 03/01/16.
 */
public class NumberExpression implements Expression {

    private BigDecimal decimalVal;
    private final double doubleVal;


    public NumberExpression(String value) {
        this(new BigDecimal(value));
    }

    public NumberExpression(BigDecimal value) {
        this.decimalVal = value;
        this.doubleVal = this.decimalVal.doubleValue();
    }

    public NumberExpression(double value) {
        this.doubleVal = value;
        this.decimalVal = null;
    }

    @Override
    public double calculate(Variables variables) {
        return doubleVal;
    }

    @Override
    public BigDecimal calculateDecimal(Variables variables, MathContext mc) {
        if(decimalVal == null) { //lazy evaluate for cases when BigDecimal mode is not used
            decimalVal = BigDecimal.valueOf(doubleVal);
        }

        return decimalVal;
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NumberExpression that = (NumberExpression) o;

        return decimalVal.equals(that.decimalVal);
    }

    @Override
    public int hashCode() {
        return decimalVal.hashCode();
    }

    @Override
    public String toString() {
        return decimalVal.toString();
    }
}
