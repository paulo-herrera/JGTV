package com.iidp.jgtv.shapes;

import com.iidp.jgtv.files.ShpFile;
import com.iidp.jgtv.others.BoundingBox;
import com.iidp.jgtv.others.LittleEndian;

import java.io.DataInputStream;

public class Polyline extends AShape {
    public Polyline() {
        super(SHP_TYPE.POLYLINE);
    }

    public static Polyline read(DataInputStream b) throws Exception {
        var p = new Polyline();
        p.readHeader(b);
        // 4 doubles with bounding box
        p.bbox = BoundingBox.read(b);

        //
        p.nparts  = LittleEndian.readInt(b);
        p.npoints = LittleEndian.readInt(b);

        p.readParts(b);
        p.readXY(b);

        /*for (int i = 0; i < p.nparts; i++) {
            var pp = LittleEndian.readInt(b);
            p.parts.add(pp);
        }

        for (int i = 0; i < p.npoints; i++) {
            var x = LittleEndian.readDouble(b);
            var y = LittleEndian.readDouble(b);
            var xy = new XY(x, y);
            p.points.add(xy);
        }*/
        //0-3 	int32 	big 	Record number (1-based)
        //var pos = b.readInt();
        // 4-7 	int32 	big 	Record length (in 16-bit words)
        //var content_size = b.readInt() * 2;
        // Read content
        // 0-3 	int32 	little 	Shape type (see reference below)
        //var shape_type = LittleEndian.readInt(b);
        //assert (shape_type == SHP_TYPE.POLYLINE.value);

        //p.display();
        return p;
    }

    public static void main(String[] args) throws Exception {
        var src = "examples/ex1/poly_lines.shp";
        var shp = ShpFile.read(src);

        shp.list_records(true);
        System.out.println("*** ALL DONE ***");
    }
}
