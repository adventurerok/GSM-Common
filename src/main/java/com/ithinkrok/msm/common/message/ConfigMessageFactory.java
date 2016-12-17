package com.ithinkrok.msm.common.message;

/**
 * Created by paul on 17/12/16.
 */
public class ConfigMessageFactory {

    private final String base;

    public ConfigMessageFactory(String base) {
        this.base = base;
    }

    public ConfigMessageBuilder newBuilder() {
        return new ConfigMessageBuilder(base);
    }
}
