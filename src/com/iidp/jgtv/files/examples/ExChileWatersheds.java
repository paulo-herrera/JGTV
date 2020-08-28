package com.iidp.jgtv.files.examples;

import com.iidp.jgtv.files.DbfFile;
import com.iidp.jgtv.files.ShpFile;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ExChileWatersheds {

    public static void main(String[] args) throws Exception {
        var src_shp = "examples/ex2_Chile_watersheds/Cuencas_DGA.shp";
        var shp = ShpFile.read(src_shp);
        shp.list_records(true);

        var directory = Paths.get(src_shp).getParent().toString();
        var filename = Paths.get(src_shp).getFileName().toString();
        var root = filename.split("\\.")[0];

        // Nothing too interesting but good enough to check if parsers work ok.
        var src_dbf = directory  + "/" +  root + ".dbf";
        var dbf = DbfFile.read(src_dbf);
        dbf.display(System.out);

        shp.toVTK("tmp/Chile_Watersheds");
        System.out.println("*** ALL DONE ***");
    }
}
