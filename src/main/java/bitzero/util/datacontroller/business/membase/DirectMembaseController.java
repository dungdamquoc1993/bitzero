/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  net.spy.memcached.AddrUtil
 *  net.spy.memcached.ConnectionFactory
 *  net.spy.memcached.ConnectionFactoryBuilder
 *  net.spy.memcached.MemcachedClient
 *  net.spy.memcached.internal.OperationFuture
 */
package bitzero.util.datacontroller.business.membase;

import bitzero.server.config.ConfigHandle;
import bitzero.util.common.business.CommonHandle;
import bitzero.util.datacontroller.business.DataControllerException;
import bitzero.util.datacontroller.business.IDataController;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;

public class DirectMembaseController
implements IDataController {
    private MemcachedClient dataCli;

    public DirectMembaseController(String game) {
        try {
            ConnectionFactoryBuilder cfb = new ConnectionFactoryBuilder();
            cfb.setOpQueueMaxBlockTime(ConfigHandle.instance().getLong("opsBlockTime").longValue());
            cfb.setOpTimeout(ConfigHandle.instance().getLong("opsTimeOut").longValue());
            this.dataCli = new MemcachedClient(cfb.build(), AddrUtil.getAddresses((String)ConfigHandle.instance().get(game)));
        }
        catch (IOException e) {
            CommonHandle.writeErrLog(e);
        }
    }

    @Override
    public Object get(String name) throws DataControllerException {
        return this.dataCli.get(name);
    }

    @Override
    public void set(String name, Object data) throws DataControllerException {
        this.dataCli.set(name, 0, data);
    }

    @Override
    public void add(String name, Object data) throws DataControllerException {
    }

    @Override
    public Object getCache(String name) throws DataControllerException {
        return null;
    }

    @Override
    public void setCache(String name, int expire, Object data) throws DataControllerException {
    }

    @Override
    public Map<String, Object> multiget(List<String> keys) {
        return Collections.emptyMap();
    }

    @Override
    public void delete(String name) {
    }

    @Override
    public void deleteCache(String name) {
    }

    public Object getOldData(String name) {
        return this.dataCli.get(name);
    }

    public Object getNewData(String name) {
        return this.dataCli.get(name);
    }
}

