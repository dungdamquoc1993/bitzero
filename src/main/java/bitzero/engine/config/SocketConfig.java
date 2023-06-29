/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.config;

import bitzero.engine.data.TransportType;

public class SocketConfig {
    protected String address;
    protected int port;
    protected TransportType type;

    public SocketConfig(String address, int port, TransportType type) {
        this.address = address;
        this.port = port;
        this.type = type;
    }

    public String getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }

    public TransportType getType() {
        return this.type;
    }

    public String toString() {
        return "{ " + this.address + ":" + this.port + ", " + this.type.toString() + " }";
    }
}

