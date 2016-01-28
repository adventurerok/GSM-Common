package com.ithinkrok.minigames.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by paul on 28/01/16.
 */
public class MSMFrameEncoder extends MessageToByteEncoder<ByteBuf> {


    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        PacketUtils.writeVarInt(msg.readableBytes(), out);
        out.writeBytes(msg);
    }
}
