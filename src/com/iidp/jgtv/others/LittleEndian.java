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

import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * Static methods to read data from binary stream that stored them in little endian format.
 */
public class LittleEndian {
    private static ByteBuffer buff;
    private static byte[] bytes8 = new byte[8];

    /**
     * Reads a 2-bytes long char stored in little endian format.
     * NOTE: to read a 1-byte char use the readString method, instead.
     *
     * @param b binary stream
     * @return a native char
     * @throws Exception
     */
    public static char readChar(DataInputStream b) throws Exception {
        var lc = b.readChar();
        return Character.reverseBytes(lc);
    }

    /**
     * Reads a 2-bytes long short integer stored in little endian format.
     *
     * @param b binary stream
     * @return a native char
     * @throws Exception
     */
    public static short readShort(DataInputStream b) throws Exception {
       var ls = b.readShort();
       return Short.reverseBytes(ls);
    }

    /**
     * Reads a 4-bytes long integer stored in little endian format.
     *
     * @param b binary stream
     * @return a native char
     * @throws Exception
     */
    public static int readInt(DataInputStream b) throws Exception {
        var li = b.readInt();
        return Integer.reverseBytes(li);
    }

    /**
     * Reads a 8-bytes long integer stored in little endian format.
     *
     * @param b binary stream
     * @return a native char
     * @throws Exception
     */
    public static long readLong(DataInputStream b) throws Exception {
        var ll = b.readLong();
        return Long.reverseBytes(ll);
    }

    /**
     * Reads a 4-bytes long float value stored in little endian format.
     *
     * @param b binary stream
     * @return a native char
     * @throws Exception
     */
    public static float readFloat(DataInputStream b) throws Exception {
        b.read(bytes8, 0, 4);
        var v = ByteBuffer.wrap(bytes8).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        return v;
    }

    /**
     * Reads a 8-bytes long float value stored in little endian format.
     *
     * @param b binary stream
     * @return a native char
     * @throws Exception
     */
    public static double readDouble(DataInputStream b) throws Exception {
        b.read(bytes8, 0, 8);
        var v = ByteBuffer.wrap(bytes8).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        return v;
    }

    private static byte[] bytesString = new byte[120];
    /**
     * Read string from byte array assuming ISO_8859_1 encoding (sequence of 1-byte long chars).
     * @param b binary stream.
     * @param length: how many bytes (characters) should be read.
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


