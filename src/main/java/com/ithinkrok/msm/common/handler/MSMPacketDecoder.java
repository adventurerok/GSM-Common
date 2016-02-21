package com.ithinkrok.msm.common.handler;

import com.ithinkrok.msm.common.Packet;
import com.ithinkrok.util.config.BinaryConfigIO;
import com.ithinkrok.util.config.BinaryConfigType;
import com.ithinkrok.util.config.Config;
import com.ithinkrok.util.config.MemoryConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paul on 28/01/16.
 */
public class MSMPacketDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        ByteBufInputStream in = new ByteBufInputStream(msg);

        int id = PacketUtils.readVarInt(in);

        Config payload = BinaryConfigIO.loadConfig(in);

        out.add(new Packet(id, payload));
    }

}
