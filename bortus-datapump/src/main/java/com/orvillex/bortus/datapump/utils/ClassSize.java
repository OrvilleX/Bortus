package com.orvillex.bortus.datapump.utils;

public class ClassSize {
    public static  final int DefaultRecordHead;
    public static  final int ColumnHead;
    public static final int REFERENCE;
    public static final int OBJECT;
    public static final int ARRAY;
    public static final int ARRAYLIST;

    static {
        REFERENCE = 8;
        OBJECT = 2 * REFERENCE;
        ARRAY = align(3 * REFERENCE);
        ARRAYLIST = align(OBJECT + align(REFERENCE) + align(ARRAY) +
                (2 * Long.SIZE / Byte.SIZE));
        DefaultRecordHead = align(align(REFERENCE) + ClassSize.ARRAYLIST + 2 * Integer.SIZE / Byte.SIZE);
        ColumnHead = align(2 * REFERENCE + Integer.SIZE / Byte.SIZE);
    }

    public static int align(int num) {
        return (int)(align((long)num));
    }

    public static long align(long num) {
        return  ((num + 7) >> 3) << 3;
    }
}
