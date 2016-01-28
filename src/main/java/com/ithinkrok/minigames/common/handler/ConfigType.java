package com.ithinkrok.minigames.common.handler;

/**
 * Created by paul on 28/01/16.
 */
public class ConfigType {

    /**
     * Use only for as a list type for lists with multiple data types
     */
    public static final int OBJECT = 0;

    public static final int CONFIG = 1;
    public static final int VAR_INT = 2;
    public static final int VAR_LONG = 4;
    public static final int FLOAT = 4;
    public static final int DOUBLE = 5;
    public static final int CHAR = 6;
    public static final int STRING = 7;
    public static final int BYTE = 8;

    public static final int LIST_MASK = 128;
}
