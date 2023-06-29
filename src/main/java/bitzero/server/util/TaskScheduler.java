/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.util;

import bitzero.engine.service.IService;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskScheduler
implements IService {
    private static AtomicInteger schedulerId = new AtomicInteger(0);
    private final ScheduledThreadPoolExecutor taskScheduler;
    private final String serviceName;
    private final Logger logger;

    public TaskScheduler(int threadPoolSize) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.serviceName = "TaskScheduler-" + schedulerId.getAndIncrement();
        this.taskScheduler = new ScheduledThreadPoolExecutor(threadPoolSize);
    }

    @Override
    public void init(Object o) {
        this.logger.info(String.valueOf(this.serviceName) + " started.");
    }

    @Override
    public void destroy(Object o) {
        List<Runnable> awaitingExecution = this.taskScheduler.shutdownNow();
        this.logger.info(String.valueOf(this.serviceName) + " stopping. Tasks awaiting execution: " + awaitingExecution.size());
    }

    @Override
    public String getName() {
        return this.serviceName;
    }

    @Override
    public void handleMessage(Object obj) {
    }

    @Override
    public void setName(String s) {
    }

    public ScheduledFuture schedule(Runnable task, int delay, TimeUnit unit) {
        //this.logger.debug("Task scheduled: " + task + ", " + delay + " " + (Object)((Object)unit));
        return this.taskScheduler.schedule(task, (long)delay, unit);
    }

    public ScheduledFuture scheduleAtFixedRate(Runnable task, int initialDelay, int period, TimeUnit unit) {
        return this.taskScheduler.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    public Boolean remove(Runnable task) {
        return this.taskScheduler.remove(task);
    }

    public void resizeThreadPool(int threadPoolSize) {
        this.taskScheduler.setCorePoolSize(threadPoolSize);
    }

    public int getThreadPoolSize() {
        return this.taskScheduler.getCorePoolSize();
    }
}

