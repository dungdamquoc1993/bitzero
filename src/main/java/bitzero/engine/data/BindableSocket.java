/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.data;

import bitzero.engine.config.SocketConfig;
import bitzero.engine.data.TransportType;
import java.nio.channels.SelectableChannel;

public class BindableSocket
extends SocketConfig {
    protected SelectableChannel channel;

    public BindableSocket(SelectableChannel channel, String address, int port, TransportType type) {
        super(address, port, type);
        this.channel = channel;
    }

    public SelectableChannel getChannel() {
        return this.channel;
    }
}

