/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.config;

import java.util.concurrent.atomic.AtomicInteger;

public class ZoneSettings {
    private static final AtomicInteger idGenerator = new AtomicInteger();
    public int id;
    public String name = "";
    public boolean isCustomLogin = false;
    public int maxUsers = 1000;
    public int minRoomNameChars = 3;
    public int maxRoomNameChars = 100;
    public int maxRooms = 500;
    public int userCountChangeUpdateInterval = 1000;
    public int userReconnectionSeconds = 10;
    public String defaultPlayerIdGeneratorClass = "";

    public ZoneSettings() {
        this.getId();
    }

    public ZoneSettings(String name) {
        this();
        this.name = name;
    }

    public synchronized int getId() {
        return this.id;
    }

    private static int setUniqueId() {
        return idGenerator.getAndIncrement();
    }
}

