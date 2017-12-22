package com.ithinkrok.util;

import java.util.UUID;

public final class UUIDUtils {

    public static final UUID ZERO = new UUID(0, 0);

    private UUIDUtils() {
    }

    public static UUID fromStringWithoutDashes(String str) {
        if (str == null) {
            throw new NullPointerException("str should not be null");
        }

        if (str.length() != 32) {
            throw new IllegalArgumentException(
                    "UUID without dashes should be 32 characters, but was given " + str.length() + " characters"
            );
        }

        String padded = str.substring(0, 8) + "-" +
                        str.substring(4, 12) + "-" +
                        str.substring(12, 16) + "-" +
                        str.substring(16, 20) + "-" +
                        str.substring(20, 32);

        return UUID.fromString(padded);
    }

}
