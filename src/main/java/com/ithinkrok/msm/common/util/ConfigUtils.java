package com.ithinkrok.msm.common.util;

import com.ithinkrok.util.config.Config;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by paul on 01/01/16.
 */
public class ConfigUtils {

    public static final MemoryConfiguration EMPTY_CONFIG = new MemoryConfiguration();

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
}
