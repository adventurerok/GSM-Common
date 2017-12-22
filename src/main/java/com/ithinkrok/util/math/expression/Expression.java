package com.ithinkrok.util.math.expression;

import com.ithinkrok.util.math.Calculator;
import com.ithinkrok.util.math.Variables;

/**
 * Created by paul on 03/01/16.
 */
public interface Expression extends Calculator{

    /**
     *
     * @return If {@link #calculate(Variables)} will always return the same result regardless of the variables
     */
    boolean isStatic();
}
