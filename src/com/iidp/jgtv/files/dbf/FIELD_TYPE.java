/*
 *  Copyright (C) 2020 Paulo A. Herrera <pauloa.herrera@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.iidp.jgtv.files.dbf;

import java.util.HashMap;
import java.util.Map;

/**
 * List of allowed types for fields of records in the .dbf file.
 */
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

    /**
     * Returns the long name of the type associated to the single char name
     * @param name single char, e.g. \"N\" or \"F\"
     * @return long name, e.g. \"Text\" for \"T\"
     */
    static String getLongName(Character name) {
        return BY_SHORT_NAME.get(name).longName;
    }

    /**
     * Tests if name (single char) corresponds to one of the allowed field types.
     *
     * @param name single char, e.g. \"N\" or \"F\"
     * @return true or false
     */
    public static boolean isFieldType(Character name) {
        return BY_SHORT_NAME.containsKey(name);
    }

    static FIELD_TYPE getFieldType(Character name) {
        return BY_SHORT_NAME.get(name);
    }
}
