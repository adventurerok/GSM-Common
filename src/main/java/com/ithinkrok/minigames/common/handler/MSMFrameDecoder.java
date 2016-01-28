package com.ithinkrok.minigames.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

/**
 * Created by paul on 28/01/16.
 */
public class MSMFrameDecoder extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
            throws Exception {
        in.markReaderIndex();

        final byte[] buf = new byte[4];
        for(int i = 0; i < buf.length; i++) {
            if(!in.isReadable()) {
                in.resetReaderIndex();
                return;
            }

            buf[i] = in.readByte();
            if(buf[i] > 0) {
                int length = PacketUtils.readVarInt(Unpooled.wrappedBuffer(in));
                if(length == 0) throw new CorruptedFrameException("Empty packet");

                if(in.readableBytes() < length) {
                    in.resetReaderIndex();
                    return;
                } else {
                    ByteBuf dst = ctx.alloc().directBuffer(length);
                    in.readBytes(dst);

                    out.add(dst);
                    return;
                }
            }
        }

        throw new CorruptedFrameException("length wider than 28-bit");
    }
}
