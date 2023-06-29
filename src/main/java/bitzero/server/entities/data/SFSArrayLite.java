/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.entities.data;

import bitzero.server.entities.data.ISFSArray;
import bitzero.server.entities.data.SFSArray;
import java.util.ArrayList;
import java.util.Collection;

public class SFSArrayLite
extends SFSArray {
    public static SFSArrayLite newInstance() {
        return new SFSArrayLite();
    }

    @Override
    public Byte getByte(int index) {
        Integer i = super.getInt(index);
        return i != null ? Byte.valueOf(i.byteValue()) : null;
    }

    @Override
    public Short getShort(int index) {
        Integer i = super.getInt(index);
        return i != null ? Short.valueOf(i.shortValue()) : null;
    }

    @Override
    public Float getFloat(int index) {
        Double d = super.getDouble(index);
        return d != null ? Float.valueOf(d.floatValue()) : null;
    }

    @Override
    public Collection<Boolean> getBoolArray(int key) {
        ISFSArray arr = this.getSFSArray(key);
        if (arr == null) {
            return null;
        }
        ArrayList<Boolean> data = new ArrayList<Boolean>();
        for (int i = 0; i < arr.size(); ++i) {
            data.add(arr.getBool(i));
        }
        return data;
    }

    @Override
    public Collection<Short> getShortArray(int key) {
        ISFSArray arr = this.getSFSArray(key);
        if (arr == null) {
            return null;
        }
        ArrayList<Short> data = new ArrayList<Short>();
        for (int i = 0; i < arr.size(); ++i) {
            data.add(arr.getInt(i).shortValue());
        }
        return data;
    }

    @Override
    public Collection<Integer> getIntArray(int key) {
        ISFSArray arr = this.getSFSArray(key);
        if (arr == null) {
            return null;
        }
        ArrayList<Integer> data = new ArrayList<Integer>();
        for (int i = 0; i < arr.size(); ++i) {
            data.add(arr.getInt(i));
        }
        return data;
    }

    @Override
    public Collection<Float> getFloatArray(int key) {
        ISFSArray arr = this.getSFSArray(key);
        if (arr == null) {
            return null;
        }
        ArrayList<Float> data = new ArrayList<Float>();
        for (int i = 0; i < arr.size(); ++i) {
            data.add(Float.valueOf(arr.getDouble(i).floatValue()));
        }
        return data;
    }

    @Override
    public Collection<Double> getDoubleArray(int key) {
        ISFSArray arr = this.getSFSArray(key);
        if (arr == null) {
            return null;
        }
        ArrayList<Double> data = new ArrayList<Double>();
        for (int i = 0; i < arr.size(); ++i) {
            data.add(arr.getDouble(i));
        }
        return data;
    }

    @Override
    public Collection<String> getUtfStringArray(int key) {
        ISFSArray arr = this.getSFSArray(key);
        if (arr == null) {
            return null;
        }
        ArrayList<String> data = new ArrayList<String>();
        for (int i = 0; i < arr.size(); ++i) {
            data.add(arr.getUtfString(i));
        }
        return data;
    }
}

