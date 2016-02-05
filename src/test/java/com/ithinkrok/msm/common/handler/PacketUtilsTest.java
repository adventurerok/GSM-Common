package com.ithinkrok.msm.common.handler;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by paul on 05/02/16.
 */
@RunWith(DataProviderRunner.class)
public class PacketUtilsTest {

    @DataProvider({"45", "2745", "-43", "7463628955243"})
    @Test
    public void readVarLongShouldEqualWrittenVarLong(long test) {
        ByteBuf bytes = Unpooled.buffer(10);

        PacketUtils.writeVarLong(test, bytes);

        long result = PacketUtils.readVarLong(bytes);

        assertThat(result).isEqualTo(test);
    }

    @DataProvider({"45", "2745", "-43", "746346543"})
    @Test
    public void readVarIntShouldEqualWrittenVarInt(int test) {
        ByteBuf bytes = Unpooled.buffer(5);

        PacketUtils.writeVarInt(test, bytes);

        int result = PacketUtils.readVarInt(bytes);

        assertThat(result).isEqualTo(test);
    }

}