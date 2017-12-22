package com.ithinkrok.util.math;

import java.math.BigDecimal;

/**
 * Created by paul on 05/01/16.
 */
public class SingleValueVariables implements Variables {

    private final double value;

    private BigDecimal decimal;

    public SingleValueVariables(double value) {
        this.value = value;
    }

    public SingleValueVariables(BigDecimal decimal) {
        if(decimal == null) {
            throw new NullPointerException("decimal cannot be null");
        }

        this.decimal = decimal;
        this.value = decimal.doubleValue();
    }

    @Override
    public double getVariable(String name) {
        return value;
    }

    @Override
    public BigDecimal getDecimalVariable(String name) {
        if(decimal == null) {
            decimal = BigDecimal.valueOf(value);
        }

        return decimal;
    }
}
