/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.entities.managers;

import bitzero.server.entities.managers.BanUserData;
import java.io.IOException;

public interface IBannedUserStorage {
    public void init();

    public void destroy();

    public BanUserData load() throws Exception;

    public void save(BanUserData var1) throws IOException;
}

