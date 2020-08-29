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
package com.iidp.jgtv.files.examples;

import com.iidp.jgtv.files.DbfFile;
import com.iidp.jgtv.files.ShpFile;

import java.nio.file.Paths;

public class ExFlorida {

    private static void precipitation() throws Exception {
        var src_shp = "examples/ex3_SpatialInfo_Florida/precipitation/precip1961_1990_a_fl.shp";
        var shp = ShpFile.read(src_shp);
        shp.list_records(false);

        var directory = Paths.get(src_shp).getParent().toString();
        var filename = Paths.get(src_shp).getFileName().toString();
        var root = filename.split("\\.")[0];

        // Nothing too interesting but good enough to check if parsers work ok.
        var src_dbf = directory  + "/" +  root + ".dbf";
        var dbf = DbfFile.read(src_dbf);
        dbf.display(System.out);

        var attrs = dbf.getFieldsAsLists();
        shp.setAttrs(attrs);

        shp.toVTK("tmp/Florida_Pp_1961_1990");
    }

    private static void geology() throws Exception {
        var src_shp = "examples/ex3_SpatialInfo_Florida/geology/geology_a_fl.shp";

        var shp = ShpFile.read(src_shp);
        shp.list_records(false);

        var directory = Paths.get(src_shp).getParent().toString();
        var filename = Paths.get(src_shp).getFileName().toString();
        var root = filename.split("\\.")[0];

        // There seems to be a problem with the .dbf file,
        // which includes fields with negative size
        // TODO: investigate further
        var src_dbf = directory  + "/" +  root + ".dbf";
        //var dbf = DbfFile.read(src_dbf);
        //dbf.display(System.out);

        //var attrs = dbf.getFieldsAsLists();
        //shp.setAttrs(attrs);

        shp.toVTK("tmp/Florida_geology");
    }

    private static void hydrology() throws Exception {
        var src_shp =  "examples/ex3_SpatialInfo_Florida/hydrologic_units/wbdhu8_a_fl099.shp";

        var shp = ShpFile.read(src_shp);
        shp.list_records(false);

        var directory = Paths.get(src_shp).getParent().toString();
        var filename = Paths.get(src_shp).getFileName().toString();
        var root = filename.split("\\.")[0];

        // There seems to be a problem with the .dbf file.
        // TODO: investigate further
        var src_dbf = directory  + "/" +  root + ".dbf";
        var dbf = DbfFile.read(src_dbf);
        dbf.display(System.out);

        //var attrs = dbf.getFieldsAsLists();
        //shp.setAttrs(attrs);

        shp.toVTK("tmp/Florida_hydrology");
    }


    public static void main(String[] args) throws Exception {
        //precipitation();
        //geology();
        hydrology();

        System.out.println("*** ALL DONE ***");
    }
}
