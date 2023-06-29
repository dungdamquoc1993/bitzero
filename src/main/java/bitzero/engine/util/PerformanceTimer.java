/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.util;

import bitzero.engine.util.IPerformanceTimer;
import java.util.LinkedList;

public class PerformanceTimer
implements IPerformanceTimer {
    private int maxSamples = 10;
    private final LinkedList<Long> samples;
    private boolean started = false;
    private volatile long startTime;

    public PerformanceTimer() {
        this(10);
    }

    public PerformanceTimer(int maxSamples) {
        this.maxSamples = maxSamples;
        this.samples = new LinkedList();
    }

    @Override
    public double getAverageMillis() {
        long total = 0;
        for (Long sample : this.samples) {
            total += sample.longValue();
        }
        return (double)(total / (long)this.samples.size()) / 1000000.0;
    }

    @Override
    public int getMaxSamples() {
        return this.maxSamples;
    }

    @Override
    public void startSampling() {
        if (this.started) {
            throw new IllegalStateException("Timer already started!");
        }
        this.started = true;
        this.startTime = System.nanoTime();
    }

    @Override
    public void stopSampling() {
        if (!this.started) {
            throw new IllegalStateException("Timer is already stopped!");
        }
        this.started = false;
        this.addSample(System.nanoTime() - this.startTime);
    }

    private void addSample(long sample) {
        if (this.samples.size() >= this.maxSamples) {
            this.samples.removeFirst();
        }
        this.samples.add(sample);
    }
}

