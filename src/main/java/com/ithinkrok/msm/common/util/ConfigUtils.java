package com.ithinkrok.msm.common.util;

import com.ithinkrok.util.config.Config;
import com.ithinkrok.util.config.ConfigSerializable;
import com.ithinkrok.util.config.MemoryConfig;

import java.util.*;

/**
 * Created by paul on 01/01/16.
 */
public class ConfigUtils {

    public static final Config EMPTY_CONFIG = new MemoryConfig();

    public static Map<String, Object> toMap(Config config) {
        Map<String, Object> result = new LinkedHashMap<>();

        for(String key : config.getKeys(false)) {
            Object obj = config.get(key);

            result.put(key, toMapObject(obj));
        }

        return result;
    }

    private static Object toMapObject(Object object) {
        if(object instanceof Config) {
            return toMap((Config) object);
        } else if(object instanceof Collection<?>) {
            List<Object> list = new ArrayList<>();

            for(Object listItem : (Iterable<?>)object) {
                list.add(toMapObject(listItem));
            }

            return list;
        }

        return object;
    }

    public static List<Config> collectionToConfigList(Iterable<? extends ConfigSerializable> input) {
        List<Config> output = new ArrayList<>();

        for(ConfigSerializable object : input) {
            output.add(object.toConfig());
        }

        return output;
    }

    public static String formatString(String str, Config config) {
        StringBuilder result = new StringBuilder();

        StringBuilder key = new StringBuilder();

        boolean inBrackets = false;

        for(int index = 0; index < str.length(); ++index) {
            char c = str.charAt(index);

            if(inBrackets) {
                if(c == '}') {
                    result.append(config.get(key.toString()));

                    key = new StringBuilder();
                } else {
                    key.append(c);
                }
            } else if(c == '{') {
                inBrackets = true;
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}
