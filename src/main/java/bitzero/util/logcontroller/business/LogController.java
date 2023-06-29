/*
 * Decompiled with CFR 0_116.
 */
package bitzero.util.logcontroller.business;

import bitzero.util.logcontroller.business.ILogController;
import bitzero.util.logcontroller.business.scribe.ScribeLogController;

public class LogController {
    static ScribeLogController _instance;
    static final Object lock;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ILogController GetController() {
        if (_instance == null) {
            Object object = lock;
            synchronized (object) {
                if (_instance == null) {
                    _instance = new ScribeLogController();
                }
            }
        }
        return _instance;
    }

    static {
        lock = new Object();
    }
}

