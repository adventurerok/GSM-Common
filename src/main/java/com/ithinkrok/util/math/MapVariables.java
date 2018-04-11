package com.ithinkrok.util.math;

import com.ithinkrok.util.config.Config;
import com.ithinkrok.util.math.Variables;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by paul on 04/01/16.
 */
public class MapVariables implements MutableVariables {

    private Map<String, Number> variables;

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

    public MapVariables(Map<String, ? extends Number> variables) {
        this.variables = new HashMap<>(variables);
    }

    public void setVariable(String name, double value) {
        variables.put(name, value);
    }

    public void setVariable(String name, Number value) {
        if(value == null) {
            throw new NullPointerException("value cannot be null");
        }

        variables.put(name, value);
    }


    @Override
    public void setVariable(String name, BigDecimal value) {
        setVariable(name, (Number) value);
    }


    @Override
    public MutableVariables clone() {
        try {
            MapVariables clone = (MapVariables) super.clone();
            clone.variables = new HashMap<>(variables);
            return clone;
        } catch (CloneNotSupportedException ignored) {
            throw new RuntimeException("We do support clone");
        }
    }


    @Override
    public double getVariable(String name) {
        Number num = variables.get(name);

        return num == null ? 0 : num.doubleValue();
    }

    @Override
    public BigDecimal getDecimalVariable(String name) {
        Number num = variables.get(name);

        if(num == null) {
            return BigDecimal.ZERO;
        } else if (num instanceof BigDecimal) {
            return (BigDecimal) num;
        } else if(num instanceof BigInteger) {
            return new BigDecimal((BigInteger) num);
        } else return BigDecimal.valueOf(num.doubleValue());
    }
}
