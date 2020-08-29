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
import java.util.ArrayList;
import java.util.List;

/**
 * Container to store information of a record in the .dbf file.
 */
public class DbfRecord {
    private static String FIELD_SEPARATOR = "/";

    String active;  // blank space = active, * = deleted
    List<Object> values;

    private DbfRecord(String _active, List<Object> _values){
        active = _active;
        values = _values;
    }

    /**
     * Read a record from a binary stream according to a list of field descriptors.
     *
     * @param b binary stream
     * @param fields list of field descriptors that specify type, size, etc of each field in the record.
     * @return a DbFRecord that stores the read data
     * @throws Exception
     */
    public static DbfRecord readRecord(DataInputStream b, List<FieldDescriptor> fields) throws Exception {
        var nfields = fields.size();
        var flag = LittleEndian.readString(b, 1); // _ blank for available, * for deleted
        var active = String.format("<%s>", flag);

        var values = new ArrayList<Object>();
        for (int i = 0; i < nfields; i++) {
            var field = fields.get(i);
            assert field.flength > 0 : "Negative length: " + field;
            var r = field.readValue(b);
            values.add(r);
        }
        return new DbfRecord(active, values);
    }

    @Override
    public String toString() {
        var str = active;
        for (Object s: values) {
            str = str + FIELD_SEPARATOR + s;
        }
        return str;
    }

    /**
     * Creates a list with values of field at a given position in the list of records.
     *
     * @param records list of records stored in a .dbf file
     * @param pos position of field in the list of fields included in each record
     * @return a list of values as Objects of the specified field. Values should have
     *         the correct type, e.g. Double or Integer, depending on the type declared
     *         in the fields descriptor for each record. Thus, they can be casted to the
     *         corresponding type value.
     */
    public static List<Object> getListOfField(List<DbfRecord> records, int pos) {
        var o = new ArrayList<Object>();
        for (DbfRecord r : records) {
            var val = r.values.get(pos);
            o.add(val);
        }
        return o;
    }

}
