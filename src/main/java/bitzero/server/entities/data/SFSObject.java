/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.entities.data;

import bitzero.engine.util.ByteUtils;
import bitzero.server.entities.data.ISFSArray;
import bitzero.server.entities.data.ISFSObject;
import bitzero.server.entities.data.SFSArray;
import bitzero.server.entities.data.SFSDataType;
import bitzero.server.entities.data.SFSDataWrapper;
import bitzero.server.protocol.serialization.DefaultObjectDumpFormatter;
import bitzero.server.protocol.serialization.DefaultSFSDataSerializer;
import bitzero.server.protocol.serialization.ISFSDataSerializer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SFSObject
implements ISFSObject {
    private Map<String, SFSDataWrapper> dataHolder = new ConcurrentHashMap<String, SFSDataWrapper>();
    private ISFSDataSerializer serializer = DefaultSFSDataSerializer.getInstance();

    public static SFSObject newFromObject(Object o) {
        return (SFSObject)DefaultSFSDataSerializer.getInstance().pojo2sfs(o);
    }

    public static SFSObject newFromBinaryData(byte[] bytes) {
        return (SFSObject)DefaultSFSDataSerializer.getInstance().binary2object(bytes);
    }

    public static ISFSObject newFromJsonData(String jsonStr) {
        return DefaultSFSDataSerializer.getInstance().json2object(jsonStr);
    }

    public static SFSObject newFromResultSet(ResultSet rset) throws SQLException {
        return DefaultSFSDataSerializer.getInstance().resultSet2object(rset);
    }

    public static SFSObject newInstance() {
        return new SFSObject();
    }

    @Override
    public Iterator<Map.Entry<String, SFSDataWrapper>> iterator() {
        return this.dataHolder.entrySet().iterator();
    }

    @Override
    public boolean containsKey(String key) {
        return this.dataHolder.containsKey(key);
    }

    @Override
    public boolean removeElement(String key) {
        return this.dataHolder.remove(key) != null;
    }

    @Override
    public int size() {
        return this.dataHolder.size();
    }

    @Override
    public byte[] toBinary() {
        return this.serializer.object2binary(this);
    }

    @Override
    public String toJson() {
        return this.serializer.object2json(this.flatten());
    }

    @Override
    public String getDump() {
        if (this.size() == 0) {
            return "[ Empty SFSObject ]";
        }
        return DefaultObjectDumpFormatter.prettyPrintDump(this.dump());
    }

    @Override
    public String getDump(boolean noFormat) {
        if (!noFormat) {
            return this.dump();
        }
        return this.getDump();
    }

    private String dump() {
        StringBuilder buffer = new StringBuilder();
        buffer.append('{');
        for (String key : this.getKeys()) {
            SFSDataWrapper wrapper = this.get(key);
            buffer.append("(").append(wrapper.getTypeId().name().toLowerCase()).append(") ").append(key).append(": ");
            if (wrapper.getTypeId() == SFSDataType.SFS_OBJECT) {
                buffer.append(((SFSObject)wrapper.getObject()).getDump(false));
            } else if (wrapper.getTypeId() == SFSDataType.SFS_ARRAY) {
                buffer.append(((SFSArray)wrapper.getObject()).getDump(false));
            } else if (wrapper.getTypeId() == SFSDataType.BYTE_ARRAY) {
                buffer.append(DefaultObjectDumpFormatter.prettyPrintByteArray((byte[])wrapper.getObject()));
            } else if (wrapper.getTypeId() == SFSDataType.CLASS) {
                buffer.append(wrapper.getObject().getClass().getName());
            } else {
                buffer.append(wrapper.getObject());
            }
            buffer.append(';');
        }
        buffer.append('}');
        return buffer.toString();
    }

    @Override
    public String getHexDump() {
        return ByteUtils.fullHexDump(this.toBinary());
    }

    @Override
    public boolean isNull(String key) {
        SFSDataWrapper wrapper = this.dataHolder.get(key);
        if (wrapper == null) {
            return false;
        }
        return wrapper.getTypeId() == SFSDataType.NULL;
    }

    @Override
    public SFSDataWrapper get(String key) {
        return this.dataHolder.get(key);
    }

    @Override
    public Boolean getBool(String key) {
        SFSDataWrapper o = this.dataHolder.get(key);
        if (o == null) {
            return null;
        }
        return (Boolean)o.getObject();
    }

    @Override
    public Collection<Boolean> getBoolArray(String key) {
        SFSDataWrapper o = this.dataHolder.get(key);
        if (o == null) {
            return null;
        }
        return (Collection)o.getObject();
    }

    @Override
    public Byte getByte(String key) {
        SFSDataWrapper o = this.dataHolder.get(key);
        if (o == null) {
            return null;
        }
        return (Byte)o.getObject();
    }

    @Override
    public byte[] getByteArray(String key) {
        SFSDataWrapper o = this.dataHolder.get(key);
        if (o == null) {
            return null;
        }
        return (byte[])o.getObject();
    }

    @Override
    public Double getDouble(String key) {
        SFSDataWrapper o = this.dataHolder.get(key);
        if (o == null) {
            return null;
        }
        return (Double)o.getObject();
    }

    @Override
    public Collection<Double> getDoubleArray(String key) {
        SFSDataWrapper o = this.dataHolder.get(key);
        if (o == null) {
            return null;
        }
        return (Collection)o.getObject();
    }

    @Override
    public Float getFloat(String key) {
        SFSDataWrapper o = this.dataHolder.get(key);
        if (o == null) {
            return null;
        }
        return (Float)o.getObject();
    }

    @Override
    public Collection<Float> getFloatArray(String key) {
        SFSDataWrapper o = this.dataHolder.get(key);
        if (o == null) {
            return null;
        }
        return (Collection)o.getObject();
    }

    @Override
    public Integer getInt(String key) {
        SFSDataWrapper o = this.dataHolder.get(key);
        if (o == null) {
            return null;
        }
        return (Integer)o.getObject();
    }

    @Override
    public Collection<Integer> getIntArray(String key) {
        SFSDataWrapper o = this.dataHolder.get(key);
        if (o == null) {
            return null;
        }
        return (Collection)o.getObject();
    }

    @Override
    public Set<String> getKeys() {
        return this.dataHolder.keySet();
    }

    @Override
    public Long getLong(String key) {
        SFSDataWrapper o = this.dataHolder.get(key);
        if (o == null) {
            return null;
        }
        return (Long)o.getObject();
    }

    @Override
    public Collection<Long> getLongArray(String key) {
        SFSDataWrapper o = this.dataHolder.get(key);
        if (o == null) {
            return null;
        }
        return (Collection)o.getObject();
    }

    @Override
    public ISFSArray getSFSArray(String key) {
        SFSDataWrapper o = this.dataHolder.get(key);
        if (o == null) {
            return null;
        }
        return (ISFSArray)o.getObject();
    }

    @Override
    public ISFSObject getSFSObject(String key) {
        SFSDataWrapper o = this.dataHolder.get(key);
        if (o == null) {
            return null;
        }
        return (ISFSObject)o.getObject();
    }

    @Override
    public Short getShort(String key) {
        SFSDataWrapper o = this.dataHolder.get(key);
        if (o == null) {
            return null;
        }
        return (Short)o.getObject();
    }

    @Override
    public Collection<Short> getShortArray(String key) {
        SFSDataWrapper o = this.dataHolder.get(key);
        if (o == null) {
            return null;
        }
        return (Collection)o.getObject();
    }

    @Override
    public Integer getUnsignedByte(String key) {
        SFSDataWrapper o = this.dataHolder.get(key);
        if (o == null) {
            return null;
        }
        return DefaultSFSDataSerializer.getInstance().getUnsignedByte(((Byte)o.getObject()).byteValue());
    }

    @Override
    public Collection<Integer> getUnsignedByteArray(String key) {
        SFSDataWrapper o = this.dataHolder.get(key);
        if (o == null) {
            return null;
        }
        DefaultSFSDataSerializer serializer = DefaultSFSDataSerializer.getInstance();
        ArrayList<Integer> intCollection = new ArrayList<Integer>();
        for (byte b : (byte[])o.getObject()) {
            intCollection.add(serializer.getUnsignedByte(b));
        }
        return intCollection;
    }

    @Override
    public String getUtfString(String key) {
        SFSDataWrapper o = this.dataHolder.get(key);
        if (o == null) {
            return null;
        }
        return (String)o.getObject();
    }

    @Override
    public Collection<String> getUtfStringArray(String key) {
        SFSDataWrapper o = this.dataHolder.get(key);
        if (o == null) {
            return null;
        }
        return (Collection)o.getObject();
    }

    @Override
    public Object getClass(String key) {
        SFSDataWrapper o = this.dataHolder.get(key);
        if (o == null) {
            return null;
        }
        return o.getObject();
    }

    @Override
    public void putBool(String key, boolean value) {
        this.putObj(key, value, SFSDataType.BOOL);
    }

    @Override
    public void putBoolArray(String key, Collection<Boolean> value) {
        this.putObj(key, value, SFSDataType.BOOL_ARRAY);
    }

    @Override
    public void putByte(String key, byte value) {
        this.putObj(key, Byte.valueOf(value), SFSDataType.BYTE);
    }

    @Override
    public void putByteArray(String key, byte[] value) {
        this.putObj(key, value, SFSDataType.BYTE_ARRAY);
    }

    @Override
    public void putDouble(String key, double value) {
        this.putObj(key, value, SFSDataType.DOUBLE);
    }

    @Override
    public void putDoubleArray(String key, Collection<Double> value) {
        this.putObj(key, value, SFSDataType.DOUBLE_ARRAY);
    }

    @Override
    public void putFloat(String key, float value) {
        this.putObj(key, Float.valueOf(value), SFSDataType.FLOAT);
    }

    @Override
    public void putFloatArray(String key, Collection<Float> value) {
        this.putObj(key, value, SFSDataType.FLOAT_ARRAY);
    }

    @Override
    public void putInt(String key, int value) {
        this.putObj(key, value, SFSDataType.INT);
    }

    @Override
    public void putIntArray(String key, Collection<Integer> value) {
        this.putObj(key, value, SFSDataType.INT_ARRAY);
    }

    @Override
    public void putLong(String key, long value) {
        this.putObj(key, value, SFSDataType.LONG);
    }

    @Override
    public void putLongArray(String key, Collection<Long> value) {
        this.putObj(key, value, SFSDataType.LONG_ARRAY);
    }

    @Override
    public void putNull(String key) {
        this.dataHolder.put(key, new SFSDataWrapper(SFSDataType.NULL, null));
    }

    @Override
    public void putSFSArray(String key, ISFSArray value) {
        this.putObj(key, value, SFSDataType.SFS_ARRAY);
    }

    @Override
    public void putSFSObject(String key, ISFSObject value) {
        this.putObj(key, value, SFSDataType.SFS_OBJECT);
    }

    @Override
    public void putShort(String key, short value) {
        this.putObj(key, value, SFSDataType.SHORT);
    }

    @Override
    public void putShortArray(String key, Collection<Short> value) {
        this.putObj(key, value, SFSDataType.SHORT_ARRAY);
    }

    @Override
    public void putUtfString(String key, String value) {
        this.putObj(key, value, SFSDataType.UTF_STRING);
    }

    @Override
    public void putUtfStringArray(String key, Collection<String> value) {
        this.putObj(key, value, SFSDataType.UTF_STRING_ARRAY);
    }

    @Override
    public void put(String key, SFSDataWrapper wrappedObject) {
        this.putObj(key, wrappedObject, null);
    }

    @Override
    public void putClass(String key, Object o) {
        this.putObj(key, o, SFSDataType.CLASS);
    }

    public String toString() {
        return "[SFSObject, size: " + this.size() + "]";
    }

    private void putObj(String key, Object value, SFSDataType typeId) {
        if (key == null) {
            throw new IllegalArgumentException("SFSObject requires a non-null key for a 'put' operation!");
        }
        if (key.length() > 255) {
            throw new IllegalArgumentException("SFSObject keys must be less than 255 characters!");
        }
        if (value == null) {
            throw new IllegalArgumentException("SFSObject requires a non-null value! If you need to add a null use the putNull() method.");
        }
        if (value instanceof SFSDataWrapper) {
            this.dataHolder.put(key, (SFSDataWrapper)value);
        } else {
            this.dataHolder.put(key, new SFSDataWrapper(typeId, value));
        }
    }

    private Map<String, Object> flatten() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        DefaultSFSDataSerializer.getInstance().flattenObject(map, this);
        return map;
    }
}

