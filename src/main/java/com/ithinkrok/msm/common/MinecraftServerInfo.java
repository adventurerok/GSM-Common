package com.ithinkrok.msm.common;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paul on 06/02/16.
 * <p>
 * Represents
 */
public class MinecraftServerInfo {

    /**
     * The type of the minecraft server
     */
    private MinecraftServerType type;

    /**
     * The name of this minecraft server.
     * Must be equal to the name in the bungeecord config for bungeecord network servers
     */
    private final String name;

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

    public MinecraftServerInfo(MinecraftServerType type, String name, boolean hasBungee, int maxPlayerCount,
                               List<String> plugins) {
        this.type = type;
        this.name = name;
        this.hasBungee = hasBungee;
        this.maxPlayerCount = maxPlayerCount;
        this.plugins = plugins;
    }

    public MinecraftServerInfo(ConfigurationSection config) {
        name = config.getString("name");

        fromConfig(config);
    }

    public MinecraftServerType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean hasBungee() {
        return hasBungee;
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public List<String> getPlugins() {
        return plugins;
    }

    public ConfigurationSection toConfig() {
        MemoryConfiguration config = new MemoryConfiguration();

        config.set("name", name);
        config.set("type", type.toString().toLowerCase());
        config.set("has_bungee", hasBungee);
        config.set("max_players", maxPlayerCount);
        config.set("plugins", new ArrayList<>(plugins));

        return config;
    }

    public void fromConfig(ConfigurationSection config) {
        type = MinecraftServerType.valueOf(config.getString("type").toUpperCase());
        hasBungee = config.getBoolean("has_bungee");
        maxPlayerCount = config.getInt("max_players");
        plugins = config.getStringList("plugins");
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
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

        MinecraftServerInfo that = (MinecraftServerInfo) o;

        if (hasBungee != that.hasBungee) return false;
        if (maxPlayerCount != that.maxPlayerCount) return false;
        if (type != that.type) return false;
        if (!name.equals(that.name)) return false;
        return plugins.equals(that.plugins);

    }

    @Override
    public String toString() {
        return "MinecraftServerInfo{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", hasBungee=" + hasBungee +
                ", maxPlayerCount=" + maxPlayerCount +
                ", plugins=" + plugins +
                '}';
    }
}
