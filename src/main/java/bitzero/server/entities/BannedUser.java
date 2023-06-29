/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.entities;

import bitzero.server.entities.managers.BanMode;

public interface BannedUser {
    public long getBanTimeMillis();

    public int getBanDurationMinutes();

    public String getName();

    public String getZoneName();

    public String getIpAddress();

    public BanMode getMode();

    public String getReason();

    public String getAdminName();

    public boolean isExpired();

    public int getBanTime();
}

