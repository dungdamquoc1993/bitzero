/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.util;

import bitzero.server.controllers.SystemRequest;
import bitzero.server.core.ICoreService;
import bitzero.server.entities.User;
import bitzero.server.entities.managers.BanMode;
import java.util.Map;

public interface IFloodFilter
extends ICoreService {
    public void setActive(boolean var1);

    public void filterRequest(SystemRequest var1, User var2);

    public void addRequestFilter(SystemRequest var1, int var2);

    public boolean isRequestFiltered(SystemRequest var1);

    public void clearAllFilters();

    public Map getRequestTable();

    public int getBanDurationMinutes();

    public void setBanDurationMinutes(int var1);

    public int getMaxFloodingAttempts();

    public void setMaxFloodingAttempts(int var1);

    public int getSecondsBeforeBan();

    public void setSecondsBeforeBan(int var1);

    public boolean isLogFloodingAttempts();

    public void setLogFloodingAttempts(boolean var1);

    public BanMode getBanMode();

    public void setBanMode(BanMode var1);

    public String getBanMessage();

    public void setBanMessage(String var1);
}

