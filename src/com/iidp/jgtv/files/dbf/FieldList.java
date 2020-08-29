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

import java.util.List;

/**
 * Container to store a field description and a list of its values.
 */
public class FieldList {
    public FieldDescriptor fd;
    public List<Object> values;

    public FieldList(FieldDescriptor _fd, List<Object> _values) {
        fd = _fd;
        values = _values;
    }

    /**
     * Returns the list of values as an array of doubles.
     *
     * @return double[]
     */
    public double[] toArrayDouble() {
        assert ((fd.type == FIELD_TYPE.FLOAT) || (fd.type == FIELD_TYPE.NUMBER));

        var a = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            a[i] = (double) values.get(i);
        }
        return a;
    }

    /**
     * Returns the list of values as an array of integers.
     *
     * @return int[]
     */
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

    /**
     * Returns the list of values as an array of strings.
     *
     * @return String[]
     */
    public String[] toArrayString() {
        //assert type == FIELD_TYPE.FLOAT;
        var a = new String[values.size()];
        for (int i = 0; i < values.size(); i++) {
            a[i] = values.get(i).toString();
        }
        return a;
    }

    // Sepárator used to concatenate values in the string that represents this list.
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

