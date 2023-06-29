/*
 * Decompiled with CFR 0_116.
 */
package bitzero.util.cross;

import bitzero.server.exceptions.BZException;
import bitzero.server.util.ByteArray;

public class CrossExtCommand {
    private ByteArray bodys = new ByteArray();
    private String cmdName = "";
    private String[] params = new String[0];

    public CrossExtCommand(String name, String[] pa) {
        this.cmdName = name;
        this.params = pa;
    }

    public byte[] getByte() {
        ByteArray tempData = new ByteArray();
        this.putString(this.cmdName);
        this.putStringArray(this.params);
        byte[] data = this.bodys.getBytes();
        try {
            tempData.writeByte(128);
            tempData.writeShort((short)(data.length + 3));
            tempData.writeByte(0);
            tempData.writeShort((short)300);
            tempData.writeBytes(data);
        }
        catch (BZException bze) {
            bze.printStackTrace();
        }
        return tempData.getBytes();
    }

    private void putStringArray(String[] array) {
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

    private void putString(String s) {
        try {
            this.bodys.writeUTF(s);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

