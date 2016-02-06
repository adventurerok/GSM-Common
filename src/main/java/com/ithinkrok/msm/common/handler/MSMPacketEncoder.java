package com.ithinkrok.msm.common.handler;

import com.ithinkrok.msm.common.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.*;

/**
 * Created by paul on 28/01/16.
 */
public class MSMPacketEncoder extends MessageToByteEncoder<Packet> {


    static void writeConfig(ConfigurationSection config, ByteBuf out, boolean writeType) {
        if(writeType) out.writeByte(ConfigType.CONFIG);

        Set<String> keys = config.getKeys(false);

        PacketUtils.writeVarInt(keys.size(), out);

        for (String key : keys) {

            PacketUtils.writeString(key, out);
            write(config.get(key), out, true);
        }
    }

    static void write(Object obj, ByteBuf out, boolean writeType) {
        if (obj instanceof String) writeString((String) obj, out, writeType);
        else if (obj instanceof Number) writeNumber((Number) obj, out, writeType);
        else if (obj instanceof ConfigurationSection) writeConfig((ConfigurationSection) obj, out, writeType);
        else if(obj instanceof Map<?, ?>) writeMapConfig((Map<?, ?>)obj, out, writeType);
        else if (obj instanceof Character) writeChar((Character) obj, out, writeType);
        else if (obj instanceof Collection<?>) writeList((Collection<?>) obj, out, writeType);
        else if(obj instanceof byte[]) writeByteArray((byte[]) obj, out, writeType);
        else if(obj instanceof Boolean) writeBoolean((Boolean)obj, out, writeType);
        else throw new UnsupportedOperationException("Unsupported object type: " + obj.getClass());
    }

    private static void writeBoolean(Boolean obj, ByteBuf out, boolean writeType) {
        if(writeType) out.writeByte(ConfigType.BOOLEAN);

        out.writeBoolean(obj);
    }

    private static void writeByteArray(byte[] obj, ByteBuf out, boolean writeType) {
        if(writeType) out.writeByte(ConfigType.BYTE_ARRAY);

        PacketUtils.writeVarInt(obj.length, out);
        out.writeBytes(obj);
    }

    @SuppressWarnings("unchecked")
    static void writeMapConfig(Map<?, ?> obj, ByteBuf out, boolean writeType) {
        MemoryConfiguration config = new MemoryConfiguration();
        config.addDefaults((Map<String, Object>) obj);

        writeConfig(config, out, writeType);
    }

    static void writeList(Collection<?> list, ByteBuf out, boolean writeType) {
        int listType = getListType(list);
        if(writeType) out.writeByte(listType);

        PacketUtils.writeVarInt(list.size(), out);

        boolean writeSubType = listType == ConfigType.LIST_MASK;

        for (Object obj : list) {
            write(obj, out, writeSubType);
        }
    }

    static int getListType(Collection<?> list) {
        if (list.isEmpty()) return ConfigType.LIST_MASK;

        Iterator<?> iterator = list.iterator();

        //Get the first object's type
        int type = getObjectType(iterator.next());

        //Check all other types against the first object's type. If they are different return the generic object type.
        while (iterator.hasNext()) {
            if (type != getObjectType(iterator.next())) return ConfigType.OBJECT;
        }

        return ConfigType.LIST_MASK | type;
    }

    static int getObjectType(Object o) {
        if (o instanceof String) return ConfigType.STRING;
        else if (o instanceof Number) {
            if (o instanceof Integer || o instanceof Short) return ConfigType.VAR_INT;
            else if (o instanceof Float) return ConfigType.FLOAT;
            else if (o instanceof Double) return ConfigType.DOUBLE;
            else if (o instanceof Byte) return ConfigType.BYTE;
            else if (o instanceof Long) return ConfigType.VAR_LONG;
            else throw new UnsupportedOperationException("Unsupported number type: " + o.getClass());
        } else if (o instanceof ConfigurationSection || o instanceof Map<?, ?>) return ConfigType.CONFIG;
        else if (o instanceof Character) return ConfigType.CHAR;
        else if (o instanceof Collection<?>) return ConfigType.LIST_MASK;
        else if (o instanceof byte[]) return ConfigType.BYTE_ARRAY;
        else if (o instanceof Boolean) return ConfigType.BOOLEAN;
        else throw new UnsupportedOperationException("Unsupported object type: " + o.getClass());
    }

    static void writeNumber(Number obj, ByteBuf out, boolean writeType) {
        if (obj instanceof Integer || obj instanceof Short) {
            if(writeType) out.writeByte(ConfigType.VAR_INT);

            PacketUtils.writeVarInt(obj.intValue(), out);
        } else if (obj instanceof Float) {
            if(writeType) out.writeByte(ConfigType.FLOAT);

            out.writeFloat(obj.floatValue());
        } else if (obj instanceof Double) {
            if(writeType) out.writeByte(ConfigType.DOUBLE);

            out.writeDouble(obj.doubleValue());
        } else if (obj instanceof Byte) {
            if(writeType) out.writeByte(ConfigType.BYTE);

            out.writeByte(obj.byteValue());
        } else if (obj instanceof Long) {
            if(writeType) out.writeByte(ConfigType.VAR_LONG);

            PacketUtils.writeVarLong(obj.longValue(), out);
        }
    }

    static void writeString(String str, ByteBuf out, boolean writeType) {
        if(writeType) out.writeByte(ConfigType.STRING);

        PacketUtils.writeString(str, out);
    }

    static void writeChar(Character obj, ByteBuf out, boolean writeType) {
        if(writeType) out.writeByte(ConfigType.CHAR);

        out.writeChar(obj);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
        PacketUtils.writeVarInt(msg.getId(), out);

        ConfigurationSection payload = msg.getPayload();
        writeConfig(payload, out, false);
    }
}
