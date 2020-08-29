package com.iidp.jgtv.files.shp;

import com.iidp.jgtv.files.ShpFile;
import com.iidp.jgtv.others.BoundingBox;
import com.iidp.jgtv.others.LittleEndian;

import java.io.DataInputStream;

public class Multipoint extends AShape {

    public Multipoint() {
        super(SHP_TYPE.MULTIPOINT);
    }

    public static Multipoint read(DataInputStream b) throws Exception {
        var p = new Multipoint();
        p.readHeader(b);

        p.bbox = BoundingBox.read(b);

        p.nparts = 1;
        p.npoints = LittleEndian.readInt(b);

        p.parts.add(1);
        p.readXY(b);

        return p;
    }

    /**
     * Todo: Check shape file example for this. It only contains one point per record.
     */
    public static void main(String[] args) throws Exception {
        var src = "examples/ex1/multi_points.shp";
        var shp = ShpFile.read(src);

        shp.list_records(true);
        System.out.println("*** ALL DONE ***");
    }
}
