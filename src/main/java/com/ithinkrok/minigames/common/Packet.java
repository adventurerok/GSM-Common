package com.ithinkrok.minigames.common;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by paul on 28/01/16.
 */
public class Packet {

    /**
     * The id of a packet. IDs 0-127 are reserved. Each MSM plugin has 1 dynamically allocated packet id.
     */
    private final byte id;

    /**
     * The payload for the packet
     */
    private final ConfigurationSection payload;

    public Packet(byte id, ConfigurationSection payload) {
        this.id = id;
        this.payload = payload;
    }

    public byte getId() {
        return id;
    }

    public ConfigurationSection getPayload() {
        return payload;
    }
}
