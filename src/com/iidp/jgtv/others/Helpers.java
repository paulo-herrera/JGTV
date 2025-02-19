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
package com.iidp.jgtv.others;

import java.util.List;

/**
 * Convience methos to convert list to arrays.
 */
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
