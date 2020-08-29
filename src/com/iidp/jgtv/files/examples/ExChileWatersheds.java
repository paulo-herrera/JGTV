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
