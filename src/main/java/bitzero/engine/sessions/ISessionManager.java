/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.sessions;

import bitzero.engine.exceptions.SessionReconnectionException;
import bitzero.engine.service.IService;
import bitzero.engine.sessions.ISession;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;

public interface ISessionManager
extends IService {
    public void addSession(ISession var1);

    public int[] getReconnectStatus();

    public void removeSession(ISession var1);

    public ISession removeSession(int var1);

    public void removeChannel(SocketChannel var1);

    public ISession removeSession(String var1);

    public ISession removeSession(SocketChannel var1);

    public boolean containsSession(ISession var1);

    public void shutDownLocalSessions();

    public List getAllSessions();

    public ISession getSessionById(int var1);

    public ISession getSessionByHash(String var1);

    public int getNodeSessionCount(String var1);

    public List getAllSessionsAtNode(String var1);

    public List getAllLocalSessions();

    public ISession getLocalSessionById(int var1);

    public ISession getLocalSessionByHash(String var1);

    public ISession getLocalSessionByConnection(SocketChannel var1);

    public int getLocalSessionCount();

    public ISession createSession(SocketChannel var1);

    public ISession createConnectionlessSession();

    public ISession createBlueBoxSession();

    public void publishLocalNode(String var1);

    public void clearClusterData();

    public void onNodeLost(String var1);

    public int getHighestCCS();

    public void addSessionToken(ISession var1);

    public ISession getSessionbyToken(String var1);

    public void onSocketDisconnected(SocketChannel var1) throws IOException;

    public void onSocketDisconnected(ISession var1) throws IOException;

    public ISession reconnectSession(ISession var1, String var2) throws SessionReconnectionException, IOException;

    public ISession createWebSocketSession(Object var1);
}

