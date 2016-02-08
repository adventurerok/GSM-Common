package com.ithinkrok.util.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by paul on 08/02/16.
 */
public interface Config {

    Set<String> getKeys(boolean deep);

    Map<String, Object> getValues(boolean deep);

    boolean contains(String path);

    Object get(String path, Object def);

    default Object get(String path){
        return get(path, null);
    }

    void set(String path, Object value);

    default void setAll(Map<String, Object> values){
        for(String path : values.keySet()) {
            set(path, values.get(path));
        }
    }

    default void setAll(Config values) {
        for(String path : values.getKeys(true)) {
            set(path, values.get(path));
        }
    }

    default String getString(String path, String def){
        Object obj = get(path);

        if(obj != null) return obj.toString();
        else return def;
    }

    default String getString(String path) {
        return getString(path, null);
    }

    default boolean isString(String path){
        return get(path) instanceof String;
    }

    default int getInt(String path, int def){
        Object obj = get(path);

        if(obj instanceof Number) return ((Number) obj).intValue();
        else return def;
    }

    default int getInt(String path) {
        return getInt(path, 0);
    }

    default boolean isInt(String path){
        return get(path) instanceof Integer;
    }

    default boolean getBoolean(String path, boolean def){
        Object obj = get(path);

        if(obj instanceof Boolean) return (Boolean) obj;
        else return def;
    }

    default boolean getBoolean(String path) {
        return getBoolean(path, false);
    }

    default boolean isBoolean(String path){
        return get(path) instanceof Boolean;
    }

    default double getDouble(String path, double def){
        Object obj = get(path);

        if(obj instanceof Number) return ((Number) obj).doubleValue();
        else return def;
    }

    default double getDouble(String path) {
        return getDouble(path, 0.0d);
    }

    boolean isDouble(String path);

    default long getLong(String path, long def){
        Object obj = get(path);

        if(obj instanceof Number) return ((Number) obj).longValue();
        else return def;
    }

    //TODO finish

    default long getLong(String path) {
        return getLong(path, 0L);
    }

    boolean isLong(String path);

    byte[] getByteArray(String path, byte[] def);

    default byte[] getByteArray(String path) {
        return getByteArray(path, new byte[0]);
    }

    boolean isByteArray(String path);

    <T> List<T> getList(String path, List<T> def);

    default <T> List<T> getList(String path) {
        return getList(path, new ArrayList<>());
    }

    boolean isList(String path);

    /**
     *
     * @param path The path to check for a list
     * @param type The type that the list should contain
     * @return If there is a list at this path and it contains an object with the type {@code type}
     */
    boolean isList(String path, Class<?> type);

    Config getConfig(String path);

    boolean isConfig(String path);
}
