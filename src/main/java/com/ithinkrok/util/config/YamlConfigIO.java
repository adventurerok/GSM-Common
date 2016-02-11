package com.ithinkrok.util.config;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Created by paul on 10/02/16.
 */
public class YamlConfigIO {

    private static final ThreadLocal<Yaml> yamlThreadLocal = new ThreadLocal<>();

    private static Yaml getYaml() {
        Yaml yaml = yamlThreadLocal.get();

        if(yaml == null) {
            yaml = new Yaml();

            yamlThreadLocal.set(yaml);
        }

        return yaml;
    }

    public static Config loadToConfig(Path path, Config config) throws IOException {
        return loadToConfig(Files.newBufferedReader(path), config);
    }

    @SuppressWarnings("unchecked")
    public static Config loadToConfig(InputStream input, Config config) throws InvalidConfigException {
        Map<String, Object> values;
        try {
            values = (Map<String, Object>) getYaml().load(input);
        } catch (Exception e) {
            throw new InvalidConfigException("Error while loading config", e);
        }
        config.setAll(values);

        return config;
    }

    @SuppressWarnings("unchecked")
    public static Config loadToConfig(Reader input, Config config) throws InvalidConfigException {
        Map<String, Object> values;
        try {
            values = (Map<String, Object>) getYaml().load(input);
        } catch (Exception e) {
            throw new InvalidConfigException("Error while loading config", e);
        }

        config.setAll(values);

        return config;
    }

}
