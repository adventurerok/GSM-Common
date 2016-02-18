package com.ithinkrok.msm.common.handler;


import com.ithinkrok.util.config.MemoryConfig;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by paul on 28/01/16.
 */
@RunWith(DataProviderRunner.class)
public class MSMPacketEncoderTest {

    @Test
    public void getObjectTypeShouldGetCorrectType() {

        assertThat(MSMPacketEncoder.getObjectType(3)).isEqualTo(ConfigType.VAR_INT);

        assertThat(MSMPacketEncoder.getObjectType("string")).isEqualTo(ConfigType.STRING);

        assertThat(MSMPacketEncoder.getObjectType(3L)).isEqualTo(ConfigType.VAR_LONG);

        assertThat(MSMPacketEncoder.getObjectType(3F)).isEqualTo(ConfigType.FLOAT);

        assertThat(MSMPacketEncoder.getObjectType(3D)).isEqualTo(ConfigType.DOUBLE);

        assertThat(MSMPacketEncoder.getObjectType((byte) 3)).isEqualTo(ConfigType.BYTE);

        assertThat(MSMPacketEncoder.getObjectType('c')).isEqualTo(ConfigType.CHAR);

        assertThat(MSMPacketEncoder.getObjectType(new MemoryConfig())).isEqualTo(ConfigType.CONFIG);
    }
}