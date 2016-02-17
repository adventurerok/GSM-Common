package com.ithinkrok.msm.common.util;

import com.ithinkrok.util.config.Config;
import com.ithinkrok.util.config.MemoryConfig;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Created by paul on 01/01/16.
 */
public class ConfigUtils {

    public static final Config EMPTY_CONFIG = new MemoryConfig();

    public static Vector getVector(Config config, String path) {
        return config.getConfigOrEmpty(path).saveObjectFields(new Vector());
    }

    public static Location getLocation(Config config, World world, String path) {
        return config.getConfigOrEmpty(path).saveObjectFields(new Location(world, 0, 0, 0));
    }

    public static List<Vector> getVectorList(Config config, String path) {
        List<Config> configList = config.getConfigList(path);

        List<Vector> result = new ArrayList<>();

        for(Config vectorConfig : configList) {
            result.add(vectorConfig.saveObjectFields(new Vector()));
        }

        return result;
    }

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
}
