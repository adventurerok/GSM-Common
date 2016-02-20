package com.ithinkrok.util;

/**
 * Created by paul on 18/02/16.
 */
public final class StringUtils {

    private StringUtils() {

    }

    public static String removeMinecraftChatCodes(String input) {
        return input.replaceAll("[§&][0-9a-zA-Z]", "");
    }

    public static String convertAmpersandToSelectionCharacter(String message) {
        return message.replace('&', '§');
    }
}
