/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  net.sf.json.JSONArray
 *  net.sf.json.JSONObject
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.protocol.serialization;

import bitzero.server.entities.data.ISFSArray;
import bitzero.server.entities.data.ISFSObject;
import bitzero.server.entities.data.SFSArray;
import bitzero.server.entities.data.SFSArrayLite;
import bitzero.server.entities.data.SFSDataType;
import bitzero.server.entities.data.SFSDataWrapper;
import bitzero.server.entities.data.SFSObject;
import bitzero.server.entities.data.SFSObjectLite;
import bitzero.server.exceptions.BZCodecException;
import bitzero.server.exceptions.BZRuntimeException;
import bitzero.server.protocol.serialization.ISFSDataSerializer;
import bitzero.server.protocol.serialization.SerializableBZType;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSFSDataSerializer
implements ISFSDataSerializer {
    private static final String CLASS_MARKER_KEY = "$C";
    private static final String CLASS_FIELDS_KEY = "$F";
    private static final String FIELD_NAME_KEY = "N";
    private static final String FIELD_VALUE_KEY = "V";
    private static DefaultSFSDataSerializer instance = new DefaultSFSDataSerializer();
    private static int BUFFER_CHUNK_SIZE = 512;
    private final Logger logger;

    public static DefaultSFSDataSerializer getInstance() {
        return instance;
    }

    private DefaultSFSDataSerializer() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    public int getUnsignedByte(byte b) {
        return 255 & b;
    }

    @Override
    public String array2json(List<Object> array) {
        return JSONArray.fromObject(array).toString();
    }

    @Override
    public ISFSArray binary2array(byte[] data) {
        if (data.length < 3) {
            throw new IllegalStateException("Can't decode an SFSArray. Byte data is insufficient. Size: " + data.length + " bytes");
        }
        ByteBuffer buffer = ByteBuffer.allocate(data.length);
        buffer.put(data);
        buffer.flip();
        return this.decodeSFSArray(buffer);
    }

    private ISFSArray decodeSFSArray(ByteBuffer buffer) {
        SFSArray sfsArray = SFSArray.newInstance();
        byte headerBuffer = buffer.get();
        if (headerBuffer != SFSDataType.SFS_ARRAY.getTypeID()) {
            throw new IllegalStateException("Invalid SFSDataType. Expected: " + SFSDataType.SFS_ARRAY.getTypeID() + ", found: " + headerBuffer);
        }
        int size = buffer.getShort();
        if (size < 0) {
            throw new IllegalStateException("Can't decode SFSArray. Size is negative = " + size);
        }
        try {
            for (int i = 0; i < size; ++i) {
                SFSDataWrapper decodedObject = this.decodeObject(buffer);
                if (decodedObject == null) {
                    throw new IllegalStateException("Could not decode SFSArray item at index: " + i);
                }
                sfsArray.add(decodedObject);
            }
        }
        catch (BZCodecException codecError) {
            throw new IllegalArgumentException(codecError.getMessage());
        }
        return sfsArray;
    }

    @Override
    public ISFSObject binary2object(byte[] data) {
        if (data.length < 3) {
            throw new IllegalStateException("Can't decode an SFSObject. Byte data is insufficient. Size: " + data.length + " bytes");
        }
        ByteBuffer buffer = ByteBuffer.allocate(data.length);
        buffer.put(data);
        buffer.flip();
        return this.decodeSFSObject(buffer);
    }

    private ISFSObject decodeSFSObject(ByteBuffer buffer) {
        SFSObject sfsObject = SFSObject.newInstance();
        byte headerBuffer = buffer.get();
        if (headerBuffer != SFSDataType.SFS_OBJECT.getTypeID()) {
            throw new IllegalStateException("Invalid SFSDataType. Expected: " + SFSDataType.SFS_OBJECT.getTypeID() + ", found: " + headerBuffer);
        }
        int size = buffer.getShort();
        if (size < 0) {
            throw new IllegalStateException("Can't decode SFSObject. Size is negative = " + size);
        }
        try {
            for (int i = 0; i < size; ++i) {
                short keySize = buffer.getShort();
                if (keySize < 0 || keySize > 255) {
                    throw new IllegalStateException("Invalid SFSObject key length. Found = " + keySize);
                }
                byte[] keyData = new byte[keySize];
                buffer.get(keyData, 0, keyData.length);
                String key = new String(keyData);
                SFSDataWrapper decodedObject = this.decodeObject(buffer);
                if (decodedObject == null) {
                    throw new IllegalStateException("Could not decode value for key: " + keyData);
                }
                sfsObject.put(key, decodedObject);
            }
        }
        catch (BZCodecException codecError) {
            throw new IllegalArgumentException(codecError.getMessage());
        }
        return sfsObject;
    }

    @Override
    public ISFSArray json2array(String jsonStr) {
        if (jsonStr.length() < 2) {
            throw new IllegalStateException("Can't decode SFSObject. JSON String is too short. Len: " + jsonStr.length());
        }
        JSONArray jsa = JSONArray.fromObject((Object)jsonStr);
        return this.decodeSFSArray(jsa);
    }

    private ISFSArray decodeSFSArray(JSONArray jsa) {
        SFSArrayLite sfsArray = SFSArrayLite.newInstance();
        for (Object value : jsa) {
            SFSDataWrapper decodedObject = this.decodeJsonObject(value);
            if (decodedObject != null) {
                sfsArray.add(decodedObject);
                continue;
            }
            throw new IllegalStateException("(json2sfarray) Could not decode value for object: " + value);
        }
        return sfsArray;
    }

    @Override
    public ISFSObject json2object(String jsonStr) {
        if (jsonStr.length() < 2) {
            throw new IllegalStateException("Can't decode SFSObject. JSON String is too short. Len: " + jsonStr.length());
        }
        JSONObject jso = JSONObject.fromObject((Object)jsonStr);
        return this.decodeSFSObject(jso);
    }

    private ISFSObject decodeSFSObject(JSONObject jso) {
        SFSObject sfsObject = SFSObjectLite.newInstance();
        for (Object key : jso.keySet()) {
            Object value = jso.get(key);
            SFSDataWrapper decodedObject = this.decodeJsonObject(value);
            if (decodedObject != null) {
                sfsObject.put((String)key, decodedObject);
                continue;
            }
            throw new IllegalStateException("(json2sfsobj) Could not decode value for key: " + key);
        }
        return sfsObject;
    }

    private SFSDataWrapper decodeJsonObject(Object o) {
        if (o instanceof Integer) {
            return new SFSDataWrapper(SFSDataType.INT, o);
        }
        if (o instanceof Long) {
            return new SFSDataWrapper(SFSDataType.LONG, o);
        }
        if (o instanceof Double) {
            return new SFSDataWrapper(SFSDataType.DOUBLE, o);
        }
        if (o instanceof Boolean) {
            return new SFSDataWrapper(SFSDataType.BOOL, o);
        }
        if (o instanceof String) {
            return new SFSDataWrapper(SFSDataType.UTF_STRING, o);
        }
        if (o instanceof JSONObject) {
            JSONObject jso = (JSONObject)o;
            if (jso.isNullObject()) {
                return new SFSDataWrapper(SFSDataType.NULL, null);
            }
            return new SFSDataWrapper(SFSDataType.SFS_OBJECT, this.decodeSFSObject(jso));
        }
        if (o instanceof JSONArray) {
            return new SFSDataWrapper(SFSDataType.SFS_ARRAY, this.decodeSFSArray((JSONArray)o));
        }
        Object[] arrobject = new Object[2];
        arrobject[0] = o;
        arrobject[1] = o == null ? "null" : o.getClass();
        throw new IllegalArgumentException(String.format("Unrecognized DataType while converting JSONObject 2 SFSObject. Object: %s, Type: %s", arrobject));
    }

    @Override
    public SFSObject resultSet2object(ResultSet rset) throws SQLException {
        ResultSetMetaData metaData = rset.getMetaData();
        SFSObject sfso = new SFSObject();
        if (rset.isBeforeFirst()) {
            rset.next();
        }
        for (int col = 1; col <= metaData.getColumnCount(); ++col) {
            String colName = metaData.getColumnName(col);
            int type = metaData.getColumnType(col);
            Object rawDataObj = rset.getObject(col);
            if (rawDataObj == null) continue;
            if (type == 0) {
                sfso.putNull(colName);
                continue;
            }
            if (type == 16) {
                sfso.putBool(colName, rset.getBoolean(col));
                continue;
            }
            if (type == 91) {
                sfso.putLong(colName, rset.getDate(col).getTime());
                continue;
            }
            if (type == 6 || type == 3 || type == 8 || type == 7) {
                sfso.putDouble(colName, rset.getDouble(col));
                continue;
            }
            if (type == 4 || type == -6 || type == 5) {
                sfso.putInt(colName, rset.getInt(col));
                continue;
            }
            if (type == -1 || type == 12 || type == 1) {
                sfso.putUtfString(colName, rset.getString(col));
                continue;
            }
            if (type == -9 || type == -16 || type == -15) {
                sfso.putUtfString(colName, rset.getNString(col));
                continue;
            }
            if (type == 93) {
                sfso.putLong(colName, rset.getTimestamp(col).getTime());
                continue;
            }
            if (type == -5) {
                sfso.putLong(colName, rset.getLong(col));
                continue;
            }
            if (type == -4) {
                byte[] binData = this.getBlobData(colName, rset.getBinaryStream(col));
                if (binData == null) continue;
                sfso.putByteArray(colName, binData);
                continue;
            }
            if (type == 2004) {
                Blob blob = rset.getBlob(col);
                sfso.putByteArray(colName, blob.getBytes(0, (int)blob.length()));
                continue;
            }
            this.logger.info("Skipping Unsupported SQL TYPE: " + type + ", Column:" + colName);
        }
        return sfso;
    }

    private byte[] getBlobData(String colName, InputStream stream) {
        return null;
    }

    @Override
    public SFSArray resultSet2array(ResultSet rset) throws SQLException {
        SFSArray sfsa = new SFSArray();
        while (rset.next()) {
            sfsa.addSFSObject(this.resultSet2object(rset));
        }
        return sfsa;
    }

    @Override
    public byte[] object2binary(ISFSObject object) {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CHUNK_SIZE);
        buffer.put((byte)SFSDataType.SFS_OBJECT.getTypeID());
        buffer.putShort((short)object.size());
        return this.obj2bin(object, buffer);
    }

    private byte[] obj2bin(ISFSObject object, ByteBuffer buffer) {
        Set<String> keys = object.getKeys();
        for (String key : keys) {
            SFSDataWrapper wrapper = object.get(key);
            Object dataObj = wrapper.getObject();
            buffer = this.encodeSFSObjectKey(buffer, key);
            buffer = this.encodeObject(buffer, wrapper.getTypeId(), dataObj);
        }
        int pos = buffer.position();
        byte[] result = new byte[pos];
        buffer.flip();
        buffer.get(result, 0, pos);
        return result;
    }

    @Override
    public byte[] array2binary(ISFSArray array) {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CHUNK_SIZE);
        buffer.put((byte)SFSDataType.SFS_ARRAY.getTypeID());
        buffer.putShort((short)array.size());
        return this.arr2bin(array, buffer);
    }

    private byte[] arr2bin(ISFSArray array, ByteBuffer buffer) {
        Iterator<SFSDataWrapper> iter = array.iterator();
        while (iter.hasNext()) {
            SFSDataWrapper wrapper = iter.next();
            Object dataObj = wrapper.getObject();
            buffer = this.encodeObject(buffer, wrapper.getTypeId(), dataObj);
        }
        int pos = buffer.position();
        byte[] result = new byte[pos];
        buffer.flip();
        buffer.get(result, 0, pos);
        return result;
    }

    @Override
    public String object2json(Map<String, Object> map) {
        return JSONObject.fromObject(map).toString();
    }

    public void flattenObject(Map<String, Object> map, SFSObject sfsObj) {
        Iterator<Map.Entry<String, SFSDataWrapper>> it = sfsObj.iterator();
        while (it.hasNext()) {
            Map.Entry<String, SFSDataWrapper> entry = it.next();
            String key = entry.getKey();
            SFSDataWrapper value = entry.getValue();
            if (value.getTypeId() == SFSDataType.SFS_OBJECT) {
                HashMap<String, Object> newMap = new HashMap<String, Object>();
                map.put(key, newMap);
                this.flattenObject(newMap, (SFSObject)value.getObject());
                continue;
            }
            if (value.getTypeId() == SFSDataType.SFS_ARRAY) {
                ArrayList<Object> newList = new ArrayList<Object>();
                map.put(key, newList);
                this.flattenArray(newList, (SFSArray)value.getObject());
                continue;
            }
            map.put(key, value.getObject());
        }
    }

    public void flattenArray(List<Object> array, SFSArray sfsArray) {
        Iterator<SFSDataWrapper> it = sfsArray.iterator();
        while (it.hasNext()) {
            SFSDataWrapper value = it.next();
            if (value.getTypeId() == SFSDataType.SFS_OBJECT) {
                HashMap<String, Object> newMap = new HashMap<String, Object>();
                array.add(newMap);
                this.flattenObject(newMap, (SFSObject)value.getObject());
                continue;
            }
            if (value.getTypeId() == SFSDataType.SFS_ARRAY) {
                ArrayList<Object> newList = new ArrayList<Object>();
                array.add(newList);
                this.flattenArray(newList, (SFSArray)value.getObject());
                continue;
            }
            array.add(value.getObject());
        }
    }

    private SFSDataWrapper decodeObject(ByteBuffer buffer) throws BZCodecException {
        SFSDataWrapper decodedObject = null;
        byte headerByte = buffer.get();
        if (headerByte == SFSDataType.NULL.getTypeID()) {
            decodedObject = this.binDecode_NULL(buffer);
        } else if (headerByte == SFSDataType.BOOL.getTypeID()) {
            decodedObject = this.binDecode_BOOL(buffer);
        } else if (headerByte == SFSDataType.BOOL_ARRAY.getTypeID()) {
            decodedObject = this.binDecode_BOOL_ARRAY(buffer);
        } else if (headerByte == SFSDataType.BYTE.getTypeID()) {
            decodedObject = this.binDecode_BYTE(buffer);
        } else if (headerByte == SFSDataType.BYTE_ARRAY.getTypeID()) {
            decodedObject = this.binDecode_BYTE_ARRAY(buffer);
        } else if (headerByte == SFSDataType.SHORT.getTypeID()) {
            decodedObject = this.binDecode_SHORT(buffer);
        } else if (headerByte == SFSDataType.SHORT_ARRAY.getTypeID()) {
            decodedObject = this.binDecode_SHORT_ARRAY(buffer);
        } else if (headerByte == SFSDataType.INT.getTypeID()) {
            decodedObject = this.binDecode_INT(buffer);
        } else if (headerByte == SFSDataType.INT_ARRAY.getTypeID()) {
            decodedObject = this.binDecode_INT_ARRAY(buffer);
        } else if (headerByte == SFSDataType.LONG.getTypeID()) {
            decodedObject = this.binDecode_LONG(buffer);
        } else if (headerByte == SFSDataType.LONG_ARRAY.getTypeID()) {
            decodedObject = this.binDecode_LONG_ARRAY(buffer);
        } else if (headerByte == SFSDataType.FLOAT.getTypeID()) {
            decodedObject = this.binDecode_FLOAT(buffer);
        } else if (headerByte == SFSDataType.FLOAT_ARRAY.getTypeID()) {
            decodedObject = this.binDecode_FLOAT_ARRAY(buffer);
        } else if (headerByte == SFSDataType.DOUBLE.getTypeID()) {
            decodedObject = this.binDecode_DOUBLE(buffer);
        } else if (headerByte == SFSDataType.DOUBLE_ARRAY.getTypeID()) {
            decodedObject = this.binDecode_DOUBLE_ARRAY(buffer);
        } else if (headerByte == SFSDataType.UTF_STRING.getTypeID()) {
            decodedObject = this.binDecode_UTF_STRING(buffer);
        } else if (headerByte == SFSDataType.UTF_STRING_ARRAY.getTypeID()) {
            decodedObject = this.binDecode_UTF_STRING_ARRAY(buffer);
        } else if (headerByte == SFSDataType.SFS_ARRAY.getTypeID()) {
            buffer.position(buffer.position() - 1);
            decodedObject = new SFSDataWrapper(SFSDataType.SFS_ARRAY, this.decodeSFSArray(buffer));
        } else if (headerByte == SFSDataType.SFS_OBJECT.getTypeID()) {
            buffer.position(buffer.position() - 1);
            ISFSObject sfsObj = this.decodeSFSObject(buffer);
            SFSDataType type = SFSDataType.SFS_OBJECT;
            Object finalSfsObj = sfsObj;
            if (sfsObj.containsKey("$C") && sfsObj.containsKey("$F")) {
                type = SFSDataType.CLASS;
                finalSfsObj = this.sfs2pojo(sfsObj);
            }
            decodedObject = new SFSDataWrapper(type, finalSfsObj);
        } else {
            throw new BZCodecException("Unknow SFSDataType ID: " + headerByte);
        }
        return decodedObject;
    }

    private ByteBuffer encodeObject(ByteBuffer buffer, SFSDataType typeId, Object object) {
        switch (typeId) {
            case BOOL: {
                buffer = this.binEncode_NULL(buffer);
                break;
            }
            case BOOL_ARRAY: {
                buffer = this.binEncode_BOOL(buffer, (Boolean)object);
                break;
            }
            case BYTE: {
                buffer = this.binEncode_BYTE(buffer, (Byte)object);
                break;
            }
            case BYTE_ARRAY: {
                buffer = this.binEncode_SHORT(buffer, (Short)object);
                break;
            }
            case CLASS: {
                buffer = this.binEncode_INT(buffer, (Integer)object);
                break;
            }
            case DOUBLE: {
                buffer = this.binEncode_LONG(buffer, (Long)object);
                break;
            }
            case DOUBLE_ARRAY: {
                buffer = this.binEncode_FLOAT(buffer, (Float)object);
                break;
            }
            case FLOAT: {
                buffer = this.binEncode_DOUBLE(buffer, (Double)object);
                break;
            }
            case FLOAT_ARRAY: {
                buffer = this.binEncode_UTF_STRING(buffer, (String)object);
                break;
            }
            case INT: {
                buffer = this.binEncode_BOOL_ARRAY(buffer, (Collection)object);
                break;
            }
            case INT_ARRAY: {
                buffer = this.binEncode_BYTE_ARRAY(buffer, (byte[])object);
                break;
            }
            case LONG: {
                buffer = this.binEncode_SHORT_ARRAY(buffer, (Collection)object);
                break;
            }
            case LONG_ARRAY: {
                buffer = this.binEncode_INT_ARRAY(buffer, (Collection)object);
                break;
            }
            case NULL: {
                buffer = this.binEncode_LONG_ARRAY(buffer, (Collection)object);
                break;
            }
            case SFS_ARRAY: {
                buffer = this.binEncode_FLOAT_ARRAY(buffer, (Collection)object);
                break;
            }
            case SFS_OBJECT: {
                buffer = this.binEncode_DOUBLE_ARRAY(buffer, (Collection)object);
                break;
            }
            case SHORT: {
                buffer = this.binEncode_UTF_STRING_ARRAY(buffer, (Collection)object);
                break;
            }
            case SHORT_ARRAY: {
                buffer = this.addData(buffer, this.array2binary((SFSArray)object));
                break;
            }
            case UTF_STRING: {
                buffer = this.addData(buffer, this.object2binary((SFSObject)object));
                break;
            }
            case UTF_STRING_ARRAY: {
                buffer = this.addData(buffer, this.object2binary(this.pojo2sfs(object)));
                break;
            }
            default: {
                throw new IllegalArgumentException("Unrecognized type in SFSObject serialization: " + (Object)((Object)typeId));
            }
        }
        return buffer;
    }

    private SFSDataWrapper binDecode_NULL(ByteBuffer buffer) {
        return new SFSDataWrapper(SFSDataType.NULL, null);
    }

    private SFSDataWrapper binDecode_BOOL(ByteBuffer buffer) throws BZCodecException {
        byte boolByte = buffer.get();
        Boolean bool = null;
        if (boolByte == 0) {
            bool = new Boolean(false);
        } else if (boolByte == 1) {
            bool = new Boolean(true);
        } else {
            throw new BZCodecException("Error decoding Bool type. Illegal value: " + bool);
        }
        return new SFSDataWrapper(SFSDataType.BOOL, bool);
    }

    private SFSDataWrapper binDecode_BYTE(ByteBuffer buffer) {
        byte boolByte = buffer.get();
        return new SFSDataWrapper(SFSDataType.BYTE, Byte.valueOf(boolByte));
    }

    private SFSDataWrapper binDecode_SHORT(ByteBuffer buffer) {
        short shortValue = buffer.getShort();
        return new SFSDataWrapper(SFSDataType.SHORT, shortValue);
    }

    private SFSDataWrapper binDecode_INT(ByteBuffer buffer) {
        int intValue = buffer.getInt();
        return new SFSDataWrapper(SFSDataType.INT, intValue);
    }

    private SFSDataWrapper binDecode_LONG(ByteBuffer buffer) {
        long longValue = buffer.getLong();
        return new SFSDataWrapper(SFSDataType.LONG, longValue);
    }

    private SFSDataWrapper binDecode_FLOAT(ByteBuffer buffer) {
        float floatValue = buffer.getFloat();
        return new SFSDataWrapper(SFSDataType.FLOAT, Float.valueOf(floatValue));
    }

    private SFSDataWrapper binDecode_DOUBLE(ByteBuffer buffer) {
        double doubleValue = buffer.getDouble();
        return new SFSDataWrapper(SFSDataType.DOUBLE, doubleValue);
    }

    private SFSDataWrapper binDecode_UTF_STRING(ByteBuffer buffer) throws BZCodecException {
        short strLen = buffer.getShort();
        if (strLen < 0) {
            throw new BZCodecException("Error decoding UtfString. Negative size: " + strLen);
        }
        byte[] strData = new byte[strLen];
        buffer.get(strData, 0, strLen);
        String decodedString = new String(strData);
        return new SFSDataWrapper(SFSDataType.UTF_STRING, decodedString);
    }

    private SFSDataWrapper binDecode_BOOL_ARRAY(ByteBuffer buffer) throws BZCodecException {
        int arraySize = this.getTypeArraySize(buffer);
        ArrayList<Boolean> array = new ArrayList<Boolean>();
        for (int j = 0; j < arraySize; ++j) {
            byte boolData = buffer.get();
            if (boolData == 0) {
                array.add(false);
                continue;
            }
            if (boolData == 1) {
                array.add(true);
                continue;
            }
            throw new BZCodecException("Error decoding BoolArray. Invalid bool value: " + boolData);
        }
        return new SFSDataWrapper(SFSDataType.BOOL_ARRAY, array);
    }

    private SFSDataWrapper binDecode_BYTE_ARRAY(ByteBuffer buffer) throws BZCodecException {
        int arraySize = buffer.getInt();
        if (arraySize < 0) {
            throw new BZCodecException("Error decoding typed array size. Negative size: " + arraySize);
        }
        byte[] byteData = new byte[arraySize];
        buffer.get(byteData, 0, arraySize);
        return new SFSDataWrapper(SFSDataType.BYTE_ARRAY, byteData);
    }

    private SFSDataWrapper binDecode_SHORT_ARRAY(ByteBuffer buffer) throws BZCodecException {
        int arraySize = this.getTypeArraySize(buffer);
        ArrayList<Short> array = new ArrayList<Short>();
        for (int j = 0; j < arraySize; ++j) {
            short shortValue = buffer.getShort();
            array.add(shortValue);
        }
        return new SFSDataWrapper(SFSDataType.SHORT_ARRAY, array);
    }

    private SFSDataWrapper binDecode_INT_ARRAY(ByteBuffer buffer) throws BZCodecException {
        int arraySize = this.getTypeArraySize(buffer);
        ArrayList<Integer> array = new ArrayList<Integer>();
        for (int j = 0; j < arraySize; ++j) {
            int intValue = buffer.getInt();
            array.add(intValue);
        }
        return new SFSDataWrapper(SFSDataType.INT_ARRAY, array);
    }

    private SFSDataWrapper binDecode_LONG_ARRAY(ByteBuffer buffer) throws BZCodecException {
        int arraySize = this.getTypeArraySize(buffer);
        ArrayList<Long> array = new ArrayList<Long>();
        for (int j = 0; j < arraySize; ++j) {
            long longValue = buffer.getLong();
            array.add(longValue);
        }
        return new SFSDataWrapper(SFSDataType.LONG_ARRAY, array);
    }

    private SFSDataWrapper binDecode_FLOAT_ARRAY(ByteBuffer buffer) throws BZCodecException {
        int arraySize = this.getTypeArraySize(buffer);
        ArrayList<Float> array = new ArrayList<Float>();
        for (int j = 0; j < arraySize; ++j) {
            float floatValue = buffer.getFloat();
            array.add(Float.valueOf(floatValue));
        }
        return new SFSDataWrapper(SFSDataType.FLOAT_ARRAY, array);
    }

    private SFSDataWrapper binDecode_DOUBLE_ARRAY(ByteBuffer buffer) throws BZCodecException {
        int arraySize = this.getTypeArraySize(buffer);
        ArrayList<Double> array = new ArrayList<Double>();
        for (int j = 0; j < arraySize; ++j) {
            double doubleValue = buffer.getDouble();
            array.add(doubleValue);
        }
        return new SFSDataWrapper(SFSDataType.DOUBLE_ARRAY, array);
    }

    private SFSDataWrapper binDecode_UTF_STRING_ARRAY(ByteBuffer buffer) throws BZCodecException {
        int arraySize = this.getTypeArraySize(buffer);
        ArrayList<String> array = new ArrayList<String>();
        for (int j = 0; j < arraySize; ++j) {
            short strLen = buffer.getShort();
            if (strLen < 0) {
                throw new BZCodecException("Error decoding UtfStringArray element. Element has negative size: " + strLen);
            }
            byte[] strData = new byte[strLen];
            buffer.get(strData, 0, strLen);
            array.add(new String(strData));
        }
        return new SFSDataWrapper(SFSDataType.UTF_STRING_ARRAY, array);
    }

    private short getTypeArraySize(ByteBuffer buffer) throws BZCodecException {
        short arraySize = buffer.getShort();
        if (arraySize < 0) {
            throw new BZCodecException("Error decoding typed array size. Negative size: " + arraySize);
        }
        return arraySize;
    }

    private ByteBuffer binEncode_NULL(ByteBuffer buffer) {
        return this.addData(buffer, new byte[1]);
    }

    private ByteBuffer binEncode_BOOL(ByteBuffer buffer, Boolean value) {
        byte[] data = new byte[2];
        data[0] = (byte)SFSDataType.BOOL.getTypeID();
        data[1] = value != false ? (byte)1 : (byte)0;
        return this.addData(buffer, data);
    }

    private ByteBuffer binEncode_BYTE(ByteBuffer buffer, Byte value) {
        byte[] data = new byte[]{(byte)SFSDataType.BYTE.getTypeID(), value.byteValue()};
        return this.addData(buffer, data);
    }

    private ByteBuffer binEncode_SHORT(ByteBuffer buffer, Short value) {
        ByteBuffer buf = ByteBuffer.allocate(3);
        buf.put((byte)SFSDataType.SHORT.getTypeID());
        buf.putShort(value);
        return this.addData(buffer, buf.array());
    }

    private ByteBuffer binEncode_INT(ByteBuffer buffer, Integer value) {
        ByteBuffer buf = ByteBuffer.allocate(5);
        buf.put((byte)SFSDataType.INT.getTypeID());
        buf.putInt(value);
        return this.addData(buffer, buf.array());
    }

    private ByteBuffer binEncode_LONG(ByteBuffer buffer, Long value) {
        ByteBuffer buf = ByteBuffer.allocate(9);
        buf.put((byte)SFSDataType.LONG.getTypeID());
        buf.putLong(value);
        return this.addData(buffer, buf.array());
    }

    private ByteBuffer binEncode_FLOAT(ByteBuffer buffer, Float value) {
        ByteBuffer buf = ByteBuffer.allocate(5);
        buf.put((byte)SFSDataType.FLOAT.getTypeID());
        buf.putFloat(value.floatValue());
        return this.addData(buffer, buf.array());
    }

    private ByteBuffer binEncode_DOUBLE(ByteBuffer buffer, Double value) {
        ByteBuffer buf = ByteBuffer.allocate(9);
        buf.put((byte)SFSDataType.DOUBLE.getTypeID());
        buf.putDouble(value);
        return this.addData(buffer, buf.array());
    }

    private ByteBuffer binEncode_UTF_STRING(ByteBuffer buffer, String value) {
        byte[] stringBytes = value.getBytes();
        ByteBuffer buf = ByteBuffer.allocate(3 + stringBytes.length);
        buf.put((byte)SFSDataType.UTF_STRING.getTypeID());
        buf.putShort((short)stringBytes.length);
        buf.put(stringBytes);
        return this.addData(buffer, buf.array());
    }

    private ByteBuffer binEncode_BOOL_ARRAY(ByteBuffer buffer, Collection<Boolean> value) {
        ByteBuffer buf = ByteBuffer.allocate(3 + value.size());
        buf.put((byte)SFSDataType.BOOL_ARRAY.getTypeID());
        buf.putShort((short)value.size());
        Iterator<Boolean> localIterator = value.iterator();
        while (localIterator.hasNext()) {
            boolean b = localIterator.next();
            buf.put(b ? (byte)1 : (byte)0);
        }
        return this.addData(buffer, buf.array());
    }

    private ByteBuffer binEncode_BYTE_ARRAY(ByteBuffer buffer, byte[] value) {
        ByteBuffer buf = ByteBuffer.allocate(5 + value.length);
        buf.put((byte)SFSDataType.BYTE_ARRAY.getTypeID());
        buf.putInt(value.length);
        buf.put(value);
        return this.addData(buffer, buf.array());
    }

    private ByteBuffer binEncode_SHORT_ARRAY(ByteBuffer buffer, Collection<Short> value) {
        ByteBuffer buf = ByteBuffer.allocate(3 + 2 * value.size());
        buf.put((byte)SFSDataType.SHORT_ARRAY.getTypeID());
        buf.putShort((short)value.size());
        Iterator<Short> localIterator = value.iterator();
        while (localIterator.hasNext()) {
            short item = localIterator.next();
            buf.putShort(item);
        }
        return this.addData(buffer, buf.array());
    }

    private ByteBuffer binEncode_INT_ARRAY(ByteBuffer buffer, Collection<Integer> value) {
        ByteBuffer buf = ByteBuffer.allocate(3 + 4 * value.size());
        buf.put((byte)SFSDataType.INT_ARRAY.getTypeID());
        buf.putShort((short)value.size());
        Iterator<Integer> localIterator = value.iterator();
        while (localIterator.hasNext()) {
            int item = localIterator.next();
            buf.putInt(item);
        }
        return this.addData(buffer, buf.array());
    }

    private ByteBuffer binEncode_LONG_ARRAY(ByteBuffer buffer, Collection<Long> value) {
        ByteBuffer buf = ByteBuffer.allocate(3 + 8 * value.size());
        buf.put((byte)SFSDataType.LONG_ARRAY.getTypeID());
        buf.putShort((short)value.size());
        Iterator<Long> localIterator = value.iterator();
        while (localIterator.hasNext()) {
            long item = localIterator.next();
            buf.putLong(item);
        }
        return this.addData(buffer, buf.array());
    }

    private ByteBuffer binEncode_FLOAT_ARRAY(ByteBuffer buffer, Collection<Float> value) {
        ByteBuffer buf = ByteBuffer.allocate(3 + 4 * value.size());
        buf.put((byte)SFSDataType.FLOAT_ARRAY.getTypeID());
        buf.putShort((short)value.size());
        Iterator<Float> localIterator = value.iterator();
        while (localIterator.hasNext()) {
            float item = localIterator.next().floatValue();
            buf.putFloat(item);
        }
        return this.addData(buffer, buf.array());
    }

    private ByteBuffer binEncode_DOUBLE_ARRAY(ByteBuffer buffer, Collection<Double> value) {
        ByteBuffer buf = ByteBuffer.allocate(3 + 8 * value.size());
        buf.put((byte)SFSDataType.DOUBLE_ARRAY.getTypeID());
        buf.putShort((short)value.size());
        Iterator<Double> localIterator = value.iterator();
        while (localIterator.hasNext()) {
            double item = localIterator.next();
            buf.putDouble(item);
        }
        return this.addData(buffer, buf.array());
    }

    private ByteBuffer binEncode_UTF_STRING_ARRAY(ByteBuffer buffer, Collection<String> value) {
        int stringDataLen = 0;
        byte[][] binStrings = new byte[value.size()][];
        int count = 0;
        for (String item : value) {
            byte[] binStr = item.getBytes();
            binStrings[count++] = binStr;
            stringDataLen += 2 + binStr.length;
        }
        ByteBuffer buf = ByteBuffer.allocate(3 + stringDataLen);
        buf.put((byte)SFSDataType.UTF_STRING_ARRAY.getTypeID());
        buf.putShort((short)value.size());
        return this.addData(buffer, buf.array());
    }

    private ByteBuffer encodeSFSObjectKey(ByteBuffer buffer, String value) {
        ByteBuffer buf = ByteBuffer.allocate(2 + value.length());
        buf.putShort((short)value.length());
        buf.put(value.getBytes());
        return this.addData(buffer, buf.array());
    }

    private ByteBuffer addData(ByteBuffer buffer, byte[] newData) {
        if (buffer.remaining() < newData.length) {
            int newSize = BUFFER_CHUNK_SIZE;
            if (newSize < newData.length) {
                newSize = newData.length;
            }
            ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() + newSize);
            buffer.flip();
            newBuffer.put(buffer);
            buffer = newBuffer;
        }
        buffer.put(newData);
        return buffer;
    }

    @Override
    public ISFSObject pojo2sfs(Object pojo) {
        SFSObject sfsObj = SFSObject.newInstance();
        try {
            this.convertPojo(pojo, sfsObj);
        }
        catch (Exception e) {
            throw new BZRuntimeException(e);
        }
        return sfsObj;
    }

    private void convertPojo(Object pojo, ISFSObject sfsObj) throws Exception {
        Class pojoClazz = pojo.getClass();
        String classFullName = pojoClazz.getCanonicalName();
        if (classFullName == null) {
            throw new IllegalArgumentException("Anonymous classes cannot be serialized!");
        }
        if (!(pojo instanceof SerializableBZType)) {
            throw new IllegalStateException("Cannot serialize object: " + pojo + ", type: " + classFullName + " -- It doesn't implement the SerializableSFSType interface");
        }
        SFSArray fieldList = SFSArray.newInstance();
        sfsObj.putUtfString("$C", classFullName);
        sfsObj.putSFSArray("$F", fieldList);
        for (Field field : pojoClazz.getDeclaredFields()) {
            try {
                int modifiers = field.getModifiers();
                if (Modifier.isTransient(modifiers) || Modifier.isStatic(modifiers)) continue;
                String fieldName = field.getName();
                Object fieldValue = null;
                fieldValue = Modifier.isPublic(modifiers) ? field.get(pojo) : this.readValueFromGetter(fieldName, field.getType().getSimpleName(), pojo);
                SFSObject fieldDescriptor = SFSObject.newInstance();
                fieldDescriptor.putUtfString("N", fieldName);
                fieldDescriptor.put("V", this.wrapPojoField(fieldValue));
                fieldList.addSFSObject(fieldDescriptor);
                continue;
            }
            catch (NoSuchMethodException err) {
                this.logger.info("-- No public getter -- Serializer skipping private field: " + field.getName() + ", from class: " + pojoClazz);
                err.printStackTrace();
            }
        }
    }

    private Object readValueFromGetter(String fieldName, String type, Object pojo) throws Exception {
        Object value = null;
        boolean isBool = type.equalsIgnoreCase("boolean");
        String getterName = "get" + StringUtils.capitalize((String)fieldName);
        Method getterMethod = pojo.getClass().getMethod(getterName, new Class[0]);
        value = getterMethod.invoke(pojo, new Object[0]);
        return value;
    }

    private SFSDataWrapper wrapPojoField(Object value) {
        if (value == null) {
            return new SFSDataWrapper(SFSDataType.NULL, null);
        }
        SFSDataWrapper wrapper = null;
        if (value instanceof Boolean) {
            wrapper = new SFSDataWrapper(SFSDataType.BOOL, value);
        } else if (value instanceof Byte) {
            wrapper = new SFSDataWrapper(SFSDataType.BYTE, value);
        } else if (value instanceof Short) {
            wrapper = new SFSDataWrapper(SFSDataType.SHORT, value);
        } else if (value instanceof Integer) {
            wrapper = new SFSDataWrapper(SFSDataType.INT, value);
        } else if (value instanceof Long) {
            wrapper = new SFSDataWrapper(SFSDataType.LONG, value);
        } else if (value instanceof Float) {
            wrapper = new SFSDataWrapper(SFSDataType.FLOAT, value);
        } else if (value instanceof Double) {
            wrapper = new SFSDataWrapper(SFSDataType.DOUBLE, value);
        } else if (value instanceof String) {
            wrapper = new SFSDataWrapper(SFSDataType.UTF_STRING, value);
        } else if (value.getClass().isArray()) {
            wrapper = new SFSDataWrapper(SFSDataType.SFS_ARRAY, this.unrollArray((Object[])value));
        } else if (value instanceof Collection) {
            wrapper = new SFSDataWrapper(SFSDataType.SFS_ARRAY, this.unrollCollection((Collection)value));
        } else if (value instanceof Map) {
            wrapper = new SFSDataWrapper(SFSDataType.SFS_OBJECT, this.unrollMap((Map)value));
        } else if (value instanceof SerializableBZType) {
            wrapper = new SFSDataWrapper(SFSDataType.SFS_OBJECT, this.pojo2sfs(value));
        }
        return wrapper;
    }

    private ISFSArray unrollArray(Object[] arr) {
        SFSArray array = SFSArray.newInstance();
        for (Object item : arr) {
            array.add(this.wrapPojoField(item));
        }
        return array;
    }

    private ISFSArray unrollCollection(Collection collection) {
        SFSArray array = SFSArray.newInstance();
        for (Object item : collection) {
            array.add(this.wrapPojoField(item));
        }
        return array;
    }

    private ISFSObject unrollMap(Map map) {
        SFSObject sfsObj = SFSObject.newInstance();
        Set<Map.Entry> entries = map.entrySet();
        for (Map.Entry item : entries) {
            Object key = item.getKey();
            if (!(key instanceof String)) continue;
            sfsObj.put((String)key, this.wrapPojoField(item.getValue()));
        }
        return sfsObj;
    }

    @Override
    public Object sfs2pojo(ISFSObject sfsObj) {
        Object pojo = null;
        if (!sfsObj.containsKey("$C") && !sfsObj.containsKey("$F")) {
            throw new BZRuntimeException("The SFSObject passed does not represent any serialized class.");
        }
        try {
            String className = sfsObj.getUtfString("$C");
            Class theClass = Class.forName(className);
            pojo = theClass.newInstance();
            if (!(pojo instanceof SerializableBZType)) {
                throw new IllegalStateException("Cannot deserialize object: " + pojo + ", type: " + className + " -- It doesn't implement the SerializableSFSType interface");
            }
            this.convertSFSObject(sfsObj.getSFSArray("$F"), pojo);
        }
        catch (Exception e) {
            throw new BZRuntimeException(e);
        }
        return pojo;
    }

    private void convertSFSObject(ISFSArray fieldList, Object pojo) throws Exception {
        for (int j = 0; j < fieldList.size(); ++j) {
            ISFSObject fieldDescriptor = fieldList.getSFSObject(j);
            String fieldName = fieldDescriptor.getUtfString("N");
            Object fieldValue = this.unwrapPojoField(fieldDescriptor.get("V"));
            this.setObjectField(pojo, fieldName, fieldValue);
        }
    }

    private void setObjectField(Object pojo, String fieldName, Object fieldValue) throws Exception {
        Class pojoClass = pojo.getClass();
        Field field = pojoClass.getDeclaredField(fieldName);
        int fieldModifier = field.getModifiers();
        if (Modifier.isTransient(fieldModifier)) {
            return;
        }
        boolean isArray = field.getType().isArray();
        if (isArray) {
            if (!(fieldValue instanceof Collection)) {
                throw new BZRuntimeException("Problem during SFSObject => POJO conversion. Found array field in POJO: " + fieldName + ", but data is not a Collection!");
            }
            Collection collection = (Collection)fieldValue;
            fieldValue = collection.toArray();
            int arraySize = collection.size();
            Object typedArray = Array.newInstance(field.getType().getComponentType(), arraySize);
            System.arraycopy(fieldValue, 0, typedArray, 0, arraySize);
            fieldValue = typedArray;
        } else if (fieldValue instanceof Collection) {
            Collection collection = (Collection)fieldValue;
            String fieldClass = field.getType().getSimpleName();
            if (fieldClass.equals("ArrayList") || fieldClass.equals("List")) {
                fieldValue = new ArrayList(collection);
            }
            if (fieldClass.equals("CopyOnWriteArrayList")) {
                fieldValue = new CopyOnWriteArrayList(collection);
            } else if (fieldClass.equals("LinkedList")) {
                fieldValue = new LinkedList(collection);
            } else if (fieldClass.equals("Vector")) {
                fieldValue = new Vector(collection);
            } else if (fieldClass.equals("Set") || fieldClass.equals("HashSet")) {
                fieldValue = new HashSet(collection);
            } else if (fieldClass.equals("LinkedHashSet")) {
                fieldValue = new LinkedHashSet(collection);
            } else if (fieldClass.equals("TreeSet")) {
                fieldValue = new TreeSet(collection);
            } else if (fieldClass.equals("CopyOnWriteArraySet")) {
                fieldValue = new CopyOnWriteArraySet(collection);
            } else if (fieldClass.equals("Queue") || fieldClass.equals("PriorityQueue")) {
                fieldValue = new PriorityQueue(collection);
            } else if (fieldClass.equals("BlockingQueue") || fieldClass.equals("LinkedBlockingQueue")) {
                fieldValue = new LinkedBlockingQueue(collection);
            } else if (fieldClass.equals("PriorityBlockingQueue")) {
                fieldValue = new PriorityBlockingQueue(collection);
            } else if (fieldClass.equals("ConcurrentLinkedQueue")) {
                fieldValue = new ConcurrentLinkedQueue(collection);
            } else if (fieldClass.equals("DelayQueue")) {
                fieldValue = new DelayQueue(collection);
            } else if (fieldClass.equals("Deque") || fieldClass.equals("ArrayDeque")) {
                fieldValue = new ArrayDeque(collection);
            } else if (fieldClass.equals("LinkedBlockingDeque")) {
                fieldValue = new LinkedBlockingDeque(collection);
            }
        }
        if (Modifier.isPublic(fieldModifier)) {
            field.set(pojo, fieldValue);
        } else {
            this.writeValueFromSetter(field, pojo, fieldValue);
        }
    }

    private void writeValueFromSetter(Field field, Object pojo, Object fieldValue) throws Exception {
        String setterName = "set" + StringUtils.capitalize((String)field.getName());
        try {
            Method setterMethod = pojo.getClass().getMethod(setterName, field.getType());
            setterMethod.invoke(pojo, fieldValue);
        }
        catch (NoSuchMethodException e) {
            this.logger.info("-- No public setter -- Serializer skipping private field: " + field.getName() + ", from class: " + pojo.getClass().getName());
        }
    }

    private Object unwrapPojoField(SFSDataWrapper wrapper) {
        Object obj = null;
        SFSDataType type = wrapper.getTypeId();
        if (type.getTypeID() <= SFSDataType.UTF_STRING.getTypeID()) {
            obj = wrapper.getObject();
        } else if (type == SFSDataType.SFS_ARRAY) {
            obj = this.rebuildArray((ISFSArray)wrapper.getObject());
        } else if (type == SFSDataType.SFS_OBJECT) {
            obj = this.rebuildMap((ISFSObject)wrapper.getObject());
        } else if (type == SFSDataType.CLASS) {
            obj = wrapper.getObject();
        }
        return obj;
    }

    private Object rebuildArray(ISFSArray sfsArray) {
        ArrayList<Object> collection = new ArrayList<Object>();
        Iterator<SFSDataWrapper> iter = sfsArray.iterator();
        while (iter.hasNext()) {
            Object item = this.unwrapPojoField(iter.next());
            collection.add(item);
        }
        return collection;
    }

    private Object rebuildMap(ISFSObject sfsObj) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        for (String key : sfsObj.getKeys()) {
            SFSDataWrapper wrapper = sfsObj.get(key);
            map.put(key, this.unwrapPojoField(wrapper));
        }
        return map;
    }

}

