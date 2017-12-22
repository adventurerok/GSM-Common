package com.ithinkrok.util.math;

import java.math.BigDecimal;

/**
 * Created by paul on 03/01/16.
 */
public interface Variables {

    /**
     * Gets the value of the variable specified by name, or a default value if none is found.
     * The default value may be implementation specific but is usually zero.
     *
     * @param name The name of the variable to get
     * @return The variable's value or a default value
     */
    double getVariable(String name);

    /**
     * Gets the value of the variable specified by name as a BigDecimal, or a default value if none is found.
     * The default value may be implementation specific but is usually zero.
     *
     * This method should never return null.
     *
     * @param name The name of the variable to get
     * @return The variable's value or a default value
     */
    default BigDecimal getDecimalVariable(String name) {
        return BigDecimal.valueOf(getVariable(name));
    }

}
