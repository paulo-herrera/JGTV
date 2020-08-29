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