/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.engine.util.scheduling2;

import bitzero.engine.service.IService;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Scheduler
implements IService {
    private static AtomicInteger schedulerId = new AtomicInteger(0);
    private String serviceName;
    private Logger logger;
    private final ScheduledThreadPoolExecutor executor;

    public Scheduler(int poolSize) {
        this(poolSize, null);
    }

    public Scheduler(int poolSize, Logger customLogger) {
        if (poolSize < 1) {
            throw new IllegalArgumentException("Cannot create a thread pool of size: " + poolSize);
        }
        this.serviceName = "Scheduler-" + schedulerId.getAndIncrement();
        this.executor = new ScheduledThreadPoolExecutor(poolSize);
        this.logger = this.logger == null ? LoggerFactory.getLogger((String)"bootLogger") : customLogger;
    }

    @Override
    public void init(Object o) {
        this.logger.info(String.valueOf(this.serviceName) + " started.");
    }

    @Override
    public void destroy(Object o) {
        List<Runnable> awaitingExecution = this.executor.shutdownNow();
        this.logger.info(String.valueOf(this.serviceName) + " stopping. Tasks awaiting execution: " + awaitingExecution.size());
    }

    @Override
    public String getName() {
        return this.serviceName;
    }

    @Override
    public void handleMessage(Object message) {
        throw new UnsupportedOperationException(String.valueOf(this.serviceName) + ": operation not supported.");
    }

    @Override
    public void setName(String name) {
        this.serviceName = name;
    }

    public void resizeThreadPool(int poolSize) {
        this.executor.setCorePoolSize(poolSize);
    }

    public int getThreadPoolSize() {
        return this.executor.getCorePoolSize();
    }

    public int getActiveThreadCount() {
        return this.executor.getActiveCount();
    }
}

