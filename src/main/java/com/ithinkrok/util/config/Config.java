package com.ithinkrok.util.config;

import org.apache.commons.lang.ArrayUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by paul on 08/02/16.
 *
 * Represents a config.
 *
 * Configs should only store primitives, arrays/lists, strings and other configs.
 */
public interface Config {

    Map<String, Object> getValues(boolean deep);

    boolean contains(String path);

    default void setAll(Map<String, Object> values) {
        for (String path : values.keySet()) {
            set(path, values.get(path));
        }
    }

    void set(String path, Object value);

    default void setAll(Config values) {
        for (String path : values.getKeys(true)) {
            set(path, values.get(path));
        }
    }

    Set<String> getKeys(boolean deep);

    default Object get(String path) {
        return get(path, null);
    }

    Object get(String path, Object def);

    default String getString(String path) {
        return getString(path, null);
    }

    default String getString(String path, String def) {
        Object obj = get(path);

        if (obj != null) return obj.toString();
        else return def;
    }

    default boolean isString(String path) {
        return get(path) instanceof String;
    }

    default int getInt(String path) {
        return getInt(path, 0);
    }

    default int getInt(String path, int def) {
        Object obj = get(path);

        if (obj instanceof Number) return ((Number) obj).intValue();
        else return def;
    }

    default boolean isInt(String path) {
        return get(path) instanceof Integer;
    }

    default boolean getBoolean(String path) {
        return getBoolean(path, false);
    }

    default boolean getBoolean(String path, boolean def) {
        Object obj = get(path);

        if (obj instanceof Boolean) return (Boolean) obj;
        else return def;
    }

    default boolean isBoolean(String path) {
        return get(path) instanceof Boolean;
    }

    default double getDouble(String path) {
        return getDouble(path, 0.0d);
    }

    default double getDouble(String path, double def) {
        Object obj = get(path);

        if (obj instanceof Number) return ((Number) obj).doubleValue();
        else return def;
    }

    default boolean isDouble(String path) {
        return get(path) instanceof Boolean;
    }

    default long getLong(String path) {
        return getLong(path, 0L);
    }

    default long getLong(String path, long def) {
        Object obj = get(path);

        if (obj instanceof Number) return ((Number) obj).longValue();
        else return def;
    }

    default boolean isLong(String path) {
        return get(path) instanceof Long;
    }

    default byte[] getByteArray(String path) {
        return getByteArray(path, new byte[0]);
    }

    default byte[] getByteArray(String path, byte[] def) {
        Object obj = get(path);

        if (obj instanceof byte[]) return (byte[]) obj;
        else return def;
    }

    default boolean isByteArray(String path) {
        return get(path) instanceof byte[];
    }

    default List<String> getStringList(String path) {
        return getList(path, String.class);
    }

    default <T> List<T> getList(String path, Class<T> clazz) {
        return getList(path, new ArrayList<>(), clazz);
    }

    @SuppressWarnings("unchecked")
    default <T> List<T> getList(String path, List<T> def, Class<T> clazz) {
        Object obj = get(path);

        if (!(obj instanceof Collection<?>)) return def;

        Collection<?> input = (Collection<?>) obj;
        List<T> result = new ArrayList<>();

        for (Object o : input) {
            if (!clazz.isInstance(o)) continue;

            result.add((T) o);
        }

        return result;
    }

    default List<Config> getConfigList(String path) {
        return getList(path, Config.class);
    }

    default boolean isList(String path) {
        return get(path) instanceof List<?>;
    }

    /**
     * @param path The path to check for a list
     * @param type The type that the list should contain
     * @return If there is a list at this path and it contains an object with the type {@code type}
     */
    default boolean isList(String path, Class<?> type) {
        if (!isList(path)) return false;

        List<?> list = getList(path, null, type);

        return list != null && !list.isEmpty();
    }

    default Config getConfigOrNull(String path) {
        Object obj = get(path);

        if (!(obj instanceof Config)) return null;
        else return (Config) obj;
    }

    Config getConfigOrEmpty(String path);

    default boolean isConfig(String path) {
        return get(path) instanceof Config;
    }

    default <T> T getType(String path, Class<T> type) {
        return getType(path, null, type);
    }

    @SuppressWarnings("unchecked")
    default <T> T getType(String path, T def, Class<T> type) {
        Object obj = get(path);

        if (!type.isInstance(obj)) return def;
        return (T) obj;
    }

    /**
     * Modifies the fields of the object passed in to the values specified in this config.
     *
     * @param object The object to modify the fields of.
     * @param <T> The type of the object
     * @return The (now modified) object
     */
    default <T> T setObjectFields(T object) {
        Field[] fields = object.getClass().getDeclaredFields();

        for(Field field : fields) {
            field.setAccessible(true);

            if(Modifier.isTransient(field.getModifiers())) continue;

            Object newField = getType(field.getName(), field.getType());
            if(newField == null) continue;

            try {
                field.set(object, newField);
            } catch (IllegalAccessException e) {
                //This should not happen hopefully.
                e.printStackTrace();
            }
        }

        return object;
    }
}
