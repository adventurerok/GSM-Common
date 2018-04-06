package com.ithinkrok.util.config;

public class TrackingConfig extends WrapperConfig {


    private int accessCount = 0;
    private int modificationCount = 0;
    private TrackingConfig parent;

    public TrackingConfig(Config wrappedConfig) {
        super(wrappedConfig);
    }

    private TrackingConfig(Config wrapped, TrackingConfig parent) {
        super(wrapped);

        this.parent = parent;
    }


    @Override
    public Object get(String path, Object def) {
        incAccessCount();
        return super.get(path, def);
    }


    private void incAccessCount() {
        ++accessCount;
        if(parent != null) {
            parent.incAccessCount();
        }
    }


    @Override
    public Config set(String path, Object value) {
        incModificationCount();
        return super.set(path, value);
    }


    private void incModificationCount() {
        ++modificationCount;
        if(parent != null) {
            parent.incModificationCount();
        }
    }


    @Override
    public Config getConfigOrEmpty(String path) {
        return new TrackingConfig(super.getConfigOrEmpty(path), this);
    }


    @Override
    public Config getConfigOrNull(String path) {
        Config config = super.getConfigOrNull(path);
        if(config != null) {
            return new TrackingConfig(config, this);
        } else return null;
    }


    public int getAccessCount() {
        return accessCount;
    }


    public int getModificationCount() {
        return modificationCount;
    }

    public void resetCounts() {
        accessCount = modificationCount = 0;
    }
}
