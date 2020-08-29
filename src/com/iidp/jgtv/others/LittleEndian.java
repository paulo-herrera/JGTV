package com.iidp.jgtv.others;

import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class LittleEndian {
    private static ByteBuffer buff;
    private static byte[] bytes8 = new byte[8];

    public static char readChar(DataInputStream b) throws Exception {
        var lc = b.readChar();
        return Character.reverseBytes(lc);
    }

    public static short readShort(DataInputStream b) throws Exception {
       var ls = b.readShort();
       return Short.reverseBytes(ls);
    }

    public static int readInt(DataInputStream b) throws Exception {
        var li = b.readInt();
        return Integer.reverseBytes(li);
    }

    public static long readLong(DataInputStream b) throws Exception {
        var ll = b.readLong();
        return Long.reverseBytes(ll);
    }

    public static float readFloat(DataInputStream b) throws Exception {
        b.read(bytes8, 0, 4);
        var v = ByteBuffer.wrap(bytes8).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        return v;
    }

    public static double readDouble(DataInputStream b) throws Exception {
        b.read(bytes8, 0, 8);
        var v = ByteBuffer.wrap(bytes8).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        return v;
    }

    private static byte[] bytesString = new byte[120];
    /**
     * Read string from byte array assuming ISO_8859_1 encoding.
     * @param b binary stream.
     * @param length: how many bytes should be read.
     * @return read String from bytes.
     * @throws Exception
     */
    public static String readString(DataInputStream b, int length) throws Exception {
        if (length < 0) return ""; // DEBUG
        var bytesString = new byte[length];

        var nread = b.read(bytesString);
        assert (nread == length): "nread: " + nread + "   length: " + length;

        var str = new String(bytesString, StandardCharsets.ISO_8859_1);
        // This is needed to prevent that the new String keep carrying null chars around.
        var pos = str.indexOf("\0");
        if (pos > 0) {
            str = str.substring(0, pos);
        }
        return str;
    }
}


