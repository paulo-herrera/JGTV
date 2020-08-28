package com.iidp.jgtv.others;

import java.io.DataInputStream;

public class BoundingBox {
    final double xmin, xmax;
    final double ymin, ymax;
    private double zmin, zmax;

    BoundingBox(double _xmin, double _xmax, double _ymin, double _ymax) {
        xmin = _xmin;
        xmax = _xmax;
        ymin = _ymin;
        ymax = _ymax;
        zmin = Double.NaN;
        zmax = Double.NaN;
    }

    BoundingBox(double _xmin, double _xmax, double _ymin, double _ymax, double _zmin, double _zmax) {
        xmin = _xmin;
        xmax = _xmax;
        ymin = _ymin;
        ymax = _ymax;
        zmin = _zmin;
        zmax = _zmax;
    }

    @Override
    public String toString() {
        var s = "";
        if (Double.isNaN(zmin) ) {
            s = String.format("[%g:%g, %g:%g]", xmin, xmax, ymin, ymax);
        } else {
            s = String.format("[%g:%g, %g:%g, %g:%g]", xmin, xmax, ymin, ymax, zmin, zmax);
        }
        return s;
    }

    /**
     * Reads bounding box from binary file.
     * It expects 4 8-bytes double values immediately available in the binary stream.
     * @param b binary stream.
     */
    public static BoundingBox read(DataInputStream b) throws Exception {
        var _xmin = LittleEndian.readDouble(b);
        var _xmax = LittleEndian.readDouble(b);
        var _ymin = LittleEndian.readDouble(b);
        var _ymax = LittleEndian.readDouble(b);

        return new BoundingBox(_xmin, _xmax, _ymin, _ymax);
    }

    /**
     * Reads zmin and zmax and update existing BoundingBox
     * It expects 2 8-bytes double values immediately available in the binary stream.
     */
    public static BoundingBox readZ(DataInputStream b, BoundingBox bbox) throws Exception {
        var _zmin = LittleEndian.readDouble(b);
        var _zmax = LittleEndian.readDouble(b);
        bbox.zmin = _zmin;
        bbox.zmax = _zmax;
        return bbox;
    }
}
