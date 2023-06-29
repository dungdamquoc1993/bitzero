/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.util;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class BinaryHelper {
    public static final byte CODE_SHORT_TYPE = 1;
    public static final byte CODE_INT_TYPE = 2;
    public static final byte CODE_CHAR_TYPE = 3;
    public static final byte CODE_FLOAT_TYPE = 4;
    public static final byte CODE_BOOLEAN_TYPE = 5;
    public static final byte CODE_STRING_TYPE = 6;
    public static final byte CODE_BYTE_TYPE = 7;
    public static final byte CODE_DOUBLE_TYPE = 8;
    public static final byte CODE_LONG_TYPE = 9;
    public static final byte CODE_SHORT_ARR_TYPE = 11;
    public static final byte CODE_INT_ARR_TYPE = 22;
    public static final byte CODE_CHAR_ARR_TYPE = 33;
    public static final byte CODE_FLOAT_ARR_TYPE = 44;
    public static final byte CODE_BOOLEAN_ARR_TYPE = 55;
    public static final byte CODE_STRING_ARR_TYPE = 66;
    public static final byte CODE_BYTE_ARR_TYPE = 77;
    public static final byte CODE_DOUBLE_ARR_TYPE = 88;
    public static final byte CODE_LONG_ARR_TYPE = 99;
    public static final byte CODE_OBJECT_TYPE = 100;
    public static final byte CODE_OBJECT_ARR_TYPE = 101;
    public static final byte CODE_NULL = -1;
    public static final short MAX_BYTE_FOR_STR = 50;
    private static Charset charset = Charset.forName("UTF-8");
    public static final int MAX_BUFFER_SIZE = 5012;

    public static byte findClassCodeType(Class c) {
        if (c == Short.class) {
            return 1;
        }
        if (c == Integer.class) {
            return 2;
        }
        if (c == Character.class) {
            return 3;
        }
        if (c == Float.class) {
            return 4;
        }
        if (c == Boolean.class) {
            return 5;
        }
        if (c == String.class) {
            return 6;
        }
        if (c == Byte.class) {
            return 7;
        }
        return -1;
    }

    public static byte findClassCode(Object c) {
        if (c instanceof Short) {
            return 1;
        }
        if (c instanceof Integer) {
            return 2;
        }
        if (c instanceof Character) {
            return 3;
        }
        if (c instanceof Float) {
            return 4;
        }
        if (c instanceof Double) {
            return 8;
        }
        if (c instanceof Long) {
            return 9;
        }
        if (c instanceof Boolean) {
            return 5;
        }
        if (c instanceof String) {
            return 6;
        }
        if (c instanceof Byte) {
            return 7;
        }
        if (c instanceof short[]) {
            return 11;
        }
        if (c instanceof int[]) {
            return 22;
        }
        if (c instanceof char[]) {
            return 33;
        }
        if (c instanceof float[]) {
            return 44;
        }
        if (c instanceof boolean[]) {
            return 55;
        }
        if (c instanceof String[]) {
            return 66;
        }
        if (c instanceof Byte[]) {
            return 77;
        }
        if (c instanceof double[]) {
            return 88;
        }
        if (c instanceof long[]) {
            return 99;
        }
        if (c instanceof Object[]) {
            return 101;
        }
        if (c instanceof Object) {
            return 100;
        }
        return -1;
    }

    public static byte[] toByte(Object o) {
        Field[] fields = o.getClass().getFields();
        ByteBuffer buffer = ByteBuffer.allocateDirect(5012);
        boolean len = false;
        block22 : for (Field f : fields) {
            try {
                Object value = f.get(o);
                byte code = BinaryHelper.findClassCode(value);
                int db;
                int n;
                Integer length;
                switch (code) {                  
                    case 1: {
                        buffer.putShort((Short)value);
                        break;
                    }
                    case 7: {
                        buffer.put(((Byte)value).byteValue());
                        break;
                    }
                    case 11: {
                        short[] shorts = (short[])value;
                        length = shorts.length;
                        buffer.putShort(length.shortValue());
                        buffer.put(BinaryHelper.toByte(shorts));
                        break;
                    }
                    case 2: {
                        buffer.putInt((Integer)value);
                        break;
                    }
                    case 22: {
                        int[] ints = (int[])value;
                        length = ints.length;
                        buffer.putShort(length.shortValue());
                        buffer.put(BinaryHelper.toByte(ints));
                        break;
                    }
                    case 3: {
                        break;
                    }
                    case 33: {
                        break;
                    }
                    case 4: {
                        buffer.putFloat(((Float)value).floatValue());
                        break;
                    }
                    case 44: {
                        float[] floats = (float[])value;
                        length = floats.length;
                        buffer.putShort(length.shortValue());
                        buffer.put(BinaryHelper.toByte(floats));
                        break;
                    }
                    case 8: {
                        buffer.putDouble((Double)value);
                        break;
                    }
                    case 88: {
                        double[] doubles = (double[])value;
                        length = doubles.length;
                        buffer.putShort(length.shortValue());
                        double[] arrd = doubles;
                        int n2 = arrd.length;
                        for (n = 0; n < n2; ++n) {
                            db = (int)arrd[n];
                            buffer.putDouble(db);
                        }
                        continue block22;
                    }
                    case 9: {
                        buffer.putDouble(((Long)value).longValue());
                        break;
                    }
                    case 99: {
                        long[] longs = (long[])value;
                        length = longs.length;
                        buffer.putShort(length.shortValue());
                        long[] arrl = longs;
                        n = arrl.length;
                        for (db = 0; db < n; ++db) {
                            long db2 = arrl[db];
                            buffer.putDouble(db2);
                        }
                        continue block22;
                    }
                    case 5: {
                        buffer.put(BinaryHelper.toByte((Boolean)value));
                        break;
                    }
                    case 55: {
                        boolean[] bools = (boolean[])value;
                        length = bools.length;
                        buffer.putShort(length.shortValue());
                        buffer.put(BinaryHelper.toByte(bools));
                        break;
                    }
                    case 77: {
                        byte[] byteArr = (byte[])value;
                        length = byteArr.length;
                        buffer.putShort(length.shortValue());
                        buffer.put(byteArr);
                        break;
                    }
                    case 6: {
                        String s = (String)value;
                        byte[] temp = BinaryHelper.toByte(s);
                        length = temp.length;
                        buffer.putShort(length.shortValue());
                        buffer.put(temp);
                        break;
                    }
                    case 66: {
                        String[] arrStr = (String[])value;
                        length = arrStr.length;
                        buffer.putShort(length.shortValue());
                        for (int i = 0; i < arrStr.length; ++i) {
                            String str = arrStr[i];
                            byte[] tempB = BinaryHelper.toByte(str);
                            length = tempB.length;
                            buffer.putShort(length.shortValue());
                            if (length <= 0) continue;
                            buffer.put(tempB);
                        }
                        break;
                    }
                }
                continue;
            }
            catch (IllegalAccessException code) {
                // empty catch block
            }
        }
        byte[] ret = new byte[buffer.position()];
        buffer.rewind();
        buffer.get(ret);
        return ret;
    }

    public static byte[] toByte(short value) {
        byte[] b = new byte[]{(byte)(value >>> 8), (byte)value};
        return b;
    }

    public static byte[] toByte(short[] value) {
        byte[] b = new byte[value.length * 2];
        for (int i = 0; i < value.length; ++i) {
            System.arraycopy(BinaryHelper.toByte(value[i]), 0, b, i * 2, 2);
        }
        return b;
    }

    public static short toShort(byte[] b) {
        short value = 0;
        value = (short)(value + (b[0] << 8));
        value = (short)(value + (b[1] & 255));
        return value;
    }

    public static short[] toShortArray(byte[] b) {
        short[] shorts = new short[b.length / 2];
        for (int i = 0; i < shorts.length; ++i) {
            shorts[i] = BinaryHelper.toShort(new byte[]{b[i * 2], b[i * 2 + 1]});
        }
        return shorts;
    }

    public static byte[] toByte(int value) {
        byte[] b = new byte[]{(byte)(value >>> 24), (byte)(value >>> 16), (byte)(value >>> 8), (byte)value};
        return b;
    }

    public static byte[] toByte(int[] value) {
        byte[] b = new byte[value.length * 4];
        for (int i = 0; i < value.length; ++i) {
            System.arraycopy(BinaryHelper.toByte(value[i]), 0, b, i * 4, 4);
        }
        return b;
    }

    public static int toInt(byte[] b) {
        int value = 0;
        value += b[0] << 24;
        value += (b[1] & 255) << 16;
        value += (b[2] & 255) << 8;
        return value += b[3] & 255;
    }

    public static int[] toIntArray(byte[] b) {
        int[] ints = new int[b.length / 4];
        for (int i = 0; i < ints.length; ++i) {
            ints[i] = BinaryHelper.toInt(new byte[]{b[i * 4], b[i * 4 + 1], b[i * 4 + 2], b[i * 4 + 3]});
        }
        return ints;
    }

    public static byte[] toByte(float value) {
        return BinaryHelper.toByte(Float.floatToIntBits(value));
    }

    public static byte[] toByte(float[] value) {
        byte[] b = new byte[value.length * 4];
        for (int i = 0; i < value.length; ++i) {
            System.arraycopy(BinaryHelper.toByte(value[i]), 0, b, i * 4, 4);
        }
        return b;
    }

    public static float toFloat(byte[] b) {
        return Float.intBitsToFloat(BinaryHelper.toInt(b));
    }

    public static float[] toFloatArray(byte[] b) {
        float[] floats = new float[b.length / 4];
        for (int i = 0; i < floats.length; ++i) {
            floats[i] = BinaryHelper.toFloat(new byte[]{b[i * 4], b[i * 4 + 1], b[i * 4 + 2], b[i * 4 + 3]});
        }
        return floats;
    }

    public static byte[] toByte(boolean value) {
        byte[] arrby = new byte[1];
        arrby[0] = value ? (byte)1 : (byte)0;
        return arrby;
    }

    public static byte[] toByte(boolean[] value) {
        byte[] b = new byte[value.length];
        for (int i = 0; i < value.length; ++i) {
            b[i] = value[i] ? (byte)1 : (byte)0;
        }
        return b;
    }

    public static void toObject(byte[] data, Object o, Object info) {
        Field[] fields = info.getClass().getFields();
        ByteBuffer buffer = ByteBuffer.wrap(data);
        for (Field f : fields) {
            try {
                Object value = f.get(info);
                byte code = BinaryHelper.findClassCode(value);
                short length;
                byte[] b;
                switch (code) {                   
                    case 1: {
                        f.setShort(o, buffer.getShort());
                        break;
                    }
                    case 11: {
                        length = buffer.getShort();
                        short[] tempSh = new short[length];
                        for (int i = 0; i < tempSh.length; ++i) {
                            tempSh[i] = buffer.get();
                        }
                        f.set(o, tempSh);
                        break;
                    }
                    case 2: {
                        f.setInt(o, buffer.getInt());
                        break;
                    }
                    case 7: {
                        f.setByte(o, buffer.get());
                        break;
                    }
                    case 77: {
                        length = buffer.getShort();
                        byte[] tempBa = new byte[length];
                        for (int i = 0; i < tempBa.length; ++i) {
                            tempBa[i] = buffer.get();
                        }
                        f.set(o, tempBa);
                        break;
                    }
                    case 22: {
                        length = buffer.getShort();
                        int[] temp = new int[length];
                        for (int i = 0; i < temp.length; ++i) {
                            temp[i] = buffer.getInt();
                        }
                        f.set(o, temp);
                        break;
                    }
                    case 3: {
                        break;
                    }
                    case 33: {
                        break;
                    }
                    case 4: {
                        f.setFloat(o, buffer.getFloat());
                        break;
                    }
                    case 44: {
                        length = buffer.getShort();
                        float[] tempF = new float[length];
                        for (int i = 0; i < tempF.length; ++i) {
                            tempF[i] = buffer.getFloat();
                        }
                        f.set(o, tempF);
                        break;
                    }
                    case 8: {
                        f.setLong(o, (long)buffer.getDouble());
                        break;
                    }
                    case 88: {
                        length = buffer.getShort();
                        long[] tempD = new long[length];
                        for (int i = 0; i < tempD.length; ++i) {
                            tempD[i] = (long)buffer.getDouble();
                        }
                        f.set(o, tempD);
                        break;
                    }
                    case 9: {
                        f.setLong(o, buffer.getLong());
                        break;
                    }
                    case 99: {
                        length = buffer.getShort();
                        double[] tempL = new double[length];
                        for (int i = 0; i < tempL.length; ++i) {
                            tempL[i] = buffer.getLong();
                        }
                        f.set(o, tempL);
                        break;
                    }
                    case 55: {
                        length = buffer.getShort();
                        boolean[] tempB = new boolean[length];
                        for (int i = 0; i < tempB.length; ++i) {
                            tempB[i] = BinaryHelper.toBool(buffer.get());
                        }
                        f.set(o, tempB);
                        break;
                    }
                    case 5: {
                        f.setBoolean(o, BinaryHelper.toBool(buffer.get()));
                        break;
                    }
                    case 6: {
                        length = buffer.getShort();
                        b = new byte[length];
                        buffer.get(b);
                        f.set(o, BinaryHelper.toString(b));
                        break;
                    }
                    case 66: {
                        length = buffer.getShort();
                        String[] arrString = new String[length];
                        for (int i = 0; i < arrString.length; ++i) {
                            short lenStr = buffer.getShort();
                            if (lenStr > 0) {
                                b = new byte[lenStr];
                                buffer.get(b);
                                arrString[i] = BinaryHelper.toString(b);
                                continue;
                            }
                            arrString[i] = "";
                        }
                        f.set(o, arrString);
                    }
                }
                continue;
            }
            catch (IllegalAccessException code) {
                // empty catch block
            }
        }
    }

    public static boolean toBool(byte b) {
        return b != 0;
    }

    public static boolean[] toBoolArray(byte[] b) {
        boolean[] bools = new boolean[b.length];
        for (int i = 0; i < b.length; ++i) {
            bools[i] = b[i] != 0;
        }
        return bools;
    }

    public static byte[] toByte(char value) {
        return BinaryHelper.toByte((short)value);
    }

    public static byte[] toByte(char[] value) {
        byte[] b = new byte[value.length * 2];
        for (int i = 0; i < value.length; ++i) {
            System.arraycopy(BinaryHelper.toByte(value[i]), 0, b, i * 2, 2);
        }
        return b;
    }

    public static char toChar(byte[] b) {
        return (char)BinaryHelper.toShort(b);
    }

    public static char[] toCharArray(byte[] b) {
        char[] chars = new char[b.length / 2];
        for (int i = 0; i < chars.length; ++i) {
            chars[i] = (char)BinaryHelper.toShort(new byte[]{b[i * 2], b[i * 2 + 1]});
        }
        return chars;
    }

    public static byte[] toByte(String value) {
        return value.getBytes(charset);
    }

    public static byte[] toByte(String[] value) {
        byte[] b = new byte[value.length * 50];
        for (int i = 0; i < value.length; ++i) {
            byte[] temp = BinaryHelper.toByte(value[i]);
            System.arraycopy(temp, 0, b, 50 * i, temp.length);
        }
        return b;
    }

    public static String toString(byte[] b) {
        int len = b.length;
        for (int i = 0; i < b.length - 1; ++i) {
            if (b[i] != 0 || b[i + 1] != 0) continue;
            len = i;
            break;
        }
        return new String(b, 0, len, charset);
    }

    public static String[] toStringArray(byte[] b) {
        String[] arrStr = new String[b.length / 50];
        for (int i = 0; i < arrStr.length; ++i) {
            byte[] temp = new byte[50];
            System.arraycopy(b, i * 50, temp, 0, 50);
            arrStr[i] = BinaryHelper.toString(temp);
        }
        return arrStr;
    }

    public static void main(String[] args) {
    }

    public static List<Field> getDeclaredFields(Object o, int modifier) {
        ArrayList<Field> fieldInfos = new ArrayList<Field>();
        Field[] fields = o.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            Field f = fields[i];
            if (f.getModifiers() != modifier) continue;
            fieldInfos.add(f);
        }
        return fieldInfos;
    }

    public static List<Field> getFields(Object o, int modifier) {
        ArrayList<Field> fieldInfos = new ArrayList<Field>();
        Field[] fields = o.getClass().getFields();
        for (int i = 0; i < fields.length; ++i) {
            Field f = fields[i];
            if (f.getModifiers() != modifier) continue;
            fieldInfos.add(f);
        }
        return fieldInfos;
    }

    public static int sizeOf(Object o) {
        int ret = -1;
        List<Field> declaredFields = BinaryHelper.getFields(o, 1);
        ret = 0;
        block24 : for (Field f : declaredFields) {
            try {
                Object value = f.get(o);
                byte code = BinaryHelper.findClassCode(value);
                int length;
                byte[] tempByte;
                switch (code) {                    
                    case 1: {
                        ret += 2;
                        break;
                    }
                    case 11: {
                        short[] shorts = (short[])value;
                        ret += 2 + 2 * shorts.length;
                        break;
                    }
                    case 2: {
                        ret += 4;
                        break;
                    }
                    case 22: {
                        int[] ints = (int[])value;
                        ret += 2 + 4 * ints.length;
                        break;
                    }
                    case 7: {
                        ++ret;
                        break;
                    }
                    case 77: {
                        byte[] bas = (byte[])value;
                        ret += 2 + 1 * bas.length;
                        break;
                    }
                    case 3: {
                        break;
                    }
                    case 33: {
                        break;
                    }
                    case 4: {
                        ret += 4;
                        break;
                    }
                    case 44: {
                        float[] floats = (float[])value;
                        ret += 2 + 4 * floats.length;
                        break;
                    }
                    case 9: {
                        ret += 8;
                        break;
                    }
                    case 99: {
                        long[] longs = (long[])value;
                        ret += 2 + 8 * longs.length;
                        break;
                    }
                    case 8: {
                        ret += 8;
                        break;
                    }
                    case 88: {
                        double[] doubles = (double[])value;
                        ret += 2 + 8 * doubles.length;
                        break;
                    }
                    case 5: {
                        ++ret;
                        break;
                    }
                    case 55: {
                        boolean[] bools = (boolean[])value;
                        ret += 2 + bools.length;
                        break;
                    }
                    case 6: {
                        String s = (String)value;
                        tempByte = BinaryHelper.toByte(s);
                        ret += 2 + tempByte.length;
                        break;
                    }
                    case 66: {
                        String[] arrStr = (String[])value;
                        length = arrStr.length;
                        ret += 2;
                        for (int i = 0; i < arrStr.length; ++i) {
                            String str = arrStr[i];
                            tempByte = BinaryHelper.toByte(str);
                            length = tempByte.length;
                            if (length <= 0) continue;
                            ret += 2 + tempByte.length;
                        }
                        continue block24;
                    }
                    case 100: {
                        byte[] tempObject = BinaryHelper.toByte(value);
                        ret += 2 + tempObject.length;
                        break;
                    }
                    case 101: {
                        Object[] arrObject = (Object[])value;
                        length = arrObject.length;
                        ret += 2;
                        for (int i = 0; i < arrObject.length; ++i) {
                            tempByte = BinaryHelper.toByte(arrObject[i]);
                            ret += 2 + tempByte.length;
                        }
                        break;
                    }
                }
            }
            catch (IllegalAccessException code) {}
        }
        return ret;
    }
}

