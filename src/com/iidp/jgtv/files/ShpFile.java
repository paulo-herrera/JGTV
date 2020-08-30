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
package com.iidp.jgtv.files;

import com.iidp.jgtv.files.dbf.FIELD_TYPE;
import com.iidp.jgtv.files.dbf.FieldList;
import com.iidp.jgtv.files.shp.*;
import com.iidp.jgtv.others.Echo;
import com.iidp.jgtv.others.Helpers;
import com.iidp.jgtv.others.LittleEndian;
import com.iidp.vtk.high_level.EVTK;
import com.iidp.vtk.low_level.VTK_CELL_TYPE;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a shape file (.shp), which is composed of:
 *  - A header with information for the whole file, and
 *  - A sequence of records, each one with:
 *      + A header
 *      + Some data that specifies the geometric information that is included in the file.
 */
public class ShpFile {

    private String src;
    private SHP_TYPE type;

    /** Length of file in bytes */
    private int length;
    /** Bounding box */
    private double xmin, xmax, ymin, ymax, zmin, zmax;
    /** MinMax of M */
    private double mmin, mmax;
    /** List of records in file. These are shapes */
    List<AShape> records;

    /** List of attributes for each shape in this file. These are read from a separate .dbf file */
    List<FieldList> attrs;
    public void setAttrs(List<FieldList> attrs) {
        this.attrs = attrs;
    }

    /** Stores comments as a list of strings.
     *  Comments are included in the exported VTK file,
     *  which is their only use for now.
     */
    List<String> shpComments;
    public void addComment(String c) {
        shpComments.add(c);
    }

    private ShpFile(String _src) {
        src = _src;
        records = new ArrayList<AShape>();
        attrs = new ArrayList<FieldList>();
        shpComments = new ArrayList<String>();
    }

    public String toString() {
        var sb = new StringBuilder();
        sb.append("SHP_FILE: " ).append(src).append("\n");
        sb.append(" length(bytes): ").append(length).append("\n");
        sb.append(" type: ").append(type).append("\n");
        sb.append(" xmin: ").append(xmin).append("\n");
        sb.append(" xmax: ").append(xmax).append("\n");
        sb.append(" ymin: ").append(ymin).append("\n");
        sb.append(" ymax: ").append(ymax).append("\n");
        sb.append(" zmin: ").append(zmin).append("\n");
        sb.append(" zmax: ").append(zmax).append("\n");
        sb.append(" mmin: ").append(mmin).append("\n");
        sb.append(" mmax: ").append(mmax).append("\n");
        return sb.toString();
    }

    /**
     * Returns an array with 3 lists that contain the x, y and z coordinates of all points in this file.
     *
     * @return
     */
    public List<List<Double>> getXYZ() {
        var x = new ArrayList<Double>();
        var y = new ArrayList<Double>();
        var z = new ArrayList<Double>();

        for(AShape as: this.records) {
           as.addX(x);
           as.addY(y);
           as.addZ(z);
        }

        var xyz = new ArrayList<List<Double>>();
        xyz.add(x);
        xyz.add(y);
        xyz.add(z);

        return xyz;
    }

    /**
     * Returns a list with the number of points in each shape that composed this file.
     * @return
     */
    public List<Integer> getPointsPerShape() {
        var ssize = new ArrayList<Integer>();
        for(AShape shape: this.records) {
            ssize.add(shape.getNPoints());
        }
        return ssize;
    }

