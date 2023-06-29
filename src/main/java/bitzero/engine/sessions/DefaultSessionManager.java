/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.engine.sessions;

import bitzero.engine.clustering.IClusterManager;
import bitzero.engine.config.EngineConfiguration;
import bitzero.engine.core.BitZeroEngine;
import bitzero.engine.core.IEngineAcceptor;
import bitzero.engine.core.security.IConnectionFilter;
import bitzero.engine.events.Event;
import bitzero.engine.events.IEvent;
import bitzero.engine.exceptions.BitZeroEngineRuntimeException;
import bitzero.engine.exceptions.SessionReconnectionException;
import bitzero.engine.service.IService;
import bitzero.engine.sessions.DefaultReconnectionManager;
import bitzero.engine.sessions.IPacketQueue;
import bitzero.engine.sessions.IPacketQueuePolicy;
import bitzero.engine.sessions.IReconnectionManager;
import bitzero.engine.sessions.ISession;
import bitzero.engine.sessions.ISessionManager;
import bitzero.engine.sessions.NonBlockingPacketQueue;
import bitzero.engine.sessions.Session;
import bitzero.engine.sessions.SessionType;
import bitzero.engine.util.Logging;
import bitzero.engine.util.scheduling.ITaskHandler;
import bitzero.engine.util.scheduling.Scheduler;
import bitzero.engine.util.scheduling.Task;
import bitzero.engine.websocket.IWebSocketChannel;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultSessionManager
implements ISessionManager {
    private static final String SESSION_CLEANING_TASK_ID = "SessionCleanerTask";
    private static final int SESSION_CLEANING_INTERVAL_SECONDS = 10;
    private static ISessionManager __instance__;
    private final Logger bootLogger = LoggerFactory.getLogger((String)"bootLogger");
    private Logger logger;
    private ConcurrentMap sessionsByNode;
    private ConcurrentMap sessionsById;
    private BitZeroEngine engine = null;
    private EngineConfiguration config = null;
    private final List<ISession> localSessions = new ArrayList<ISession>();
    private final ConcurrentMap<Integer, ISession> localSessionsById = new ConcurrentHashMap<Integer, ISession>();
    private final ConcurrentMap<SocketChannel, ISession> localSessionsByConnection = new ConcurrentHashMap<SocketChannel, ISession>();
    private final ConcurrentMap<String, ISession> localSessionsByToken = new ConcurrentHashMap<String, ISession>();
    private String serviceName = "DefaultSessionManager";
    private Task sessionCleanTask;
    private Scheduler systemScheduler;
    IReconnectionManager reconnectionManager;
    private IPacketQueuePolicy packetQueuePolicy;
    private int highestCCS = 0;
    private volatile int reconnectTimes = 0;
    private volatile int reconnectSuccessTimes = 0;
    private volatile int ghostReconnectTimes = 0;

    public static ISessionManager getInstance() {
        if (__instance__ == null) {
            __instance__ = new DefaultSessionManager();
        }
        return __instance__;
    }

    private DefaultSessionManager() {
        if (this.sessionsByNode == null) {
            this.sessionsByNode = new ConcurrentHashMap();
        }
        if (this.sessionsById == null) {
            this.sessionsById = new ConcurrentHashMap();
        }
        this.reconnectionManager = new DefaultReconnectionManager(this);
    }

    @Override
    public int[] getReconnectStatus() {
        return new int[]{this.reconnectTimes, this.reconnectSuccessTimes, this.ghostReconnectTimes};
    }

    @Override
    public void init(Object o) {
        this.engine = BitZeroEngine.getInstance();
        this.config = this.engine.getConfiguration();
        this.logger = LoggerFactory.getLogger(DefaultSessionManager.class);
        this.systemScheduler = (Scheduler)this.engine.getServiceByName("scheduler");
        this.sessionCleanTask = new Task("SessionCleanerTask");
        this.systemScheduler.addScheduledTask(this.sessionCleanTask, 10, true, new SessionCleaner(null));
        ((IService)((Object)this.reconnectionManager)).init(this.systemScheduler);
        try {
            Class packetPolicyClass = Class.forName(this.config.getPacketQueuePolicyClass());
            this.packetQueuePolicy = (IPacketQueuePolicy)packetPolicyClass.newInstance();
        }
        catch (Exception e) {
            this.bootLogger.warn("SessionManager could not load a valid PacketQueuePolicy. Reason: " + e);
            Logging.logStackTrace(this.bootLogger, e);
        }
    }

    @Override
    public void destroy(Object o) {
        this.sessionCleanTask.setActive(false);
        ((IService)((Object)this.reconnectionManager)).destroy(null);
        this.shutDownLocalSessions();
        this.localSessionsById.clear();
        this.localSessionsByConnection.clear();
    }

    @Override
    public void publishLocalNode(String nodeId) {
        if (this.sessionsByNode.get(nodeId) != null) {
            throw new IllegalStateException("NodeID already exists in the cluster: " + nodeId);
        }
        this.sessionsByNode.put(nodeId, this.localSessions);
    }

    @Override
    public int getHighestCCS() {
        return this.highestCCS;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addSession(ISession session) {
        List<ISession> list = this.localSessions;
        synchronized (list) {
            this.localSessions.add(session);
        }
        this.localSessionsById.put(session.getId(), session);
        if (session.getType() == SessionType.DEFAULT) {
            this.localSessionsByConnection.put(session.getConnection(), session);
        }
        if (this.localSessions.size() > this.highestCCS) {
            this.highestCCS = this.localSessions.size();
        }
    }

    @Override
    public void addSessionToken(ISession session) {
        this.localSessionsByToken.put(session.getHashId(), session);
    }

    @Override
    public ISession getSessionbyToken(String key) {
        ISession reSession = null;
        try {
            reSession = this.localSessionsByToken.get(key);
            if (reSession != null) {
                ++this.ghostReconnectTimes;
            }
        }
        catch (Exception e) {
            reSession = null;
        }
        return reSession;
    }

    @Override
    public boolean containsSession(ISession session) {
        return this.localSessionsById.containsValue(session);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSession(ISession session) {
        if (session == null) {
            return;
        }
        List<ISession> list = this.localSessions;
        synchronized (list) {
            this.localSessions.remove(session);
        }
        SocketChannel connection = session.getConnection();
        int id = session.getId();
        this.localSessionsById.remove(id);
        this.localSessionsByToken.remove(session.getHashId());
        if (connection != null) {
            this.localSessionsByConnection.remove(connection);
        }
        if (session.getType() == SessionType.DEFAULT || session.getType() == SessionType.WEBSOCKET) {
            this.engine.getEngineAcceptor().getConnectionFilter().removeAddress(session.getAddress());
        }
        if (this.config.isClustered()) {
            this.sessionsById.remove(id);
        }
        this.logger.info("Session removed: " + session);
    }

    @Override
    public ISession removeSession(int id) {
        ISession session = this.localSessionsById.get(id);
        if (session != null) {
            this.removeSession(session);
        }
        return session;
    }

    @Override
    public ISession removeSession(String hash) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public ISession removeSession(SocketChannel connection) {
        ISession session = this.getLocalSessionByConnection(connection);
        if (session != null) {
            this.removeSession(session);
        }
        return session;
    }

    @Override
    public void removeChannel(SocketChannel connection) {
        this.localSessionsByConnection.remove(connection);
    }

    @Override
    public void onSocketDisconnected(SocketChannel connection) throws IOException {
        ISession session = this.localSessionsByConnection.get(connection);
        if (session == null) {
            return;
        }
        this.localSessionsByConnection.remove(connection);
        session.setConnected(false);
        this.onSocketDisconnected(session);
    }

    @Override
    public void onSocketDisconnected(ISession session) throws IOException {
        if (session.getReconnectionSeconds() > 0) {
            this.reconnectionManager.onSessionLost(session);
            this.dispatchSessionReconnectionTryEvent(session);
        } else {
            this.removeSession(session);
            this.dispatchLostSessionEvent(session);
        }
    }

    @Override
    public ISession reconnectSession(ISession tempSession, String sessionToken) throws SessionReconnectionException, IOException {
        ISession resumedSession = null;
        ++this.reconnectTimes;
        resumedSession = this.reconnectionManager.reconnectSession(tempSession, sessionToken);
        ++this.reconnectSuccessTimes;
        this.localSessionsByConnection.put(tempSession.getConnection(), resumedSession);
        tempSession.setConnection(null);
        this.logger.info("Session was resurrected: " + resumedSession + ", using temp Session: " + tempSession + ", " + resumedSession.getReconnectionSeconds());
        return resumedSession;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public LinkedList<ISession> getAllLocalSessions() {
        LinkedList<ISession> allSessions = null;
        List<ISession> list = this.localSessions;
        synchronized (list) {
            allSessions = new LinkedList<ISession>(this.localSessions);
        }
        return allSessions;
    }

    @Override
    public List getAllSessions() {
        LinkedList<ISession> sessions = null;
        sessions = this.config.isClustered() ? new LinkedList<ISession>(this.sessionsById.values()) : this.getAllLocalSessions();
        return sessions;
    }

    @Override
    public List getAllSessionsAtNode(String nodeName) {
        ArrayList allSessions = null;
        List theSessions = (List)this.sessionsByNode.get(nodeName);
        if (theSessions != null) {
            allSessions = new ArrayList(theSessions);
        }
        return allSessions;
    }

    @Override
    public ISession getLocalSessionByHash(String hash) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public ISession getLocalSessionById(int id) {
        return this.localSessionsById.get(id);
    }

    @Override
    public ISession getSessionByHash(String hash) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public ISession getLocalSessionByConnection(SocketChannel connection) {
        return this.localSessionsByConnection.get(connection);
    }

    @Override
    public ISession getSessionById(int id) {
        return (ISession)this.sessionsById.get(id);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void shutDownLocalSessions() {
        List<ISession> list = this.localSessions;
        synchronized (list) {
            Iterator<ISession> it = this.localSessions.iterator();
            while (it.hasNext()) {
                ISession session = it.next();
                it.remove();
                try {
                    session.close();
                }
                catch (IOException e) {
                    this.bootLogger.warn("I/O Error while closing session: " + session);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onNodeLost(String nodeId) {
        List<ISession> nodeSessions = (List<ISession>)this.sessionsByNode.remove(nodeId);
        if (nodeSessions == null) {
            throw new IllegalStateException("Unable to remove node sessions from cluster. Lost Node ID: " + nodeId);
        }
        ConcurrentMap concurrentMap = this.sessionsById;
        synchronized (concurrentMap) {
            for (ISession session : nodeSessions) {
                this.sessionsById.remove(session.getId());
            }
        }
    }

    @Override
    public void clearClusterData() {
        this.sessionsById.clear();
        this.sessionsByNode.clear();
    }

    @Override
    public String getName() {
        return this.serviceName;
    }

    @Override
    public void setName(String name) {
        this.serviceName = name;
    }

    @Override
    public void handleMessage(Object message) {
        throw new UnsupportedOperationException("Not implemented in this class!");
    }

    @Override
    public ISession createSession(SocketChannel connection) {
        Session session = new Session();
        session.setSessionManager(this);
        session.setConnection(connection);
        session.setMaxIdleTime(this.engine.getConfiguration().getDefaultMaxSessionIdleTime());
        session.setNodeId(this.config.isClustered() ? this.engine.getClusterManager().getLocalNodeName() : "");
        session.setType(SessionType.DEFAULT);
        session.setReconnectionSeconds(this.engine.getConfiguration().getGlobalReconnectionSeconds());
        NonBlockingPacketQueue packetQueue = new NonBlockingPacketQueue(this.engine.getConfiguration().getSessionPacketQueueMaxSize());
        packetQueue.setPacketQueuePolicy(this.packetQueuePolicy);
        session.setPacketQueue(packetQueue);
        return session;
    }

    @Override
    public ISession createConnectionlessSession() {
        Session session = new Session();
        session.setSessionManager(this);
        session.setNodeId(this.config.isClustered() ? this.engine.getClusterManager().getLocalNodeName() : "");
        session.setType(SessionType.VOID);
        session.setConnected(true);
        return session;
    }

    @Override
    public ISession createBlueBoxSession() {
        Session session = new Session();
        session.setSessionManager(this);
        session.setMaxIdleTime(this.engine.getConfiguration().getDefaultMaxSessionIdleTime());
        session.setNodeId(this.config.isClustered() ? this.engine.getClusterManager().getLocalNodeName() : "");
        session.setType(SessionType.BLUEBOX);
        NonBlockingPacketQueue packetQueue = new NonBlockingPacketQueue(this.engine.getConfiguration().getSessionPacketQueueMaxSize());
        packetQueue.setPacketQueuePolicy(this.packetQueuePolicy);
        session.setPacketQueue(packetQueue);
        return session;
    }

    @Override
    public int getLocalSessionCount() {
        return this.localSessions.size();
    }

    @Override
    public int getNodeSessionCount(String nodeId) {
        List nodeSessionList = (List)this.sessionsByNode.get(nodeId);
        if (nodeSessionList == null) {
            throw new BitZeroEngineRuntimeException("Can't find session count for requested node in the cluster. Node not found: " + nodeId);
        }
        return nodeSessionList.size();
    }

    private void applySessionCleaning() {
        if (this.getLocalSessionCount() > 0) {
            for (ISession session : this.getAllLocalSessions()) {
                if (session == null || session.isFrozen()) continue;
                if (session.isMarkedForEviction()) {
                    this.terminateSession(session);
                    this.logger.info("Terminated idle logged-in session: " + session);
                    continue;
                }
                if (!session.isIdle()) continue;
                if (session.isLoggedIn()) {
                    this.logger.debug("Firing Client Disconnection " + session);
                    session.setMarkedForEviction();
                    this.dispatchSessionIdleEvent(session);
                    continue;
                }
                this.terminateSession(session);
                this.logger.debug("Removed idle session: " + session);
            }
        }
        Event event = new Event("sessionIdleCheckComplete");
        this.engine.dispatchEvent(event);
    }

    public void terminateSession(ISession session) {
        if (session.getType() == SessionType.DEFAULT) {
            SocketChannel connection = session.getConnection();
            session.setReconnectionSeconds(0);
            try {
                if (connection.socket() != null) {
                    connection.socket().shutdownInput();
                    connection.socket().shutdownOutput();
                    connection.close();
                }
                session.setConnected(false);
            }
            catch (IOException err) {
                this.logger.warn("Failed closing connection while removing idle Session: " + session);
            }
        } else if (session.isWebsocket()) {
            IWebSocketChannel channel = (IWebSocketChannel)session.getSystemProperty("wsChannel");
            channel.close();
            return;
        }
        this.removeSession(session);
        this.dispatchLostSessionEvent(session);
    }

    private void dispatchLostSessionEvent(ISession closedSession) {
        Event event = new Event("sessionLost");
        event.setParameter("session", closedSession);
        this.engine.dispatchEvent(event);
    }

    private void dispatchSessionIdleEvent(ISession idleSession) {
        Event event = new Event("sessionIdle");
        event.setParameter("session", idleSession);
        this.engine.dispatchEvent(event);
    }

    private void dispatchSessionReconnectionTryEvent(ISession session) {
        Event event = new Event("sessionReconnectionTry");
        event.setParameter("session", session);
        this.engine.dispatchEvent(event);
    }

    @Override
    public ISession createWebSocketSession(Object channel) {
        Session session = new Session();
        session.setSessionManager(this);
        session.setMaxIdleTime(this.engine.getConfiguration().getDefaultMaxSessionIdleTime());
        session.setNodeId(this.config.isClustered() ? this.engine.getClusterManager().getLocalNodeName() : "");
        session.setType(SessionType.WEBSOCKET);
        session.setConnected(true);
        NonBlockingPacketQueue packetQueue = new NonBlockingPacketQueue(this.engine.getConfiguration().getSessionPacketQueueMaxSize());
        packetQueue.setPacketQueuePolicy(this.packetQueuePolicy);
        session.setPacketQueue(packetQueue);
        session.setSystemProperty("wsChannel", channel);
        return session;
    }

    private final class SessionCleaner
    implements ITaskHandler {
        @Override
        public void doTask(Task task) throws Exception {
            DefaultSessionManager.this.applySessionCleaning();
        }

        private SessionCleaner() {
        }

        SessionCleaner(SessionCleaner sessioncleaner) {
            this();
        }
    }

}

