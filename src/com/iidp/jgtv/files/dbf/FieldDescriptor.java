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

import com.iidp.jgtv.others.LittleEndian;

import java.io.DataInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Specifies the type, size, etc of a single field included as part
 * of a record in the .dbf file.
 */
public class FieldDescriptor {
    // This format is supposed to be the only one allowed
    private static String DEFAULT_DATE_FMT = "yyyyMMdd";
    private static DateTimeFormatter dateFmt;
    static {
        dateFmt = DateTimeFormatter.ofPattern(DEFAULT_DATE_FMT);
    }

    /** Field name */
    public final String name;
    /** Type of fields: text, number, etc */
    public final FIELD_TYPE type;
    /** Length of record in bytes or characters (1 byte long)*/
    public final byte flength;
    /** Decimal digits. Length of decimal digits in bytes or characters (1 byte long) */
    public final byte fdecimal;

    public FieldDescriptor(String _name, String _ftype, byte _flength, byte _fdecimal) {
        name = _name;
        var ft = _ftype.toCharArray()[0];
        type = FIELD_TYPE.getFieldType(ft);
        flength = _flength;
        fdecimal = _fdecimal;
    }

    /**
     * Read text that is a field of a record specified for this field descriptor.
     * NOTE: Value is read as native endian.
     *
     * @param b binary stream.
     * @return read text as String
     * @throws Exception
     */
    public String readString(DataInputStream b) throws Exception {
        var str = LittleEndian.readString(b, flength);
        return str;
    }

    public Object readValue(DataInputStream b) throws Exception {
        var str = readString(b);
        return getValue(str);
    }

    public Object getValue(String str) {
        str = str.strip();

        if (type == FIELD_TYPE.FLOAT) {
            assert str.length() > 0 : "Empty string";
            return Double.parseDouble(str);
        } else if ((type == FIELD_TYPE.NUMBER) && (fdecimal > 0)) { // QGIS SEEMS TO USE NUMBER FOR INT AND FLOAT
            assert str.length() > 0 : "Empty string";
            return Double.parseDouble(str);
        } else if ((type == FIELD_TYPE.NUMBER) && (fdecimal == 0)) { // QGIS SEEMS TO USE NUMBER FOR INT AND FLOAT
            assert str.length() > 0 : "Empty string";
            return Double.parseDouble(str);
        } else if (type == FIELD_TYPE.TEXT) {
            return str.strip();
        } else if (type == FIELD_TYPE.LOGICAL) {
            assert str.length() > 0 : "Empty string";
            return Boolean.parseBoolean(str);
        } else if (type == FIELD_TYPE.DATE) {
            if (str.length() == 8) {
                return LocalDate.parse(str, dateFmt);
            } else {
                assert false : "Wrong date format: >" + str + "<";
                return null;
            }

        } else {
            assert false: "Unknown type";
            return null;
        }
    }

    @Override
    public String toString() {
        var str = String.format("Field[%6s]<%3d,%2d>: %11s", type.longName, flength, fdecimal, name);
        return str;
    }
}
