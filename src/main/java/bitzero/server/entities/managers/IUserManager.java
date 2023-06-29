/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.entities.managers;

import bitzero.engine.sessions.ISession;
import bitzero.server.core.ICoreService;
import bitzero.server.entities.Room;
import bitzero.server.entities.User;
import bitzero.server.entities.Zone;
import java.util.List;

public interface IUserManager
extends ICoreService {
    public User getUserByName(String var1);

    public User getUserById(int var1);

    public User getUserBySession(ISession var1);

    public List<User> getAllUsers();

    public List<ISession> getAllSessions();

    public void addUser(User var1);

    public void removeUser(User var1);

    public void removeUser(String var1);

    public void removeUser(int var1);

    public void removeUser(ISession var1);

    public void disconnectUser(User var1);

    public void disconnectUser(String var1);

    public void disconnectUser(int var1);

    public void disconnectUser(ISession var1);

    public boolean containsId(int var1);

    public boolean containsName(String var1);

    public boolean containsSessions(ISession var1);

    public boolean containsUser(User var1);

    public Zone getOwnerZone();

    public void setOwnerZone(Zone var1);

    public Room getOwnerRoom();

    public void setOwnerRoom(Room var1);

    public int getUserCount();

    public int getHighestCCU();
}

