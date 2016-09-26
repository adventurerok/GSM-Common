package com.ithinkrok.util.config;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
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
        try (BufferedReader input = Files.newBufferedReader(path)) {
            return loadToConfig(input, config);
        }
    }

    public static void saveConfig(Writer output, Config config) {
        getYaml().dump(ConfigUtils.toMap(config), output);
    }

    public static void saveConfig(OutputStream output, Config config) {
        //Don't close as that is the caller's responsibility
        saveConfig(new OutputStreamWriter(output), config);
    }

    public static void saveConfig(Path path, Config config) throws IOException {
        try (BufferedWriter output = Files.newBufferedWriter(path)) {
            saveConfig(output, config);
        }
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

        if(values == null) throw new InvalidConfigException("Yaml.load() produced null instead of a map");

        config.setAll(values);

        return config;
    }



}
