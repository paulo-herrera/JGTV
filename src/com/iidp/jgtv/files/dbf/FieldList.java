package com.iidp.jgtv.files.dbf;

import com.iidp.jgtv.files.dbf.FIELD_TYPE;
import com.iidp.jgtv.files.dbf.FieldDescriptor;

import java.util.List;

/**
 * Simple container to store a field description and a list of values.
 */
public class FieldList {
    public FieldDescriptor fd;
    public List<Object> values;

    public FieldList(FieldDescriptor _fd, List<Object> _values) {
        fd = _fd;
        values = _values;
    }

    public double[] toArrayDouble() {
        assert ((fd.type == FIELD_TYPE.FLOAT) || (fd.type == FIELD_TYPE.NUMBER));

        var a = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            a[i] = (double) values.get(i);
        }
        return a;
    }

    public int[] toArrayInt() {
        // Even in this case there is no guarantee that the values are integers.
        // Hence, better to assume that all numbers are float and convert them to double.
        // Leave this for now.
        assert ( (fd.type == FIELD_TYPE.NUMBER) && (fd.fdecimal == 0));
        var a = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            a[i] = (int) values.get(i);
        }
        return a;
    }

    public String[] toArrayString() {
        //assert type == FIELD_TYPE.FLOAT;
        var a = new String[values.size()];
        for (int i = 0; i < values.size(); i++) {
            a[i] = values.get(i).toString();
        }
        return a;
    }

    private static String SEP = ";";
    @Override
    public String toString() {
        //assert type == FIELD_TYPE.FLOAT;
        var a = "Attribute<<" + fd.name + ">> ";
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                a =  a + SEP + values.get(i).toString();
            } else {
                a =  a + values.get(i).toString();
            }
        }
        return a;
    }
}

