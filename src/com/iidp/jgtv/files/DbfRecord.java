package com.iidp.jgtv.files;

import com.iidp.jgtv.others.LittleEndian;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

public class DbfRecord {
    private static String FIELD_SEPARATOR = "/";

    String active;  // blank space = active, * = deleted
    List<Object> values;

    private DbfRecord(String _active, List<Object> _values){
        active = _active;
        values = _values;
    }

    public static DbfRecord readRecord(DataInputStream b, List<FieldDescriptor> fields) throws Exception {
        var nfields = fields.size();
        var flag = LittleEndian.readString(b, 1); // _ blank for available, * for deleted
        var active = String.format("<%s>", flag);

        var values = new ArrayList<Object>();
        for (int i = 0; i < nfields; i++) {
            var field = fields.get(i);
            var r = field.readValue(b);
            values.add(r);
        }
        return new DbfRecord(active, values);
    }

    public String toString() {
        var str = active;
        for (Object s: values) {
            str = str + FIELD_SEPARATOR + s;
        }
        return str;
    }

    public static List<Object> getListOfField(List<DbfRecord> records, int pos) {
        var o = new ArrayList<Object>();
        for (DbfRecord r : records) {
            var val = r.values.get(pos);
            o.add(val);
        }
        return o;
    }



}
