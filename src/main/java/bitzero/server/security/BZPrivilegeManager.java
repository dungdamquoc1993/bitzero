/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.security;

import bitzero.server.controllers.SystemRequest;
import bitzero.server.entities.User;
import bitzero.server.security.BZPermissionProfile;
import bitzero.server.security.PrivilegeManager;
import bitzero.server.security.SystemPermission;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BZPrivilegeManager
implements PrivilegeManager {
    private final Map<Short,BZPermissionProfile> privilegeProfiles = new ConcurrentHashMap();
    private final Logger log;
    private volatile boolean active;

    public BZPrivilegeManager() {
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean containsPermissionProfile(short profileId) {
        return this.privilegeProfiles.containsKey(profileId);
    }

    @Override
    public boolean containsPermissionProfile(String profileName) {
        boolean found = false;
        Short id = this.findIdFromName(profileName);
        if (id != null) {
            found = this.containsPermissionProfile(id);
        }
        return found;
    }

    @Override
    public BZPermissionProfile getPermissionProfile(short profileId) {
        return (BZPermissionProfile)this.privilegeProfiles.get(profileId);
    }

    @Override
    public BZPermissionProfile getPermissionProfile(String profileName) {
        Short id = this.findIdFromName(profileName);
        if (id == null) {
            return null;
        }
        return (BZPermissionProfile)this.privilegeProfiles.get(id);
    }

    @Override
    public void removePermissionProfile(short permId) {
        this.privilegeProfiles.remove(permId);
    }

    @Override
    public void removePermissionProfile(String profileName) {
        Short id = this.findIdFromName(profileName);
        if (id != null) {
            this.privilegeProfiles.remove(id);
        }
    }

    @Override
    public void setPermissionProfile(BZPermissionProfile profile) {
        if (this.privilegeProfiles.containsKey(profile.getId())) {
            this.log.warn("Profile with duplicate ID: " + profile.getId() + ", name: " + profile.getName() + " was not added to the manager");
            return;
        }
        this.privilegeProfiles.put(profile.getId(), profile);
    }

    @Override
    public boolean isRequestAllowed(User user, SystemRequest request) {
        if (!this.isActive()) {
            return true;
        }
        boolean success = false;
        BZPermissionProfile profile = (BZPermissionProfile)this.privilegeProfiles.get(user.getPrivilegeId());
        if (profile != null) {
            success = profile.isRequestAllowed(request);
        }
        return success;
    }

    @Override
    public boolean isFlagSet(User user, SystemPermission permission) {
        boolean success = false;
        BZPermissionProfile profile = (BZPermissionProfile)this.privilegeProfiles.get(user.getPrivilegeId());
        if (profile != null) {
            success = profile.isFlagSet(permission);
        }
        return success;
    }

    private Short findIdFromName(String name) {
        Short profileId = null;
        for (BZPermissionProfile profile : this.privilegeProfiles.values()) {
            if (!profile.getName().equals(name)) continue;
            profileId = profile.getId();
            break;
        }
        return profileId;
    }

    public void dump() {
        for (Short id : this.privilegeProfiles.keySet()) {
            System.out.println(id + ":");
            BZPermissionProfile profile = (BZPermissionProfile)this.privilegeProfiles.get(id);
            System.out.println("\tAllowed Sys Req:");
            for (SystemRequest sysReq : SystemRequest.values()) {
                if (!profile.isRequestAllowed(sysReq)) continue;
                System.out.println("\t\t" + (Object)((Object)sysReq));
            }
            System.out.println("\tPermission Flags:");
            for (SystemPermission perm : SystemPermission.values()) {
                if (!profile.isFlagSet(perm)) continue;
                System.out.println("\t\t" + (Object)((Object)perm));
            }
        }
    }
}

