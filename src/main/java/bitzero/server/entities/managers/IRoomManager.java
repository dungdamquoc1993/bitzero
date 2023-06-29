/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.entities.managers;

import bitzero.server.api.CreateRoomSettings;
import bitzero.server.core.ICoreService;
import bitzero.server.entities.Room;
import bitzero.server.entities.User;
import bitzero.server.entities.Zone;
import bitzero.server.exceptions.BZCreateRoomException;
import bitzero.server.exceptions.BZRoomException;
import java.util.List;

public interface IRoomManager
extends ICoreService {
    public void addRoom(Room var1);

    public Room createRoom(CreateRoomSettings var1) throws BZCreateRoomException;

    public Room createRoom(CreateRoomSettings var1, User var2) throws BZCreateRoomException;

    public List getGroups();

    public void addGroup(String var1);

    public void removeGroup(String var1);

    public boolean containsGroup(String var1);

    public boolean containsRoom(int var1);

    public boolean containsRoom(String var1);

    public boolean containsRoom(Room var1);

    public boolean containsRoom(int var1, String var2);

    public boolean containsRoom(String var1, String var2);

    public boolean containsRoom(Room var1, String var2);

    public Room getRoomById(int var1);

    public Room getRoomByName(String var1);

    public List<Room> getRoomList();

    public List<Room> getRoomListFromGroup(String var1);

    public void checkAndRemove(Room var1);

    public void removeRoom(Room var1);

    public void removeRoom(int var1);

    public void removeRoom(String var1);

    public Zone getOwnerZone();

    public void setOwnerZone(Zone var1);

    public void removeUser(User var1);

    public void removeUser(User var1, Room var2);

    public void changeRoomName(Room var1, String var2) throws BZRoomException;

    public void changeRoomPasswordState(Room var1, String var2);

    public void changeRoomCapacity(Room var1, int var2, int var3);

    public void setDefaultRoomPlayerIdGeneratorClass(Class var1);
}

