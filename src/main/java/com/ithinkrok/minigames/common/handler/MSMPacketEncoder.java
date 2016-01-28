package com.ithinkrok.minigames.common.handler;

import com.ithinkrok.minigames.common.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by paul on 28/01/16.
 */
public class MSMPacketEncoder extends MessageToByteEncoder<Packet> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
        out.writeByte(msg.getId());

        ConfigurationSection payload = msg.getPayload();
        writeConfig(payload, out);
    }

    private void writeConfig(ConfigurationSection config, ByteBuf out) {
        out.writeByte(ConfigType.CONFIG);

        Set<String> keys = config.getKeys(false);

        PacketUtils.writeVarInt(keys.size(), out);

        for (String key : keys) {

            PacketUtils.writeString(key, out);
            write(config.get(key), out);
        }
    }

    private void write(Object obj, ByteBuf out) {
        if (obj instanceof String) writeString((String) obj, out);
        else if (obj instanceof Number) writeNumber((Number) obj, out);
        else if (obj instanceof ConfigurationSection) writeConfig((ConfigurationSection) obj, out);
        else if (obj instanceof Character) writeChar((Character) obj, out);
        else if (obj instanceof List<?>) writeList((List<?>) obj, out);
        else throw new UnsupportedOperationException("Unsupported object type: " + obj.getClass());
    }

    private void writeList(List<?> list, ByteBuf out) {
        out.writeByte(getListType(list));

        PacketUtils.writeVarInt(list.size(), out);

        for(Object obj : list) {
            write(obj, out);
        }
    }

    private int getListType(List<?> list) {
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

    private int getObjectType(Object o) {
        if (o instanceof String) return ConfigType.STRING;
        else if (o instanceof Number) {
            if (o instanceof Integer || o instanceof Short) return ConfigType.VAR_INT;
            else if (o instanceof Float) return ConfigType.FLOAT;
            else if (o instanceof Double) return ConfigType.DOUBLE;
            else if (o instanceof Byte) return ConfigType.BYTE;
            else if (o instanceof Long) return ConfigType.VAR_LONG;
            else throw new UnsupportedOperationException("Unsupported number type: " + o.getClass());
        } else if (o instanceof ConfigurationSection) return ConfigType.CONFIG;
        else if (o instanceof Character) return ConfigType.CHAR;
        else if (o instanceof List<?>) return ConfigType.LIST_MASK;
        else throw new UnsupportedOperationException("Unsupported object type: " + o.getClass());
    }

    private void writeChar(Character obj, ByteBuf out) {
        out.writeByte(ConfigType.CHAR);

        out.writeChar(obj);
    }

    private void writeNumber(Number obj, ByteBuf out) {
        if (obj instanceof Integer || obj instanceof Short) {
            out.writeByte(ConfigType.VAR_INT);

            PacketUtils.writeVarInt(obj.intValue(), out);
        } else if (obj instanceof Float) {
            out.writeByte(ConfigType.FLOAT);

            out.writeFloat(obj.floatValue());
        } else if (obj instanceof Double) {
            out.writeByte(ConfigType.DOUBLE);

            out.writeDouble(obj.doubleValue());
        } else if (obj instanceof Byte) {
            out.writeByte(ConfigType.BYTE);

            out.writeByte(obj.byteValue());
        } else if (obj instanceof Long) {
            out.writeByte(ConfigType.VAR_LONG);

            PacketUtils.writeVarLong(obj.longValue(), out);
        }
    }

    private void writeString(String str, ByteBuf out) {
        out.writeByte(ConfigType.STRING);

        PacketUtils.writeString(str, out);
    }
}
