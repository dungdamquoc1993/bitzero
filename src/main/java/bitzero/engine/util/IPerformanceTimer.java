/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.util;

public interface IPerformanceTimer {
    public void startSampling();

    public void stopSampling();

    public double getAverageMillis();

    public int getMaxSamples();
}

