package com.ithinkrok.msm.common.handler;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by paul on 28/01/16.
 */
public class PacketUtils {

    public static void writeString(String s, DataOutput buf) throws IOException {
        Preconditions.checkArgument(s.length() <= Short.MAX_VALUE,
                "Cannot send string longer than Short.MAX_VALUE (got %s characters)", s.length());

        byte[] b = s.getBytes(Charsets.UTF_8);
        writeVarInt(b.length, buf);
        buf.write(b);
    }

    public static void writeVarInt(int value, DataOutput output) throws IOException {
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

    public static String readString(DataInput buf) throws IOException {
        int len = readVarInt(buf);
        Preconditions.checkArgument(len <= Short.MAX_VALUE,
                "Cannot receive string longer than Short.MAX_VALUE (got %s characters)", len);

        byte[] b = new byte[len];
        buf.readFully(b);

        return new String(b, Charsets.UTF_8);
    }

    public static int readVarInt(DataInput input) throws IOException {
        return readVarInt(input, 5);
    }

    @SuppressWarnings("Duplicates")
    public static int readVarInt(DataInput input, int maxBytes) throws IOException {
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

    public static void writeVarLong(long value, DataOutput output) throws IOException {
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
    public static long readVarLong(DataInput input) throws IOException {
        return readVarLong(input, 10);
    }

    @SuppressWarnings("Duplicates")
    public static long readVarLong(DataInput input, int maxBytes) throws IOException {
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
