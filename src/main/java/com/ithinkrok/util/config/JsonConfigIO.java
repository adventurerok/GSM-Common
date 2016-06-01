package com.ithinkrok.util.config;

import org.json.simple.JSONValue;

import java.util.Map;

/**
 * Created by paul on 12/03/16.
 */
public class JsonConfigIO {

    public static String dumpConfig(Config config) {
        Map<String, Object> map = ConfigUtils.toMap(config);

        return JSONValue.toJSONString(map);
    }

}
