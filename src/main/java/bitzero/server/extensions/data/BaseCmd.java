/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package bitzero.server.extensions.data;

import bitzero.server.extensions.data.DataCmd;
import bitzero.server.util.BinaryHelper;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.List;
import org.apache.log4j.Logger;

public class BaseCmd {
    protected DataCmd data;
    public static Logger logger = Logger.getLogger((String)"request");

    public BaseCmd(DataCmd data) {
        this.data = data;
    }

    protected ByteBuffer makeBuffer() {
        ByteBuffer buffer = ByteBuffer.wrap(this.data.getRawData());
        return buffer;
    }

    protected byte readByte(ByteBuffer buffer) {
        byte data = 0;
        try {
            data = buffer.get();
        }
        catch (Exception var3_3) {
            // empty catch block
        }
        return data;
    }

    protected byte[] readByteArray(ByteBuffer buffer) {
        byte[] data = new byte[]{};
        try {
            data = new byte[buffer.getShort()];
            for (int i = 0; i < data.length; ++i) {
                data[i] = buffer.get();
            }
        }
        catch (Exception i) {
            // empty catch block
        }
        return data;
    }

    protected short readShort(ByteBuffer buffer) {
        short data = 0;
        try {
            data = buffer.getShort();
        }
        catch (Exception var3_3) {
            // empty catch block
        }
        return data;
    }

    protected int readInt(ByteBuffer buffer) {
        int data = 0;
        try {
            data = buffer.getInt();
        }
        catch (Exception var3_3) {
            // empty catch block
        }
        return data;
    }

    protected float readFloat(ByteBuffer buffer) {
        float data = 0.0f;
        try {
            data = buffer.getFloat();
        }
        catch (Exception var3_3) {
            // empty catch block
        }
        return data;
    }

    protected long readDouble(ByteBuffer buffer) {
        long data = 0;
        try {
            data = (long)buffer.getDouble();
        }
        catch (Exception var4_3) {
            // empty catch block
        }
        return data;
    }

    protected long readLong(ByteBuffer buffer) {
        long data = 0;
        try {
            data = buffer.getLong();
        }
        catch (Exception var4_3) {
            // empty catch block
        }
        return data;
    }

    protected boolean readBoolean(ByteBuffer buffer) {
        boolean data = false;
        try {
            data = BinaryHelper.toBool(buffer.get());
        }
        catch (Exception var3_3) {
            // empty catch block
        }
        return data;
    }

    protected String readString(ByteBuffer buffer) {
        String data = "";
        try {
            short length = buffer.getShort();
            byte[] b = new byte[length];
            buffer.get(b);
            data = BinaryHelper.toString(b);
        }
        catch (Exception length) {
            // empty catch block
        }
        return data;
    }

    protected int[] readIntArray(ByteBuffer buffer) {
        int[] data = new int[]{};
        try {
            data = new int[buffer.getShort()];
            for (int i = 0; i < data.length; ++i) {
                data[i] = buffer.getInt();
            }
        }
        catch (Exception i) {
            // empty catch block
        }
        return data;
    }

    protected short[] readShortArray(ByteBuffer buffer) {
        short[] data = new short[]{};
        try {
            data = new short[buffer.getShort()];
            for (int i = 0; i < data.length; ++i) {
                data[i] = buffer.getShort();
            }
        }
        catch (Exception i) {
            // empty catch block
        }
        return data;
    }

    protected long[] readLongArray(ByteBuffer buffer) {
        long[] data = new long[]{};
        try {
            data = new long[buffer.getShort()];
            for (int i = 0; i < data.length; ++i) {
                data[i] = buffer.getLong();
            }
        }
        catch (Exception i) {
            // empty catch block
        }
        return data;
    }

    protected long[] readDoubleArray(ByteBuffer buffer) {
        long[] data = new long[]{};
        try {
            data = new long[buffer.getShort()];
            for (int i = 0; i < data.length; ++i) {
                data[i] = (long)buffer.getDouble();
            }
        }
        catch (Exception i) {
            // empty catch block
        }
        return data;
    }

    protected float[] readFloatArray(ByteBuffer buffer) {
        float[] data = new float[]{};
        try {
            data = new float[buffer.getShort()];
            for (int i = 0; i < data.length; ++i) {
                data[i] = buffer.getFloat();
            }
        }
        catch (Exception i) {
            // empty catch block
        }
        return data;
    }

    protected boolean[] readBooleanArray(ByteBuffer buffer) {
        boolean[] data = new boolean[]{};
        try {
            data = new boolean[buffer.getShort()];
            for (int i = 0; i < data.length; ++i) {
                data[i] = BinaryHelper.toBool(buffer.get());
            }
        }
        catch (Exception i) {
            // empty catch block
        }
        return data;
    }

