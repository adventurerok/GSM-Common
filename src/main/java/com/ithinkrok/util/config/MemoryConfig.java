package com.ithinkrok.util.config;

import org.apache.commons.lang.Validate;

import java.util.*;

/**
 * Created by paul on 08/02/16.
 */
public class MemoryConfig implements Config {

    private final Map<String, Object> values;
    private final char separator;

    public MemoryConfig() {
        this('.');
    }

    public MemoryConfig(char separator) {
        this(new LinkedHashMap<>(), separator);
    }

    @SuppressWarnings("unchecked")
    public MemoryConfig(Map<String, ?> values) {
        this(values, '.');
    }

    @SuppressWarnings("unchecked")
    public MemoryConfig(Map<String, ?> values, char separator) {
        this.separator = separator;
        this.values = new LinkedHashMap<>();

        for (Map.Entry<String, ?> entry : values.entrySet()) {
            //Correct maps to configs is now done in the set() method
            set(entry.getKey(), entry.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    private Object correctMapsToConfigs(Object obj) {
        //Replace maps with configs
        if (obj instanceof Map<?, ?>) {
            return new MemoryConfig((Map<String, Object>) obj, separator);
        } else if (obj instanceof Collection<?>) {
            List<Object> correctedList = new ArrayList<>();

            for (Object listItem : (Iterable<Object>) obj) {
                correctedList.add(correctMapsToConfigs(listItem));
            }

            return correctedList;
        }

        return obj;
    }

    @Override
    public Map<String, Object> getValues(boolean deep) {
        if (!deep) return new LinkedHashMap<>(values);

        Map<String, Object> result = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String path = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Config) {
                Map<String, Object> subMap = ((Config) value).getValues(true);

                for (Map.Entry<String, Object> subEntry : subMap.entrySet()) {
                    result.put(path + separator + subEntry.getKey(), subEntry.getValue());
                }
            }
        }

        return result;
    }

    @Override
    public boolean contains(String path) {
        Validate.notNull(path, "path cannot be null");

        //We do contain ourself
        if (path.isEmpty()) return true;

        int splitterIndex = path.indexOf(separator);

        if (splitterIndex != -1) {
            String configPath = path.substring(0, splitterIndex);

            Object subConfig = values.get(configPath);

            if (!(subConfig instanceof Config)) return false;

            return ((Config) subConfig).contains(path.substring(splitterIndex + 1));
        } else {
            return values.containsKey(path);
        }
    }

    @Override
    public void set(String path, Object value) {
        Validate.notEmpty(path, "Cannot set an empty path");

        int splitterIndex = path.indexOf(separator);

        if (splitterIndex != -1) {
            String configPath = path.substring(0, splitterIndex);

            Config subConfig = getOrCreateConfig(configPath);

            subConfig.set(path.substring(splitterIndex + 1), value);
        } else {
            values.put(path, correctMapsToConfigs(value));
        }
    }

    @Override
    public char getSeparator() {
        return separator;
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        if (!deep) return new HashSet<>(values.keySet());

        Set<String> result = new HashSet<>();

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String path = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Config) {
                Set<String> subKeys = ((Config) value).getKeys(true);

                for (String subKey : subKeys) {
                    result.add(path + separator + subKey);
                }
            } else {
                result.add(path);
            }
        }

        return result;
    }

    @Override
    public Object get(String path, Object def) {
        Validate.notNull(path, "path cannot be null");

        if (path.isEmpty()) return this;

        int splitterIndex = path.indexOf(separator);

        if (splitterIndex != -1) {
            String configPath = path.substring(0, splitterIndex);

            Object subConfig = values.get(configPath);

            //Instanceof does null check for us
            if (!(subConfig instanceof Config)) return def;

            return ((Config) subConfig).get(path.substring(splitterIndex + 1), def);
        } else {
            if (!values.containsKey(path)) return def;
            return values.get(path);
        }
    }

    @Override
    public Config getConfigOrEmpty(String path) {
        Config config = getConfigOrNull(path);

        return config != null ? config : new MemoryConfig();
    }

    private Config getOrCreateConfig(String name) {
        Object obj = values.get(name);

        if (obj == null) {
            obj = new MemoryConfig(separator);
            values.put(name, obj);
        } else if (!(obj instanceof Config)) {
            throw new RuntimeException("Object at path " + name + " is not a config");
        }

        return (Config) obj;
    }
}
