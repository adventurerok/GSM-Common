package com.ithinkrok.msm.common.message;

import com.ithinkrok.util.config.Config;
import com.ithinkrok.util.config.MemoryConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by paul on 17/12/16.
 */
public class ConfigMessageBuilder {

    public static final String CLICK_OPEN_URL = "open_url";
    public static final String CLICK_RUN_COMMAND = "run_command";
    public static final String CLICK_SUGGEST_COMMAND = "suggest_command";

    public static final String HOVER_SHOW_ITEM = "show_item";
    public static final String HOVER_SHOW_ACHIEVEMENT = "show_achievement";
    public static final String HOVER_SHOW_TEXT = "show_text";

    private final Config result;

    private final Map<String, Config> namedSubconfigs = new HashMap<>();

    protected ConfigMessageBuilder(String base) {
        result = handleBase(base);

        namedSubconfigs.put("all", result);
    }

    public Config getResult() {
        return result;
    }

    public Config getNamedSubconfig(String name) {
        return namedSubconfigs.get(name);
    }

    public void setClickAction(String subconfigName, String action, String value) {
        setAction("clickEvent", subconfigName, action, value);
    }

    public void setHoverAction(String subconfigName, String action, Object value) {
        setAction("hoverEvent", subconfigName, action, value);
    }

    private void setAction(String eventName, String subconfigName, String action, Object value) {
        Config event = new MemoryConfig();
        event.set("action", action);
        event.set("value", value);

        Config config = getNamedSubconfig(subconfigName);

        config.set(eventName, event);
    }

    private Config handleBase(String base) {
        List<Config> extras = new ArrayList<>();

        int bracketLevel = 0;

        StringBuilder current = new StringBuilder();

        char last = ' ';

        for(int index = 0; index < base.length(); ++index) {
            char c = base.charAt(index);

            if(last == '\\') {
                current.append(c);
                last = ' ';
                continue;
            }

            if(c == '{') {
                ++bracketLevel;

                if(current.length() > 0 && bracketLevel == 1) {
                    extras.add(handleNonBracket(current.toString()));
                    current = new StringBuilder();
                }
            } else if(c == '}') {
                --bracketLevel;

                if(current.length() > 0 && bracketLevel == 0) {
                    extras.add(handleBracket(current.toString()));
                    current = new StringBuilder();
                }
            } else if(c == '\\') {
                last = c;
                continue;
            } else {
                current.append(c);
            }

            last = c;
        }

        if(current.length() != 0) {
            extras.add(handleNonBracket(current.toString()));
        }

        Config result = new MemoryConfig();
        result.set("text", "");
        result.set("extra", extras);

        return result;
    }

    private static Map<Character, String> colorNames = new HashMap<>();

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
    }

    private Config handleNonBracket(String text) {
        Config baseConfig = new MemoryConfig();

        List<Config> extras = new ArrayList<>();

        char last = ' ';

        StringBuilder current = new StringBuilder();

        for(int index = 0; index < text.length(); ++index) {
            char c = text.charAt(index);

            if(last == '§' && c != '§') {
                c = Character.toLowerCase(c);

                if(current.length() > 0) {
                    MemoryConfig copy = new MemoryConfig(baseConfig);
                    copy.set("text", current.toString());
                    extras.add(copy);

                    current = new StringBuilder();
                }

                if(colorNames.containsKey(c)) {
                    baseConfig.set("color", colorNames.get(c));
                } else switch(c) {
                    case 'l':
                        baseConfig.set("bold", !baseConfig.getBoolean("bold"));
                        break;
                    case 'o':
                        baseConfig.set("italic", !baseConfig.getBoolean("italic"));
                        break;
                    case 'n':
                        baseConfig.set("underlined", !baseConfig.getBoolean("underlined"));
                        break;
                    case 'm':
                        baseConfig.set("strikethrough", !baseConfig.getBoolean("strikethrough"));
                        break;
                    case 'k':
                        baseConfig.set("obfuscated", !baseConfig.getBoolean("obfuscated"));
                        break;

                }

                last = ' ';
                continue;
            } if(last != '§' && c == '§') {
                last = '§';
            } else if(last == '§') {
                current.append(c);
                last = ' ';
            } else {
                current.append(c);
                last = c;
            }

        }

        if(current.length() > 0) {
            MemoryConfig copy = new MemoryConfig(baseConfig);
            copy.set("text", current.toString());
            extras.add(copy);
        }

        Config result = new MemoryConfig();
        result.set("text", "");
        result.set("extra", extras);

        return result;
    }

    private Config handleBracket(String inBracket) {
        int colonIndex = inBracket.indexOf(':');

        if(colonIndex < 0) {
            return handleBase(inBracket);
        }

        String name = inBracket.substring(0, colonIndex);

        String inner = inBracket.substring(colonIndex + 1);

        Config result = handleBase(inner);

        namedSubconfigs.put(name, result);

        return result;
    }

}