    private static ShpFile read_header(DataInputStream b, ShpFile shp) throws Exception {
        // Header is 100 bytes long
        // Records in header are written in a mix of big and little endian binary data
        // Java by default reads in big endian

        //0-3 int32 big File code (always hex value 0x0000270a)
        var fc = b.readInt();
        assert (fc == 9994) : "Wrong file code: " + fc;

        //4-23 	int32 	big 	Unused; five uint32
        for (int i = 0; i < 5; i++) {
            b.readInt();
        }

        //24-27 	int32 	big 	File length (in 16-bit words, including the header)
        shp.length = b.readInt() * 2; // size in bytes
        //Echo.msg("Lenght (bytes): " + shp.length, 2);

        //28-31 	int32 	little 	Version
        var version = LittleEndian.readInt(b);
        //Echo.msg("Version: " + version, 2);
        //assert (version == 1000) : "Wrong version: " + version;

        //32-35 	int32 	little 	Shape type (see reference below)
        var shape_type = LittleEndian.readInt(b);
        //Echo.msg("Shape type: " + shape_type, 2);
        shp.type = SHP_TYPE.getShpType(shape_type);

        //36-67  double 	little 	Minimum bounding rectangle (MBR) of all shapes contained within the dataset;
        //       four doubles in the following order: min X, min Y, max X, max Y
        shp.xmin = LittleEndian.readDouble(b);
        shp.xmax = LittleEndian.readDouble(b);
        shp.ymin = LittleEndian.readDouble(b);
        shp.ymax = LittleEndian.readDouble(b);

        //68-83 	double 	little 	Range of Z; two doubles in the following order: min Z, max Z
        shp.zmin = LittleEndian.readDouble(b);
        shp.zmax = LittleEndian.readDouble(b);
        //if verbose: print( "(xmin, xmax, ymin, ymax, zmin, zmax): (%g,%g,%g,%g,%g,%g)"%bbox)

        //84-99 	double 	little 	Range of M; two doubles in the following order: min M, max M
        shp.mmin = LittleEndian.readDouble(b);
        shp.mmax = LittleEndian.readDouble(b);

        //if verbose: print("(mmin, mmax): (%g, %g)"%mm) # no clue what M values are for!!!
        return shp;
    }

    private int read_record(DataInputStream b) throws Exception {
        AShape record = null;

        if (type == SHP_TYPE.NULL) {
            record = Null.read(b);

        } else if (type == SHP_TYPE.POINT) {
            record = Point.read(b);

        } else if (type == SHP_TYPE.MULTIPOINT) {
            record = Multipoint.read(b);

        } else if (type == SHP_TYPE.POLYLINE) {
            record = Polyline.read(b);

        } else if (type == SHP_TYPE.POLYGON) {
            record = Polygon.read(b);

        } else if (type == SHP_TYPE.POLYGONZ) {
            record = PolygonZ.read(b);

        } else {
            assert false : "Unknown file type: " + type;
            record = null;
        }

        records.add(record);
        return record.size;
    }

    public static ShpFile read(String filename) throws Exception {
        return read(filename, false);
    }

    public static ShpFile read(String filename, boolean verbose) throws Exception {
        var src = new File(filename);
        Echo.msg("Reading .shp from: " + src.getAbsolutePath(), 0);

        var s = new FileInputStream(src);
        var bi = new BufferedInputStream(s);
        var b = new DataInputStream(bi);

        var shp = new ShpFile(filename);

        // Read header information
        read_header(b, shp);

        // Read records
        var remain = shp.length - 100; // header size = 100 bytes
        while (remain > 0) {
            var readb = shp.read_record(b);
            remain = remain - readb;
        }

        b.close();
        if (verbose) {
            System.out.println(shp);
        }
        Echo.msg("  Done reading .shp", 0);
        return shp;
    }

    public void list_records(boolean verbose) {
        list_records(verbose, System.out);
    }

    public void list_records(boolean verbose, PrintStream out) {
        out.println("Records in file: ");
        for (AShape r : records) {
            out.println(r);
            if (verbose) r.display(out);
        }
    }

    /**
     * Sets the elevation (z coordinate) equal to the value of an attribute.
     * It is mainly used to assign elevation to objects that are associated to it,
     * e.g. iso-contours of levation, so they can be shown in 3D.
     *
     * @param attr
     * @param pointsPerCell
     * @param z
     */
    /*private void setZByAttribute(FieldList attr, int[] pointsPerCell, double[] z) {
        assert ((attr.fd.type == FIELD_TYPE.FLOAT) || (attr.fd.type == FIELD_TYPE.NUMBER));
        var val = attr.toArrayDouble(); // this should have ncells elements

        // we assume each record represent a cell. This could be different for files with multiple parts.
        var ncells = records.size();

        var ii = 0;
        for (int i = 0; i < ncells; i++) {
            var np = pointsPerCell[i];
            var zval = (double) attr.values.get(i);
            for (int j = 0; j < np; j++) {
                z[ii] = zval;
                ii += 1;
            }
        }
        //System.out.printf("z.length: %d   ii: %d \n", z.length, ii);
    }*/

