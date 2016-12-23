package com.ithinkrok.util.math;

import com.ithinkrok.util.config.Config;
import com.ithinkrok.util.math.Variables;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by paul on 04/01/16.
 */
public class MapVariables implements Variables {

    private final Map<String, Double> variables;

    public MapVariables() {
        this(new HashMap<>());
    }

    public MapVariables(Config config) {
        this(new HashMap<>());

        for(String name : config.getKeys(true)) {
            setVariable(name, config.getDouble(name));
        }
    }

    public MapVariables(MapVariables copy) {
        this(new HashMap<>());

        variables.putAll(copy.variables);
    }

    public MapVariables(Map<String, Double> variables) {
        this.variables = variables;
    }

    public void setVariable(String name, double value) {
        variables.put(name, value);
    }

    @Override
    public double getVariable(String name) {
        Double d = variables.get(name);

        return d == null ? 0 : d;
    }
}
