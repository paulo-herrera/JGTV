package com.iidp.jgtv.shapes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static SHP_TYPE getShpType(String name) {
        return BY_LABEL.get(name);
    }

    public static SHP_TYPE getShpType(int value) {
        return BY_VALUE.get(value);
    }

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
