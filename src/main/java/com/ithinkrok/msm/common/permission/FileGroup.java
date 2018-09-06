package com.ithinkrok.msm.common.permission;

import com.ithinkrok.util.config.Config;
import com.ithinkrok.util.config.MemoryConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileGroup implements Group {

    private final String name;
    private final Path path;

    private int priority = 0;
    private Set<String> parents = new HashSet<>();

    private List<ContextedPermission> permissions = new ArrayList<>();

    private Config permittedConfig = new MemoryConfig();


    public FileGroup(Path path) throws IOException {
        this.name = filename(path);
        this.path = path;

        read(path);
    }


    private static String filename(Path path) {
        String nameWithExtension = path.getFileName().toString();

        int lastIndex = nameWithExtension.lastIndexOf('.');
        if (lastIndex >= 0) {
            return nameWithExtension.substring(0, lastIndex);
        } else return nameWithExtension;
    }


    private void read(Path path) throws IOException {
        if(!Files.exists(path)) return;

        List<String> lines = Files.readAllLines(path);

        for (String line : lines) {
            int commentIndex = line.indexOf('#');
            if(commentIndex >= 0) line = line.substring(0, commentIndex);

            line = line.trim();
            if(line.isEmpty()) continue;


        }

    }


    @Override
    public String getName() {
        return name;
    }


    @Override
    public int getPriority() {
        return priority;
    }


    @Override
    public Set<String> getParents() {
        return parents;
    }


    @Override
    public List<ContextedPermission> getPermissions() {
        return permissions;
    }


    @Override
    public Config getPermittedConfig() {
        return permittedConfig;
    }
}
