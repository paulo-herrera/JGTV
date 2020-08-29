package com.iidp.jgtv.files.examples;

import com.iidp.jgtv.files.DbfFile;
import com.iidp.jgtv.files.ShpFile;

import java.nio.file.Paths;

/**
 * TODO: Check this type of file.
 *       The file exported from QGIS does not make sense according to the specs.
 */
public class Ex_Multipart {

    public static void main(String[] args) throws Exception {
        var src_shp = "examples/ex1_SimpleShapes/polygons_multipart.shp";
        var shp = ShpFile.read(src_shp);
        shp.list_records(true);

        var directory = Paths.get(src_shp).getParent().toString();
        var filename = Paths.get(src_shp).getFileName().toString();
        var root = filename.split("\\.")[0];

        // Nothing too interesting but good enough to check if parsers work ok.
        var src_dbf = directory  + "/" +  root + ".dbf";
        var dbf = DbfFile.read(src_dbf);
        dbf.display(System.out);

        var attrs = dbf.getFieldsAsLists();
        shp.setAttrs(attrs);

        shp.toVTK("tmp/PolygonsMultipart");
        System.out.println("*** ALL DONE ***");
    }
}
