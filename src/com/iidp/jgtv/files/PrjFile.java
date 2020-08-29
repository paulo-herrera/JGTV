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

import com.iidp.jgtv.others.Echo;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class PrjFile {
    final String content;

    public String getContent() {
        return content;
    }

    private PrjFile(String path) throws Exception {
        var src = new DataInputStream(new FileInputStream(new File(path)));
        content = src.readLine();
        src.close();
    }

    public static PrjFile read(String src) throws Exception {
        return read(src, false);
    }

    public static PrjFile read(String src, boolean verbose) throws Exception {
        var path = new File(src);
        Echo.msg("Reading .prj from: " + path.getAbsolutePath(), 0);
        var prj = new PrjFile(path.getAbsolutePath());
        if (verbose) {
            System.out.println(prj.content);
        }
        Echo.msg("   Done reading .prj.", 0);
        return prj;
    }
}