package com.ithinkrok.util.config;

/**
 * Created by paul on 28/01/16.
 */
public class BinaryConfigType {

    /**
     * Use only for as a list type for lists with multiple data types
     */
    public static final int OBJECT = 0;

    public static final int CONFIG = 1;
    public static final int VAR_INT = 2;
    public static final int VAR_LONG = 4;
    public static final int FLOAT = 5;
    public static final int DOUBLE = 6;
    public static final int CHAR = 7;
    public static final int STRING = 8;
    public static final int BYTE = 9;

    public static final int BYTE_ARRAY = 10;
    public static final int BOOLEAN = 11;
    public static final int NULL = 12;
    public static final int BIG_DECIMAL = 13;

    public static final int LIST_MASK = 128;

}
