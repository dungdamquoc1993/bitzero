/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.entities.managers;

import bitzero.engine.sessions.ISession;
import bitzero.server.BitZeroServer;
import bitzero.server.api.APIManager;
import bitzero.server.api.IBZApi;
import bitzero.server.core.BaseCoreService;
import bitzero.server.entities.BZBannedUser;
import bitzero.server.entities.BannedUser;
import bitzero.server.entities.User;
import bitzero.server.entities.managers.BanMode;
import bitzero.server.entities.managers.BanUserData;
import bitzero.server.entities.managers.IBannedUserManager;
import bitzero.server.entities.managers.IBannedUserStorage;
import bitzero.server.exceptions.BZRuntimeException;
import bitzero.server.exceptions.ExceptionMessageComposer;
import bitzero.server.util.ClientDisconnectionReason;
import bitzero.server.util.IDisconnectionReason;
import bitzero.server.util.TaskScheduler;
import bitzero.server.util.UsersUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BZBannedUserManager
extends BaseCoreService
implements IBannedUserManager {
    private static String STORAGE_DEFAULT_CLASS = "bitzero.server.entities.managers.BZBannedUserStorage";
    private static final int KICK_TIMER_CLEANER_TASK_INTERVAL = 12;
    private static final long KICK_TIMER_MAX_LENGTH = 86400000;
    private static final int BAN_AUTOSAVE_INTERVAL = 1;
    private static final int BAN_EXPIRER_TASK_INTERVAL = 2;
    private final Map kickHistoryByZone = new ConcurrentHashMap();
    private Map bannedUsersByName;
    private Map bannedUsersByIp;
    private BitZeroServer bz;
    private final Runnable kickHistoryCleanerTask;
    private final Runnable banExpirerTask;
    private final Runnable banAutoSaverTask;
    private final Thread shutDownHandler;
    private final Logger logger;
    private boolean autoRemoveBan;
    private boolean persistent;
    private IBannedUserStorage storage;

    public BZBannedUserManager() {
        this.kickHistoryCleanerTask = new KickHistoryCleanerTask();
        this.banExpirerTask = new BanExpirerTask();
        this.banAutoSaverTask = new BanDataAutoSaveTask();
        this.shutDownHandler = new ShutDownHandler();
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.autoRemoveBan = false;
        this.persistent = false;
    }

    @Override
    public void init(Object o) {
        super.init(o);
        try {
            Class clazz = Class.forName(STORAGE_DEFAULT_CLASS);
            this.storage = (IBannedUserStorage)clazz.newInstance();
        }
        catch (Exception e) {
            ExceptionMessageComposer composer = new ExceptionMessageComposer(e);
            composer.setDescription("the specified persistence class for the BannedUserManager cannot be found or instantiated");
            composer.setPossibleCauses("double check the fully qualified class name and make sure it implements the IBannedUserPersister interface.");
            this.logger.error(composer.toString());
        }
        this.storage.init();
        try {
            BanUserData storageData = this.storage.load();
            this.bannedUsersByIp = storageData.getBannedUsersByIp();
            this.bannedUsersByName = storageData.getBannedUsersByName();
            this.logger.info("BanUser data loaded: " + this.getTotalRecords() + " records.");
        }
        catch (Exception e) {
            this.bannedUsersByIp = new ConcurrentHashMap();
            this.bannedUsersByName = new ConcurrentHashMap();
            if (e instanceof FileNotFoundException) {
                this.logger.info("No BannedUser data available, starting with a clean DB.");
            }
            this.logger.warn("Failure loading the BannedUser DB: " + e);
        }
        this.bz = BitZeroServer.getInstance();
        this.bz.getTaskScheduler().scheduleAtFixedRate(this.kickHistoryCleanerTask, 12, 12, TimeUnit.HOURS);
        this.bz.getTaskScheduler().scheduleAtFixedRate(this.banExpirerTask, 2, 2, TimeUnit.HOURS);
        this.bz.getTaskScheduler().scheduleAtFixedRate(this.banAutoSaverTask, 1, 1, TimeUnit.HOURS);
        Runtime.getRuntime().addShutdownHook(this.shutDownHandler);
    }

    @Override
    public void destroy(Object o) {
        super.destroy(o);
        this.storage.destroy();
    }

    @Override
    public void kickUser(User userToKick, User modUser, String kickMessage, int delaySeconds) {
        this.kickUser(userToKick, modUser, kickMessage, delaySeconds, false);
    }

    @Override
    public void kickUser(User userToKick, User modUser, String kickMessage, int delaySeconds, boolean isBan) {
        if (userToKick.isBeingKicked()) {
            return;
        }
        userToKick.setBeingKicked(true);
        ClientDisconnectionReason disconnectionReason = isBan ? ClientDisconnectionReason.BAN : ClientDisconnectionReason.KICK;
        this.addUserToKickHistory(userToKick);
        userToKick.setReconnectionSeconds(0);
        if (delaySeconds <= 0) {
            userToKick.disconnect(disconnectionReason);
            return;
        }
        if (kickMessage == null || kickMessage.length() == 0) {
            kickMessage = " unknow reason ! ";
        }
        modUser = modUser != null ? modUser : UsersUtil.getServerModerator();
        this.bz.getAPIManager().getBzApi().sendModeratorMessage(modUser, kickMessage, null, Arrays.asList(userToKick.getSession()));
        this.bz.getTaskScheduler().schedule(new KickTaskRunner(userToKick, disconnectionReason), delaySeconds, TimeUnit.SECONDS);
    }

    @Override
    public void banUser(User userToBan, User modUser, int durationMinutes, BanMode mode, String reason, String banMessage, int delaySeconds) {
        modUser = modUser != null ? modUser : UsersUtil.getServerModerator();
        BZBannedUser bannedUser = new BZBannedUser(userToBan, durationMinutes, mode, reason, modUser.getName());
        this.addBannedUser(bannedUser);
        if (banMessage == null || banMessage.length() == 0) {
            banMessage = " unknow reason ! ";
        }
        this.kickUser(userToBan, modUser, banMessage, delaySeconds, true);
        String msg = String.format("User: %s is banned. Reason: %s", userToBan.getName(), reason);
        this.logger.info(msg);
    }

    @Override
    public void banUser(String userName, int durationMinutes, BanMode mode, String reason, String adminName) {
        BZBannedUser bannedUser = new BZBannedUser(userName, durationMinutes, mode, reason, adminName);
        this.addBannedUser(bannedUser);
    }

    @Override
    public void banUser(String userName, int durationMinutes, BanMode mode, String reason) {
        this.banUser(userName, durationMinutes, mode, reason, null);
    }

    private void addBannedUser(BannedUser banned) {
        BanMode mode = banned.getMode();
        String banKey = null;
        Map bannedUsersMap = null;
        if (mode == BanMode.BY_ADDRESS) {
            banKey = banned.getIpAddress();
            bannedUsersMap = this.bannedUsersByIp;
        } else {
            banKey = banned.getName();
            bannedUsersMap = this.bannedUsersByName;
        }
        bannedUsersMap.put(banKey, banned);
    }

    @Override
    public int getKickCount(String name, int rangeInSeconds) {
        List kickTimeList;
        int kickCount = 0;
        long timeRangeMillis = rangeInSeconds * 1000;
        long rangeCheck = System.currentTimeMillis() - timeRangeMillis;
        Map kickHistory = this.kickHistoryByZone;
        if (kickHistory != null && (kickTimeList = (List)kickHistory.get(name)) != null && kickTimeList.size() > 0) {
            Iterator iterator = kickTimeList.iterator();
            while (iterator.hasNext()) {
                long kickTime = (Long)iterator.next();
                if (kickTime <= rangeCheck) continue;
                ++kickCount;
            }
        }
        return kickCount;
    }

    @Override
    public boolean isIpBanned(String ipAddress) {
        boolean isBanned = false;
        BannedUser bUser = (BannedUser)this.bannedUsersByIp.get(ipAddress);
        if (bUser != null && !bUser.isExpired()) {
            isBanned = true;
        }
        return isBanned;
    }

    @Override
    public int getBanDuration(String userName, BanMode mode) {
        int duration = 0;
        BannedUser bUser = null;
        bUser = mode == BanMode.BY_ADDRESS ? (BannedUser)this.bannedUsersByIp.get(userName) : (BannedUser)this.bannedUsersByName.get(userName);
        if (bUser != null) {
            duration = bUser.getBanTime();
        }
        return duration;
    }

    @Override
    public boolean isNameBanned(String userName) {
        boolean isBanned = false;
        BannedUser bUser = (BannedUser)this.bannedUsersByName.get(userName);
        if (bUser != null && !bUser.isExpired()) {
            isBanned = true;
        }
        return isBanned;
    }

    @Override
    public void removeBannedUser(String id, BanMode mode) {
        if (mode == BanMode.BY_ADDRESS) {
            this.bannedUsersByIp.remove(id);
        } else {
            this.bannedUsersByName.remove(id);
        }
    }

    @Override
    public List getBannedUsersByIp() {
        return new ArrayList(this.bannedUsersByIp.values());
    }

    @Override
    public List getBannedUsersByName(String zoneName) {
        return new ArrayList(this.bannedUsersByName.values());
    }

    @Override
    public boolean isAutoRemoveBan() {
        return this.autoRemoveBan;
    }

    @Override
    public boolean isPersistent() {
        return this.persistent;
    }

    @Override
    public void setAutoRemoveBan(boolean flag) {
        this.autoRemoveBan = flag;
    }

    @Override
    public void setPersistent(boolean flag) {
        this.persistent = flag;
    }

    @Override
    public void setPersistenceClass(String className) {
        if (this.active) {
            throw new BZRuntimeException("Cannot change the BannedUserManager persistence class at runtime! Please change it in the configuration and restart the server.");
        }
        if (className != null && className.length() > 0) {
            STORAGE_DEFAULT_CLASS = className;
        }
    }

    @Override
    public void sendWarningMessage(User recipient, User senderMod, String message) {
        if (senderMod == null) {
            senderMod = UsersUtil.getServerModerator();
        }
        this.bz.getAPIManager().getBzApi().sendModeratorMessage(senderMod, message, null, Arrays.asList(recipient.getSession()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addUserToKickHistory(User user) {
        Map kickHistory = this.kickHistoryByZone;
        ArrayList<Long> kickTimeList = (ArrayList<Long>)kickHistory.get(user.getName());
        if (kickTimeList == null) {
            kickTimeList = new ArrayList<Long>();
            kickHistory.put(user.getName(), kickTimeList);
        }
        ArrayList<Long> arrayList = kickTimeList;
        synchronized (arrayList) {
            kickTimeList.add(System.currentTimeMillis());
        }
    }

    private int getTotalRecords() {
        int tot = this.bannedUsersByIp.size();
        return tot += this.bannedUsersByName.size();
    }

    private final class ShutDownHandler
    extends Thread {
        @Override
        public void run() {
            try {
                BZBannedUserManager.this.storage.save(new BanUserData(BZBannedUserManager.this.bannedUsersByName, BZBannedUserManager.this.bannedUsersByIp));
                BZBannedUserManager.this.logger.info("BanUser data saved.");
            }
            catch (IOException e) {
                BZBannedUserManager.this.logger.warn("Failed saving BanUserData on server quit: " + e);
            }
        }
    }

    static final class KickTaskRunner
    implements Runnable {
        private final User target;
        private final IDisconnectionReason reason;

        @Override
        public void run() {
            if (this.target != null) {
                this.target.disconnect(this.reason);
            }
        }

        public KickTaskRunner(User target, IDisconnectionReason reason) {
            this.target = target;
            this.reason = reason;
        }
    }

    private final class KickHistoryCleanerTask
    implements Runnable {
        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            if (BZBannedUserManager.this.logger.isDebugEnabled()) {
                BZBannedUserManager.this.logger.debug("KickCleanerTask running");
            }
            try {
                long timeRange = System.currentTimeMillis() - 86400000;
                Map kickHistory = BZBannedUserManager.this.kickHistoryByZone;
                Iterator iter = kickHistory.values().iterator();
                while (iter.hasNext()) {
                    List kickTimeList = (List)iter.next();
                    if (kickTimeList.size() <= 0) continue;
                    List list = kickTimeList;
                    synchronized (list) {
                        Iterator iter2 = kickTimeList.iterator();
                        while (iter2.hasNext()) {
                            long kickTime = (Long)iter2.next();
                            if (kickTime >= timeRange) continue;
                            iter2.remove();
                        }
                        if (kickTimeList.size() == 0) {
                            iter.remove();
                        }
                        continue;
                    }
                }
            }
            catch (Exception e) {
                BZBannedUserManager.this.logger.warn("Unexpected exception: " + e);
            }
        }
    }

    private final class BanExpirerTask
    implements Runnable {
        @Override
        public void run() {
            try {
                if (BZBannedUserManager.this.logger.isDebugEnabled()) {
                    BZBannedUserManager.this.logger.debug("BanExpirer running");
                }
                this.cleanExpiredBanByIP();
                this.cleanExpiredBanByName();
            }
            catch (Exception err) {
                BZBannedUserManager.this.logger.warn("Problem in BanExpirer iteration: " + err);
            }
        }

        private void cleanExpiredBanByIP() {
            int removedItems = this.expirerLoop(BZBannedUserManager.this.bannedUsersByIp);
            if (removedItems > 0) {
                BZBannedUserManager.this.logger.debug("Removed " + removedItems + " expired banned users by IP");
            }
        }

        private void cleanExpiredBanByName() {
            int removedItems = this.expirerLoop(BZBannedUserManager.this.bannedUsersByName);
            if (removedItems > 0) {
                BZBannedUserManager.this.logger.debug("Removed " + removedItems + " expired banned users by name ");
            }
        }

        private int expirerLoop(Map<String,BannedUser> data) {
            int count = 0;
            for (Map.Entry entry : data.entrySet()) {
                BannedUser bUser = (BannedUser)entry.getValue();
                if (!bUser.isExpired()) continue;
                data.remove(entry.getKey());
                ++count;
            }
            return count;
        }
    }

    private final class BanDataAutoSaveTask
    implements Runnable {
        @Override
        public void run() {
            try {
                long t1 = System.currentTimeMillis();
                BZBannedUserManager.this.storage.save(new BanUserData(BZBannedUserManager.this.bannedUsersByName, BZBannedUserManager.this.bannedUsersByIp));
                if (BZBannedUserManager.this.logger.isDebugEnabled()) {
                    BZBannedUserManager.this.logger.debug("Ban User data autosave done in " + (System.currentTimeMillis() - t1) + "ms.");
                }
            }
            catch (Exception e) {
                BZBannedUserManager.this.logger.warn("Banned User Data auto-save failed: " + e);
            }
        }
    }

}

