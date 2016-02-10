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

    public static Config loadToConfig(InputStream input, Config config) {
        @SuppressWarnings("unchecked")
        Map<String, Object> values = (Map<String, Object>) getYaml().load(input);

        config.setAll(values);

        return config;
    }

    public static Config loadToConfig(Reader input, Config config) {
        @SuppressWarnings("unchecked")
        Map<String, Object> values = (Map<String, Object>) getYaml().load(input);

        config.setAll(values);

        return config;
    }

}
