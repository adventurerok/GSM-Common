package com.ithinkrok.util.lang;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by paul on 20/11/15.
 *
 * Handles a language properties file
 */
public class LangFile implements LanguageLookup {

    private final Map<Object, String> languageStrings = new HashMap<>();

    public LangFile(Path in) throws IOException {
        try (InputStream stream = Files.newInputStream(in)) {
            addFromProperties(loadProperties(stream));
        }
    }

    public LangFile(InputStream in) throws IOException {
        addFromProperties(loadProperties(in));
    }

    private static Properties loadProperties(InputStream in) throws IOException {
        Properties properties = new Properties();
        properties.load(in);

        return properties;
    }

    public LangFile(Properties properties) {
        addFromProperties(properties);
    }

    private void addFromProperties(Properties properties) {
        for(Object key : properties.keySet()){
            String value = properties.getProperty(key.toString());
            value = value.replace('&', '§');
            languageStrings.put(key, value);
        }
    }

    @Override
    public String getLocale(String locale){
        String result = languageStrings.get(locale);

        if(result == null){
            try{
                //print the stack trace
                throw new RuntimeException("Missing language string for: " + locale);
            } catch(RuntimeException e){
                e.printStackTrace();
            }

            languageStrings.put(locale, locale);
            return locale;
        }

        return result;
    }

    @Override
    public String getLocale(String locale, Object...args){
        return String.format(getLocale(locale), args);
    }

    @Override
    public boolean hasLocale(String name) {
        String result = languageStrings.get(name);
        return result != null && !result.equals(name);
    }
}
