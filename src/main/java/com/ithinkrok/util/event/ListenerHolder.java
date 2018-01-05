package com.ithinkrok.util.event;

public interface ListenerHolder {

    void addListener(String name, CustomListener listener);

    void removeListener(String name);



}
