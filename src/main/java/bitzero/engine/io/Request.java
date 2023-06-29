/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.io;

import bitzero.engine.data.MessagePriority;
import bitzero.engine.data.TransportType;
import bitzero.engine.io.AbstractEngineMessage;
import bitzero.engine.io.IRequest;
import bitzero.engine.sessions.ISession;
import bitzero.engine.sessions.SessionType;
import bitzero.server.BitZeroServer;

public final class Request
extends AbstractEngineMessage
implements IRequest {
    private ISession sender;
    private TransportType type = TransportType.TCP;
    private MessagePriority priority = MessagePriority.NORMAL;
    private long timeStamp = System.nanoTime();

    @Override
    public ISession getSender() {
        return this.sender;
    }

    @Override
    public TransportType getTransportType() {
        return this.type;
    }

    @Override
    public void setSender(ISession session) {
        this.sender = session;
    }

    @Override
    public void setTransportType(TransportType type) {
        this.type = type;
    }

    @Override
    public MessagePriority getPriority() {
        return this.priority;
    }

    @Override
    public void setPriority(MessagePriority priority) {
        this.priority = priority;
    }

    @Override
    public long getTimeStamp() {
        return this.timeStamp;
    }

    @Override
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean isTcp() {
        return this.type == TransportType.TCP;
    }

    @Override
    public boolean isUdp() {
        return this.type == TransportType.UDP;
    }

    @Override
    public boolean isWebsocket() {
        return this.sender.getType() == SessionType.WEBSOCKET;
    }

    public String toString() {
        if (!BitZeroServer.isDebug()) {
            return "";
        }
        return String.format("[Req Type: %s, Prt: %s, Sender: %s]", new Object[]{this.type, this.priority, this.sender});
    }
}

