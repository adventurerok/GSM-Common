package com.ithinkrok.util.math;

import java.math.BigDecimal;

public interface MutableVariables extends Variables {

    /**
     * Sets the value of the variable specified by name
     *
     * @param name The name of the variable to set
     * @param value The value to set the variable to
     */
    void setVariable(String name, double value);

    /**
     * Sets the value of the variable specified by name. By default converts the BigDecimal to a double and calls
     * {@link #setVariable(String, double)}
     *
     * @param name The name of the variable to set
     * @param value The value to set the variable to
     */
    default void setVariable(String name, BigDecimal value) {
        setVariable(name, value.doubleValue());
    }

    MutableVariables copyVariables();
}
