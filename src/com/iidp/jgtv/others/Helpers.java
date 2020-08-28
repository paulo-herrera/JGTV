package com.iidp.jgtv.others;

import java.util.List;

public class Helpers {

    public static double[] toArrayDouble(List<Double> a){
        var aa = new double[a.size()];
        for (int i = 0; i < a.size(); i++) {
            aa[i] = a.get(i);
        }
        return aa;
    }

    public static int[] toArrayInteger(List<Integer> a){
        var aa = new int[a.size()];
        for (int i = 0; i < a.size(); i++) {
            aa[i] = a.get(i);
        }
        return aa;
    }

}
