package com.ithinkrok.msm.common;

import com.ithinkrok.util.config.Config;
import com.ithinkrok.util.config.ConfigSerializable;

/**
 * Created by paul on 06/03/16.
 */
public interface ClientInfo extends ConfigSerializable {

    void fromConfig(Config config);

    String getName();

    String getType();

    int getMaxPlayerCount();
}
