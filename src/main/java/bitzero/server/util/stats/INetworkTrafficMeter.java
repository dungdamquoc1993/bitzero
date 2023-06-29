/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.util.stats;

import java.util.List;

public interface INetworkTrafficMeter {
    public int getMonitoredHours();

    public int getSamplingRateMinutes();

    public long getTrafficAverage();

    public long getMaxTraffic();

    public long getMinTraffic();

    public List<Long> getDataPoints();

    public List<Long> getDataPoints(int var1);

    public long getLastUpdateMillis();

    public void onTick();
}

