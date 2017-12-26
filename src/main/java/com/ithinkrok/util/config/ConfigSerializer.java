package com.ithinkrok.util.config;

public interface ConfigSerializer<T> {

    T deserialize(Config config);

    Config serialize(T t);

}