    protected String[] readStringArray(ByteBuffer buffer) {
        String[] data = new String[]{};
        try {
            data = new String[buffer.getShort()];
            for (int i = 0; i < data.length; ++i) {
                short lenStr = buffer.getShort();
                if (lenStr > 0) {
                    byte[] b = new byte[lenStr];
                    buffer.get(b);
                    data[i] = BinaryHelper.toString(b);
                    continue;
                }
                data[i] = "";
            }
        }
        catch (Exception b) {
            // empty catch block
        }
        return data;
    }

    public void unpackData() {
        byte[] rawByte = this.data.getRawData();
        List<Field> declaredFields = BinaryHelper.getDeclaredFields(this, 1);
        ByteBuffer buffer = ByteBuffer.wrap(rawByte);
        for (Field f : declaredFields) {
            try {
                Object value = f.get(this);
                byte code = value != null ? BinaryHelper.findClassCode(value) : BinaryHelper.findClassCodeType(f.getType());
                short length;
                byte[] b;
                switch (code) {                  
                    case 7: {
                        f.setByte(this, buffer.get());
                        break;
                    }
                    case 1: {
                        f.setShort(this, buffer.getShort());
                        break;
                    }
                    case 11: {
                        length = buffer.getShort();
                        short[] tempSh = new short[length];
                        for (int i = 0; i < tempSh.length; ++i) {
                            tempSh[i] = buffer.get();
                        }
                        f.set(this, tempSh);
                        break;
                    }
                    case 2: {
                        f.setInt(this, buffer.getInt());
                        break;
                    }
                    case 22: {
                        length = buffer.getShort();
                        int[] temp = new int[length];
                        for (int i = 0; i < temp.length; ++i) {
                            temp[i] = buffer.getInt();
                        }
                        f.set(this, temp);
                        break;
                    }
                    case 3: {
                        break;
                    }
                    case 33: {
                        break;
                    }
                    case 4: {
                        f.setFloat(this, buffer.getFloat());
                        break;
                    }
                    case 44: {
                        length = buffer.getShort();
                        float[] tempF = new float[length];
                        for (int i = 0; i < tempF.length; ++i) {
                            tempF[i] = buffer.getFloat();
                        }
                        f.set(this, tempF);
                        break;
                    }
                    case 8: {
                        f.setLong(this, (long)buffer.getDouble());
                        break;
                    }
                    case 88: {
                        length = buffer.getShort();
                        long[] tempD = new long[length];
                        for (int i = 0; i < tempD.length; ++i) {
                            tempD[i] = (long)buffer.getDouble();
                        }
                        f.set(this, tempD);
                        break;
                    }
                    case 9: {
                        f.setLong(this, buffer.getLong());
                        break;
                    }
                    case 99: {
                        length = buffer.getShort();
                        double[] tempL = new double[length];
                        for (int i = 0; i < tempL.length; ++i) {
                            tempL[i] = buffer.getLong();
                        }
                        f.set(this, tempL);
                        break;
                    }
                    case 55: {
                        length = buffer.getShort();
                        boolean[] tempB = new boolean[length];
                        for (int i = 0; i < tempB.length; ++i) {
                            tempB[i] = BinaryHelper.toBool(buffer.get());
                        }
                        f.set(this, tempB);
                        break;
                    }
                    case 5: {
                        f.setBoolean(this, BinaryHelper.toBool(buffer.get()));
                        break;
                    }
                    case 6: {
                        length = buffer.getShort();
                        b = new byte[length];
                        buffer.get(b);
                        f.set(this, BinaryHelper.toString(b));
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
                        f.set(this, arrString);
                        break;
                    }
                    case 100: {
                        length = buffer.getShort();
                        b = new byte[length];
                        buffer.get(b);
                        value = f.get(this);
                        BinaryHelper.toObject(b, value, value);
                        f.set(this, value);
                        break;
                    }
                    case 101: {
                        length = buffer.getShort();
                        Object[] arrObj = new Object[length];
                        Object info = ((Object[])f.get(this))[0];
                        Class c = info.getClass();
                        for (int i = 0; i < arrObj.length; ++i) {
                            length = buffer.getShort();
                            b = new byte[length];
                            buffer.get(b);
                            arrObj[i] = c.newInstance();
                            BinaryHelper.toObject(b, arrObj[i], info);
                        }
                        f.set(this, arrObj);
                    }
                }
            }
            catch (IllegalAccessException e) {
                if (!logger.isDebugEnabled()) continue;
                e.printStackTrace();
            }
            catch (InstantiationException e) {
                if (!logger.isDebugEnabled()) continue;
                e.printStackTrace();
            }
            catch (Exception e) {
                if (!logger.isDebugEnabled()) continue;
                e.printStackTrace();
            }
        }
    }
}

