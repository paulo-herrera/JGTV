package com.iidp.jgtv.files.dbf;

import java.util.HashMap;
import java.util.Map;

public enum FIELD_TYPE {
    TEXT('C', "Text"),
    NUMBER('N', "Number"),
    FLOAT('F', "Float"),
    LOGICAL('L', "Logical"),
    DATE('D', "Date"), // default format YYYYMMDD
    MEMO('M', "Memo");

    public static Map<Character, FIELD_TYPE> BY_SHORT_NAME = new HashMap<Character, FIELD_TYPE>();
    static {
        for (FIELD_TYPE e : values()) {
            BY_SHORT_NAME.put(e.shortName, e);
        }
    }

    public final char shortName;
    public final String longName;

    private FIELD_TYPE(char _shortName, String _longName) {
        shortName = _shortName;
        longName = _longName;
    }

    static String getLongName(Character name) {
        return BY_SHORT_NAME.get(name).longName;
    }

    public static boolean isFieldType(Character name) {
        return BY_SHORT_NAME.containsKey(name);
    }

    static FIELD_TYPE getFieldType(Character name) {
        return BY_SHORT_NAME.get(name);
    }
}
