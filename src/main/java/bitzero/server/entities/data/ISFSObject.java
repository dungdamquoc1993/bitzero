/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.entities.data;

import bitzero.server.entities.data.ISFSArray;
import bitzero.server.entities.data.SFSDataWrapper;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public interface ISFSObject {
    public boolean isNull(String var1);

    public boolean containsKey(String var1);

    public boolean removeElement(String var1);

    public Set<String> getKeys();

    public int size();

    public Iterator<Map.Entry<String, SFSDataWrapper>> iterator();

    public byte[] toBinary();

    public String toJson();

    public String getDump();

    public String getDump(boolean var1);

    public String getHexDump();

    public SFSDataWrapper get(String var1);

    public Boolean getBool(String var1);

    public Byte getByte(String var1);

    public Integer getUnsignedByte(String var1);

    public Short getShort(String var1);

    public Integer getInt(String var1);

    public Long getLong(String var1);

    public Float getFloat(String var1);

    public Double getDouble(String var1);

    public String getUtfString(String var1);

    public Collection<Boolean> getBoolArray(String var1);

    public byte[] getByteArray(String var1);

    public Collection<Integer> getUnsignedByteArray(String var1);

    public Collection<Short> getShortArray(String var1);

    public Collection<Integer> getIntArray(String var1);

    public Collection<Long> getLongArray(String var1);

    public Collection<Float> getFloatArray(String var1);

    public Collection<Double> getDoubleArray(String var1);

    public Collection<String> getUtfStringArray(String var1);

    public ISFSArray getSFSArray(String var1);

    public ISFSObject getSFSObject(String var1);

    public Object getClass(String var1);

    public void putNull(String var1);

    public void putBool(String var1, boolean var2);

    public void putByte(String var1, byte var2);

    public void putShort(String var1, short var2);

    public void putInt(String var1, int var2);

    public void putLong(String var1, long var2);

    public void putFloat(String var1, float var2);

    public void putDouble(String var1, double var2);

    public void putUtfString(String var1, String var2);

    public void putBoolArray(String var1, Collection<Boolean> var2);

    public void putByteArray(String var1, byte[] var2);

    public void putShortArray(String var1, Collection<Short> var2);

    public void putIntArray(String var1, Collection<Integer> var2);

    public void putLongArray(String var1, Collection<Long> var2);

    public void putFloatArray(String var1, Collection<Float> var2);

    public void putDoubleArray(String var1, Collection<Double> var2);

    public void putUtfStringArray(String var1, Collection<String> var2);

    public void putSFSArray(String var1, ISFSArray var2);

    public void putSFSObject(String var1, ISFSObject var2);

    public void putClass(String var1, Object var2);

    public void put(String var1, SFSDataWrapper var2);
}

