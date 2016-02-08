package com.ithinkrok.util.config;

import org.apache.commons.lang.Validate;

import java.util.*;

/**
 * Created by paul on 08/02/16.
 */
public class MemoryConfig implements Config {

    private final Map<String, Object> values;

    public MemoryConfig() {
        this(new HashMap<>());
    }

    public MemoryConfig(Map<String, Object> values) {
        this.values = values;
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        if(!deep) return new HashSet<>(values.keySet());

        Set<String> result = new HashSet<>();

        for(Map.Entry<String, Object> entry : values.entrySet()) {
            String path = entry.getKey();
            Object value = entry.getValue();

            if(value instanceof Config) {
                Set<String> subKeys = ((Config) value).getKeys(true);

                for(String subKey : subKeys) {
                    result.add(path + "." + subKey);
                }
            } else {
                result.add(path);
            }
        }

        return result;
    }

    @Override
    public Map<String, Object> getValues(boolean deep) {
        if(!deep) return new HashMap<>(values);

        Map<String, Object> result = new HashMap<>();

        for(Map.Entry<String, Object> entry : values.entrySet()) {
            String path = entry.getKey();
            Object value = entry.getValue();

            if(value instanceof Config) {
                Map<String, Object> subMap = ((Config) value).getValues(true);

                for(Map.Entry<String, Object> subEntry : subMap.entrySet()) {
                    result.put(path + "." + subEntry.getKey(), subEntry.getValue());
                }
            }
        }

        return result;
    }

    @Override
    public boolean contains(String path) {
        Validate.notNull(path, "path cannot be null");

        //We do contain ourself
        if(path.isEmpty()) return true;

        int splitterIndex = path.indexOf('.');

        if(splitterIndex != -1) {
            String configPath = path.substring(0, splitterIndex);

            Object subConfig = values.get(configPath);

            if(!(subConfig instanceof Config)) return false;

            return ((Config) subConfig).contains(path.substring(splitterIndex + 1));
        } else {
            return values.containsKey(path);
        }
    }

    @Override
    public Object get(String path, Object def) {
        Validate.notNull(path, "path cannot be null");

        if(path.isEmpty()) return this;

        int splitterIndex = path.indexOf('.');

        if(splitterIndex != -1) {
            String configPath = path.substring(0, splitterIndex);

            Object subConfig = values.get(configPath);

            //Instanceof does null check for us
            if(!(subConfig instanceof Config)) return def;

            return ((Config) subConfig).get(path.substring(splitterIndex + 1), def);
        } else {
            if(!values.containsKey(path)) return def;
            return values.get(path);
        }
    }

    @Override
    public void set(String path, Object value) {
        Validate.notEmpty(path, "Cannot set an empty path");

        int splitterIndex = path.indexOf('.');

        if(splitterIndex != -1) {
            String configPath = path.substring(0, splitterIndex);

            Config subConfig = getOrCreateConfig(configPath);

            subConfig.set(path.substring(splitterIndex + 1), value);
        } else {
            values.put(path, value);
        }
    }

    private Config getOrCreateConfig(String name) {
        Object obj = values.get(name);

        if(obj == null) {
            obj = new MemoryConfig();
            values.put(name, obj);
        } else if(!(obj instanceof Config)) {
            throw new RuntimeException("Object at path " + name + " is not a config");
        }

        return (Config) obj;
    }
}
