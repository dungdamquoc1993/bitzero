/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.extensions.data;

import bitzero.server.BitZeroServer;
import bitzero.server.config.CoreSettings;
import bitzero.server.config.IConfigurator;
import bitzero.server.util.BinaryHelper;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.List;
import org.slf4j.LoggerFactory;

public class BaseMsg {
    public Byte Error = 0;
    protected short Id = 0;

    public BaseMsg(short type) {
        this.Id = type;
        this.Error = 0;
    }

    protected BaseMsg(short type, int error) {
        this.Id = type;
        this.Error = Byte.valueOf((byte)error);
    }

    public short getId() {
        return this.Id;
    }

    protected ByteBuffer makeBuffer() {
        ByteBuffer bf = ByteBuffer.allocate(BitZeroServer.getInstance().getConfigurator().getCoreSettings().maxPacketBufferSize);
        bf.put(this.Error.byteValue());
        return bf;
    }

    protected byte[] packBuffer(ByteBuffer bf) {
        int pos = bf.position();
        byte[] result = new byte[pos];
        bf.flip();
        bf.get(result, 0, pos);
        return result;
    }

    protected void putStr(ByteBuffer bf, String value) {
        String s = value;
        byte[] tempByte = BinaryHelper.toByte(s);
        Integer length = tempByte.length;
        bf.putShort(length.shortValue());
        bf.put(tempByte);
    }

    protected void putBoolean(ByteBuffer bf, Boolean b) {
        boolean tempB = b;
        bf.put(BinaryHelper.toByte(tempB));
    }

    protected void putLong(ByteBuffer bf, long value) {
        bf.putLong(value);
    }

    protected void putIntArray(ByteBuffer bf, int[] value) {
        int[] ints = value;
        Integer length = value.length;
        bf.putShort(length.shortValue());
        bf.put(BinaryHelper.toByte(ints));
    }

    protected void putLongArray(ByteBuffer bf, long[] value) {
        long[] longs = value;
        Integer length = value.length;
        bf.putShort(length.shortValue());
        for (long db : longs) {
            bf.putLong(db);
        }
    }

    protected void putFloatArray(ByteBuffer bf, float[] value) {
        float[] longs = value;
        Integer length = value.length;
        bf.putShort(length.shortValue());
        bf.put(BinaryHelper.toByte(longs));
    }

    protected void putDoubleArray(ByteBuffer bf, double[] value) {
        double[] longs = value;
        Integer length = value.length;
        bf.putShort(length.shortValue());
        bf.put(BinaryHelper.toByte(longs));
    }

    protected void putBooleanArray(ByteBuffer bf, boolean[] value) {
        boolean[] longs = value;
        Integer length = value.length;
        bf.putShort(length.shortValue());
        bf.put(BinaryHelper.toByte(longs));
    }

    protected void putShortArray(ByteBuffer bf, short[] value) {
        short[] longs = value;
        Integer length = value.length;
        bf.putShort(length.shortValue());
        bf.put(BinaryHelper.toByte(longs));
    }

    protected void putByteArray(ByteBuffer bf, byte[] value) {
        byte[] longs = value;
        Integer length = value.length;
        bf.putShort(length.shortValue());
        bf.put(longs);
    }

    protected void putStringArray(ByteBuffer bf, String[] value) {
        String[] arrStr = value;
        Integer length = arrStr.length;
        bf.putShort(length.shortValue());
        for (int i = 0; i < arrStr.length; ++i) {
            String str = arrStr[i];
            byte[] tempByte = BinaryHelper.toByte(str);
            length = tempByte.length;
            bf.putShort(length.shortValue());
            if (length <= 0) continue;
            bf.put(tempByte);
        }
    }

    public byte[] createData() {
        Field[] fields = new Field[this.getClass().getFields().length];
        try {
            fields[0] = this.getClass().getField("Error");
        }
        catch (NoSuchFieldException e) {
            return null;
        }
        List<Field> declaredFields = BinaryHelper.getDeclaredFields(this, 1);
        for (int i = 0; i < declaredFields.size(); ++i) {
            fields[i + 1] = declaredFields.get(i);
        }
        ByteBuffer buffer = ByteBuffer.allocate(BinaryHelper.sizeOf(this));
        block28 : for (Field f : fields) {
            try {
                Object value = f.get(this);
                byte code = BinaryHelper.findClassCode(value);
                int n;
                int db;
                Integer length;
                byte[] tempByte;
                switch (code) {
                   
                    case 1: {
                        buffer.putShort((Short)value);
                        break;
                    }
                    case 11: {
                        short[] shorts = (short[])value;
                        length = shorts.length;
                        buffer.putShort(length.shortValue());
                        buffer.put(BinaryHelper.toByte(shorts));
                        break;
                    }
                    case 7: {
                        buffer.put(((Byte)value).byteValue());
                        break;
                    }
                    case 77: {
                        byte[] byteArr = (byte[])value;
                        length = byteArr.length;
                        buffer.putShort(length.shortValue());
                        buffer.put(byteArr);
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
                        continue block28;
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
                            buffer.putLong(db2);
                        }
                        continue block28;
                    }
                    case 5: {
                        boolean tempB = (Boolean)value;
                        buffer.put(BinaryHelper.toByte(tempB));
                        break;
                    }
                    case 55: {
                        boolean[] bools = (boolean[])value;
                        length = bools.length;
                        buffer.putShort(length.shortValue());
                        buffer.put(BinaryHelper.toByte(bools));
                        break;
                    }
                    case 6: {
                        String s = (String)value;
                        tempByte = BinaryHelper.toByte(s);
                        length = tempByte.length;
                        buffer.putShort(length.shortValue());
                        buffer.put(tempByte);
                        break;
                    }
                    case 66: {
                        String[] arrStr = (String[])value;
                        length = arrStr.length;
                        buffer.putShort(length.shortValue());
                        for (int i2 = 0; i2 < arrStr.length; ++i2) {
                            String str = arrStr[i2];
                            tempByte = BinaryHelper.toByte(str);
                            length = tempByte.length;
                            buffer.putShort(length.shortValue());
                            if (length <= 0) continue;
                            buffer.put(tempByte);
                        }
                        continue block28;
                    }
                    case 100: {
                        byte[] tempObject = BinaryHelper.toByte(value);
                        length = tempObject.length;
                        buffer.putShort(length.shortValue());
                        buffer.put(tempObject);
                        break;
                    }
                    case 101: {
                        Object[] arrObject = (Object[])value;
                        length = arrObject.length;
                        buffer.putShort(length.shortValue());
                        for (int i3 = 0; i3 < arrObject.length; ++i3) {
                            tempByte = BinaryHelper.toByte(arrObject[i3]);
                            length = tempByte.length;
                            buffer.putShort(length.shortValue());
                            buffer.put(tempByte);
                        }
                        break;
                    }
                }
                continue;
            }
            catch (IllegalAccessException e) {
                if (!LoggerFactory.getLogger((String)"request").isDebugEnabled()) continue;
                e.printStackTrace();
                continue;
            }
            catch (Exception e) {
                if (!LoggerFactory.getLogger((String)"request").isDebugEnabled()) continue;
                e.printStackTrace();
            }
        }
        byte[] ret = new byte[buffer.position()];
        buffer.rewind();
        buffer.get(ret);
        return ret;
    }
}

