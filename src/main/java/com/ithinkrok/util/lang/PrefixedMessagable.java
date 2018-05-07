package com.ithinkrok.util.lang;

import com.ithinkrok.util.config.Config;

/**
 * Created by paul on 02/01/16.
 */
public interface PrefixedMessagable {

    default void sendMessage(String message){
        sendMessageNoPrefix(getMessagePrefix() + message);
    }

    void sendMessageNoPrefix(String message);

    void sendMessageNoPrefix(Config message);

    default void sendMessageNoPrefix(Object message){
        if(message instanceof Config) {
            sendMessageNoPrefix((Config)message);
        } else {
            if(message == null) sendMessageNoPrefix((String)null);
            else sendMessageNoPrefix(message.toString());
        }
    }

    String getMessagePrefix();

    default void sendLocale(String locale, Object... args){
        sendLocaleNoPrefix(locale, args);
    }

    default void sendLocaleNoPrefix(String locale, Object... args){
        sendMessageNoPrefix(getLanguageLookup().getLocale(locale, args));
    }

    LanguageLookup getLanguageLookup();
}
