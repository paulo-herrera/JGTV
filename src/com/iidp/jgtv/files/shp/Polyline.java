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

public class Polyline extends AShape {

    private Polyline() {
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

        return p;
    }

    public static void main(String[] args) throws Exception {
        var src = "examples/ex1/poly_lines.shp";
        var shp = ShpFile.read(src);

        shp.list_records(true);
        System.out.println("*** ALL DONE ***");
    }
}
