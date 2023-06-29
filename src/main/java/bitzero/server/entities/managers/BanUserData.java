/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.entities.managers;

import java.io.Serializable;
import java.util.Map;

public class BanUserData
implements Serializable {
    private static final long serialVersionUID = -5727904595766376640L;
    private final Map bannedUsersByName;
    private final Map bannedUsersByIp;

    public BanUserData(Map bannedUsersByNameAndZone, Map bannedUsersByIp) {
        this.bannedUsersByName = bannedUsersByNameAndZone;
        this.bannedUsersByIp = bannedUsersByIp;
    }

    public Map getBannedUsersByName() {
        return this.bannedUsersByName;
    }

    public Map getBannedUsersByIp() {
        return this.bannedUsersByIp;
    }
}

