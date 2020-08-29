package com.iidp.jgtv.files.shp;

import java.io.DataInputStream;

/**
 * Represents a null value.
 */
public class Null extends AShape {

    private Null() {
        super(SHP_TYPE.NULL);
    }

    public static Null read(DataInputStream b) throws Exception {
        var p = new Null(); // 8 header
        p.readHeader(b);
        assert (p.size - 8 == 0);

        p.bbox = null;
        p.npoints = 0;
        p.nparts = 0;

        return p;
    }
}
