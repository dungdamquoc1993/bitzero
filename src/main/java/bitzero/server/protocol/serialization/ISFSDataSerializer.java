/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.protocol.serialization;

import bitzero.server.entities.data.ISFSArray;
import bitzero.server.entities.data.ISFSObject;
import bitzero.server.entities.data.SFSArray;
import bitzero.server.entities.data.SFSObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ISFSDataSerializer {
    public byte[] object2binary(ISFSObject var1);

    public byte[] array2binary(ISFSArray var1);

    public ISFSObject binary2object(byte[] var1);

    public ISFSArray binary2array(byte[] var1);

    public String object2json(Map<String, Object> var1);

    public String array2json(List<Object> var1);

    public ISFSObject json2object(String var1);

    public ISFSArray json2array(String var1);

    public ISFSObject pojo2sfs(Object var1);

    public Object sfs2pojo(ISFSObject var1);

    public SFSObject resultSet2object(ResultSet var1) throws SQLException;

    public SFSArray resultSet2array(ResultSet var1) throws SQLException;
}

