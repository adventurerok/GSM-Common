package com.ithinkrok.util.math;

import java.math.BigDecimal;

/**
 * Created by paul on 03/01/16.
 *
 * Calculates a value based on variable input
 */
public interface Calculator {

    /**
     * Calculates a double value given the input variables
     *
     * @param variables The variables object to use for variables lookup
     * @return The result of the calculation
     */
    double calculate(Variables variables);

    /**
     * Calculates a boolean value given the input variables.
     *
     * By default, this is true if the result of {@link #calculate(Variables)} cast to an int is not zero.
     *
     * @param variables The variables object to use for variables lookup
     * @return The boolean result of the calculation
     */
    default boolean calculateBoolean(Variables variables) {
        return (int)(Math.floor(calculate(variables))) != 0;
    }

    /**
     * Calculates a BigDecimal value for the given input variables.
     *
     * By default, just wraps {@link #calculate(Variables)} using {@link BigDecimal#valueOf(double)}.
     *
     * @param variables The variables object to use for variables lookup
     * @return The BigDecimal result of the calculation
     */
    default BigDecimal calculateDecimal(Variables variables) {
        return BigDecimal.valueOf(calculate(variables));
    }

}
