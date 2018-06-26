package com.ithinkrok.msm.common.message;

import com.ithinkrok.util.config.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigMessageUtils {

    private static Map<Character, String> colorNames = new HashMap<>();
    private static Map<String, Character> colorChars = new HashMap<>();

    static {
        colorNames.put('0', "black");
        colorNames.put('1', "dark_blue");
        colorNames.put('2', "dark_green");
        colorNames.put('3', "dark_aqua");
        colorNames.put('4', "dark_red");
        colorNames.put('5', "dark_purple");
        colorNames.put('6', "gold");
        colorNames.put('7', "gray");
        colorNames.put('8', "dark_gray");
        colorNames.put('9', "blue");
        colorNames.put('a', "green");
        colorNames.put('b', "aqua");
        colorNames.put('c', "red");
        colorNames.put('d', "light_purple");
        colorNames.put('e', "yellow");
        colorNames.put('f', "white");
        colorNames.put('r', "reset");

        for (Map.Entry<Character, String> entry : colorNames.entrySet()) {
            colorChars.put(entry.getValue(), entry.getKey());
        }

    }

    public static Config toConfigMessage(String message) {
        return new ConfigMessageBuilder(message).getResult();
    }

    public static void prependMessage(Config base, Config toPrepend) {
        List<Object> extra = base.getList("extra", Object.class);
        extra.add(0, toPrepend);
        base.set("extra", extra);
    }

    public static void appendMessage(Config base, Config toAppend) {
        List<Object> extra = base.getList("extra", Object.class);
        extra.add(toAppend);
        base.set("extra", extra);
    }

    public static String messageToString(Config message) {
        StringBuilder result = new StringBuilder();

        appendMessageToString(message, result);

        return result.toString();
    }

    private static void appendMessageToString(Config message, StringBuilder str) {
        String color = message.getString("color");
        if(colorChars.containsKey(color)) {
            str.append('§').append(colorChars.get(color));
        }

        if(message.getBoolean("bold")) {
            str.append("§l");
        }
        if(message.getBoolean("italic")) {
            str.append("§o");
        }
        if(message.getBoolean("underlined")) {
            str.append("§n");
        }
        if(message.getBoolean("strikethrough")) {
            str.append("§m");
        }
        if(message.getBoolean("obfuscated")) {
            str.append("§k");
        }


        str.append(message.getString("text", ""));

        List<Object> extra = message.getList("extra", Object.class);
        for (Object anObject : extra) {
            if(anObject instanceof Config) {
                appendMessageToString((Config) anObject, str);
            } else {
                str.append(anObject.toString());
            }
        }

    }

    public static String getColorCodeName(char colorCode) {
        return colorNames.get(colorCode);
    }

    public static char getColorChar(String colorName) {
        Character result = colorChars.get(colorName.toLowerCase());
        if(result == null) {
            throw new IllegalArgumentException("Invalid color name: " + colorName);
        }

        return result;
    }

}
