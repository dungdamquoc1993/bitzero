/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.engine.sessions;

import bitzero.engine.config.EngineConfiguration;
import bitzero.engine.core.BitZeroEngine;
import bitzero.engine.core.IEngineWriter;
import bitzero.engine.events.Event;
import bitzero.engine.events.IEvent;
import bitzero.engine.exceptions.SessionReconnectionException;
import bitzero.engine.service.IService;
import bitzero.engine.sessions.IPacketQueue;
import bitzero.engine.sessions.IReconnectionManager;
import bitzero.engine.sessions.ISession;
import bitzero.engine.sessions.ISessionManager;
import bitzero.engine.util.scheduling.ITaskHandler;
import bitzero.engine.util.scheduling.Scheduler;
import bitzero.engine.util.scheduling.Task;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultReconnectionManager
implements IService,
IReconnectionManager {
    public static volatile Boolean ONLY_REAL_TCP = false;
    private static final String SERVICE_NAME = "DefaultReconnectionManager";
    private static final String RECONNETION_CLEANING_TASK_ID = "SessionReconnectionCleanerTask";
    private final ISessionManager sessionManager;
    private final Map frozenSessionsByHash = new ConcurrentHashMap();
    private final Logger logger;
    private Task sessionReconnectionCleanTask;
    private Scheduler systemScheduler;
    private BitZeroEngine engine;

    public DefaultReconnectionManager(ISessionManager sessionManager) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.sessionManager = sessionManager;
    }

    @Override
    public void init(Object o) {
        this.engine = BitZeroEngine.getInstance();
        ONLY_REAL_TCP = this.engine.getConfiguration().isOnlyRealTCP();
        this.systemScheduler = (Scheduler)o;
        this.sessionReconnectionCleanTask = new Task("SessionReconnectionCleanerTask");
        this.systemScheduler.addScheduledTask(this.sessionReconnectionCleanTask, 3, true, new ReconnectionSessionCleaner(null));
    }

    @Override
    public void destroy(Object o) {
        this.sessionReconnectionCleanTask.setActive(false);
        this.frozenSessionsByHash.clear();
    }

    @Override
    public String getName() {
        return "DefaultReconnectionManager";
    }

    @Override
    public void handleMessage(Object message) {
        throw new UnsupportedOperationException("Not supported in this class");
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("Not supported in this class");
    }

    @Override
    public ISessionManager getSessionManager() {
        return this.sessionManager;
    }

    @Override
    public void onSessionLost(ISession session) {
        this.addSession(session);
        session.freeze();
    }

    @Override
    public ISession getReconnectableSession(String token) {
        ISession session = (ISession)this.frozenSessionsByHash.get(token);
        if (session != null || ONLY_REAL_TCP.booleanValue()) {
            return session;
        }
        session = this.sessionManager.getSessionbyToken(token);
        if (session == null) {
            this.logger.debug("resumedSession no Found: ", (Object)token);
        } else if (session.getReconnectionSeconds() > 0) {
            this.logger.info("Session was resurrected: " + session + " magic resurrected ");
            session.freeze();
            return session;
        }
        return null;
    }

    @Override
    public ISession reconnectSession(ISession tempSession, String prevSessionToken) throws SessionReconnectionException {
        SocketChannel connection = tempSession.getConnection();
        ISession session = this.getReconnectableSession(prevSessionToken);
        if (session == null) {
            this.dispatchSessionReconnectionFailureEvent(tempSession);
            throw new SessionReconnectionException("Session Reconnection failure. The passed Session is not managed by the ReconnectionManager: " + connection);
        }
        if (!connection.isConnected()) {
            throw new SessionReconnectionException("Session Reconnection failure. The new socket is not connected: " + session.toString());
        }
        if (session.isReconnectionTimeExpired()) {
            this.dispatchSessionReconnectionFailureEvent(tempSession);
            throw new SessionReconnectionException("Session Reconnection failure. Time expired for Session: " + session.toString());
        }
        this.sessionManager.removeChannel(session.getConnection());
        session.setConnection(connection);
        this.removeSession(session);
        session.unfreeze();
        if (!session.getPacketQueue().isEmpty()) {
            this.engine.getEngineWriter().continueWriteOp(session);
        }
        this.dispatchSessionReconnectionSuccessEvent(session);
        this.logger.debug("Reconnection done. Sessions remaining: " + this.frozenSessionsByHash);
        return session;
    }

    private void addSession(ISession session) {
        if (this.frozenSessionsByHash.containsKey(session.getHashId())) {
            throw new IllegalStateException("Unexpected: Session is already managed by ReconnectionManager. " + session.toString());
        }
        if (session.getReconnectionSeconds() <= 0) {
            throw new IllegalStateException("Unexpected: Session cannot be frozen. " + session.toString());
        }
        this.frozenSessionsByHash.put(session.getHashId(), session);
        this.logger.debug("Session added in ReconnectionManager: " + session + ", ReconnTime: " + session.getReconnectionSeconds() + "s");
    }

    private void removeSession(ISession session) {
        this.frozenSessionsByHash.remove(session.getHashId());
        this.logger.debug("Session removed from ReconnectionManager: " + session);
    }

    private void dispatchSessionReconnectionSuccessEvent(ISession session) {
        Event event = new Event("sessionReconnectionSuccess");
        event.setParameter("session", session);
        this.engine.dispatchEvent(event);
    }

    private void dispatchSessionDisconnectEvent(ISession session) {
        Event event = new Event("sessionDisconnect");
        event.setParameter("session", session);
        this.engine.dispatchEvent(event);
    }

    private void dispatchSessionReconnectionFailureEvent(ISession incomingSession) {
        Event event = new Event("sessionReconnectionFailure");
        event.setParameter("session", incomingSession);
        this.engine.dispatchEvent(event);
    }

    private void applySessionCleaning() {
        if (this.frozenSessionsByHash.size() > 0) {
            Iterator iter = this.frozenSessionsByHash.values().iterator();
            while (iter.hasNext()) {
                ISession session = (ISession)iter.next();
                if (!session.isReconnectionTimeExpired()) continue;
                iter.remove();
                this.logger.debug("Removing expired reconnectable Session: " + session);
                session.setReconnectionSeconds(0);
                try {
                    this.sessionManager.onSocketDisconnected(session);
                }
                catch (IOException e) {
                    this.logger.warn("I/O Error while closing session: " + session);
                }
            }
        }
    }

    private final class ReconnectionSessionCleaner
    implements ITaskHandler {
        @Override
        public void doTask(Task task) throws Exception {
            DefaultReconnectionManager.this.applySessionCleaning();
        }

        private ReconnectionSessionCleaner() {
        }

        ReconnectionSessionCleaner(ReconnectionSessionCleaner reconnectionsessioncleaner) {
            this();
        }
    }

}

