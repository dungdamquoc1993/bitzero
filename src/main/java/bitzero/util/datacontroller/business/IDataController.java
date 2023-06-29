/*
 * Decompiled with CFR 0_116.
 */
package bitzero.util.datacontroller.business;

import bitzero.util.datacontroller.business.DataControllerException;
import java.util.List;
import java.util.Map;

public interface IDataController {
    public Object get(String var1) throws DataControllerException;

    public Map<String, Object> multiget(List<String> var1) throws DataControllerException;

    public void set(String var1, Object var2) throws DataControllerException;

    public void add(String var1, Object var2) throws DataControllerException;

    public void delete(String var1) throws DataControllerException;

    public Object getCache(String var1) throws DataControllerException;

    public void setCache(String var1, int var2, Object var3) throws DataControllerException;

    public void deleteCache(String var1) throws DataControllerException;
}

