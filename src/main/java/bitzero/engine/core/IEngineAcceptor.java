/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.core;

import bitzero.engine.config.SocketConfig;
import bitzero.engine.core.security.IConnectionFilter;
import java.io.IOException;
import java.util.List;

public interface IEngineAcceptor {
    public void bindSocket(SocketConfig var1) throws IOException;

    public List getBoundSockets();

    public void handleAcceptableConnections();

    public IConnectionFilter getConnectionFilter();

    public void setConnectionFilter(IConnectionFilter var1);
}

