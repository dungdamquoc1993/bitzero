/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.util.stats;

import bitzero.server.util.stats.ITrafficMeter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ZoneTrafficMeter
implements ITrafficMeter {
    private static final int DEFAULT_MONITORED_HOURS = 24;
    private static final int DEFAULT_SAMPLING_RATE_MINUTES = 5;
    private int monitoredHours;
    private int samplingRateMinutes;
    private int samplesPerHour;
    private int maxTrafficValueEverSeen = 0;
    private int minTrafficValueEverSeen = Integer.MAX_VALUE;
    private volatile long lastUpdateTime;
    LinkedList<List> trafficDataByHour;

    public ZoneTrafficMeter() {
        this(24, 5);
    }

    public ZoneTrafficMeter(int monitoredHours, int samplingRateMinutes) {
        this.monitoredHours = monitoredHours;
        this.samplingRateMinutes = samplingRateMinutes;
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
    public List getDataPoints() {
        return this.getDataPoints(0);
    }

    @Override
    public List getDataPoints(int howManyPoints) {
        List flatData = this.getFlatData();
        if (howManyPoints < 1) {
            return flatData;
        }
        if (howManyPoints > flatData.size()) {
            return flatData;
        }
        int steps = flatData.size() / howManyPoints;
        ArrayList<Integer> selectedDataPoints = new ArrayList<Integer>();
        for (int i = 0; i < flatData.size(); i += steps) {
            selectedDataPoints.add((Integer)flatData.get(i));
        }
        return selectedDataPoints;
    }

    @Override
    public int getMaxTraffic() {
        return this.maxTrafficValueEverSeen;
    }

    @Override
    public int getMinTraffic() {
        return this.minTrafficValueEverSeen;
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
    public int getTrafficAverage() {
        ArrayList<Integer> values = new ArrayList<Integer>();
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

    @Override
    public int getTrafficAverage(int previousHours) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onTick() {
        int currHour = this.trafficDataByHour.size();
        List samples = (List)this.trafficDataByHour.get(currHour - 1);
        int userCountNow = 1;
        if (userCountNow > this.maxTrafficValueEverSeen) {
            this.maxTrafficValueEverSeen = userCountNow;
        }
        if (userCountNow < this.minTrafficValueEverSeen) {
            this.minTrafficValueEverSeen = userCountNow;
        }
        List list = samples;
        synchronized (list) {
            samples.add(userCountNow);
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

    private int getAverage(List<Integer> data) {
        if (data.size() == 0) {
            return 0;
        }
        long tot = 0;
        for (Integer value : data) {
            tot += (long)value.intValue();
        }
        return (int)tot / data.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List getFlatData() {
        ArrayList flatData = new ArrayList();
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

