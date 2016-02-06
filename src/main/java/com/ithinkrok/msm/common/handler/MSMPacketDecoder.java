package com.ithinkrok.msm.common.handler;

import com.ithinkrok.msm.common.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paul on 28/01/16.
 */
public class MSMPacketDecoder extends MessageToMessageDecoder<ByteBuf> {


    static ConfigurationSection readConfig(ByteBuf msg) {
        int size = PacketUtils.readVarInt(msg);

        MemoryConfiguration config = new MemoryConfiguration();

        for (int count = 0; count < size; ++count) {
            String key = PacketUtils.readString(msg);
            Object value = read(msg);

            config.set(key, value);
        }

        return config;
    }

    static Object read(ByteBuf msg) {
        int type = msg.readByte() & 0xFF;

        return read(msg, type);
    }

    private static Object read(ByteBuf msg, int type) {
        switch (type) {
            case ConfigType.STRING:
                return PacketUtils.readString(msg);
            case ConfigType.VAR_INT:
                return PacketUtils.readVarInt(msg);
            case ConfigType.FLOAT:
                return msg.readFloat();
            case ConfigType.DOUBLE:
                return msg.readDouble();
            case ConfigType.BYTE:
                return msg.readByte();
            case ConfigType.VAR_LONG:
                return PacketUtils.readVarLong(msg);
            case ConfigType.CHAR:
                return msg.readChar();
            case ConfigType.CONFIG:
                return readConfig(msg);
            case ConfigType.BYTE_ARRAY:
                return readByteArray(msg);
            case ConfigType.BOOLEAN:
                return msg.readBoolean();
            default:
                if ((type & ConfigType.LIST_MASK) == ConfigType.LIST_MASK) {
                    return readList(msg, type);
                } else throw new UnsupportedOperationException("Unsupported data type:" + type);
        }
    }

    private static byte[] readByteArray(ByteBuf msg) {
        int length = PacketUtils.readVarInt(msg);

        byte[] result = new byte[length];
        msg.readBytes(result);

        return result;
    }

    private static List<?> readList(ByteBuf msg, int type) {
        type ^= ConfigType.LIST_MASK;

        int size = PacketUtils.readVarInt(msg);
        List<Object> list = new ArrayList<>(size);

        for (int count = 0; count < size; ++count) {
            if (type == ConfigType.OBJECT) list.add(read(msg));
            else list.add(read(msg, type));
        }

        return list;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        byte id = msg.readByte();

        ConfigurationSection payload = readConfig(msg);

        out.add(new Packet(id, payload));
    }

}
