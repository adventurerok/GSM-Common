package com.ithinkrok.util.config;

import java.util.*;

/**
 * Created by paul on 11/03/16.
 * <p>
 * A config which is made of multiple subconfigs.
 * Useful for defaults.
 */
public class MultiConfig implements Config {

    //The lower the configs index in the list = the higher priority
    private final List<Config> subConfigs;

    private final char separator;

    public MultiConfig() {
        this(new ArrayList<>(), '.');
    }

    public MultiConfig(List<Config> subConfigs, char separator) {
        this.subConfigs = subConfigs;
        this.separator = separator;
    }

    public MultiConfig(List<Config> subConfigs) {
        this(subConfigs, getDefaultSeparator(subConfigs));
    }

    private static char getDefaultSeparator(List<Config> subConfigs) {
        if (subConfigs.isEmpty()) return '.';
        else return subConfigs.get(0).getSeparator();
    }

    public MultiConfig(char separator) {
        this(new ArrayList<>(), separator);
    }

    public List<Config> getSubConfigs() {
        return subConfigs;
    }

    @Override
    public Map<String, Object> getValues(boolean deep) {
        Map<String, Object> result = new HashMap<>();

        for (int index = subConfigs.size() - 1; index >= 0; --index) {
            result.putAll(subConfigs.get(index).getValues(deep));
        }

        return result;
    }

    @Override
    public boolean contains(String path) {
        for (Config subConfig : subConfigs) {
            if (subConfig.contains(path)) return true;
        }

        return false;
    }

    @Override
    public Config set(String path, Object value) {
        if (subConfigs.isEmpty()) {
            subConfigs.add(new MemoryConfig(separator));
        }

        subConfigs.get(0).set(path, value);
        return this;
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        Set<String> keys = new HashSet<>();

        for (Config subConfig : subConfigs) {
            keys.addAll(subConfig.getKeys(deep));
        }

        return keys;
    }

    @Override
    public Object get(String path, Object def) {
        for (Config subConfig : subConfigs) {
            if(!subConfig.contains(path)) continue;

            return subConfig.get(path, def);
        }

        return def;
    }

    @Override
    public char getSeparator() {
        return separator;
    }

    @Override
    public Config getConfigOrEmpty(String path) {
        Config config = getConfigOrNull(path);

        return config != null ? config : new MemoryConfig();
    }
}
