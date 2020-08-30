package com.iidp.jgtv.others;

import com.iidp.jgtv.exportToVTK;

public class Runner {
    public static void main(String[] args) throws Exception {
        // -h -v
        var args2 = new String[] { "--dst", "e:/tmp",
                                   "--src", "E:/tmp/santiago/Curvas_nivels_50m.shp",
                                   "--attrib",
                                   "--elev", "100.0" };
        exportToVTK.execute(args2);
    }

}
