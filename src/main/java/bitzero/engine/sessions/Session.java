/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.sessions;

import bitzero.engine.config.EngineConfiguration;
import bitzero.engine.core.BitZeroEngine;
import bitzero.engine.sessions.IPacketQueue;
import bitzero.engine.sessions.ISession;
import bitzero.engine.sessions.ISessionManager;
import bitzero.engine.sessions.SessionType;
import bitzero.engine.websocket.IWebSocketChannel;
import bitzero.server.BitZeroServer;
import bitzero.util.common.business.CommonHandle;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class Session
implements ISession {
    public static final String ENCRYPTION_ENABLED = "session_encryption_enabled";
    public static final String DATA_BUFFER = "session_data_buffer";
    public static final String PROTOCOL = "session_protocol";
    public static final String NO_IP = "NO_IP";
    public static final String BBCLIENT = "bbClient";
    public static final String PACKET_READ_STATE = "read_state";
    public static final int MOBILE_RECONNECT_TIME = 30;
    private static AtomicInteger idCounter = new AtomicInteger(0);
    private volatile long readBytes = 0;
    private volatile long writtenBytes = 0;
    private volatile int droppedMessages = 0;
    private SocketChannel connection;
    private DatagramChannel datagramChannel;
    private volatile long creationTime;
    private volatile long lastReadTime;
    private volatile long lastWriteTime;
    private volatile long lastActivityTime;
    private volatile long lastLoggedInActivityTime;
    private int id;
    private String hashId;
    private String nodeId;
    private SessionType type;
    private volatile String clientIpAddress;
    private volatile int clientPort;
    private int serverPort;
    private String serverAddress;
    private int maxIdleTime;
    private int maxLoggedInIdleTime;
    private volatile int reconnectionSeconds;
    private volatile long freezeTime = 0;
    private volatile boolean frozen = false;
    private boolean markedForEviction = false;
    private volatile boolean connected = false;
    private volatile boolean loggedIn = false;
    private IPacketQueue packetQueue;
    private ISessionManager sessionManager;
    private Map systemProperties;
    private Map properties;
    private volatile boolean isMobile = false;

    public Session() {
        this.lastWriteTime = this.lastActivityTime = System.currentTimeMillis();
        this.lastReadTime = this.lastActivityTime;
        this.creationTime = this.lastActivityTime;
        this.setId(Session.getUniqueId());
        this.setHashId("---");
        this.properties = new ConcurrentHashMap();
        this.systemProperties = new ConcurrentHashMap();
    }

    public Session(SocketAddress address) {
        this.lastReadTime = this.lastWriteTime = System.currentTimeMillis();
        this.creationTime = this.lastWriteTime;
        this.setId(-1);
        this.properties = new ConcurrentHashMap();
        this.systemProperties = new ConcurrentHashMap();
        this.systemProperties.put("sender", address);
    }

    private static int getUniqueId() {
        return idCounter.incrementAndGet();
    }

    @Override
    public boolean isMobile() {
        return this.isMobile;
    }

    @Override
    public boolean isWebsocket() {
        return this.type == SessionType.WEBSOCKET;
    }

    @Override
    public void setMobile(boolean isMobile) {
        this.isMobile = isMobile;
        if (isMobile) {
            this.reconnectionSeconds = 30;
        }
    }

    @Override
    public void addReadBytes(long amount) {
        this.readBytes += amount;
    }

    @Override
    public void addWrittenBytes(long amount) {
        this.writtenBytes += amount;
    }

    @Override
    public SocketChannel getConnection() {
        return this.connection;
    }

    @Override
    public DatagramChannel getDatagramChannel() {
        return this.datagramChannel;
    }

    @Override
    public void setDatagrmChannel(DatagramChannel channel) {
        this.datagramChannel = channel;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public String getHashId() {
        return this.hashId;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getFullIpAddress() {
        return this.clientPort <= 0 ? this.getAddress() : String.valueOf(this.getAddress()) + ":" + this.clientPort;
    }

    @Override
    public void setAddress(String var1){
        this.clientIpAddress = var1;
    }

    @Override
    public String getAddress() {
        if(this.clientIpAddress != null){
//            CommonHandle.writeErrLog("Connect ip: " +  this.clientIpAddress);
            return this.clientIpAddress;
        }

        if (this.type == SessionType.WEBSOCKET) {
            IWebSocketChannel channel = (IWebSocketChannel)this.getSystemProperty("wsChannel");
            String adr = channel.getRemoteAddress().toString();
            return adr.substring(1, adr.indexOf(58));
        }

        return this.clientIpAddress;
    }

    @Override
    public int getClientPort() {
        if (this.type == SessionType.WEBSOCKET && this.clientPort == 0) {
            IWebSocketChannel channel = (IWebSocketChannel)this.getSystemProperty("wsChannel");
            this.clientPort = this.extractPortFromRemoteHostAddress(channel.getRemoteAddress().toString());
        }
        return this.clientPort;
    }

    @Override
    public int getServerPort() {
        if (this.type == SessionType.WEBSOCKET && this.serverPort == 0) {
            IWebSocketChannel channel = (IWebSocketChannel)this.getSystemProperty("wsChannel");
            this.serverPort = this.extractPortFromRemoteHostAddress(channel.getLocalAddress().toString());
        }
        return this.serverPort;
    }

    @Override
    public String getFullServerIpAddress() {
        return String.valueOf(this.serverAddress) + ":" + this.serverPort;
    }

    @Override
    public String getServerAddress() {
        return this.serverAddress;
    }

    @Override
    public long getLastActivityTime() {
        return this.lastActivityTime;
    }

    @Override
    public long getLastReadTime() {
        return this.lastReadTime;
    }

    @Override
    public long getLastWriteTime() {
        return this.lastWriteTime;
    }

    @Override
    public int getMaxIdleTime() {
        return this.maxIdleTime;
    }

    @Override
    public IPacketQueue getPacketQueue() {
        return this.packetQueue;
    }

    @Override
    public String getNodeId() {
        return this.nodeId;
    }

    @Override
    public Object getProperty(String key) {
        return this.properties.get(key);
    }

    @Override
    public void removeProperty(String key) {
        this.properties.remove(key);
    }

    @Override
    public long getReadBytes() {
        if (this.type == SessionType.BLUEBOX) {
            return this.readBytes;
        }
        return this.readBytes;
    }

    @Override
    public Object getSystemProperty(String key) {
        return this.systemProperties.get(key);
    }

    @Override
    public void removeSystemProperty(String key) {
        this.systemProperties.remove(key);
    }

    @Override
    public SessionType getType() {
        return this.type;
    }

    @Override
    public long getWrittenBytes() {
        if (this.type == SessionType.BLUEBOX) {
            return this.writtenBytes;
        }
        return this.writtenBytes;
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    @Override
    public void setConnected(boolean value) {
        this.connected = value;
    }

    @Override
    public boolean isLoggedIn() {
        return this.loggedIn;
    }

    @Override
    public void setLoggedIn(boolean value) {
        this.loggedIn = value;
    }

    @Override
    public int getMaxLoggedInIdleTime() {
        return this.maxLoggedInIdleTime;
    }

    @Override
    public void setMaxLoggedInIdleTime(int idleTime) {
        if (idleTime < this.maxIdleTime) {
            idleTime = this.maxIdleTime + 60;
        }
        this.maxLoggedInIdleTime = idleTime;
    }

    @Override
    public long getLastLoggedInActivityTime() {
        return this.lastLoggedInActivityTime;
    }

    @Override
    public void setLastLoggedInActivityTime(long timestamp) {
        this.lastLoggedInActivityTime = timestamp;
    }

    @Override
    public boolean isLocal() {
        boolean isLocal = true;
        if (BitZeroEngine.getInstance().getConfiguration().isClustered()) {
            return isLocal;
        }
        return isLocal;
    }

    @Override
    public boolean isIdle() {
        if (this.loggedIn) {
            return this.isLoggedInIdle();
        }
        return this.isSocketIdle();
    }

    private boolean isSocketIdle() {
        boolean isIdle = false;
        if (this.maxIdleTime > 0) {
            long elapsedSinceLastActivity = System.currentTimeMillis() - this.lastActivityTime;
            isIdle = elapsedSinceLastActivity / 1000 > (long)this.maxIdleTime;
        }
        return isIdle;
    }

    private boolean isLoggedInIdle() {
        boolean isIdle = false;
        if (this.maxLoggedInIdleTime > 0) {
            long elapsedSinceLastActivity = System.currentTimeMillis() - this.lastLoggedInActivityTime;
            isIdle = elapsedSinceLastActivity / 1000 > (long)this.maxLoggedInIdleTime;
        }
        return isIdle;
    }

    @Override
    public boolean isMarkedForEviction() {
        return this.markedForEviction;
    }

    @Override
    public void setConnection(SocketChannel connection) {
        if (connection == null) {
            this.reconnectionDestroy();
            return;
        }
        if (this.reconnectionSeconds > 0) {
            this.setSocketConnection(connection);
        } else {
            if (this.connection != null) {
                throw new IllegalArgumentException("You cannot overwrite the connection linked to a Session!");
            }
            this.setSocketConnection(connection);
        }
    }

    private void setSocketConnection(SocketChannel connection) {
        this.connection = connection;
        this.serverPort = connection.socket().getLocalPort();
        this.serverAddress = connection.socket().getLocalAddress().toString().substring(1);
        if (connection != null && connection.socket() != null && !connection.socket().isClosed()) {
            String hostAddr = connection.socket().getRemoteSocketAddress().toString().substring(1);
            String[] adr = hostAddr.split("\\:");
//            this.clientIpAddress = adr[0];
            try {
                this.clientPort = Integer.parseInt(adr[1]);
            }
            catch (NumberFormatException var4_4) {
                // empty catch block
            }
            this.connected = true;
        } else {
//            this.clientIpAddress = "[unknown]";
        }
    }

    private void reconnectionDestroy() {
        this.packetQueue = null;
        this.connection = null;
        this.sessionManager.removeSession(this);
    }

    @Override
    public void setPacketQueue(IPacketQueue queue) {
        if (this.packetQueue != null) {
            throw new IllegalStateException("Cannot reassing the packet queue. Queue already exists!");
        }
        this.packetQueue = queue;
    }

    @Override
    public void setCreationTime(long timestamp) {
        this.creationTime = timestamp;
    }

    @Override
    public void setHashId(String hash) {
        this.hashId = hash;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void setLastActivityTime(long timestamp) {
        this.lastActivityTime = timestamp;
    }

    @Override
    public void setLastReadTime(long timestamp) {
        this.lastReadTime = this.lastActivityTime = timestamp;
    }

    @Override
    public void setLastWriteTime(long timestamp) {
        this.lastWriteTime = this.lastActivityTime = timestamp;
    }

    @Override
    public void setMarkedForEviction() {
        this.markedForEviction = true;
        this.reconnectionSeconds = 0;
    }

    @Override
    public void setMaxIdleTime(int idleTime) {
        this.maxIdleTime = idleTime;
    }

    @Override
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public void setProperty(String key, Object property) {
        this.properties.put(key, property);
    }

    @Override
    public void setSystemProperty(String key, Object property) {
        this.systemProperties.put(key, property);
    }

    @Override
    public void setType(SessionType type) {
        this.type = type;
        if (type == SessionType.VOID) {
            this.clientIpAddress = "NO_IP";
            this.clientPort = 0;
        }
    }

    @Override
    public int getDroppedMessages() {
        return this.droppedMessages;
    }

    @Override
    public void addDroppedMessages(int amount) {
        this.droppedMessages += amount;
    }

    @Override
    public ISessionManager getSessionManager() {
        return this.sessionManager;
    }

    @Override
    public void setSessionManager(ISessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public boolean isFrozen() {
        return this.frozen;
    }

    @Override
    public synchronized void freeze() {
        this.frozen = true;
        this.freezeTime = System.currentTimeMillis();
    }

    @Override
    public synchronized void unfreeze() {
        this.frozen = false;
        this.freezeTime = 0;
    }

    @Override
    public long getFreezeTime() {
        return this.freezeTime;
    }

    @Override
    public boolean isReconnectionTimeExpired() {
        long expiry = this.freezeTime + (long)(1000 * this.reconnectionSeconds);
        return System.currentTimeMillis() > expiry;
    }

    @Override
    public void close() throws IOException {
        IWebSocketChannel channel;
        this.packetQueue = null;
        if (this.type == SessionType.DEFAULT && this.connection != null) {
            Socket socket = this.connection.socket();
            if (socket != null && !socket.isClosed()) {
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
                this.connection.close();
            }
            this.datagramChannel = null;
        } else if (this.type == SessionType.WEBSOCKET && (channel = (IWebSocketChannel)this.getSystemProperty("wsChannel")) != null) {
            channel.close();
        }
        this.connected = false;
        this.sessionManager.removeSession(this);
    }

    @Override
    public int getReconnectionSeconds() {
        return this.reconnectionSeconds;
    }

    @Override
    public void setReconnectionSeconds(int value) {
        this.reconnectionSeconds = value < 0 ? 0 : value;
    }

    public String toString() {
        if (!BitZeroServer.isDebug()) {
            return "";
        }
        Object[] arrobject = new Object[4];
        arrobject[0] = this.id;
        arrobject[1] = this.type;
        arrobject[2] = this.loggedIn ? "Yes" : "No";
        arrobject[3] = this.getFullIpAddress();
        return String.format("{ Id: %s, Type: %s, Logged: %s, IP: %s }", arrobject);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ISession)) {
            return false;
        }
        boolean isEqual = false;
        ISession session = (ISession)obj;
        if (session.getId() == this.id) {
            isEqual = true;
        }
        return isEqual;
    }

    private int extractPortFromRemoteHostAddress(String adr) {
        int port = 0;
        try {
            port = Integer.parseInt(adr.substring(adr.indexOf(58) + 1));
        }
        catch (NumberFormatException var3_3) {
            // empty catch block
        }
        return port;
    }
}

