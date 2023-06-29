/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.util;

import java.text.DecimalFormat;

public class ServerUptime {
    private static final int ONE_DAY = 86400000;
    private static final int ONE_HOUR = 3600000;
    private static final int ONE_MINUTE = 60000;
    private static final int ONE_SECOND = 1000;
    private int days;
    private int hours;
    private int minutes;
    private int seconds;

    public ServerUptime(long unixTime) {
        this.days = (int)Math.floor(unixTime / 86400000);
        this.hours = (int)Math.floor((unixTime -= 86400000 * (long)this.days) / 3600000);
        this.minutes = (int)Math.floor((unixTime -= (long)(3600000 * this.hours)) / 60000);
        this.seconds = (int)Math.floor((unixTime -= (long)(60000 * this.minutes)) / 1000);
    }

    public int[] toArray() {
        int[] data = new int[]{this.days, this.hours, this.minutes, this.seconds};
        return data;
    }

    public int getDays() {
        return this.days;
    }

    public int getHours() {
        return this.hours;
    }

    public int getMinutes() {
        return this.minutes;
    }

    public int getSeconds() {
        return this.seconds;
    }

    public String toString() {
        DecimalFormat fmt = new DecimalFormat("##00");
        return String.format("%s days, %s:%s:%s", this.days, fmt.format(this.hours), fmt.format(this.minutes), fmt.format(this.seconds));
    }
}

