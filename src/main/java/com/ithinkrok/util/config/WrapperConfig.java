package com.ithinkrok.util.config;

import java.util.Map;
import java.util.Set;

/**
 * Created by paul on 10/02/16.
 *
 * Intended to be overridden to provide extra config functionality
 */
public class WrapperConfig implements Config {

    protected final Config wrappedConfig;

    public WrapperConfig(Config wrappedConfig) {

        //Ensure we do not have multiple nested WrapperConfigs
        if(wrappedConfig.getClass().equals(getClass())) {
            this.wrappedConfig = ((WrapperConfig) wrappedConfig).getWrappedConfig();
        } else {
            this.wrappedConfig = wrappedConfig;
        }
    }

    public Config getWrappedConfig() {
        return wrappedConfig;
    }

    @Override
    public Map<String, Object> getValues(boolean deep) {
        return wrappedConfig.getValues(deep);
    }

    @Override
    public boolean contains(String path) {
        return wrappedConfig.contains(path);
    }

    @Override
    public Config set(String path, Object value) {
        wrappedConfig.set(path, value);

        return this;
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return wrappedConfig.getKeys(deep);
    }

    @Override
    public Object get(String path, Object def) {
        return wrappedConfig.get(path, def);
    }

    @Override
    public char getSeparator() {
        return wrappedConfig.getSeparator();
    }

    @Override
    public Config getConfigOrEmpty(String path) {
        return wrappedConfig.getConfigOrEmpty(path);
    }
}
