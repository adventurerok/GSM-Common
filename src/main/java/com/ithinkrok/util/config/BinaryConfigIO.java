package com.ithinkrok.util.config;

import com.ithinkrok.msm.common.handler.PacketUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

/**
 * Created by paul on 21/02/16.
 */
public class BinaryConfigIO {

    public static void saveConfig(DataOutput output, Config config) throws IOException {
        writeConfig(config, output, false);
    }

    public static Config loadConfig(DataInput input) throws IOException {
        return readConfig(input);
    }

    static Config readConfig(DataInput msg) throws IOException {
        char separator = (char)(msg.readByte() & 0xFF);

        int size = PacketUtils.readVarInt(msg);

        Config config = new MemoryConfig(separator);

        for (int count = 0; count < size; ++count) {
            String key = PacketUtils.readString(msg);
            Object value = read(msg);

            config.set(key, value);
        }

        return config;
    }

    static Object read(DataInput msg) throws IOException {
        int type = msg.readByte() & 0xFF;

        return read(msg, type);
    }

    private static Object read(DataInput msg, int type) throws IOException {
        switch (type) {
            case BinaryConfigType.STRING:
                return PacketUtils.readString(msg);
            case BinaryConfigType.VAR_INT:
                return PacketUtils.readVarInt(msg);
            case BinaryConfigType.FLOAT:
                return msg.readFloat();
            case BinaryConfigType.DOUBLE:
                return msg.readDouble();
            case BinaryConfigType.BYTE:
                return msg.readByte();
            case BinaryConfigType.VAR_LONG:
                return PacketUtils.readVarLong(msg);
            case BinaryConfigType.CHAR:
                return msg.readChar();
            case BinaryConfigType.CONFIG:
                return readConfig(msg);
            case BinaryConfigType.BYTE_ARRAY:
                return readByteArray(msg);
            case BinaryConfigType.BOOLEAN:
                return msg.readBoolean();
            case BinaryConfigType.NULL:
                return null;
            default:
                if ((type & BinaryConfigType.LIST_MASK) == BinaryConfigType.LIST_MASK) {
                    return readList(msg, type);
                } else throw new UnsupportedOperationException("Unsupported data type:" + type);
        }
    }

    private static byte[] readByteArray(DataInput msg) throws IOException {
        int length = PacketUtils.readVarInt(msg);

        byte[] result = new byte[length];
        msg.readFully(result);

        return result;
    }

    private static List<?> readList(DataInput msg, int type) throws IOException {
        type ^= BinaryConfigType.LIST_MASK;

        int size = PacketUtils.readVarInt(msg);
        List<Object> list = new ArrayList<>(size);

        for (int count = 0; count < size; ++count) {
            if (type == BinaryConfigType.OBJECT) list.add(read(msg));
            else list.add(read(msg, type));
        }

        return list;
    }

    private static void writeConfig(Config config, DataOutput out, boolean writeType) throws IOException {
        if(writeType) out.writeByte(BinaryConfigType.CONFIG);

        //Only support config separators that use char codes from 0-255
        out.writeByte(config.getSeparator() & 0xFF);

        Set<String> keys = config.getKeys(false);

        PacketUtils.writeVarInt(keys.size(), out);

        for (String key : keys) {

            PacketUtils.writeString(key, out);
            write(config.get(key), out, true);
        }
    }

    static void write(Object obj, DataOutput out, boolean writeType) throws IOException {
        if (obj instanceof String) writeString((String) obj, out, writeType);
        else if(obj instanceof UUID) writeString(obj.toString(), out, writeType);
        else if (obj instanceof Number) writeNumber((Number) obj, out, writeType);
        else if (obj instanceof Config) writeConfig((Config) obj, out, writeType);
        else if(obj instanceof Map<?, ?>) writeMapConfig((Map<?, ?>)obj, out, writeType);
        else if (obj instanceof Character) writeChar((Character) obj, out, writeType);
        else if (obj instanceof Collection<?>) writeList((Collection<?>) obj, out, writeType);
        else if(obj instanceof byte[]) writeByteArray((byte[]) obj, out, writeType);
        else if(obj instanceof Boolean) writeBoolean((Boolean)obj, out, writeType);
        else if(obj == null) writeNull(out, writeType);
        else throw new UnsupportedOperationException("Unsupported object type: " + obj.getClass());
    }

