/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.entities;

import bitzero.server.entities.BannedUser;
import bitzero.server.entities.User;
import bitzero.server.entities.managers.BanMode;
import java.io.Serializable;
import org.slf4j.LoggerFactory;

public class BZBannedUser
implements BannedUser,
Serializable {
    private static final long serialVersionUID = -8591228575994508738L;
    private final long banTimeMillis = System.currentTimeMillis();
    private final int banDurationMinutes;
    private final long banDurationMillis;
    private final String name;
    private String ipAddress;
    private final BanMode mode;
    private final String reason;
    private final String adminName;

    public BZBannedUser(User user, int durationMinutes, BanMode mode, String reason, String adminName) {
        this(user.getName(), durationMinutes, mode, reason, adminName);
        this.ipAddress = user.getIpAddress();
    }

    public BZBannedUser(User user, int durationMinutes, BanMode mode) {
        this(user, durationMinutes, mode, null, null);
        this.ipAddress = user.getIpAddress();
    }

    public BZBannedUser(String userName, int durationMinutes, BanMode mode, String reason, String adminName) {
        if (durationMinutes < 1) {
            LoggerFactory.getLogger(this.getClass()).warn("Invalid ban duration: " + durationMinutes + ", Automatically converted to 24hrs.");
            durationMinutes = 1440;
        }
        this.banDurationMinutes = durationMinutes;
        this.banDurationMillis = (long)this.banDurationMinutes * 60 * 1000;
        this.name = userName;
        this.ipAddress = null;
        this.mode = mode;
        this.adminName = adminName == null ? "[Server]" : adminName;
        this.reason = reason == null ? "{Unknown}" : reason;
    }

    public BZBannedUser(String userName, int durationMinutes, BanMode mode) {
        this(userName, durationMinutes, mode, null, null);
    }

    @Override
    public String getZoneName() {
        return "";
    }

    @Override
    public long getBanTimeMillis() {
        return this.banTimeMillis;
    }

    @Override
    public String getIpAddress() {
        return this.ipAddress;
    }

    @Override
    public BanMode getMode() {
        return this.mode;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getReason() {
        return this.reason;
    }

    @Override
    public String getAdminName() {
        return this.adminName;
    }

    @Override
    public int getBanDurationMinutes() {
        return this.banDurationMinutes;
    }

    @Override
    public boolean isExpired() {
        return System.currentTimeMillis() > this.banTimeMillis + this.banDurationMillis;
    }

    @Override
    public int getBanTime() {
        return (int)((this.banTimeMillis + this.banDurationMillis - System.currentTimeMillis()) / 1000);
    }

    public String toString() {
        return String.format("BannedUser [name: %s, ip: %s, mode: %s, reason: %s]", new Object[]{this.name, this.ipAddress, this.mode, this.reason});
    }
}

