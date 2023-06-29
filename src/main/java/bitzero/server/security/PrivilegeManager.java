/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.security;

import bitzero.server.controllers.SystemRequest;
import bitzero.server.entities.User;
import bitzero.server.security.BZPermissionProfile;
import bitzero.server.security.SystemPermission;

public interface PrivilegeManager {
    public boolean isActive();

    public void setActive(boolean var1);

    public void setPermissionProfile(BZPermissionProfile var1);

    public void removePermissionProfile(short var1);

    public void removePermissionProfile(String var1);

    public boolean containsPermissionProfile(short var1);

    public boolean containsPermissionProfile(String var1);

    public BZPermissionProfile getPermissionProfile(short var1);

    public BZPermissionProfile getPermissionProfile(String var1);

    public boolean isRequestAllowed(User var1, SystemRequest var2);

    public boolean isFlagSet(User var1, SystemPermission var2);
}

