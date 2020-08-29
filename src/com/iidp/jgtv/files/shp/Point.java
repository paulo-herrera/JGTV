package com.iidp.jgtv.files.shp;

import com.iidp.jgtv.files.ShpFile;

import java.io.DataInputStream;

public class Point extends AShape {

    public Point() {
        super(SHP_TYPE.POINT);
    }

   /* public Point(int _idx, int _size) {
        super(SHP_TYPE.POINT, _idx, _size);
    } */

   /* private Point setXY(double x, double y) {
        var xy = new XY(x,y);
        super.points.add(xy);
        super.npoints = 1;
        super.parts.add(1);
        super.nparts = 1;
        super.bbox = null;
        return this;
    } */

    public static Point read(DataInputStream b) throws Exception {
        var p = new Point(); // 8 header
        p.readHeader(b);

        p.bbox = null; // BoundingBox.read(b);

        p.npoints = 1;
        p.nparts = 1;

        p.parts.add(0);
        p.readXY(b);

        return p;
    }

    public static void main(String[] args) throws Exception {
        var src = "examples/ex1/points.shp";
        var shp = ShpFile.read(src);

        shp.list_records(true);
        System.out.println("*** ALL DONE ***");
    }
}
