/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.engine.controllers;

import bitzero.engine.controllers.ControllerType;
import bitzero.engine.controllers.IController;
import bitzero.engine.controllers.RequestComparator;
import bitzero.engine.exceptions.RequestQueueFullException;
import bitzero.engine.io.IRequest;
import bitzero.engine.util.Logging;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController
implements IController,
Runnable {
    protected Object id;
    protected String name;
    protected BlockingQueue requestQueue;
    protected ExecutorService threadPool;
    protected int threadPoolSize;
    protected volatile int maxQueueSize;
    protected volatile boolean isActive;
    private volatile int threadId;
    protected final Logger bootLogger = LoggerFactory.getLogger((String)"bootLogger");
    protected final Logger logger;

    public AbstractController() {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.threadPoolSize = -1;
        this.maxQueueSize = -1;
        this.isActive = false;
        this.threadId = 1;
    }

    @Override
    public void enqueueRequest(IRequest request) throws RequestQueueFullException {
        if (this.requestQueue.size() >= this.maxQueueSize) {
            throw new RequestQueueFullException();
        }
        this.requestQueue.add(request);
    }

    @Override
    public void init(Object o) {
        if (this.isActive) {
            throw new IllegalArgumentException("Object is already initialized. Destroy it first!");
        }
        if (this.threadPoolSize < 1) {
            throw new IllegalArgumentException("Illegal value for a thread pool size: " + this.threadPoolSize);
        }
        if (this.maxQueueSize < 1) {
            throw new IllegalArgumentException("Illegal value for max queue size: " + this.maxQueueSize);
        }
        RequestComparator requestComparator = new RequestComparator();
        this.requestQueue = new PriorityBlockingQueue(50, requestComparator);
        this.threadPool = Executors.newFixedThreadPool(this.threadPoolSize);
        this.isActive = true;
        this.initThreadPool();
        this.bootLogger.info(String.format("Controller started: %s -- Queue: %s/%s", this.getClass().getName(), this.getQueueSize(), this.getMaxQueueSize()));
    }

    @Override
    public void destroy(Object o) {
        this.isActive = false;
        List<Runnable> leftOvers = this.threadPool.shutdownNow();
        this.logger.info("Controller stopping: " + this.getClass().getName() + ", Unprocessed tasks: " + leftOvers.size());
    }

    @Override
    public void handleMessage(Object obj) {
    }

    @Override
    public void run() {
        Thread.currentThread().setName(String.valueOf(this.getClass().getName()) + "-" + this.threadId++);
        while (this.isActive) {
            try {
                IRequest request = (IRequest)this.requestQueue.take();
                this.processRequest(request);
            }
            catch (InterruptedException e) {
                this.isActive = false;
                this.logger.warn("Controller main loop was interrupted");
                Logging.logStackTrace(this.logger, e);
            }
            catch (Throwable t) {
                Logging.logStackTrace(this.logger, t);
            }
        }
        this.bootLogger.info("Controller worker threads stopped: " + this.getClass().getName());
    }

    public abstract void processRequest(IRequest var1) throws Exception;

    @Override
    public Object getId() {
        return this.id;
    }

    @Override
    public void setId(Object id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getThreadPoolSize() {
        return this.threadPoolSize;
    }

    @Override
    public void setThreadPoolSize(int threadPoolSize) {
        if (this.threadPoolSize < 1) {
            this.threadPoolSize = threadPoolSize;
        }
    }

    @Override
    public int getQueueSize() {
        return this.requestQueue.size();
    }

    @Override
    public int getMaxQueueSize() {
        return this.maxQueueSize;
    }

    @Override
    public void setMaxQueueSize(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    protected void initThreadPool() {
        for (int j = 0; j < this.threadPoolSize; ++j) {
            this.threadPool.execute(this);
        }
    }

    @Override
    public ControllerType getControllerType() {
        return ControllerType.DEFAULT;
    }
}

