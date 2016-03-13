package com.ithinkrok.msm.common.handler;

import com.ithinkrok.msm.common.Packet;
import com.ithinkrok.util.config.BinaryConfigIO;
import com.ithinkrok.util.config.Config;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.DataOutput;

/**
 * Created by paul on 28/01/16.
 */
public class MSMPacketEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf buf) throws Exception {
        ByteBufOutputStream out = new ByteBufOutputStream(buf);

        PacketUtils.writeVarInt(msg.getId(), out);

        Config payload = msg.getPayload();
        BinaryConfigIO.saveConfig((DataOutput) out, payload);
    }
}
