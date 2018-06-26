package com.ithinkrok.util.command;

import org.apache.commons.lang.ArrayUtils;

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
    private final List<String> defaultArgs;

    public CustomCommand(String commandName, Map<String, Object> params, List<String> defaultArgs) {
        this.command = commandName;
        this.params = params;
        this.defaultArgs = defaultArgs;
    }

    public String parametersToString(String...ignoreParams) {
        StringBuilder result = new StringBuilder();
        boolean appendSpace = false;


        for(Map.Entry<String, Object> param : params.entrySet()) {
            if(param.getKey().equals("default")) continue;
            if(ArrayUtils.contains(ignoreParams, param.getKey())) continue;

            if(!appendSpace) appendSpace = true;
            else result.append(' ');

            result.append('-').append(param.getKey());
            result.append(' ').append(param.getValue());
        }

        return result.toString();
    }

    public CustomCommand(String fullCommand) {
        List<String> args = CommandUtils.splitStringIntoArguments(fullCommand);

        command = args.remove(0).toLowerCase();

        params = CommandUtils.parseArgumentListToMap(args);

        //noinspection unchecked
        defaultArgs = (List<String>) params.get("default");
    }

    public Map<String, Object> getParameters() {
        return params;
    }

    public List<String> getArgs() {
        return defaultArgs;
    }

    public int getArgumentCount() {
        return defaultArgs.size();
    }

    public double getDoubleArg(int index, double def) {
        try {
            return Double.parseDouble(defaultArgs.get(index));
        } catch (Exception ignored) {
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
            return Integer.parseInt(defaultArgs.get(index));
        } catch (Exception ignored) {
            return def;
        }
    }

    public long getLongArg(int index, long def) {
        try {
            return Long.parseLong(defaultArgs.get(index));
        } catch(Exception ignored) {
            return def;
        }
    }

    public String getRemainingArgsAsString(int index) {
        StringBuilder result = new StringBuilder();

        boolean addSpace = false;

        for(int count = index; count < defaultArgs.size(); ++count) {

            if(!addSpace) addSpace = true;
            else result.append(' ');

            result.append(String.valueOf(defaultArgs.get(count)));
        }

        return result.toString();
    }

    public String getRemainingArgsAndParamsAsString(int index, String...ignoreParams) {
        StringBuilder result = new StringBuilder(getRemainingArgsAsString(index));

        String params = parametersToString(ignoreParams);

        if(result.length() != 0 && !params.isEmpty()) result.append(' ');

        result.append(params);

        return result.toString();
    }

    public boolean hasIntArg(int index) {
        if(!hasArg(index)) return false;

        try{
            //noinspection ResultOfMethodCallIgnored
            Integer.parseInt(defaultArgs.get(index));
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    public boolean hasDoubleArg(int index) {
        if(!hasArg(index)) return false;

        try{
            //noinspection ResultOfMethodCallIgnored
            Double.parseDouble(defaultArgs.get(index));
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    public boolean hasArg(int index) {
        return defaultArgs.size() > index;
    }

    public boolean getBooleanArg(int index, boolean def) {
        if(!hasArg(index)) return def;

        switch(defaultArgs.get(index).toLowerCase()) {
            case "true":
            case "yes":
                return true;
            case "false":
            case "no":
                return false;
            default:
                return def;
        }
    }


    public boolean hasBooleanArg(int index) {
        if(!hasArg(index)) return false;

        switch(defaultArgs.get(index)) {
            case "true":
            case "yes":
            case "false":
            case "no":
                return true;
            default:
                return false;
        }
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

        List<String> newArgs = new ArrayList<>();

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
        } catch (Exception ignored) {
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
