package com.ithinkrok.msm.common;

import com.ithinkrok.util.config.Config;

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
    private final Config payload;

    public Packet(int id, Config payload) {
        this.id = id;
        this.payload = payload;
    }

    public int getId() {
        return id;
    }

    public Config getPayload() {
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
