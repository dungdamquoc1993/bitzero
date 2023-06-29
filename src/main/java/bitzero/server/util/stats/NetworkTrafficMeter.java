/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.util.stats;

import bitzero.engine.core.BitZeroEngine;
import bitzero.engine.core.IEngineReader;
import bitzero.engine.core.IEngineWriter;
import bitzero.server.util.stats.INetworkTrafficMeter;
import bitzero.server.util.stats.TrafficType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NetworkTrafficMeter
implements INetworkTrafficMeter {
    public static final int DEFAULT_MONITORED_HOURS = 24;
    public static final int DEFAULT_SAMPLING_RATE_MINUTES = 1;
    private int monitoredHours;
    private int samplingRateMinutes;
    private int samplesPerHour;
    private volatile long maxTrafficValueEverSeen = 0;
    private volatile long minTrafficValueEverSeen = Long.MAX_VALUE;
    private volatile long lastUpdateTime;
    private LinkedList<List> trafficDataByHour;
    private TrafficType trafficType;

    public NetworkTrafficMeter(TrafficType type) {
        this(type, 24, 1);
    }

    public NetworkTrafficMeter(TrafficType type, int monitoredHours, int samplingRateMinutes) {
        this.monitoredHours = monitoredHours;
        this.samplingRateMinutes = samplingRateMinutes;
        this.trafficType = type;
        this.trafficDataByHour = new LinkedList();
        this.trafficDataByHour.add(new ArrayList());
        this.lastUpdateTime = System.currentTimeMillis();
        this.samplesPerHour = 60 / this.samplingRateMinutes;
    }

    @Override
    public long getLastUpdateMillis() {
        return this.lastUpdateTime;
    }

    @Override
    public List<Long> getDataPoints() {
        return this.getDataPoints(0);
    }

    @Override
    public long getMaxTraffic() {
        return this.maxTrafficValueEverSeen;
    }

    @Override
    public long getMinTraffic() {
        return this.minTrafficValueEverSeen;
    }

    @Override
    public List<Long> getDataPoints(int howManyPoints) {
        List<Long> flatData = this.getFlatData();
        if (howManyPoints < 1) {
            return flatData;
        }
        if (howManyPoints > flatData.size()) {
            return flatData;
        }
        int steps = flatData.size() / howManyPoints;
        ArrayList<Long> selectedDataPoints = new ArrayList<Long>();
        for (int i = 0; i < flatData.size(); i += steps) {
            selectedDataPoints.add(flatData.get(i));
        }
        return selectedDataPoints;
    }

    @Override
    public int getMonitoredHours() {
        return this.monitoredHours;
    }

    @Override
    public int getSamplingRateMinutes() {
        return this.samplingRateMinutes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long getTrafficAverage() {
        ArrayList<Long> values = new ArrayList<Long>();
        LinkedList linkedList = this.trafficDataByHour;
        synchronized (linkedList) {
            Iterator iterator = this.trafficDataByHour.iterator();
            while (iterator.hasNext()) {
                List samples;
                List list = samples = (List)iterator.next();
                synchronized (list) {
                    values.add(this.getAverage(samples));
                    continue;
                }
            }
        }
        return this.getAverage(values);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onTick() {
        int currHour = this.trafficDataByHour.size();
        List samples = (List)this.trafficDataByHour.get(currHour - 1);
        long trafficValue = this.trafficType == TrafficType.INCOMING ? BitZeroEngine.getInstance().getEngineReader().getReadBytes() / 1024 : BitZeroEngine.getInstance().getEngineWriter().getWrittenBytes() / 1024;
        if (trafficValue > this.maxTrafficValueEverSeen) {
            this.maxTrafficValueEverSeen = trafficValue;
        }
        if (trafficValue < this.minTrafficValueEverSeen) {
            this.minTrafficValueEverSeen = trafficValue;
        }
        List list = samples;
        synchronized (list) {
            samples.add(trafficValue);
        }
        if (samples.size() == this.samplesPerHour) {
            list = this.trafficDataByHour;
            synchronized (list) {
                if (this.trafficDataByHour.size() == this.monitoredHours) {
                    this.trafficDataByHour.removeFirst();
                }
                this.trafficDataByHour.add(new ArrayList());
            }
        }
        this.lastUpdateTime = System.currentTimeMillis();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        LinkedList linkedList = this.trafficDataByHour;
        synchronized (linkedList) {
            int hour = 1;
            for (List samples : this.trafficDataByHour) {
                sb.append(hour++).append(" => ").append(samples.toString()).append("\n");
            }
        }
        return sb.toString();
    }

    private long getAverage(List<Long> data) {
        if (data.size() == 0) {
            return 0;
        }
        long tot = 0;
        for (Long value : data) {
            tot += value.longValue();
        }
        return (int)tot / data.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<Long> getFlatData() {
        ArrayList<Long> flatData = new ArrayList<Long>();
        LinkedList linkedList = this.trafficDataByHour;
        synchronized (linkedList) {
            Iterator iterator = this.trafficDataByHour.iterator();
            while (iterator.hasNext()) {
                List samples;
                List list = samples = (List)iterator.next();
                synchronized (list) {
                    flatData.addAll(samples);
                    continue;
                }
            }
        }
        return flatData;
    }
}

