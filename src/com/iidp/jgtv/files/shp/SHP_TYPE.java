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
package com.iidp.jgtv.files.shp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * List of allowed types for shapes stored in a .shp file.
 */
public enum SHP_TYPE {

    NULL("NULL", 0),
    POINT("POINT", 1),
    POLYLINE("POLYLINE", 3),
    POLYGON("POLYGON", 5),
    MULTIPOINT("MULTIPOINT", 8),
    POINTZ("POINTZ", 11),
    POLYLINEZ("POLYLINEZ", 13),
    POLYGONZ("POLYGONZ", 15),
    MULTIPOINTZ("MULTIPOINTZ", 18),
    POINTM("POINTM", 21),
    POLYLINEM("POLYLINEM", 23),
    POLYGONM("POLYGONM", 25),
    MULTIPOINTM("MULTIPOINTM", 28),
    MULTIPATCH("MULTIPATCH", 31);

    private static final Map<String, SHP_TYPE> BY_LABEL = new HashMap<String, SHP_TYPE>();
    private static final Map<Integer, SHP_TYPE> BY_VALUE = new HashMap<Integer, SHP_TYPE>();
    static {
        for (SHP_TYPE e : values()) {
            BY_LABEL.put(e.name, e);
            BY_VALUE.put(e.value, e);
        }
    }

    public final int value;
    public final String name;

    private SHP_TYPE(String _name, int _value) {
        name = _name;
        value = _value;
    }

    /**
     * Returns shape type given a name, e.g. MULTIPOINTZ.
     *
     * @param name shape name
     * @return the type as SHP_TYPE
     */
    public static SHP_TYPE getShpType(String name) {
        return BY_LABEL.get(name);
    }

    /**
     * Returns shape type given a integer value, e.g. 0.
     *
     * @param value position in the list of shape types
     * @return the type as SHP_TYPE
     */
    public static SHP_TYPE getShpType(int value) {
        return BY_VALUE.get(value);
    }

    /**
     * @return a list of values associated to shape types.
     */
    public static List<Integer> getListValues() {
        var lv = new ArrayList<Integer>();
        for (SHP_TYPE s: SHP_TYPE.values()) {
            lv.add(s.value);
        }
        return lv;
    }

    public static void main(String[] args) {
        System.out.println("========================================================");
        System.out.println("Iteration:");
        System.out.println("========================================================");
        for (SHP_TYPE s: SHP_TYPE.values()) {
            System.out.println(s + ": " + s.value);
        }

        System.out.println("========================================================");
        System.out.println("By value:");
        System.out.println("========================================================");
        var lv = getListValues();
        for (Integer i: lv) {
            System.out.println(SHP_TYPE.getShpType(i));
        }
    }
}
