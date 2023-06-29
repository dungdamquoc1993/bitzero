/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.core;

import bitzero.server.core.BaseCoreService;
import bitzero.server.core.IBZEvent;
import bitzero.server.core.IBZEventListener;
import bitzero.server.core.IBZEventManager;
import bitzero.server.core.IBZEventType;
import bitzero.util.common.business.CommonHandle;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BZEventManager
extends BaseCoreService
implements IBZEventManager {
    private int corePoolSize = 20;
    private int maxPoolSize = 30;
    private int threadKeepAliveTime = 60;
    private final ThreadPoolExecutor threadPool;
    private final Map listenersByEvent = new ConcurrentHashMap();
    private final Logger logger = LoggerFactory.getLogger(BZEventManager.class);

    public BZEventManager() {
        this.setName("BZEventManager");
        this.threadPool = new ThreadPoolExecutor(this.corePoolSize, this.maxPoolSize, this.threadKeepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    @Override
    public void init(Object o) {
        super.init(o);
        this.logger.info(String.valueOf(this.name) + " initalized");
    }

    @Override
    public void destroy(Object o) {
        super.destroy(o);
        this.listenersByEvent.clear();
        this.logger.info(String.valueOf(this.name) + " shut down.");
    }

    @Override
    public void setThreadPoolSize(int poolSize) {
        this.threadPool.setCorePoolSize(poolSize);
    }

    @Override
    public synchronized void addEventListener(IBZEventType type, IBZEventListener listener) {
        CopyOnWriteArraySet<IBZEventListener> listeners = (CopyOnWriteArraySet<IBZEventListener>)this.listenersByEvent.get(type);
        if (listeners == null) {
            listeners = new CopyOnWriteArraySet<IBZEventListener>();
            this.listenersByEvent.put(type, listeners);
        }
        listeners.add(listener);
    }

    @Override
    public boolean hasEventListener(IBZEventType type) {
        boolean found = false;
        Set listeners = (Set)this.listenersByEvent.get(type);
        if (listeners != null && listeners.size() > 0) {
            found = true;
        }
        return found;
    }

    @Override
    public synchronized void removeEventListener(IBZEventType type, IBZEventListener listener) {
        Set listeners = (Set)this.listenersByEvent.get(type);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    @Override
    public void dispatchEvent(IBZEvent event) {
        Set<IBZEventListener> listeners = (Set)this.listenersByEvent.get(event.getType());
        if (listeners != null && listeners.size() > 0) {
            for (IBZEventListener listener : listeners) {
                this.threadPool.execute(new BZEventRunner(listener, event));
            }
        }
    }

    @Override
    public void dispatchImmediateEvent(IBZEvent event) {
        Set<IBZEventListener> listeners = (Set)this.listenersByEvent.get(event.getType());
        if (listeners != null && listeners.size() > 0) {
            for (IBZEventListener listener : listeners) {
                this.run(listener, event);
            }
        }
    }

    private void run(IBZEventListener listener, IBZEvent event) {
        try {
            listener.handleServerEvent(event);
        }
        catch (Exception e) {
            CommonHandle.writeErrLog(e);
            LoggerFactory.getLogger(this.getClass()).warn("Error in event immediate handler: " + e + ", Event: " + event + " Listener: " + listener);
        }
    }

    @Override
    public Executor getThreadPool() {
        return this.threadPool;
    }

    private static final class BZEventRunner
    implements Runnable {
        private final IBZEventListener listener;
        private final IBZEvent event;

        @Override
        public void run() {
            try {
                this.listener.handleServerEvent(this.event);
            }
            catch (Exception e) {
                CommonHandle.writeErrLog(e);
                LoggerFactory.getLogger(this.getClass()).warn("Error in event handler: " + e + ", Event: " + this.event + " Listener: " + this.listener);
            }
        }

        public BZEventRunner(IBZEventListener listener, IBZEvent event) {
            this.listener = listener;
            this.event = event;
        }
    }

}

