package com.ithinkrok.util.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by paul on 08/02/16.
 * <p>
 * Represents a config.
 * <p>
 * Configs should only store primitives, arrays/lists, strings and other configs.
 * <p>
 * It is possible to address values in subconfigs by including the address in the subconfig after the separator character.
 * For instance, if we have a config at 'cfg' that contains an int 'anint', and the separator character is '.',
 * we can address this as 'cfg.anint'. This can be used for configs at any depth.
 */
public interface Config {

    /**
     * Converts the config into a Map
     *
     * @param deep If true, instead of putting Maps for subconfigs,
     *             use the full path using the separator character to split
     * @return This config as a Map
     */
    Map<String, Object> getValues(boolean deep);

    /**
     * @param path The path to check
     * @return If the config contains an item at path
     */
    boolean contains(String path);

    /**
     * Load the values from the map into this config.
     *
     * @param values The map to load in.
     */
    default void setAll(Map<String, Object> values) {
        for (String path : values.keySet()) {
            set(path, values.get(path));
        }
    }

    /**
     * Sets the value at path to value
     *
     * @param path  The path to put the value
     * @param value The value
     * @return The modified config object, to allow for chain calls
     */
    Config set(String path, Object value);

    default void setAll(Config values) {
        for (String path : values.getKeys(true)) {
            set(path, values.get(path));
        }
    }

    /**
     * @param deep Should full paths be returned for keys in subconfigs, separated by the separator character?
     * @return The keys that have values in this config
     */
    Set<String> getKeys(boolean deep);

    /**
     * Finds and retrieves the object at the given path. If the path is empty, returns this object.
     *
     * @param path The path of the object
     * @return The object at the given path, or null if not found
     */
    default Object get(String path) {
        return get(path, null);
    }

    /**
     * Gets the raw value this config contains at path, returning the placeholder def is nothing is found.
     *
     * @param path The path to get the value at
     * @param def The placeholder if no value is found at path, e.g. {@code null}
     * @return The raw object contained at path, or def if nothing is there.
     */
    Object get(String path, Object def);

    /**
     * @return The separator character.
     */
    char getSeparator();

    /**
     *
     * @return toString() of the object at the path, or null if there is none
     */
    default String getString(String path) {
        return getString(path, null);
    }

    /**
     *
     * @return toString() of the object at the path, or def if there is none
     */
    default String getString(String path, String def) {
        Object obj = get(path);

        if (obj != null) return obj.toString();
        else return def;
    }

    /**
     *
     * @return If there is a String stored at the given path
     */
    default boolean isString(String path) {
        return get(path) instanceof String;
    }

    /**
     *
     * @return The int value of the number stored at the path, or 0 if there is none
     */
    default int getInt(String path) {
        return getInt(path, 0);
    }

    /**
     * @return The int value of the number stored at the path, or def if there is none
     */
    default int getInt(String path, int def) {
        Object obj = get(path);

        if (obj instanceof Number) return ((Number) obj).intValue();
        else return def;
    }

    /**
     *
     * @return If the object at the path is an instance of Integer
     */
    default boolean isInt(String path) {
        return get(path) instanceof Integer;
    }

    /**
     *
     * @return The short value of the number stored at the path, or 0 if there is none
     */
    default short getShort(String path) {
        return getShort(path, 0);
    }

    /**
     * @return The short value of the number stored at the path, or def if there is none
     */
    default short getShort(String path, int def) {
        Object obj = get(path);

        if (obj instanceof Number) return ((Number) obj).shortValue();
        else return (short) def;
    }

    /**
     *
     * @return If the object at the path is an instance of Short
     */
    default boolean isShort(String path) {
        return get(path) instanceof Short;
    }

    /**
     *
     * @return True if the boolean true is stored at path, otherwise false
     */
    default boolean getBoolean(String path) {
        return getBoolean(path, false);
    }

    /**
     * @return The boolean value stored at path, or def is there is none
     */
    default boolean getBoolean(String path, boolean def) {
        Object obj = get(path);

        if (obj instanceof Boolean) return (Boolean) obj;
        else return def;
    }

    /**
     * @return If there is a boolean stored at path
     */
    default boolean isBoolean(String path) {
        return get(path) instanceof Boolean;
    }

    default boolean isNumber(String path) {
        return get(path) instanceof Number;
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
        return get(path) instanceof Double;
    }

    default BigDecimal getBigDecimal(String path) {
        return getBigDecimal(path, BigDecimal.ZERO);
    }

    default BigDecimal getBigDecimal(String path, BigDecimal def) {
        Object obj = get(path);

        if (obj instanceof Number) {
            if (obj instanceof BigDecimal) {
                return (BigDecimal) obj;
            } else if (obj instanceof BigInteger) {
                return new BigDecimal((BigInteger) obj);
            } else {
                return BigDecimal.valueOf(((Number) obj).doubleValue());
            }
        } else return def;
    }

    default boolean isBigDecimal(String path) {
        return get(path) instanceof BigDecimal;
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

    /**
     * Modifies the fields of the object passed in to the values specified in this config.
     *
     * @param object The object to modify the fields of.
     * @param <T>    The type of the object
     * @return The (now modified) object
     */
    default <T> T getAllFields(T object) {
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            if (Modifier.isStatic(field.getModifiers())) continue;
            if (Modifier.isTransient(field.getModifiers())) continue;

            if (!contains(field.getName())) continue;

            try {
                if (field.getType().equals(double.class)) {
                    if (!isNumber(field.getName())) continue;
                    field.setDouble(object, getDouble(field.getName()));
                } else if (field.getType().equals(float.class)) {
                    if (!isNumber(field.getName())) continue;
                    field.setFloat(object, (float) getDouble(field.getName()));
                } else if (field.getType().equals(int.class)) {
                    if (!isNumber(field.getName())) continue;
                    field.setInt(object, getInt(field.getName()));
                } else if (field.getType().equals(boolean.class)) {
                    if (!isBoolean(field.getName())) continue;
                    field.setBoolean(object, getBoolean(field.getName()));
                } else if (field.getType().equals(long.class)) {
                    if (!isNumber(field.getName())) continue;
                    field.setLong(object, getLong(field.getName()));
                } else if (field.getType().equals(short.class)) {
                    if (!isNumber(field.getName())) continue;
                    field.setShort(object, (short) getInt(field.getName()));
                } else if (field.getType().equals(byte.class)) {
                    if (!isNumber(field.getName())) continue;
                    field.setByte(object, (byte) getInt(field.getName()));
                } else {
                    Object newValue = getType(field.getName(), field.getType());
                    if (newValue == null) continue;

                    field.set(object, newValue);
                }

            } catch (IllegalAccessException e) {
                //This should not happen hopefully.
                e.printStackTrace();
            }
        }

        return object;
    }

    /**
     * Loads the fields of the object into this config.
     *
     * @param object The object itself
     * @param <T>    The type of object
     * @return The object, again
     */
    default <T> T setAllFields(T object) {
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) continue;

            try {
                set(field.getName(), field.get(object));
            } catch (IllegalAccessException e) {
                //hopefully we will be fine
                e.printStackTrace();
            }
        }

        return object;
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
}