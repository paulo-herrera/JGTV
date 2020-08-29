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

import com.iidp.jgtv.files.ShpFile;
import com.iidp.jgtv.others.BoundingBox;
import com.iidp.jgtv.others.LittleEndian;

import java.io.DataInputStream;

public class Polygon extends AShape {

    private Polygon() {
        super(SHP_TYPE.POLYGON);
    }

    public static Polygon read(DataInputStream b) throws Exception {
        // Read content
        var p = new Polygon(); // 8 header

        p.readHeader(b);

        // 4 doubles with bounding box
        p.bbox = BoundingBox.read(b);

        // Read points
        p.nparts  = LittleEndian.readInt(b);
        p.npoints = LittleEndian.readInt(b);

        p.readParts(b);
        p.readXY(b);

        /* for (int i = 0; i < p.nparts; i++) {
            var pp = LittleEndian.readInt(b);
            p.parts.add(pp);
        }

        for (int i = 0; i < p.npoints; i++) {
            var x = LittleEndian.readDouble(b);
            var y = LittleEndian.readDouble(b);
            var xy = new XY(x, y);
            p.points.add(xy);
        } */
        return p;

        //0-3 	int32 	big 	Record number (1-based)
        //var pos = b.readInt();
        // 4-7 	int32 	big 	Record length (in 16-bit words)
        //var content_size = b.readInt() * 2;
        // 0-3 	int32 	little 	Shape type (see reference below)
        // var shape_type = LittleEndian.readInt(b);
        // assert (shape_type == SHP_TYPE.POLYGON.value);
    }

    public static void main(String[] args) throws Exception {
        var src = "examples/ex1/polygons.shp";
        var shp = ShpFile.read(src);

        shp.list_records(true);
        System.out.println("*** ALL DONE ***");
    }
}
