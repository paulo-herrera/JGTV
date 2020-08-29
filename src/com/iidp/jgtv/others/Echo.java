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

/**
 * Helper to print messages with common styles.
 */
public class Echo {
    public static void msg0(String text) {
        System.out.println("* " + text);
    }

    public static void msg1(String text) {
        System.out.println(" - " + text);
    }

    public static void msg2(String text) {
        System.out.println("  + " + text);
    }

    public static void msg(String text, int level) {
        switch(level) {
            case 0: msg0(text); break;
            case 1: msg1(text); break;
            case 2: msg2(text); break;
            default: assert false : "WRONG OPTION";
        }
    }
}
