/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.entities.managers;

import bitzero.server.core.ICoreService;
import bitzero.server.entities.User;
import bitzero.server.entities.managers.BanMode;
import java.util.List;

public interface IBannedUserManager
extends ICoreService {
    public boolean isAutoRemoveBan();

    public void setAutoRemoveBan(boolean var1);

    public boolean isPersistent();

    public void setPersistent(boolean var1);

    public void setPersistenceClass(String var1);

    public void kickUser(User var1, User var2, String var3, int var4);

    public void kickUser(User var1, User var2, String var3, int var4, boolean var5);

    public void banUser(User var1, User var2, int var3, BanMode var4, String var5, String var6, int var7);

    public void banUser(String var1, int var2, BanMode var3, String var4);

    public void banUser(String var1, int var2, BanMode var3, String var4, String var5);

    public void sendWarningMessage(User var1, User var2, String var3);

    public int getKickCount(String var1, int var2);

    public boolean isNameBanned(String var1);

    public boolean isIpBanned(String var1);

    public void removeBannedUser(String var1, BanMode var2);

    public List getBannedUsersByIp();

    public List getBannedUsersByName(String var1);

    public int getBanDuration(String var1, BanMode var2);
}

