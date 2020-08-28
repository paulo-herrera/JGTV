package com.iidp.jgtv.others;

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
