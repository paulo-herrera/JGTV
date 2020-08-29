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

import com.iidp.jgtv.others.BoundingBox;
import com.iidp.jgtv.others.LittleEndian;
import com.iidp.jgtv.others.Range;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all types of shapes that can be stored in a .shp file.
 * There is one derived class of this class for each type of shape allowed
 * in a .shp file.
 */
public abstract class AShape {
    /** Value assigned to z-coordinate for files that do not contain that information */
    private static double DEFAULT_ZVALUE = 0.0;
    public static void setDefaultZValue(double value) {
        DEFAULT_ZVALUE = value;
    }

    /** Shape type */
    public final SHP_TYPE type;
    /** Record position */
    public int idx;
    /** Returns position of this record in the file */
    public int getIDX() { return idx; }
    /** X coordinate */
    protected List<Double> x;
    /** Y coordinate */
    protected List<Double> y;
    /** Z coordinate */
    protected List<Double> z;
    /** Number of points */
    protected int npoints;
    public int getNPoints() { return npoints; }

    /** Array of parts */
    protected List<Integer> parts;
    /** Number of parts */
    protected int nparts;
    /** Returns number of parts in this shape */
    public int getNParts() { return nparts; }
    /** Bounding box */
    protected BoundingBox bbox;
    /** Range for M variable */
    protected Range rangeM;
    /** Array with M values */
    List<Double> m;
    /** Size in bytes of this shape, it should include header (8 bytes) for each record */
    public int size;

    /** Constructor should only be called from derived classes */
    protected AShape(SHP_TYPE _type) {
        type = _type;
        idx = -1;
        size = -1;
        x = new ArrayList<Double>();
        y = new ArrayList<Double>();
        z = new ArrayList<Double>();
        parts = new ArrayList<Integer>();
        rangeM = new Range(Double.NaN, Double.NaN);
        m = new ArrayList<Double>();
    }

    /**
     * Reads header of record in .shp file
     *
     * @param b binary stream forgot
     * @return an AShape that stores read information
     * @throws Exception
     */
    protected AShape readHeader(DataInputStream b) throws Exception {
        //0-3 	int32 	big 	Record number (1-based)
        var pos = b.readInt();
        // 4-7 	int32 	big 	Record length (in 16-bit words)
        var content_size = b.readInt() * 2;

        var shape_type = LittleEndian.readInt(b); // 4
        assert shape_type == this.type.value;

        this.idx = pos;
        this.size = content_size + 8;

        return this;
    }

    /**
     * Reads list of parts in the record.
     * Note: nparts should have been set before
     *
     * @param b binary stream
     * @return a AShape that stores the read information
     * @throws Exception
     */
    protected AShape readParts(DataInputStream b) throws Exception {
        for (int i = 0; i < this.nparts; i++) {
            var pp = LittleEndian.readInt(b);
            this.parts.add(pp);
        }
        return this;
    }

    /**
     * Reads list of  coordinates (x,y) for points in a record.
     * Note: npoints should have been set before
     *
     * @param b binary stream
     * @return a AShape that stores the read information
     * @throws Exception
     */
    protected AShape readXY(DataInputStream b) throws Exception {
        for (int i = 0; i < this.npoints; i++) {
            var xx = LittleEndian.readDouble(b);
            var yy = LittleEndian.readDouble(b);
            x.add(xx);
            y.add(yy);
            z.add(DEFAULT_ZVALUE); // this can make easier to export to VTK
        }
        return this;
    }

    /**
     * Reads list of  coordinates z coordinate for points in a record.
     * Note: npoints should have been set before
     *       this is only necessary for Z and M variants of the .shp file.
     *
     * @param b binary stream
     * @return a AShape that stores the read information
     * @throws Exception
     */
    protected AShape readZ(DataInputStream b) throws Exception {
        for (int i = 0; i < this.npoints; i++) {
            var zz = LittleEndian.readDouble(b);
            z.add(zz);
        }
        return this;
    }

    /**
     * Reads list of  coordinates z coordinate for points in a record.
     * Note: npoints should have been set before
     *       this is only necessary for M variants of the .shp file.
     *
     * @param b binary stream
     * @return a AShape that stores the read information
     * @throws Exception
     */
    protected AShape readM(DataInputStream b) throws Exception {
        var mmin = LittleEndian.readDouble(b);
        var mmax = LittleEndian.readDouble(b);

        this.rangeM = new Range(mmin, mmax);

        for (int i = 0; i < this.npoints; i++) {
            var mm = LittleEndian.readDouble(b);
            m.add(mm);
        }
        return this;
    }

    /**
     * @return the type associated to this shape
     */
    public SHP_TYPE getType() {
        return type;
    }

    @Override
    public String toString() {
        var s = String.format("%s@%d[%d bytes]", type, idx, size);
        return s;
    }

    private static int MAX_LISTED_VALUES = 10;
    /**
     * Prints summary of this shape to stream.
     */
    public void display(PrintStream out) {
        out.println("====================================");
        out.println(type);
        if (bbox != null) out.println("BoundingBox: " + bbox);
        out.println("npoints: " + npoints);
        for (int i = 0; i < npoints; i++) {
            var xy = String.format("(%g,%g,%g)", x.get(i), y.get(i), z.get(i));
            out.println(xy);
            if (i > MAX_LISTED_VALUES) {
                out.println("continue..");
                break;
            }
        }

        out.println("nparts: " + nparts);
        for(Integer p: parts) {
            out.println("First point in part: " + p);
        }

        if (m.size() > 0) {
            var mm = String.format("mmin: %g, mmax: %g", rangeM.mmin, rangeM.mmax);
            out.println(mm);
            for (int i = 0; i < m.size(); i++) {
                var _m = m.get(i);
                out.println(_m);
                if (i > MAX_LISTED_VALUES) {
                    out.println("continue...");
                    break;
                }
            }
        }

        out.println("====================================");
    }

    /**
     * Adds x coordinate of points in this shape to list.
     *
     * @param lx list where x coordinate should be added.
     * @return list with appended points.
     */
    public List<Double> addX(List<Double> lx) {
        for (int i = 0; i < x.size(); i++) {
            lx.add(x.get(i));
        }
        return lx;
    }

    /**
     * Adds y coordinate of points in this shape to list.
     *
     * @param ly list where y coordinate should be added.
     * @return list with appended points.
     */
    public List<Double> addY(List<Double> ly) {
        for (int i = 0; i < y.size(); i++) {
            ly.add(y.get(i));
        }
        return ly;
    }

    /**
     * Adds z coordinate of points in this shape to list.
     *
     * @param lz list where z coordinate should be added.
     * @return list with appended points.
     */
    public List<Double> addZ(List<Double> lz) {
        for (int i = 0; i < z.size(); i++) {
            lz.add(z.get(i));
        }
        return lz;
    }
}
