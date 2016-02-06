package com.ithinkrok.msm.common;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by paul on 28/01/16.
 */
public class Packet {

    /**
     * The id of a protocol;
     */
    private final int id;

    /**
     * The payload for the packet
     */
    private final ConfigurationSection payload;

    public Packet(int id, ConfigurationSection payload) {
        this.id = id;
        this.payload = payload;
    }

    public int getId() {
        return id;
    }

    public ConfigurationSection getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "id=" + id +
                ", payload=" + payload +
                '}';
    }
}
