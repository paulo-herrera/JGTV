package com.iidp.jgtv.files;

import com.iidp.jgtv.others.LittleEndian;

import java.io.DataInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FieldDescriptor {
    private static String DEFAULT_DATE_FMT = "yyyyMMdd";
    private static DateTimeFormatter dateFmt;
    static {
        dateFmt = DateTimeFormatter.ofPattern(DEFAULT_DATE_FMT);
    }

    public final String name;
    /** Type of fields: text, number, etc */
    public final FIELD_TYPE type;
    /** Length of record in bytes or characters (1 byte long)*/
    public final byte flength;
    /** Decimal digits. This seems not to be related to size in binary format */
    public final byte fdecimal;

    public FieldDescriptor(String _name, String _ftype, byte _flength, byte _fdecimal) {
        name = _name;
        var ft = _ftype.toCharArray()[0];
        type = FIELD_TYPE.getFieldType(ft);
        flength = _flength;
        fdecimal = _fdecimal;
    }

    public String readString(DataInputStream b) throws Exception {
        var str = LittleEndian.readString(b, flength);
        return str;
    }

    public Object readValue(DataInputStream b) throws Exception {
        var str = readString(b);
        return getValue(str);
    }

    public Object getValue(String str) {
        if (type == FIELD_TYPE.FLOAT) {
            return Double.parseDouble(str);
        } else if ((type == FIELD_TYPE.NUMBER) && (fdecimal > 0)) { // QGIS SEEMS TO USE NUMBER FOR INT AND FLOAT
            return Double.parseDouble(str);
        } else if ((type == FIELD_TYPE.NUMBER) && (fdecimal == 0)) { // QGIS SEEMS TO USE NUMBER FOR INT AND FLOAT
            return Double.parseDouble(str.strip());
        } else if (type == FIELD_TYPE.TEXT) {
            return str.strip();
        } else if (type == FIELD_TYPE.LOGICAL) {
            return Boolean.parseBoolean(str);
        } else if (type == FIELD_TYPE.DATE) {
            return LocalDate.parse(str, dateFmt);
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
