package com.ithinkrok.msm.common.message;

public final class LogColor {

    public static final String RESET_COLOR = "§r";

    public static final String ARG1_COLOR = "§1";
    public static final String ARG2_COLOR = "§2";
    public static final String ARG3_COLOR = "§3";

    public static final String GAME_COLOR = "§5"; //dark purple
    public static final String OTHER1_COLOR = "§7"; //gray
    public static final String OTHER2_COLOR = "§8"; //dark gray
    public static final String USER_COLOR = "§a"; //green
    public static final String SERVER_COLOR = "§b"; //aqua
    public static final String TYPE_COLOR = "§d"; //pink

    public static final String KEY_COLOR = "§b"; //aqua
    public static final String VALUE_COLOR = "§6"; //gold

    public static String FORMAT(String color, Object name) {
        return FORMAT(color, name, RESET_COLOR);
    }

    public static String FORMAT(String color, Object name, String reset) {
        return color + name + reset;
    }

    public static String FORMAT_USER(String name) {
        return FORMAT_USER(name, RESET_COLOR);
    }

    public static String FORMAT_USER(String name, String reset) {
        return USER_COLOR + name + reset;
    }

    public static String FORMAT_SERVER(String name) {
        return FORMAT_SERVER(name, RESET_COLOR);
    }

    public static String FORMAT_SERVER(String name, String reset) {
        return SERVER_COLOR + name + reset;
    }

    public static String FORMAT_GAME(String name) {
        return FORMAT_GAME(name, RESET_COLOR);
    }

    public static String FORMAT_GAME(String name, String reset) {
        return GAME_COLOR + name + reset;
    }

    public static String FORMAT_TYPE(String name) {
        return FORMAT_TYPE(name, RESET_COLOR);
    }

    public static String FORMAT_TYPE(String name, String reset) {
        return TYPE_COLOR + name + reset;
    }

    public static String FORMAT_KEY_VALUE_GROUP(String reset, Object... keyValues) {
        if(keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("keyValues must have even length");
        }

        StringBuilder builder = new StringBuilder();

        for(int index = 0; index < keyValues.length; index += 2) {
            String key = keyValues[index].toString();
            String value = keyValues[index + 1].toString();

            builder.append(KEY_COLOR).append(key);
            builder.append(reset).append(": ");
            if(value.contains(" ") || value.contains(":") || value.contains(",")) {
                builder.append("'").append(VALUE_COLOR).append(value).append(reset).append("'");
            } else {
                builder.append(VALUE_COLOR).append(value).append(reset);
            }

            if(index < keyValues.length - 2) {
                builder.append(", ");
            }
        }

        return builder.toString();
    }
}
