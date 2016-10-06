package com.ithinkrok.util.math;

import com.ithinkrok.util.math.Variables;

/**
 * Created by paul on 05/01/16.
 */
public class SingleValueVariables implements Variables {

    private final double value;

    public SingleValueVariables(double value) {
        this.value = value;
    }

    @Override
    public double getVariable(String name) {
        return value;
    }
}
