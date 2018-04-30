package com.ithinkrok.msm.common.command;

import com.ithinkrok.util.StringUtils;
import com.ithinkrok.util.config.Config;
import com.ithinkrok.util.config.ConfigSerializable;
import com.ithinkrok.util.config.MemoryConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by paul on 09/03/16.
 */
public class CommandInfo implements ConfigSerializable {
    protected final String name;
    protected final String usage;
    protected final String description;
    protected final String permission;
    protected final List<String> aliases;
    protected final Map<Pattern, List<String>> tabCompletion = new HashMap<>();

    public CommandInfo(String name, Config config) {
        this.name = name;
        this.aliases = config.getStringList("aliases");
        this.usage = StringUtils.convertAmpersandToSelectionCharacter(config.getString("usage"));
        this.description = StringUtils.convertAmpersandToSelectionCharacter(config.getString("description"));
        this.permission = config.getString("permission");

        if(!config.contains("tab_complete")) return;

        for(Config tabConfig : config.getConfigList("tab_complete")) {
            String pattern = tabConfig.getString("pattern");

            List<String> values = tabConfig.getStringList("values");

            tabCompletion.put(Pattern.compile(pattern), values);
        }
    }

    public String getName() {
        return name;
    }

    public String getUsage() {
        return usage;
    }

    public String getDescription() {
        return description;
    }

    public String getPermission() {
        return permission;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public Map<Pattern, List<String>> getTabCompletion() {
        return tabCompletion;
    }

    public Config toConfig() {
        Config result = new MemoryConfig();

        result.set("name", name);
        result.set("usage", usage);
        result.set("description", description);
        result.set("permission", permission);
        result.set("aliases", aliases);

        List<Config> tabConfigs = new ArrayList<>();

        for(Map.Entry<Pattern, List<String>> tabEntry : tabCompletion.entrySet()) {
            Config tabConfig = new MemoryConfig();

            tabConfig.set("pattern", tabEntry.getKey().toString());
            tabConfig.set("values", tabEntry.getValue());

            tabConfigs.add(tabConfig);
        }

        result.set("tab_complete", tabConfigs);

        return result;
    }
}
