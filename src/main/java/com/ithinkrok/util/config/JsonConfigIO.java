package com.ithinkrok.util.config;

import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by paul on 12/03/16.
 */
public class JsonConfigIO {

    public static String dumpConfig(Config config) {
        Map<String, Object> map = ConfigUtils.toMap(config);

        return JSONValue.toJSONString(map);
    }

    public static String dumpConfigs(List<Config> configs) {
        List<Object> list = new ArrayList<>();

        for (Config config : configs) {
            list.add(ConfigUtils.toMap(config));
        }

        return JSONValue.toJSONString(list);
    }

}
