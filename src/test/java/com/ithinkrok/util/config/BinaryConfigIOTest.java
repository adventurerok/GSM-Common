package com.ithinkrok.util.config;


import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;


/**
 * Created by paul on 28/01/16.
 */
@RunWith(DataProviderRunner.class)
public class BinaryConfigIOTest {

    @Test
    public void getObjectTypeShouldGetCorrectType() {

        assertThat(BinaryConfigIO.getObjectType(3)).isEqualTo(BinaryConfigType.VAR_INT);

        assertThat(BinaryConfigIO.getObjectType("string")).isEqualTo(BinaryConfigType.STRING);

        assertThat(BinaryConfigIO.getObjectType(3L)).isEqualTo(BinaryConfigType.VAR_LONG);

        assertThat(BinaryConfigIO.getObjectType(3F)).isEqualTo(BinaryConfigType.FLOAT);

        assertThat(BinaryConfigIO.getObjectType(3D)).isEqualTo(BinaryConfigType.DOUBLE);

        assertThat(BinaryConfigIO.getObjectType((byte) 3)).isEqualTo(BinaryConfigType.BYTE);

        assertThat(BinaryConfigIO.getObjectType('c')).isEqualTo(BinaryConfigType.CHAR);

        assertThat(BinaryConfigIO.getObjectType(new MemoryConfig())).isEqualTo(BinaryConfigType.CONFIG);
    }
}