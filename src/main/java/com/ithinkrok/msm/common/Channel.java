package com.ithinkrok.msm.common;

import com.ithinkrok.util.config.Config;

/**
 * Created by paul on 03/02/16.
 */
public interface Channel {

    void write(Config packet);
}
