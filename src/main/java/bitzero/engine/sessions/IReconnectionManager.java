/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.sessions;

import bitzero.engine.exceptions.SessionReconnectionException;
import bitzero.engine.sessions.ISession;
import bitzero.engine.sessions.ISessionManager;

public interface IReconnectionManager {
    public ISession getReconnectableSession(String var1);

    public ISession reconnectSession(ISession var1, String var2) throws SessionReconnectionException;

    public void onSessionLost(ISession var1);

    public ISessionManager getSessionManager();
}

