package com.ithinkrok.util.config;

public interface ConfigSerializer<T> {

    Config serialize(T t);

}
