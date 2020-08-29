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
