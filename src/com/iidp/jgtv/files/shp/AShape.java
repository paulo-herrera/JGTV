package com.iidp.jgtv.files.shp;

import com.iidp.jgtv.others.BoundingBox;
import com.iidp.jgtv.others.LittleEndian;
import com.iidp.jgtv.others.Range;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class AShape {

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

    /** Note: nparts should have been set before */
    protected AShape readParts(DataInputStream b) throws Exception {
        for (int i = 0; i < this.nparts; i++) {
            var pp = LittleEndian.readInt(b);
            this.parts.add(pp);
        }
        return this;
    }

    /** Note: npoints should have been set before */
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

    /** Note: npoints should have been set before */
    protected AShape readZ(DataInputStream b) throws Exception {
        for (int i = 0; i < this.npoints; i++) {
            var zz = LittleEndian.readDouble(b);
            z.add(zz);
        }
        return this;
    }

    /** Note: npoints should have been set before */
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

    public SHP_TYPE getType() {
        return type;
    }

    @Override
    public String toString() {
        var s = String.format("%s@%d[%d bytes]", type, idx, size);
        return s;
    }

    public void display() {
        System.out.println("====================================");
        System.out.println(type);
        if (bbox != null) System.out.println("BoundingBox: " + bbox);
        System.out.println("npoints: " + npoints);
        for (int i = 0; i < npoints; i++) {
            var xy = String.format("(%g,%g,%g)", x.get(i), y.get(i), z.get(i));
            System.out.println(xy);
            if (i > 10) {
                System.out.println("continue..");
                break;
            }
        }

        System.out.println("nparts: " + nparts);
        for(Integer p: parts) {
            System.out.println("First point in part: " + p);
        }

        if (m.size() > 0) {
            var mm = String.format("mmin: %g, mmax: %g", rangeM.mmin, rangeM.mmax);
            System.out.println(mm);
            for (int i = 0; i < m.size(); i++) {
                var _m = m.get(i);
                System.out.println(_m);
                if (i > 10) {
                    System.out.println("continue...");
                    break;
                }
            }
        }

        System.out.println("====================================");
    }

    /**
     * Adds x coordinate of points in this shape to list lx.
     *
     * @param lx: list where x coordinate should be added.
     * @return list with appended points.
     */
    public List<Double> addX(List<Double> lx) {
        for (int i = 0; i < x.size(); i++) {
            lx.add(x.get(i));
        }
        return lx;
    }

    /**
     * Adds y coordinate of points in this shape to list ly.
     *
     * @param ly: list where y coordinate should be added.
     * @return list with appended points.
     */
    public List<Double> addY(List<Double> ly) {
        for (int i = 0; i < y.size(); i++) {
            ly.add(y.get(i));
        }
        return ly;
    }

    /**
     * Adds z coordinate of points in this shape to list lz.
     *
     * @param lz: list where z coordinate should be added.
     * @return list with appended points.
     */
    public List<Double> addZ(List<Double> lz) {
        for (int i = 0; i < z.size(); i++) {
            lz.add(z.get(i));
        }
        return lz;
    }
}
