/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.util;

import java.nio.ByteBuffer;

public class ByteUtils {
    private static final int HEX_BYTES_PER_LINE = 16;
    private static final char TAB = '\t';
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final char DOT = '.';

    public static byte[] resizeByteArray(byte[] source, int pos, int size) {
        byte[] tmpArray = new byte[size];
        System.arraycopy(source, pos, tmpArray, 0, size);
        return tmpArray;
    }

    public static String fullHexDump(ByteBuffer buffer, int bytesPerLine) {
        return ByteUtils.fullHexDump(buffer.array(), bytesPerLine);
    }

    public static String fullHexDump(ByteBuffer buffer) {
        return ByteUtils.fullHexDump(buffer.array(), 16);
    }

    public static String fullHexDump(byte[] buffer) {
        return ByteUtils.fullHexDump(buffer, 16);
    }

    public static String fullHexDump(byte[] buffer, int bytesPerLine) {
        StringBuilder sb = new StringBuilder("Binary size: ").append(buffer.length).append("\n");
        StringBuilder hexLine = new StringBuilder();
        StringBuilder chrLine = new StringBuilder();
        int index = 0;
        int count = 0;
        do {
            char currByte;
            String hexByte;
            if ((hexByte = Integer.toHexString((currByte = (char) buffer[index]) & 255)).length() == 1) {
                hexLine.append("0");
            }
            hexLine.append(hexByte.toUpperCase()).append(" ");
            char currChar = currByte < '!' || currByte > '~' ? '.' : currByte;
            chrLine.append(currChar);
            if (++count != bytesPerLine) continue;
            count = 0;
            sb.append(hexLine).append('\t').append(chrLine).append(NEW_LINE);
            hexLine.delete(0, hexLine.length());
            chrLine.delete(0, chrLine.length());
        } while (++index < buffer.length);
        if (count != 0) {
            for (int j = bytesPerLine - count; j > 0; --j) {
                hexLine.append("   ");
                chrLine.append(" ");
            }
            sb.append(hexLine).append('\t').append(chrLine).append(NEW_LINE);
        }
        return sb.toString();
    }
}

