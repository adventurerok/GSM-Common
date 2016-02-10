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
public class ConfigLoader {

    private static final ThreadLocal<Yaml> yamlThreadLocal = new ThreadLocal<>();

    private static Yaml getYaml() {
        Yaml yaml = yamlThreadLocal.get();

        if(yaml == null) {
            yaml = new Yaml();

            yamlThreadLocal.set(yaml);
        }

        return yaml;
    }

    public static Config loadConfig(Path path) throws IOException {
        return loadConfig(Files.newBufferedReader(path));
    }

    public static Config loadConfig(InputStream input) {
        @SuppressWarnings("unchecked")
        Map<String, Object> values = (Map<String, Object>) getYaml().load(input);

        return new MemoryConfig(values);
    }

    public static Config loadConfig(Reader input) {
        @SuppressWarnings("unchecked")
        Map<String, Object> values = (Map<String, Object>) getYaml().load(input);

        return new MemoryConfig(values);
    }

}
