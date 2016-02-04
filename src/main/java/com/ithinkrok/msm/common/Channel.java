package com.ithinkrok.msm.common;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by paul on 03/02/16.
 */
public interface Channel {

    void write(ConfigurationSection packet);
}
