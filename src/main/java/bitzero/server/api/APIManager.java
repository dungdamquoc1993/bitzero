/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.api;

import bitzero.engine.service.IService;
import bitzero.server.BitZeroServer;
import bitzero.server.api.BZApi;
import bitzero.server.api.IBZApi;
import bitzero.server.api.response.ResponseApi;

public class APIManager
implements IService {
    private final String serviceName = "APIManager";
    private BitZeroServer bz;
    private ResponseApi resApi;
    private IBZApi bzApi;

    @Override
    public void init(Object o) {
        this.bz = BitZeroServer.getInstance();
        this.resApi = new ResponseApi();
        this.bzApi = new BZApi(this.bz);
    }

    public IBZApi getBzApi() {
        return this.bzApi;
    }

    public ResponseApi getResApi() {
        return this.resApi;
    }

    @Override
    public void destroy(Object obj) {
    }

    @Override
    public String getName() {
        return "APIManager";
    }

    @Override
    public void handleMessage(Object msg) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void setName(String arg0) {
        throw new UnsupportedOperationException("Not supported");
    }
}

