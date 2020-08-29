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
package com.iidp.jgtv.others;

import java.io.DataInputStream;

/**
 * Defines a bounding box (xmin, xmax, ymin, ymax, zmin, zmax) to store and read
 * information.
 */
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
