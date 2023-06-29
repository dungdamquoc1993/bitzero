/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 */
package bitzero.server.util.deadlock;

import bitzero.util.common.business.CommonHandle;
import com.google.gson.Gson;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Collection;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArraySet;

public class ThreadDeadlockDetector {
    private final Timer threadCheck = new Timer("DeadlockDetector", true);
    private final ThreadMXBean mbean = ManagementFactory.getThreadMXBean();
    private final Collection<Listener> listeners = new CopyOnWriteArraySet<Listener>();
    private static final int DEFAULT_DEADLOCK_CHECK_PERIOD = 60000;

    public ThreadDeadlockDetector() {
        this(60000);
    }

    public ThreadDeadlockDetector(int deadlockCheckPeriod) {
        this.threadCheck.schedule(new TimerTask(){

            @Override
            public void run() {
                ThreadDeadlockDetector.this.checkForDeadlocks();
            }
        }, 10, (long)deadlockCheckPeriod);
    }

    private void checkForDeadlocks() {
        long[] ids = this.findDeadlockedThreads();
        if (ids != null && ids.length > 0) {
            CommonHandle.writeErrLogDebug("Dead lock detected!");
            CommonHandle.writeErrLogDebug("");
            Thread[] threads = new Thread[ids.length];
            for (int i = 0; i < threads.length; ++i) {
                ThreadInfo tInfo = this.mbean.getThreadInfo(ids[i]);
                CommonHandle.writeErrLogDebug(new Gson().toJson((Object)tInfo));
                CommonHandle.writeErrLogDebug("TheadId : " + tInfo.getThreadId());
                CommonHandle.writeErrLogDebug("TheadName : " + tInfo.getThreadName());
                CommonHandle.writeErrLogDebug("LockName : " + tInfo.getLockName());
                CommonHandle.writeErrLogDebug("LockOwnerId : " + tInfo.getLockOwnerId());
                CommonHandle.writeErrLogDebug("LockOwnerName : " + tInfo.getLockOwnerName());
                threads[i] = this.findMatchingThread(tInfo);
                CommonHandle.writeErrLogDebug("");
            }
            CommonHandle.writeErrLogDebug("Dead check");
            this.fireDeadlockDetected(threads);
        }
    }

    private long[] findDeadlockedThreads() {
        if (this.mbean.isSynchronizerUsageSupported()) {
            return this.mbean.findDeadlockedThreads();
        }
        return this.mbean.findMonitorDeadlockedThreads();
    }

    private void fireDeadlockDetected(Thread[] threads) {
        for (Listener l : this.listeners) {
            l.deadlockDetected(threads);
        }
    }

    private Thread findMatchingThread(ThreadInfo inf) {
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.getId() != inf.getThreadId()) continue;
            return thread;
        }
        throw new IllegalStateException("Deadlocked Thread not found");
    }

    public boolean addListener(Listener l) {
        return this.listeners.add(l);
    }

    public boolean removeListener(Listener l) {
        return this.listeners.remove(l);
    }

    public static interface Listener {
        public void deadlockDetected(Thread[] var1);
    }

}

