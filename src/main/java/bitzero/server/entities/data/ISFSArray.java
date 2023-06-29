/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.entities.data;

import bitzero.server.entities.data.ISFSObject;
import bitzero.server.entities.data.SFSDataWrapper;
import java.util.Collection;
import java.util.Iterator;

public interface ISFSArray {
    public boolean contains(Object var1);

    public Iterator<SFSDataWrapper> iterator();

    public Object getElementAt(int var1);

    public SFSDataWrapper get(int var1);

    public void removeElementAt(int var1);

    public int size();

    public byte[] toBinary();

    public String toJson();

    public String getHexDump();

    public String getDump();

    public String getDump(boolean var1);

    public void addNull();

    public void addBool(boolean var1);

    public void addByte(byte var1);

    public void addShort(short var1);

    public void addInt(int var1);

    public void addLong(long var1);

    public void addFloat(float var1);

    public void addDouble(double var1);

    public void addUtfString(String var1);

    public void addBoolArray(Collection<Boolean> var1);

    public void addByteArray(byte[] var1);

    public void addShortArray(Collection<Short> var1);

    public void addIntArray(Collection<Integer> var1);

    public void addLongArray(Collection<Long> var1);

    public void addFloatArray(Collection<Float> var1);

    public void addDoubleArray(Collection<Double> var1);

    public void addUtfStringArray(Collection<String> var1);

    public void addSFSArray(ISFSArray var1);

    public void addSFSObject(ISFSObject var1);

    public void addClass(Object var1);

    public void add(SFSDataWrapper var1);

    public boolean isNull(int var1);

    public Boolean getBool(int var1);

    public Byte getByte(int var1);

    public Integer getUnsignedByte(int var1);

    public Short getShort(int var1);

    public Integer getInt(int var1);

    public Long getLong(int var1);

    public Float getFloat(int var1);

    public Double getDouble(int var1);

    public String getUtfString(int var1);

    public Collection<Boolean> getBoolArray(int var1);

    public byte[] getByteArray(int var1);

    public Collection<Integer> getUnsignedByteArray(int var1);

    public Collection<Short> getShortArray(int var1);

    public Collection<Integer> getIntArray(int var1);

    public Collection<Long> getLongArray(int var1);

    public Collection<Float> getFloatArray(int var1);

    public Collection<Double> getDoubleArray(int var1);

    public Collection<String> getUtfStringArray(int var1);

    public Object getClass(int var1);

    public ISFSArray getSFSArray(int var1);

    public ISFSObject getSFSObject(int var1);
}

