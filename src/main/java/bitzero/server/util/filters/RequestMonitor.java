/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.util.filters;

import bitzero.server.controllers.SystemRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestMonitor {
    private final Map rateMeters = new ConcurrentHashMap();

    public int updateRequest(SystemRequest request) {
        RequestRateMeter meter = (RequestRateMeter)this.rateMeters.get((Object)request);
        if (meter != null) {
            return meter.updateAndCheck();
        }
        this.rateMeters.put(request, new RequestRateMeter());
        return 1;
    }

    private static final class RequestRateMeter {
        private static final int DEFAULT_SECONDS = 1;
        private int rateMonitorMillis;
        private long lastUpdate;
        private int reqCounter;

        public synchronized int updateAndCheck() {
            long now = System.currentTimeMillis();
            if (now - this.lastUpdate > (long)this.rateMonitorMillis) {
                this.reqCounter = 0;
            }
            ++this.reqCounter;
            this.lastUpdate = now;
            return this.reqCounter;
        }

        public RequestRateMeter() {
            this(1);
        }

        public RequestRateMeter(int secondsMonitored) {
            this.rateMonitorMillis = secondsMonitored * 1000;
            this.lastUpdate = System.currentTimeMillis();
            this.reqCounter = 0;
        }
    }

}

