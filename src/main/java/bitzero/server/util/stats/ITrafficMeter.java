/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.util.stats;

import java.util.List;

public interface ITrafficMeter {
    public int getMonitoredHours();

    public int getSamplingRateMinutes();

    public int getTrafficAverage();

    public int getTrafficAverage(int var1);

    public int getMaxTraffic();

    public int getMinTraffic();

    public List getDataPoints();

    public List getDataPoints(int var1);

    public long getLastUpdateMillis();

    public void onTick();
}

