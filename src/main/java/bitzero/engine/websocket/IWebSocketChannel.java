/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.jboss.netty.buffer.ChannelBuffer
 */
package bitzero.engine.websocket;

import java.net.SocketAddress;
import org.jboss.netty.buffer.ChannelBuffer;

public interface IWebSocketChannel {
    public void write(ChannelBuffer var1);

    public SocketAddress getRemoteAddress();

    public SocketAddress getLocalAddress();

    public void close();
}

