/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.util;

import bitzero.engine.sessions.ISession;
import bitzero.server.controllers.SystemRequest;
import bitzero.server.core.BaseCoreService;
import bitzero.server.entities.User;
import bitzero.server.entities.managers.BanMode;
import bitzero.server.entities.managers.IBannedUserManager;
import bitzero.server.util.IFloodFilter;
import bitzero.server.util.UsersUtil;
import bitzero.server.util.filters.RequestMonitor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BZFloodFilter
extends BaseCoreService
implements IFloodFilter {
    private final Map requestTable = new ConcurrentHashMap();
    private final IBannedUserManager banUserManager;
    private final Logger log;
    private volatile int banDurationMinutes;
    private volatile int maxFloodingAttempts;
    private volatile int secondsBeforeBan;
    private volatile boolean logFloodingAttempts;
    private BanMode banMode;
    private String banMessage;

    public BZFloodFilter(IBannedUserManager manager) {
        this.log = LoggerFactory.getLogger((String)this.getClass().getName());
        this.banDurationMinutes = 60;
        this.maxFloodingAttempts = 5;
        this.secondsBeforeBan = 5;
        this.logFloodingAttempts = false;
        this.banMode = BanMode.BY_NAME;
        this.banMessage = "You are being banned, too many flooding attempts.";
        super.setName(this.getClass().getName());
        this.banUserManager = manager;
        this.requestTable.put(SystemRequest.PublicMessage, 50);
    }

    @Override
    public void filterRequest(SystemRequest reqType, User user) {
        Integer maxReqsPerSecond;
        if (!this.active) {
            return;
        }
        RequestMonitor monitor = (RequestMonitor)user.getSession().getSystemProperty("FloodFilterRequestTable");
        if (monitor == null) {
            monitor = new RequestMonitor();
            user.getSession().setSystemProperty("FloodFilterRequestTable", monitor);
        }
        if ((maxReqsPerSecond = (Integer)this.requestTable.get((Object)reqType)) == null) {
            return;
        }
        int userReqsPerSecond = monitor.updateRequest(reqType);
        if (userReqsPerSecond >= maxReqsPerSecond) {
            boolean isFirstOccurrence;
            boolean bl = isFirstOccurrence = maxReqsPerSecond - userReqsPerSecond == -1;
            if (isFirstOccurrence) {
                int currentFloodWarns = user.getFloodWarnings() + 1;
                user.setFloodWarnings(currentFloodWarns);
                if (this.logFloodingAttempts) {
                    this.log.warn(String.format("Flooding: %s , Request: %s, User warns: %s", new Object[]{user, reqType, currentFloodWarns}));
                }
                if (currentFloodWarns >= this.maxFloodingAttempts) {
                    this.banUserManager.banUser(user, UsersUtil.getServerModerator(), this.banDurationMinutes, this.banMode, "flooding", this.banMessage, this.secondsBeforeBan);
                }
            }
        } else {
            return;
        }
    }

    @Override
    public void setActive(boolean flag) {
        this.active = flag;
    }

    @Override
    public void addRequestFilter(SystemRequest request, int reqPerSecond) {
        this.requestTable.put(request, reqPerSecond);
    }

    @Override
    public Map getRequestTable() {
        return new HashMap(this.requestTable);
    }

    @Override
    public void clearAllFilters() {
        this.requestTable.clear();
    }

    @Override
    public boolean isRequestFiltered(SystemRequest request) {
        return this.requestTable.containsKey((Object)request);
    }

    @Override
    public int getBanDurationMinutes() {
        return this.banDurationMinutes;
    }

    @Override
    public void setBanDurationMinutes(int banDurationMinutes) {
        this.banDurationMinutes = banDurationMinutes;
    }

    @Override
    public int getMaxFloodingAttempts() {
        return this.maxFloodingAttempts;
    }

    @Override
    public void setMaxFloodingAttempts(int maxFloodingAttempts) {
        this.maxFloodingAttempts = maxFloodingAttempts;
    }

    @Override
    public int getSecondsBeforeBan() {
        return this.secondsBeforeBan;
    }

    @Override
    public void setSecondsBeforeBan(int secondsBeforeBan) {
        this.secondsBeforeBan = secondsBeforeBan;
    }

    @Override
    public boolean isLogFloodingAttempts() {
        return this.logFloodingAttempts;
    }

    @Override
    public void setLogFloodingAttempts(boolean logFloodingAttempts) {
        this.logFloodingAttempts = logFloodingAttempts;
    }

    @Override
    public BanMode getBanMode() {
        return this.banMode;
    }

    @Override
    public void setBanMode(BanMode banMode) {
        this.banMode = banMode;
    }

    @Override
    public String getBanMessage() {
        return this.banMessage;
    }

    @Override
    public void setBanMessage(String banMessage) {
        this.banMessage = banMessage;
    }
}

