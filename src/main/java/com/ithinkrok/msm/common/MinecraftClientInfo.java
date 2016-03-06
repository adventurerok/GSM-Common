package com.ithinkrok.msm.common;

import com.ithinkrok.util.config.Config;
import com.ithinkrok.util.config.MemoryConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paul on 06/02/16.
 * <p>
 * Represents
 */
public class MinecraftClientInfo implements ClientInfo {

    /**
     * The name of this minecraft server.
     * Must be equal to the name in the bungeecord config for bungeecord network servers
     */
    private final String name;

    /**
     * The subType of the minecraft server
     */
    private MinecraftClientType subType;
    /**
     * If this minecraft server is in a bungeecord network
     */
    private boolean hasBungee;

    /**
     * The maximum player count for this server. -1 indicates no maximum
     */
    private int maxPlayerCount;

    /**
     * The list of plugins that the server has
     */
    private List<String> plugins;

    public MinecraftClientInfo(MinecraftClientType subType, String name, boolean hasBungee, int maxPlayerCount,
                               List<String> plugins) {
        this.subType = subType;
        this.name = name;
        this.hasBungee = hasBungee;
        this.maxPlayerCount = maxPlayerCount;
        this.plugins = plugins;
    }

    public MinecraftClientInfo(Config config) {
        name = config.getString("name");

        fromConfig(config);
    }

    @Override
    public void fromConfig(Config config) {
        subType = MinecraftClientType.valueOf(config.getString("subtype").toUpperCase());
        hasBungee = config.getBoolean("has_bungee");
        maxPlayerCount = config.getInt("max_players");
        plugins = config.getStringList("plugins");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return "minecraft";
    }

    @Override
    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public MinecraftClientType getSubType() {
        return subType;
    }

    public boolean hasBungee() {
        return hasBungee;
    }

    public List<String> getPlugins() {
        return plugins;
    }

    @Override
    public Config toConfig() {
        Config config = new MemoryConfig();

        config.set("name", name);
        config.set("subtype", subType.toString().toLowerCase());
        config.set("has_bungee", hasBungee);
        config.set("max_players", maxPlayerCount);
        config.set("plugins", new ArrayList<>(plugins));

        return config;
    }

    @Override
    public int hashCode() {
        int result = subType.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (hasBungee ? 1 : 0);
        result = 31 * result + maxPlayerCount;
        result = 31 * result + plugins.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MinecraftClientInfo that = (MinecraftClientInfo) o;

        if (hasBungee != that.hasBungee) return false;
        if (maxPlayerCount != that.maxPlayerCount) return false;
        if (subType != that.subType) return false;
        if (!name.equals(that.name)) return false;
        return plugins.equals(that.plugins);

    }

    @Override
    public String toString() {
        return "MinecraftClientInfo{" +
                "subtype=" + subType +
                ", name='" + name + '\'' +
                ", hasBungee=" + hasBungee +
                ", maxPlayerCount=" + maxPlayerCount +
                ", plugins=" + plugins +
                '}';
    }
}
