/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  com.vinplay.dal.service.CacheService
 *  com.vinplay.dal.service.impl.CacheServiceImpl
 *  com.vinplay.vbee.common.exceptions.KeyNotFoundException
 */
package bitzero.util.datacontroller.business.membase;

import bitzero.util.datacontroller.business.DataControllerException;
import bitzero.util.datacontroller.business.IDataController;
import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HazelCastController
implements IDataController {
    private CacheService cacheService = new CacheServiceImpl();

    @Override
    public Object get(String name) throws DataControllerException {
        try {
            return this.cacheService.getObject(name);
        }
        catch (KeyNotFoundException e) {
            return null;
        }
        catch (Exception e) {
            throw new DataControllerException(e);
        }
    }

    @Override
    public Map<String, Object> multiget(List<String> keys) throws DataControllerException {
        try {
            HashSet<String> keysUnique = new HashSet<String>();
            keysUnique.addAll(keys);
            return this.cacheService.getBulk(keysUnique);
        }
        catch (Exception e) {
            throw new DataControllerException(e);
        }
    }

    @Override
    public void set(String name, Object data) throws DataControllerException {
        try {
            this.cacheService.setObject(name, data);
        }
        catch (Exception e) {
            throw new DataControllerException(e);
        }
    }

    @Override
    public void add(String name, Object data) throws DataControllerException {
        try {
            this.cacheService.setObject(name, data);
        }
        catch (Exception e) {
            throw new DataControllerException(e);
        }
    }

    @Override
    public void delete(String name) throws DataControllerException {
        try {
            this.cacheService.removeObject(name);
        }
        catch (Exception e) {
            throw new DataControllerException(e);
        }
    }

    @Override
    public Object getCache(String name) throws DataControllerException {
        try {
            return this.cacheService.getObject(name);
        }
        catch (KeyNotFoundException e) {
            return null;
        }
        catch (Exception e) {
            throw new DataControllerException(e);
        }
    }

    @Override
    public void setCache(String name, int expire, Object data) throws DataControllerException {
        try {
            this.cacheService.setObject(name, expire, data);
        }
        catch (Exception e) {
            throw new DataControllerException(e);
        }
    }

    @Override
    public void deleteCache(String name) throws DataControllerException {
        try {
            this.cacheService.removeObject(name);
        }
        catch (Exception e) {
            throw new DataControllerException(e);
        }
    }
}

