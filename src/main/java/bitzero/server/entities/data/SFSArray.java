/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.entities.data;

import bitzero.engine.util.ByteUtils;
import bitzero.server.entities.data.ISFSArray;
import bitzero.server.entities.data.ISFSObject;
import bitzero.server.entities.data.SFSDataType;
import bitzero.server.entities.data.SFSDataWrapper;
import bitzero.server.protocol.serialization.DefaultObjectDumpFormatter;
import bitzero.server.protocol.serialization.DefaultSFSDataSerializer;
import bitzero.server.protocol.serialization.ISFSDataSerializer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SFSArray
implements ISFSArray {
    private ISFSDataSerializer serializer = DefaultSFSDataSerializer.getInstance();
    private List<SFSDataWrapper> dataHolder = new ArrayList<SFSDataWrapper>();

    public static SFSArray newFromBinaryData(byte[] bytes) {
        return (SFSArray)DefaultSFSDataSerializer.getInstance().binary2array(bytes);
    }

    public static SFSArray newFromResultSet(ResultSet rset) throws SQLException {
        return DefaultSFSDataSerializer.getInstance().resultSet2array(rset);
    }

    public static SFSArray newFromJsonData(String jsonStr) {
        return (SFSArray)DefaultSFSDataSerializer.getInstance().json2array(jsonStr);
    }

    public static SFSArray newInstance() {
        return new SFSArray();
    }

    @Override
    public String getDump() {
        if (this.size() == 0) {
            return "[ Empty SFSArray ]";
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
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        Object objDump = null;
        for (SFSDataWrapper wrappedObject : this.dataHolder) {
            objDump = wrappedObject.getTypeId() == SFSDataType.SFS_OBJECT ? ((ISFSObject)wrappedObject.getObject()).getDump(false) : (wrappedObject.getTypeId() == SFSDataType.SFS_ARRAY ? ((ISFSArray)wrappedObject.getObject()).getDump(false) : (wrappedObject.getTypeId() == SFSDataType.BYTE_ARRAY ? DefaultObjectDumpFormatter.prettyPrintByteArray((byte[])wrappedObject.getObject()) : (wrappedObject.getTypeId() == SFSDataType.CLASS ? wrappedObject.getObject().getClass().getName() : wrappedObject.getObject())));
            sb.append(" (").append(wrappedObject.getTypeId().name().toLowerCase()).append(") ").append(objDump).append(';');
        }
        if (this.size() > 0) {
            sb.delete(sb.length() - 1, sb.length());
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String getHexDump() {
        return ByteUtils.fullHexDump(this.toBinary());
    }

    @Override
    public byte[] toBinary() {
        return this.serializer.array2binary(this);
    }

    @Override
    public String toJson() {
        return DefaultSFSDataSerializer.getInstance().array2json(this.flatten());
    }

    @Override
    public boolean isNull(int index) {
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        if (wrapper == null) {
            return false;
        }
        return wrapper.getTypeId() == SFSDataType.NULL;
    }

    @Override
    public SFSDataWrapper get(int index) {
        return this.dataHolder.get(index);
    }

    @Override
    public Boolean getBool(int index) {
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        return wrapper != null ? (Boolean)wrapper.getObject() : null;
    }

    @Override
    public Byte getByte(int index) {
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        return wrapper != null ? (Byte)wrapper.getObject() : null;
    }

    @Override
    public Integer getUnsignedByte(int index) {
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        return wrapper != null ? Integer.valueOf(DefaultSFSDataSerializer.getInstance().getUnsignedByte(((Byte)wrapper.getObject()).byteValue())) : null;
    }

    @Override
    public Short getShort(int index) {
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        return wrapper != null ? (Short)wrapper.getObject() : null;
    }

    @Override
    public Integer getInt(int index) {
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        return wrapper != null ? (Integer)wrapper.getObject() : null;
    }

    @Override
    public Long getLong(int index) {
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        return wrapper != null ? (Long)wrapper.getObject() : null;
    }

    @Override
    public Float getFloat(int index) {
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        return wrapper != null ? (Float)wrapper.getObject() : null;
    }

    @Override
    public Double getDouble(int index) {
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        return wrapper != null ? (Double)wrapper.getObject() : null;
    }

    @Override
    public String getUtfString(int index) {
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        return wrapper != null ? (String)wrapper.getObject() : null;
    }

    @Override
    public Collection<Boolean> getBoolArray(int index) {
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        return wrapper != null ? (Collection)wrapper.getObject() : null;
    }

    @Override
    public byte[] getByteArray(int index) {
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        return wrapper != null ? (byte[])wrapper.getObject() : null;
    }

    @Override
    public Collection<Integer> getUnsignedByteArray(int index) {
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        if (wrapper == null) {
            return null;
        }
        DefaultSFSDataSerializer serializer = DefaultSFSDataSerializer.getInstance();
        ArrayList<Integer> intCollection = new ArrayList<Integer>();
        for (byte b : (byte[])wrapper.getObject()) {
            intCollection.add(serializer.getUnsignedByte(b));
        }
        return intCollection;
    }

    @Override
    public Collection<Short> getShortArray(int index) {
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        return wrapper != null ? (Collection)wrapper.getObject() : null;
    }

    @Override
    public Collection<Integer> getIntArray(int index) {
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        return wrapper != null ? (Collection)wrapper.getObject() : null;
    }

    @Override
    public Collection<Long> getLongArray(int index) {
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        return wrapper != null ? (Collection)wrapper.getObject() : null;
    }

    @Override
    public Collection<Float> getFloatArray(int index) {
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        return wrapper != null ? (Collection)wrapper.getObject() : null;
    }

    @Override
    public Collection<Double> getDoubleArray(int index) {
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        return wrapper != null ? (Collection)wrapper.getObject() : null;
    }

    @Override
    public Collection<String> getUtfStringArray(int index) {
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        return wrapper != null ? (Collection)wrapper.getObject() : null;
    }

    @Override
    public ISFSArray getSFSArray(int index) {
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        return wrapper != null ? (ISFSArray)wrapper.getObject() : null;
    }

    @Override
    public ISFSObject getSFSObject(int index) {
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        return wrapper != null ? (ISFSObject)wrapper.getObject() : null;
    }

    @Override
    public Object getClass(int index) {
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        return wrapper != null ? wrapper.getObject() : null;
    }

    @Override
    public void addBool(boolean value) {
        this.addObject(value, SFSDataType.BOOL);
    }

    @Override
    public void addBoolArray(Collection<Boolean> value) {
        this.addObject(value, SFSDataType.BOOL_ARRAY);
    }

    @Override
    public void addByte(byte value) {
        this.addObject(Byte.valueOf(value), SFSDataType.BYTE);
    }

    @Override
    public void addByteArray(byte[] value) {
        this.addObject(value, SFSDataType.BYTE_ARRAY);
    }

    @Override
    public void addDouble(double value) {
        this.addObject(value, SFSDataType.DOUBLE);
    }

    @Override
    public void addDoubleArray(Collection<Double> value) {
        this.addObject(value, SFSDataType.DOUBLE_ARRAY);
    }

    @Override
    public void addFloat(float value) {
        this.addObject(Float.valueOf(value), SFSDataType.FLOAT);
    }

    @Override
    public void addFloatArray(Collection<Float> value) {
        this.addObject(value, SFSDataType.FLOAT_ARRAY);
    }

    @Override
    public void addInt(int value) {
        this.addObject(value, SFSDataType.INT);
    }

    @Override
    public void addIntArray(Collection<Integer> value) {
        this.addObject(value, SFSDataType.INT_ARRAY);
    }

    @Override
    public void addLong(long value) {
        this.addObject(value, SFSDataType.LONG);
    }

    @Override
    public void addLongArray(Collection<Long> value) {
        this.addObject(value, SFSDataType.LONG_ARRAY);
    }

    @Override
    public void addNull() {
        this.addObject(null, SFSDataType.NULL);
    }

    @Override
    public void addSFSArray(ISFSArray value) {
        this.addObject(value, SFSDataType.SFS_ARRAY);
    }

    @Override
    public void addSFSObject(ISFSObject value) {
        this.addObject(value, SFSDataType.SFS_OBJECT);
    }

    @Override
    public void addShort(short value) {
        this.addObject(value, SFSDataType.SHORT);
    }

    @Override
    public void addShortArray(Collection<Short> value) {
        this.addObject(value, SFSDataType.SHORT_ARRAY);
    }

    @Override
    public void addUtfString(String value) {
        this.addObject(value, SFSDataType.UTF_STRING);
    }

    @Override
    public void addUtfStringArray(Collection<String> value) {
        this.addObject(value, SFSDataType.UTF_STRING_ARRAY);
    }

    @Override
    public void addClass(Object o) {
        this.addObject(o, SFSDataType.CLASS);
    }

    @Override
    public void add(SFSDataWrapper wrappedObject) {
        this.dataHolder.add(wrappedObject);
    }

    @Override
    public boolean contains(Object obj) {
        if (obj instanceof ISFSArray || obj instanceof ISFSObject) {
            throw new UnsupportedOperationException("ISFSArray and ISFSObject are not supported by this method.");
        }
        boolean found = false;
        Iterator<SFSDataWrapper> iter = this.dataHolder.iterator();
        while (iter.hasNext()) {
            Object item = iter.next().getObject();
            if (!item.equals(obj)) continue;
            found = true;
            break;
        }
        return found;
    }

    @Override
    public Object getElementAt(int index) {
        Object item = null;
        SFSDataWrapper wrapper = this.dataHolder.get(index);
        if (wrapper != null) {
            // empty if block
        }
        item = wrapper.getObject();
        return item;
    }

    @Override
    public Iterator<SFSDataWrapper> iterator() {
        return this.dataHolder.iterator();
    }

    @Override
    public void removeElementAt(int index) {
        this.dataHolder.remove(index);
    }

    @Override
    public int size() {
        return this.dataHolder.size();
    }

    public String toString() {
        return "[SFSArray, size: " + this.size() + "]";
    }

    private void addObject(Object value, SFSDataType typeId) {
        this.dataHolder.add(new SFSDataWrapper(typeId, value));
    }

    private List<Object> flatten() {
        ArrayList<Object> list = new ArrayList<Object>();
        DefaultSFSDataSerializer.getInstance().flattenArray(list, this);
        return list;
    }
}

