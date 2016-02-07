package com.ithinkrok.util.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by paul on 07/02/16.
 */
public class CustomCommand {

    private final String command;
    private final Map<String, Object> params;
    private final List<Object> defaultArgs;

    public CustomCommand(String commandName, Map<String, Object> params, List<Object> defaultArgs) {
        this.command = commandName;
        this.params = params;
        this.defaultArgs = defaultArgs;
    }

    public Map<String, Object> getParameters() {
        return params;
    }

    public List<Object> getArgs() {
        return defaultArgs;
    }

    public int getArgumentCount() {
        return defaultArgs.size();
    }

    public double getDoubleArg(int index, double def) {
        try {
            return ((Number) defaultArgs.get(index)).doubleValue();
        } catch (Exception e) {
            return def;
        }
    }

    public Object getArg(int index) {
        return getArg(index, null);
    }

    public Object getArg(int index, Object def) {
        if(index >= defaultArgs.size()) return def;

        return defaultArgs.get(index);
    }

    public int getIntArg(int index, int def) {
        try {
            return ((Number) defaultArgs.get(index)).intValue();
        } catch (Exception ignored) {
            return def;
        }
    }

    public boolean hasIntArg(int index) {
        if (!hasDoubleArg(index)) return false;

        Number num = (Number) defaultArgs.get(index);
        return num.intValue() == num.doubleValue();
    }

    public boolean hasDoubleArg(int index) {
        return hasArg(index) && defaultArgs.get(index) instanceof Number;
    }

    public boolean hasArg(int index) {
        return defaultArgs.size() > index;
    }

    public boolean getBooleanArg(int index, boolean def) {
        try {
            return ((Boolean) defaultArgs.get(index));
        } catch (Exception e) {
            return def;
        }
    }


    public boolean hasBooleanArg(int index) {
        return hasArg(index) && defaultArgs.get(index) instanceof Boolean;
    }

    public boolean hasParameter(String name) {
        return params.containsKey(name);
    }

    public double getDoubleParam(String name, double def) {
        try {
            return ((Number) params.get(name)).doubleValue();
        } catch (Exception ignored) {
            return def;
        }
    }

    public int getIntParam(String name, int def) {
        try {
            return ((Number) params.get(name)).intValue();
        } catch (Exception ignored) {
            return def;
        }
    }

    public CustomCommand subCommand() {
        if (defaultArgs.size() < 1) return null;

        List<Object> newArgs = new ArrayList<>();

        for (int index = 1; index < defaultArgs.size(); ++index) newArgs.add(defaultArgs.get(index));

        Map<String, Object> newParams = new HashMap<>(params);

        return new CustomCommand(getStringArg(0, null), newParams, newArgs);
    }

    public String getStringArg(int index, String def) {
        if (index >= defaultArgs.size()) return def;
        Object o = defaultArgs.get(index);

        return o != null ? o.toString() : def;
    }

    public boolean getBooleanParam(String name, boolean def) {
        try {
            return (Boolean) params.get(name);
        } catch (Exception e) {
            return def;
        }
    }

    public String getStringParam(String name, String def) {
        Object o = params.get(name);
        return o != null ? o.toString() : def;
    }

    public String getCommand() {
        return command;
    }
}
