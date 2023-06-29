/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.security;

import bitzero.server.controllers.SystemRequest;
import bitzero.server.security.SystemPermission;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BZPermissionProfile {
    private short id;
    private String name;
    private Set deniedRequest;
    private Set permissionFlags;

    public BZPermissionProfile(short id, String name, List deniedRequests) {
        this(id, name, deniedRequests, null);
    }

    public BZPermissionProfile(short id, String name, List deniedRequests, List flags) {
        this.id = id;
        this.name = name;
        this.deniedRequest = new HashSet();
        this.permissionFlags = new HashSet();
        if (deniedRequests != null) {
            this.deniedRequest.addAll(deniedRequests);
        }
        if (flags != null) {
            this.permissionFlags.addAll(flags);
        }
    }

    public boolean isRequestAllowed(SystemRequest request) {
        return !this.deniedRequest.contains((Object)request);
    }

    public boolean isFlagSet(SystemPermission permission) {
        return this.permissionFlags.contains((Object)permission);
    }

    public short getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}

