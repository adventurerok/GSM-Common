package com.ithinkrok.util.config;

public interface ConfigDeserializer<T> {

    T deserialize(Config config);

}
