package com.ithinkrok.util;

import java.util.List;

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
        return message != null ? message.replace('&', '§') : null;
    }


    /**
     * Format a list as an "English" string, using commas and and
     *
     * @param list The list (e.g. ["The list", "formatted with commas", "and"])
     * @param comma String to use as a comma
     * @param and String to use as and
     * @return The list, formatted with commas and and
     */
    public static String listToString(List<String> list, String comma, String and) {
        if(list.isEmpty()) return "";
        if(list.size() == 1) return list.get(0);

        StringBuilder result = new StringBuilder(list.get(0));

        for(int index = 1; index < list.size() - 1; ++index) {
            result.append(comma).append(list.get(index));
        }

        result.append(and);
        result.append(list.get(list.size() - 1));

        return result.toString();
    }
}
