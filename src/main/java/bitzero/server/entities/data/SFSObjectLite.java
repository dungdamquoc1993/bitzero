/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.entities.data;

import bitzero.server.entities.data.ISFSArray;
import bitzero.server.entities.data.SFSObject;
import java.util.ArrayList;
import java.util.Collection;

public final class SFSObjectLite
extends SFSObject {
    public static SFSObject newInstance() {
        return new SFSObjectLite();
    }

    @Override
    public Byte getByte(String key) {
        Integer i = super.getInt(key);
        return i != null ? Byte.valueOf(i.byteValue()) : null;
    }

    @Override
    public Short getShort(String key) {
        Integer i = super.getInt(key);
        return i != null ? Short.valueOf(i.shortValue()) : null;
    }

    @Override
    public Float getFloat(String key) {
        Double d = super.getDouble(key);
        return d != null ? Float.valueOf(d.floatValue()) : null;
    }

    @Override
    public Collection<Boolean> getBoolArray(String key) {
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
    public Collection<Short> getShortArray(String key) {
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
    public Collection<Integer> getIntArray(String key) {
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
    public Collection<Float> getFloatArray(String key) {
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
    public Collection<Double> getDoubleArray(String key) {
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
    public Collection<String> getUtfStringArray(String key) {
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

