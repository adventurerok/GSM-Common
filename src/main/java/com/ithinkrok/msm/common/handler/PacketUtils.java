package com.ithinkrok.msm.common.handler;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;

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

    @SuppressWarnings("Duplicates")
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

    @SuppressWarnings("Duplicates")
    public static void writeVarInt(int value, ByteBuf output) throws IOException {
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

    public static void writeBigDecimal(BigDecimal value, DataOutput output) throws IOException {
        String str = value.toString();

        int upper = 0;
        int part;

        for (int index = 0; index < str.length(); ++index) {
            char c = str.charAt(index);

            part = getBCD(c);

            if(part == -1) {
                throw new RuntimeException("Unexpected character in BigDecimal.toString(): " +
                                           c + " in " + str);
            }

            if(index % 2 == 0) {
                upper = part << 4;
            } else {
                output.writeByte(upper | part);
            }
        }

        if(str.length() % 2 == 0) {
            output.writeByte(0xff);
        } else {
            output.writeByte(upper | 0xf);
        }
    }

    private static int getBCD(char c) {
        if ('0' <= c && c <= '9') {
            return (byte) (c - '0');
        }

        switch (c) {
            case '.':
                return 0xa;
            case '+':
                return 0xb;
            case '-':
                return 0xc;
            case 'E':
            case 'e':
                return 0xd;
            default:
                return -1;
        }
    }

    private static char fromBCD(int bcd) {
        if(0 <= bcd && bcd <= 9) {
            return (char) (bcd + '0');
        }

        switch(bcd) {
            case 0xa:
                return '.';
            case 0xb:
                return '+';
            case 0xc:
                return '-';
            case 0xd:
                return 'E';
            case 0xf:
                return '\0';
            default:
                throw new RuntimeException("Unknown BCD code: 0x" + Integer.toString(bcd, 16));
        }
    }

    public static BigDecimal readBigDecimal(DataInput input) throws IOException {
        StringBuilder str = new StringBuilder();

        while(true) {
            int full = input.readByte() & 0xff;

            char upper = fromBCD(full >> 4);
            if(upper == '\0') break;

            str.append(upper);

            char lower = fromBCD(full & 0xf);
            if(lower == '\0') break;

            str.append(lower);
        }

        return new BigDecimal(str.toString());
    }

}
