/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.security;

import bitzero.server.security.IPermissionProfile;

public enum DefaultPermissionProfile implements IPermissionProfile
{
    GUEST(0),
    STANDARD(1),
    MODERATOR(2),
    ADMINISTRATOR(3);
    
    private short id;

    private DefaultPermissionProfile(int id) {
        this.id = (short)id;
    }

    @Override
    public short getId() {
        return this.id;
    }
}

