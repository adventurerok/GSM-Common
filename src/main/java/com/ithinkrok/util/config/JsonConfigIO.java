package com.ithinkrok.util.config;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.util.ArrayList;
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

    @SuppressWarnings("unchecked")
    public static Config loadConfig(String json) {
        Object parsed = JSONValue.parse(json);
        if(!(parsed instanceof Map)) {
            throw new RuntimeException("json is not a JSONObject(Map): " + json);
        }

        return new MemoryConfig((Map<String, ?>) parsed);
    }

    @SuppressWarnings("unchecked")
    public static List<Config> loadConfigs(String jsonArray) {
        Object parsed = JSONValue.parse(jsonArray);
        if(!(parsed instanceof List)) {
            throw new RuntimeException("jsonArray is not a JSONArray(List): " + jsonArray);
        }

        JSONArray array = (JSONArray) parsed;
        List<Config> result = new ArrayList<>();

        for (Object jsonObject : array) {
            if(!(jsonObject instanceof Map)) {
                throw new RuntimeException("Not a list of JSONObjects(Maps): " + jsonArray);
            }

            result.add(new MemoryConfig((Map<String, ?>) jsonObject));
        }

        return result;
    }

}
