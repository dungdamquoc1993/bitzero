/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.io;

import bitzero.engine.data.MessagePriority;
import bitzero.engine.data.TransportType;
import bitzero.engine.io.IEngineMessage;
import bitzero.engine.sessions.ISession;

public interface IRequest
extends IEngineMessage {
    public TransportType getTransportType();

    public void setTransportType(TransportType var1);

    public ISession getSender();

    public void setSender(ISession var1);

    public MessagePriority getPriority();

    public void setPriority(MessagePriority var1);

    public long getTimeStamp();

    public void setTimeStamp(long var1);

    public boolean isTcp();

    public boolean isUdp();

    public boolean isWebsocket();
}

