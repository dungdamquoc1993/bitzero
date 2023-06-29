/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.util.deadlock;

import bitzero.server.util.deadlock.ThreadDeadlockDetector;
import bitzero.util.common.business.CommonHandle;

public class DefaultDeadlockListener
implements ThreadDeadlockDetector.Listener {
    @Override
    public void deadlockDetected(Thread[] threads) {
        CommonHandle.writeErrLogDebug("-------------------");
        CommonHandle.writeErrLogDebug("Deadlocked Threads:");
        for (Thread thread : threads) {
            CommonHandle.writeErrLogDebug(thread);
            for (StackTraceElement ste : thread.getStackTrace()) {
                CommonHandle.writeErrLogDebug(ste);
            }
            CommonHandle.writeErrLogDebug("");
        }
    }
}

