package com.iidp.jgtv.files;

import java.util.List;

/** Simple container to store a field description and a list of values */
public class FieldList {
    String name;
    FIELD_TYPE type;
    List<Object> values;

    FieldList(String _name, FIELD_TYPE _type, List<Object> _values) {
        name = _name;
        type = _type;
        values = _values;
    }

    public double[] toArrayDouble() {
        assert type == FIELD_TYPE.FLOAT;
        var a = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            a[i] = (double) values.get(i);
        }
        return a;
    }

    public int[] toArrayInt() {
        assert type == FIELD_TYPE.NUMBER;
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
        var a = "Attribute<<" + name + ">> ";
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

