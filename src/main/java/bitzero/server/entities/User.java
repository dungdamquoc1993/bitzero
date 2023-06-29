/*
 * Decompiled with CFR 0_116.
 *
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.entities;

import bitzero.engine.sessions.ISession;
import bitzero.engine.sessions.SessionType;
import bitzero.server.BitZeroServer;
import bitzero.server.api.APIManager;
import bitzero.server.api.IBZApi;
import bitzero.server.entities.Room;
import bitzero.server.entities.Zone;
import bitzero.server.util.IDisconnectionReason;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class User {
    private static AtomicInteger autoID = new AtomicInteger(0);
    private int id = User.getNewID();
    private ISession session;
    private String name;
    private short privilegeId = 0;
    private volatile long lastLoginTime = 0;
    private volatile long lastJoinRoomTime;
    private volatile Room joinedRoom;
    private volatile int playerIdByRoomId;
    private final ConcurrentMap properties;
    private final ConcurrentMap variables;
    private volatile int badWordsWarnings = 0;
    private volatile int floodWarnings = 0;
    private volatile boolean beingKicked = false;
    private volatile boolean connected = false;
    private boolean joining = false;
    private boolean isBot = false;
    private volatile Zone currentZone;
    private Logger logger;
    private int idRoom;

    private static int getNewID() {
        return autoID.getAndIncrement();
    }

    public User(ISession session) {
        this("", session);
    }

    public User(String name, ISession session) {
        this.name = name;
        this.session = session;
        this.beingKicked = false;
        this.joinedRoom = null;
        this.properties = new ConcurrentHashMap();
        this.playerIdByRoomId = 0;
        this.variables = new ConcurrentHashMap();
        this.updateLastRequestTime();
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    public int getUniqueId() {
        return this.id;
    }

    public boolean isBot() {
        return this.isBot;
    }

    public void setIsBot(boolean isBot) {
        this.isBot = isBot;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(int idRoom) {
        this.idRoom = idRoom;
    }

    public short getPrivilegeId() {
        return this.privilegeId;
    }

    public void setPrivilegeId(short id) {
        this.privilegeId = id;
    }

    public boolean isMobile() {
        if (this.session == null) {
            return false;
        }
        return this.session.isMobile();
    }

    public boolean isSuperUser() {
        return false;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public boolean isLocal() {
        if (this.session == null) {
            return false;
        }
        return this.session.isLocal();
    }

    public synchronized void setConnected(boolean flag) {
        this.connected = flag;
    }

    public synchronized boolean isJoining() {
        return this.joining;
    }

    public synchronized void setJoining(boolean flag) {
        this.joining = flag;
    }

    public String getIpAddress() {
        if (this.session == null) {
            return "127.0.0.1";
        }
        return this.session.getAddress();
    }

    public synchronized void addJoinedRoom(Room room) {
        this.joinedRoom = room;
    }

    public synchronized void removeJoinedRoom(Room room) {
        this.joinedRoom = null;
        this.playerIdByRoomId = 0;
    }

    public void disconnect(IDisconnectionReason reason) {
        BitZeroServer.getInstance().getAPIManager().getBzApi().disconnectUser(this, reason);
    }

    public boolean isNpc() {
        if (this.session == null) {
            return false;
        }
        return this.session.getType() == SessionType.VOID;
    }

    public synchronized Room getJoinedRoom() {
        return this.joinedRoom;
    }

    public Zone getZone() {
        return this.currentZone;
    }

    public void setZone(Zone currentZone) {
        this.currentZone = currentZone;
    }

    public long getLoginTime() {
        return this.lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public long getLastJoinRoomTime() {
        return this.lastJoinRoomTime;
    }

    public void setLastJoinRoomTime(long lastJoinRoomTime) {
        this.lastJoinRoomTime = lastJoinRoomTime;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public synchronized int getPlayerId() {
        return this.playerIdByRoomId;
    }

    public synchronized void setPlayerId(int id, Room room) {
        this.playerIdByRoomId = id;
        this.joinedRoom = room;
    }

    public boolean isPlayer() {
        return this.joinedRoom != null && this.playerIdByRoomId > 0;
    }

    public boolean isSpectator() {
        return this.joinedRoom != null && this.playerIdByRoomId < 0;
    }

    public Object getProperty(Object key) {
        return this.properties.get(key);
    }

    public void setProperty(Object key, Object val) {
        this.properties.put(key, val);
    }

    public boolean containsProperty(Object key) {
        return this.properties.containsKey(key);
    }

    public void removeProperty(Object key) {
        this.properties.remove(key);
    }

    public ISession getSession() {
        return this.session;
    }

    public String toString() {
        String s = this.session == null ? "127.0.0.1" : this.session.getFullIpAddress();
        String test_session  = (this.session == null) + "";
        return String.format("( User Name: %s, Id: %s, Priv: %s, Sess: %s ) ", this.name, this.id, this.privilegeId, s +"   test_session " + test_session);
    }

    public long getLastRequestTime() {
        if (this.session == null) {
            return 0;
        }
        return this.session.getLastLoggedInActivityTime();
    }

    public synchronized void updateLastRequestTime() {
        this.setLastRequestTime(System.currentTimeMillis());
    }

    public void setLastRequestTime(long lastRequestTime) {
        if (this.session != null) {
            this.session.setLastLoggedInActivityTime(lastRequestTime);
        }
    }

    public int getBadWordsWarnings() {
        return this.badWordsWarnings;
    }

    public void setBadWordsWarnings(int badWordsWarnings) {
        this.badWordsWarnings = badWordsWarnings;
    }

    public int getFloodWarnings() {
        return this.floodWarnings;
    }

    public void setFloodWarnings(int floodWarnings) {
        this.floodWarnings = floodWarnings;
    }

    public long getLastLoginTime() {
        return this.lastLoginTime;
    }

    public boolean isBeingKicked() {
        return this.beingKicked;
    }

    public void setBeingKicked(boolean flag) {
        this.beingKicked = flag;
    }

    public int getReconnectionSeconds() {
        if (this.session == null) {
            return 0;
        }
        return this.session.getReconnectionSeconds();
    }

    public void setReconnectionSeconds(int seconds) {
        if (this.session != null) {
            this.session.setReconnectionSeconds(seconds);
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof User)) {
            return false;
        }
        User user = (User)obj;
        boolean isEqual = false;
        if (user.getId() == this.id) {
            isEqual = true;
        }
        return isEqual;
    }

    public String getDump() {
        throw new UnsupportedOperationException("Sorry, not implemented yet!");
    }

    private void populateTransientFields() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }
}

