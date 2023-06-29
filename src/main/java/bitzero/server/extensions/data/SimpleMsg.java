/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.extensions.data;

import bitzero.server.extensions.data.BaseMsg;
import bitzero.server.util.ByteArray;

public class SimpleMsg
extends BaseMsg {
    protected ByteArray bodys = new ByteArray();

    public SimpleMsg(short type) {
        super(type);
        this.putByte(this.Error.byteValue());
    }

    public SimpleMsg(short type, int error) {
        super(type, error);
        this.putByte(this.Error.byteValue());
    }

    @Override
    public byte[] createData() {
        return this.bodys.getBytes();
    }

    public void putByte(byte b) {
        this.bodys.writeByte(b);
    }

    public void putInt(int i) {
        try {
            this.bodys.writeInt(i);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putShort(short i) {
        try {
            this.bodys.writeShort(i);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putLong(long i) {
        try {
            this.bodys.writeDouble(i);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putDouble(long l) {
        try {
            this.bodys.writeDouble(l);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putDouble(double l) {
        try {
            this.bodys.writeDouble(l);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putBoolean(boolean b) {
        try {
            this.bodys.writeBool(b);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putString(String s) {
        try {
            this.bodys.writeUTF(s);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putIntArray(int[] array) {
        try {
            this.bodys.writeShort((short)array.length);
            for (int i = 0; i < array.length; ++i) {
                this.putInt(array[i]);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putShortArray(short[] array) {
        try {
            this.bodys.writeShort((short)array.length);
            for (int i = 0; i < array.length; ++i) {
                this.putShort(array[i]);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putLongArray(long[] array) {
        try {
            this.bodys.writeShort((short)array.length);
            for (int i = 0; i < array.length; ++i) {
                this.putDouble(array[i]);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putBoleanArray(boolean[] array) {
        try {
            this.bodys.writeShort((short)array.length);
            for (int i = 0; i < array.length; ++i) {
                this.putBoolean(array[i]);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putDoubleArray(double[] array) {
        try {
            this.bodys.writeShort((short)array.length);
            for (int i = 0; i < array.length; ++i) {
                this.putDouble(array[i]);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putStringArray(String[] array) {
        try {
            this.bodys.writeShort((short)array.length);
            for (int i = 0; i < array.length; ++i) {
                this.putString(array[i]);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putByteArray(byte[] array) {
        try {
            this.bodys.writeShort((short)array.length);
            for (int i = 0; i < array.length; ++i) {
                this.putByte(array[i]);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