    private static void writeNull(DataOutput out, boolean writeType) throws IOException {
        if(writeType) out.writeByte(BinaryConfigType.NULL);
    }

    private static void writeBoolean(Boolean obj, DataOutput out, boolean writeType) throws IOException {
        if(writeType) out.writeByte(BinaryConfigType.BOOLEAN);

        out.writeBoolean(obj);
    }

    private static void writeByteArray(byte[] obj, DataOutput out, boolean writeType) throws IOException {
        if(writeType) out.writeByte(BinaryConfigType.BYTE_ARRAY);

        PacketUtils.writeVarInt(obj.length, out);
        out.write(obj);
    }

    @SuppressWarnings("unchecked")
    static void writeMapConfig(Map<?, ?> obj, DataOutput out, boolean writeType) throws IOException {
        Config config = new MemoryConfig();
        config.setAll((Map<String, Object>) obj);

        writeConfig(config, out, writeType);
    }

    static void writeList(Collection<?> list, DataOutput out, boolean writeType) throws IOException {
        int listType = getListType(list);
        if(writeType) out.writeByte(listType);

        PacketUtils.writeVarInt(list.size(), out);

        boolean writeSubType = listType == BinaryConfigType.LIST_MASK;

        for (Object obj : list) {
            write(obj, out, writeSubType);
        }
    }

    static int getListType(Collection<?> list) {
        if (list.isEmpty()) return BinaryConfigType.LIST_MASK;

        Iterator<?> iterator = list.iterator();

        //Get the first object's type
        int type = getObjectType(iterator.next());

        //Check all other types against the first object's type. If they are different return the generic object type.
        while (iterator.hasNext()) {
            if (type != getObjectType(iterator.next())) return BinaryConfigType.OBJECT;
        }

        return BinaryConfigType.LIST_MASK | type;
    }

    static int getObjectType(Object o) {
        if (o instanceof String) return BinaryConfigType.STRING;
        else if (o instanceof Number) {
            if (o instanceof Integer || o instanceof Short) return BinaryConfigType.VAR_INT;
            else if (o instanceof Float) return BinaryConfigType.FLOAT;
            else if (o instanceof Double) return BinaryConfigType.DOUBLE;
            else if (o instanceof Byte) return BinaryConfigType.BYTE;
            else if (o instanceof Long) return BinaryConfigType.VAR_LONG;
            else throw new UnsupportedOperationException("Unsupported number type: " + o.getClass());
        } else if (o instanceof Config || o instanceof Map<?, ?>) return BinaryConfigType.CONFIG;
        else if (o instanceof Character) return BinaryConfigType.CHAR;
        else if (o instanceof Collection<?>) return BinaryConfigType.LIST_MASK;
        else if (o instanceof byte[]) return BinaryConfigType.BYTE_ARRAY;
        else if (o instanceof Boolean) return BinaryConfigType.BOOLEAN;
        else if(o == null) return BinaryConfigType.NULL;
        else throw new UnsupportedOperationException("Unsupported object type: " + o.getClass());
    }

    static void writeNumber(Number obj, DataOutput out, boolean writeType) throws IOException {
        if (obj instanceof Integer || obj instanceof Short) {
            if(writeType) out.writeByte(BinaryConfigType.VAR_INT);

            PacketUtils.writeVarInt(obj.intValue(), out);
        } else if (obj instanceof Float) {
            if(writeType) out.writeByte(BinaryConfigType.FLOAT);

            out.writeFloat(obj.floatValue());
        } else if (obj instanceof Double) {
            if(writeType) out.writeByte(BinaryConfigType.DOUBLE);

            out.writeDouble(obj.doubleValue());
        } else if (obj instanceof Byte) {
            if(writeType) out.writeByte(BinaryConfigType.BYTE);

            out.writeByte(obj.byteValue());
        } else if (obj instanceof Long) {
            if(writeType) out.writeByte(BinaryConfigType.VAR_LONG);

            PacketUtils.writeVarLong(obj.longValue(), out);
        }
    }

    static void writeString(String str, DataOutput out, boolean writeType) throws IOException {
        if(writeType) out.writeByte(BinaryConfigType.STRING);

        PacketUtils.writeString(str, out);
    }

    static void writeChar(Character obj, DataOutput out, boolean writeType) throws IOException {
        if(writeType) out.writeByte(BinaryConfigType.CHAR);

        out.writeChar(obj);
    }
}
