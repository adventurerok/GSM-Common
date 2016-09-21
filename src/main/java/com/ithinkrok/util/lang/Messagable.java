package com.ithinkrok.util.lang;

/**
 * Created by paul on 02/01/16.
 */
public interface Messagable {

    default void sendMessage(String message){
        sendMessageNoPrefix(message);
    }

    void sendMessageNoPrefix(String message);

    default void sendLocale(String locale, Object... args){
        sendLocaleNoPrefix(locale, args);
    }

    default void sendLocaleNoPrefix(String locale, Object... args){
        sendMessageNoPrefix(getLanguageLookup().getLocale(locale, args));
    }

    LanguageLookup getLanguageLookup();
}
