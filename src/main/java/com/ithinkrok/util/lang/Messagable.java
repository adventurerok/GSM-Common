package com.ithinkrok.util.lang;

import com.ithinkrok.util.config.Config;

public interface Messagable {


    void sendMessage(String message);

    void sendMessage(Config message);

    default void sendMessage(Object message){
        if(message instanceof Config) {
            sendMessage((Config)message);
        } else {
            if(message == null) sendMessage((String)null);
            else sendMessage(message.toString());
        }
    }

    void sendLocale(String locale, Object... args);

    LanguageLookup getLanguageLookup();
}
