/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.sessions;

import bitzero.engine.sessions.IPacketQueue;
import bitzero.engine.sessions.ISessionManager;
import bitzero.engine.sessions.SessionType;
import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;

public interface ISession {
    public int getId();

    public void setId(int var1);

    public String getHashId();

    public void setHashId(String var1);

    public SessionType getType();

    public void setType(SessionType var1);

    public String getNodeId();

    public void setNodeId(String var1);

    public boolean isLocal();

    public boolean isLoggedIn();

    public void setLoggedIn(boolean var1);

    public IPacketQueue getPacketQueue();

    public void setPacketQueue(IPacketQueue var1);

    public SocketChannel getConnection();

    public void setConnection(SocketChannel var1);

    public DatagramChannel getDatagramChannel();

    public void setDatagrmChannel(DatagramChannel var1);

    public long getCreationTime();

    public void setCreationTime(long var1);

    public boolean isConnected();

    public void setConnected(boolean var1);

    public long getLastActivityTime();

    public void setLastActivityTime(long var1);

    public long getLastLoggedInActivityTime();

    public void setLastLoggedInActivityTime(long var1);

    public long getLastReadTime();

    public void setLastReadTime(long var1);

    public long getLastWriteTime();

    public void setLastWriteTime(long var1);

    public long getReadBytes();

    public void addReadBytes(long var1);

    public long getWrittenBytes();

    public void addWrittenBytes(long var1);

    public int getDroppedMessages();

    public void addDroppedMessages(int var1);

    public int getMaxIdleTime();

    public void setMaxIdleTime(int var1);

    public int getMaxLoggedInIdleTime();

    public void setMaxLoggedInIdleTime(int var1);

    public boolean isMarkedForEviction();

    public void setMarkedForEviction();

    public boolean isIdle();

    public boolean isFrozen();

    public void freeze();

    public void unfreeze();

    public long getFreezeTime();

    public boolean isReconnectionTimeExpired();

    public Object getSystemProperty(String var1);

    public void setSystemProperty(String var1, Object var2);

    public void removeSystemProperty(String var1);

    public Object getProperty(String var1);

    public void setProperty(String var1, Object var2);

    public void removeProperty(String var1);

    public String getFullIpAddress();

    public void setAddress(String var1);

    public String getAddress();

    public int getClientPort();

    public String getServerAddress();

    public int getServerPort();

    public String getFullServerIpAddress();

    public ISessionManager getSessionManager();

    public void setSessionManager(ISessionManager var1);

    public void close() throws IOException;

    public int getReconnectionSeconds();

    public void setReconnectionSeconds(int var1);

    public boolean isMobile();

    public boolean isWebsocket();

    public void setMobile(boolean var1);
}