    /**
     * Exports information in this Shape file as a VTK file.
     *
     * @param path path where file should be saved without extension.
     * @return
     *
     * TODO: evaluate if setZByAttribute is a good idea
     */
    private String toVTK(String path, boolean setZByAttribute, int attrPosition) throws Exception {
        System.out.println("Exporting to VTK...");

        // Get common information for all shapes
        var xyz = getXYZ();
        var x = xyz.get(0);
        var y = xyz.get(1);
        var z = xyz.get(2);
        var pointsPerRecord = getPointsPerShape();
        var ncells = records.size();

        // Make containers to store information
        var pointData  = EVTK.makePointData();
        var cellData   = EVTK.makeCellData();
        var comments = EVTK.makeComments();

        for (String c: shpComments) {
            comments.add(c);
        }

        // Fill cellData with attributes read from a .dbf file,
        // which should have been previously set.
        for (FieldList fl: attrs) {
            if (fl.fd.type == FIELD_TYPE.FLOAT) {
                var d = fl.toArrayDouble();
                cellData.addData(fl.fd.name, d);
            } else if (fl.fd.type == FIELD_TYPE.NUMBER) { // it could be int or double
                var d = fl.toArrayDouble();
                cellData.addData(fl.fd.name, d);
            } else  if (fl.fd.type == FIELD_TYPE.TEXT) {
                var d = fl.toString();
                comments.add(d);
            }  else  if (fl.fd.type == FIELD_TYPE.DATE) {
                var d = fl.toString();
                comments.add(d);
            } else {
                assert false : "Unknown field type: " + type;
            }
        }

        // Prepare data to export
        var xx = Helpers.toArrayDouble(x);
        var yy = Helpers.toArrayDouble(y);
        var zz = Helpers.toArrayDouble(z);
        var pointsPerShape = Helpers.toArrayInteger(pointsPerRecord);

        // This could require more thought
       /* if (setZByAttribute && attrs.size() > 0) {
            var attr = attrs.get(attrPosition);
            setZByAttribute(attr, pointsPerShape, zz);
        }*/

        var full_path = "";
        if (type == SHP_TYPE.POINT || type == SHP_TYPE.MULTIPOINT) {
            full_path = EVTK.pointsToVTK(path, xx, yy, zz, pointData, comments);

        } else  if (type == SHP_TYPE.POLYLINE || type == SHP_TYPE.POLYLINEZ) {
            full_path = EVTK.polylinesToVTK(path, xx, yy, zz, pointsPerShape, cellData, pointData, comments);

        } else  if (type == SHP_TYPE.POLYGON || type == SHP_TYPE.POLYGONZ) {

            // Points should be counter clock-wise. Add a small check LATER
            var conn   = new int[xx.length]; // check number of points that are exported in Shape file for polygons
            for (int i = 0; i < xx.length; i++) {
                conn[i] = i;
            }

            var offsets = new int[ncells];
            var cell_types = new VTK_CELL_TYPE[ncells];
            var off = 0;
            for (int i = 0; i < ncells; i++) {
                off = off + pointsPerShape[i];
                offsets[i] = off;
                cell_types[i] = VTK_CELL_TYPE.VTK_POLYGON;
            }

            full_path = EVTK.unstructuredGridToVTK(path, xx, yy, zz, conn, offsets, cell_types, cellData, pointData, comments);
        } else {
            assert false : "Not implemented for type: " + type;
        }

        System.out.println("DONE. File exported to: " + full_path);
        return full_path;
    }

    public String toVTK(String path) throws Exception {
       var full_path = toVTK(path, false, 0);
       return full_path;
    }

    public static void main(String[] args) throws Exception {
        var src = "examples/ex1_SimpleShapes/polygons.shp";

        var shp = ShpFile.read(src);
        shp.list_records(true);

        var parts = src.split("/");
        var filename = parts[2];
        var root = filename.split("\\.")[0];

        var src_dbf = "examples/ex1_SimpleShapes/" + root + ".dbf";
        var dbf = DbfFile.read(src_dbf);
        var attrs = dbf.getFieldsAsLists();
        shp.setAttrs(attrs);

        var src_prj = "examples/ex1_SimpleShapes/" + root + ".prj";
        var prj = PrjFile.read(src_prj);
        shp.addComment(prj.content);

        var full_path = shp.toVTK("tmp/" + root, true, 0);

        System.out.println("*** ALL DONE ***");
    }
}
