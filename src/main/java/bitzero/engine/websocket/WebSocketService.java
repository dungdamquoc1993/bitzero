/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.websocket;

import bitzero.engine.config.EngineConfiguration;
import bitzero.engine.core.BitZeroEngine;
import bitzero.engine.io.IProtocolCodec;
import bitzero.engine.service.BaseCoreService;
import bitzero.engine.websocket.WebSocketConfig;
import bitzero.engine.websocket.WebSocketProtocolCodec;
import bitzero.engine.websocket.WebSocketStats;
import bitzero.engine.websocket.netty.WebSocketBoot;

public class WebSocketService
extends BaseCoreService {
    private volatile boolean inited = false;
    private final WebSocketStats webSocketStats = new WebSocketStats();
    private final WebSocketProtocolCodec protocolCodec = new WebSocketProtocolCodec(this.webSocketStats);
    private final boolean isActive = true;

    @Override
    public void init(Object o) {
        if (this.inited) {
            throw new IllegalArgumentException("Service is already initialized. Destroy it first!");
        }
        this.inited = true;
        o = BitZeroEngine.getInstance().getConfiguration().getWebSocketEngineConfig();
        new WebSocketBoot((WebSocketConfig)o, this.protocolCodec);
    }

    @Override
    public void destroy(Object o) {
        super.destroy(o);
    }

    public boolean isActive() {
        return this.isActive;
    }

    public WebSocketStats getWebSocketStats() {
        return this.webSocketStats;
    }

    public WebSocketProtocolCodec getProtocolCodec() {
        return this.protocolCodec;
    }
}

