/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.util.executor;

import bitzero.server.util.executor.SmartExecutorConfig;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartThreadPoolExecutor
extends ThreadPoolExecutor {
    private final Logger logger;
    private final SmartExecutorConfig cfg;
    private final int maxThreads;
    private final int backupThreadsExpirySeconds;
    private volatile long lastQueueCheckTime;
    private volatile long lastBackupTime;
    private volatile boolean threadShutDownNotified = false;

    public SmartThreadPoolExecutor(SmartExecutorConfig config) {
        super(config.coreThreads, Integer.MAX_VALUE, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new SFSThreadFactory(config.name));
        this.cfg = config;
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.maxThreads = this.cfg.coreThreads + this.cfg.backupThreads * this.cfg.maxBackups;
        this.backupThreadsExpirySeconds = this.cfg.backupThreadsExpiry * 1000;
        this.lastQueueCheckTime = -1;
    }

    @Override
    public void execute(Runnable command) {
        if (this.getPoolSize() >= this.cfg.coreThreads) {
            boolean needsBackup = this.checkQueueWarningLevel();
            if (needsBackup) {
                if (this.getPoolSize() >= this.maxThreads) {
                    this.logger.warn(String.format("Queue size is big: %s, but all backup thread are already active: %s", this.getQueue().size(), this.getPoolSize()));
                } else {
                    this.setCorePoolSize(this.getPoolSize() + this.cfg.backupThreads);
                    this.lastBackupTime = this.lastQueueCheckTime = System.currentTimeMillis();
                    this.threadShutDownNotified = false;
                    this.logger.info(String.format("Added %s new threads, current size is: %s", this.cfg.backupThreads, this.getPoolSize()));
                }
            } else if (this.getPoolSize() > this.cfg.coreThreads) {
                boolean isQueueSizeSmallEnough;
                boolean isTimeToShutDownBackupThreads = System.currentTimeMillis() - this.lastBackupTime > (long)this.backupThreadsExpirySeconds;
                boolean bl = isQueueSizeSmallEnough = this.getQueue().size() < this.cfg.queueSizeTriggeringBackupExpiry;
                if (isTimeToShutDownBackupThreads && isQueueSizeSmallEnough && !this.threadShutDownNotified) {
                    this.setCorePoolSize(this.cfg.coreThreads);
                    this.threadShutDownNotified = true;
                    this.logger.info("Shutting down old backup threads");
                }
            }
        }
        super.execute(command);
    }

    private boolean checkQueueWarningLevel() {
        boolean needsBackup = false;
        boolean queueIsBusy = this.getQueue().size() >= this.cfg.queueSizeTriggeringBackup;
        long now = System.currentTimeMillis();
        if (this.lastQueueCheckTime < 0) {
            this.lastQueueCheckTime = now;
        }
        if (queueIsBusy) {
            if (now - this.lastQueueCheckTime > (long)(this.cfg.secondsTriggeringBackup * 1000)) {
                needsBackup = true;
            }
        } else {
            this.lastQueueCheckTime = now;
        }
        return needsBackup;
    }

    public int getCoreThreads() {
        return this.cfg.coreThreads;
    }

    public int getBackupThreads() {
        return this.cfg.backupThreads;
    }

    public int getMaxBackups() {
        return this.cfg.maxBackups;
    }

    public int getQueueSizeTriggeringBackup() {
        return this.cfg.queueSizeTriggeringBackup;
    }

    public int getSecondsTriggeringBackup() {
        return this.cfg.secondsTriggeringBackup;
    }

    public int getBackupThreadsExpiry() {
        return this.cfg.backupThreadsExpiry;
    }

    public int getQueueSizeTriggeringBackupExpiry() {
        return this.cfg.queueSizeTriggeringBackupExpiry;
    }

    private static final class SFSThreadFactory
    implements ThreadFactory {
        private static final AtomicInteger POOL_ID = new AtomicInteger(0);
        private static final String THREAD_BASE_NAME = "SFSWorker:%s:%s";
        private final AtomicInteger threadId = new AtomicInteger(1);
        private final String poolName;

        public SFSThreadFactory(String poolName) {
            this.poolName = poolName;
            POOL_ID.incrementAndGet();
        }

        @Override
        public Thread newThread(Runnable r) {
            Object[] arrobject = new Object[2];
            arrobject[0] = this.poolName != null ? this.poolName : Integer.valueOf(POOL_ID.get());
            arrobject[1] = this.threadId.getAndIncrement();
            Thread t = new Thread(r, String.format("SFSWorker:%s:%s", arrobject));
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != 5) {
                t.setPriority(5);
            }
            return t;
        }
    }

}

