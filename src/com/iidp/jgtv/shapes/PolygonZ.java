package com.iidp.jgtv.shapes;

import com.iidp.jgtv.files.ShpFile;
import com.iidp.jgtv.others.BoundingBox;
import com.iidp.jgtv.others.LittleEndian;

import java.io.DataInputStream;

public class PolygonZ extends AShape {

    public PolygonZ() {
        super(SHP_TYPE.POLYGONZ);
    }

    public static PolygonZ read(DataInputStream b) throws Exception {
        var p = new PolygonZ();
        p.readHeader(b);

        p.bbox = BoundingBox.read(b);

        // Read points
        p.nparts  = LittleEndian.readInt(b);
        p.npoints = LittleEndian.readInt(b);

        p.readParts(b);
        p.readXY(b);

        p.bbox = BoundingBox.readZ(b, p.bbox);
        p.readZ(b);

        p.readM(b);

        return p;
    }

    public static void main(String[] args) throws Exception {
        var src = "examples/ex1/polygonz.shp";
        var shp = ShpFile.read(src);

        shp.list_records(true);
        System.out.println("*** ALL DONE ***");
    }
}
