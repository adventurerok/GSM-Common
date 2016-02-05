package com.ithinkrok.msm.common.handler;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paul on 28/01/16.
 */
public class PacketUtils {

    public static void writeString(String s, ByteBuf buf) {
        Preconditions.checkArgument(s.length() <= Short.MAX_VALUE,
                "Cannot send string longer than Short.MAX_VALUE (got %s characters)", s.length());

        byte[] b = s.getBytes(Charsets.UTF_8);
        writeVarInt(b.length, buf);
        buf.writeBytes(b);
    }

    public static void writeVarInt(int value, ByteBuf output) {
        int part;
        while (true) {
            part = value & 0x7F;

            value >>>= 7;
            if (value != 0) {
                part |= 0x80;
            }

            output.writeByte(part);

            if (value == 0) {
                break;
            }
        }
    }

    public static String readString(ByteBuf buf) {
        int len = readVarInt(buf);
        Preconditions.checkArgument(len <= Short.MAX_VALUE,
                "Cannot receive string longer than Short.MAX_VALUE (got %s characters)", len);

        byte[] b = new byte[len];
        buf.readBytes(b);

        return new String(b, Charsets.UTF_8);
    }

    public static int readVarInt(ByteBuf input) {
        return readVarInt(input, 5);
    }

    @SuppressWarnings("Duplicates")
    public static int readVarInt(ByteBuf input, int maxBytes) {
        int out = 0;
        int bytes = 0;
        byte in;
        while (true) {
            in = input.readByte();

            out |= (in & 0x7F) << (bytes++ * 7);

            if (bytes > maxBytes) {
                throw new RuntimeException("VarInt too big");
            }

            if ((in & 0x80) != 0x80) {
                break;
            }
        }

        return out;
    }

    public static void writeVarLong(long value, ByteBuf output) {
        int part;
        while (true) {
            part = (int) (value & 0x7F);

            value >>>= 7;
            if (value != 0) {
                part |= 0x80;
            }

            output.writeByte(part);

            if (value == 0) {
                break;
            }
        }
    }

    @SuppressWarnings("Duplicates")
    public static long readVarLong(ByteBuf input) {
        return readVarLong(input, 10);
    }

    @SuppressWarnings("Duplicates")
    public static long readVarLong(ByteBuf input, int maxBytes) {
        long out = 0;
        long bytes = 0;
        byte in;
        while (true) {
            in = input.readByte();

            out |= (in & 0x7FL) << (bytes++ * 7L);

            if (bytes > maxBytes) {
                throw new RuntimeException("VarLong too big");
            }

            if ((in & 0x80) != 0x80L) {
                break;
            }
        }

        return out;
    }
}
