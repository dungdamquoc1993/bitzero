/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.io;

import bitzero.engine.data.TransportType;
import bitzero.engine.io.IEngineMessage;
import bitzero.engine.sessions.ISession;
import java.util.Collection;

public interface IResponse
extends IEngineMessage {
    public TransportType getTransportType();

    public void setTransportType(TransportType var1);

    public Object getTargetController();

    public void setTargetController(Object var1);

    public Collection getRecipients();

    public void setRecipients(Collection var1);

    public void setRecipients(ISession var1);

    public boolean isTCP();

    public boolean isUDP();

    public void write();

    public void write(int var1);
}

