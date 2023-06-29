/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.entities.managers;

import bitzero.engine.sessions.ISession;
import bitzero.engine.sessions.ISessionManager;
import bitzero.server.BitZeroServer;
import bitzero.server.api.APIManager;
import bitzero.server.api.IBZApi;
import bitzero.server.core.BaseCoreService;
import bitzero.server.entities.Room;
import bitzero.server.entities.User;
import bitzero.server.entities.Zone;
import bitzero.server.entities.managers.IUserManager;
import bitzero.server.exceptions.BZRuntimeException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BZUserManager
extends BaseCoreService
implements IUserManager {
    private final ConcurrentMap usersByName = new ConcurrentHashMap();
    private final ConcurrentMap usersBySession = new ConcurrentHashMap();
    private final ConcurrentMap usersById = new ConcurrentHashMap();
    private Room ownerRoom;
    private Zone ownerZone;
    private Logger logger;
    private int highestCCU = 0;

    public BZUserManager() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public void addUser(User user) {
        if (this.containsId(user.getId())) {
            throw new BZRuntimeException("Can't add User: " + user.getName() + " - Already exists in Room: " + this.ownerRoom + ", Zone: " + this.ownerZone);
        }
        this.usersById.put(user.getId(), user);
        this.usersByName.put(user.getName(), user);
        this.usersBySession.put(user.getSession(), user);
        if (this.usersById.size() > this.highestCCU) {
            this.highestCCU = this.usersById.size();
        }
    }

    @Override
    public User getUserById(int id) {
        return (User)this.usersById.get(id);
    }

    @Override
    public User getUserByName(String name) {
        return (User)this.usersByName.get(name);
    }

    @Override
    public User getUserBySession(ISession session) {
        return (User)this.usersBySession.get(session);
    }

    @Override
    public void removeUser(int userId) {
        User user = (User)this.usersById.get(userId);
        if (user == null) {
            this.logger.warn("Can't remove user with ID: " + userId + ". User was not found.");
        } else {
            this.removeUser(user);
        }
    }

    @Override
    public void removeUser(String name) {
        User user = (User)this.usersByName.get(name);
        if (user == null) {
            this.logger.warn("Can't remove user with name: " + name + ". User was not found.");
        } else {
            this.removeUser(user);
        }
    }

    @Override
    public void removeUser(ISession session) {
        User user = (User)this.usersBySession.get(session);
        if (user == null) {
            throw new BZRuntimeException("Can't remove user with session: " + session + ". User was not found.");
        }
        this.removeUser(user);
    }

    @Override
    public void removeUser(User user) {
        this.usersById.remove(user.getId());
        this.usersByName.remove(user.getName());
        this.usersBySession.remove(user.getSession());
    }

    @Override
    public boolean containsId(int userId) {
        return this.usersById.containsKey(userId);
    }

    @Override
    public boolean containsName(String name) {
        return this.usersByName.containsKey(name);
    }

    @Override
    public boolean containsSessions(ISession session) {
        return this.usersBySession.containsKey(session);
    }

    @Override
    public boolean containsUser(User user) {
        return this.usersById.containsKey(user.getId());
    }

    @Override
    public Room getOwnerRoom() {
        return this.ownerRoom;
    }

    @Override
    public void setOwnerRoom(Room ownerRoom) {
        this.ownerRoom = ownerRoom;
    }

    @Override
    public Zone getOwnerZone() {
        return this.ownerZone;
    }

    @Override
    public void setOwnerZone(Zone ownerZone) {
        this.ownerZone = ownerZone;
    }

    public List getAllUsers() {
        return new ArrayList(this.usersById.values());
    }

    public List getAllSessions() {
        return new ArrayList(this.usersBySession.keySet());
    }

    @Override
    public int getUserCount() {
        return this.usersById.values().size();
    }

    @Override
    public int getHighestCCU() {
        return this.highestCCU;
    }

    @Override
    public void disconnectUser(int userId) {
        User user = (User)this.usersById.get(userId);
        if (user == null) {
            this.logger.warn("Can't disconnect user with id: " + userId + ". User was not found.");
        } else {
            this.disconnectUser(user);
        }
    }

    @Override
    public void disconnectUser(ISession session) {
        User user = (User)this.usersBySession.get(session);
        if (user == null) {
            this.logger.warn("Can't disconnect user with session: " + session + ". User was not found.");
        } else {
            this.disconnectUser(user);
        }
    }

    @Override
    public void disconnectUser(String name) {
        User user = (User)this.usersByName.get(name);
        if (user == null) {
            this.logger.warn("Can't disconnect user with name: " + name + ". User was not found.");
        } else {
            this.disconnectUser(user);
        }
    }

    @Override
    public void disconnectUser(User user) {
        this.removeUser(user);
    }

    public void purgeOrphanedUsers() {
        ISessionManager mgr = BitZeroServer.getInstance().getSessionManager();
        IBZApi api = BitZeroServer.getInstance().getAPIManager().getBzApi();
        int tot = 0;
        for (ISession session : (List<ISession>)this.usersBySession.keySet()) {
            if (mgr.containsSession(session)) continue;
            User evictable = (User)this.usersBySession.get(session);
            api.disconnectUser(evictable);
            ++tot;
        }
        this.logger.info("Evicted " + tot + " users.");
    }

    private String getOwnerDetails() {
        StringBuilder sb = new StringBuilder();
        if (this.ownerZone != null) {
            sb.append("Zone: ").append(this.ownerZone.getName());
        } else if (this.ownerRoom != null) {
            sb.append("Zone: ").append(this.ownerRoom.getZone().getName()).append("Room: ").append(this.ownerRoom.getName()).append(", Room Id: ").append(this.ownerRoom.getId());
        }
        return sb.toString();
    }

    private void populateTransientFields() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }
}

