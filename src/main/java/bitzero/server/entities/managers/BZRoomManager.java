/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.entities.managers;

import bitzero.server.BitZeroServer;
import bitzero.server.api.APIManager;
import bitzero.server.api.CreateRoomSettings;
import bitzero.server.api.IBZApi;
import bitzero.server.core.BaseCoreService;
import bitzero.server.entities.BZRoomRemoveMode;
import bitzero.server.entities.Room;
import bitzero.server.entities.User;
import bitzero.server.entities.Zone;
import bitzero.server.entities.managers.IRoomManager;
import bitzero.server.exceptions.BZCreateRoomException;
import bitzero.server.exceptions.BZErrorCode;
import bitzero.server.exceptions.BZErrorData;
import bitzero.server.exceptions.BZRoomException;
import bitzero.server.exceptions.BZRuntimeException;
import bitzero.server.exceptions.IErrorCode;
import bitzero.server.util.DefaultPlayerIdGenerator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BZRoomManager
extends BaseCoreService
implements IRoomManager {
    private final Map roomsById = new ConcurrentHashMap();
    private final Map roomsByName = new ConcurrentHashMap();
    private final Map roomsByGroup = new ConcurrentHashMap();
    private final List groups = new ArrayList();
    private Logger logger;
    private BitZeroServer bz = BitZeroServer.getInstance();
    private Zone ownerZone;
    private Class playerIdGeneratorClass = DefaultPlayerIdGenerator.class;
    private static int[] $SWITCH_TABLE$com$BitZeroServer$v2$entities$BZRoomRemoveMode;

    public BZRoomManager() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public Room createRoom(CreateRoomSettings params) throws BZCreateRoomException {
        return this.createRoom(params, null);
    }

    @Override
    public Room createRoom(CreateRoomSettings params, User owner) throws BZCreateRoomException {
        String roomName = params.getName();
        try {
            this.validateRoomName(roomName);
        }
        catch (BZRoomException roomExc) {
            throw new BZCreateRoomException(roomExc.getMessage(), roomExc.getErrorData());
        }
        Room newRoom = new Room(roomName);
        newRoom.setZone(this.ownerZone);
        newRoom.setGroupId(params.getGroupId());
        newRoom.setPassword(params.getPassword());
        newRoom.setDynamic(params.isDynamic());
        newRoom.setHidden(params.isHidden());
        newRoom.setMaxUsers(params.getMaxUsers());
        if (params.isGame()) {
            newRoom.setMaxSpectators(params.getMaxSpectators());
        } else {
            newRoom.setMaxSpectators(0);
        }
        newRoom.setGame(params.isGame(), params.getCustomPlayerIdGeneratorClass() != null ? params.getCustomPlayerIdGeneratorClass() : this.playerIdGeneratorClass);
        newRoom.setOwner(owner);
        newRoom.setAutoRemoveMode(params.getAutoRemoveMode());
        if (this.roomsById.size() >= this.ownerZone.getMaxAllowedRooms()) {
            BZErrorData errorData = new BZErrorData(BZErrorCode.CREATE_ROOM_ZONE_FULL);
            throw new BZCreateRoomException("Zone is full. Can't add any more rooms.", errorData);
        }
        this.addRoom(newRoom);
        newRoom.setActive(true);
        return newRoom;
    }

    public Class getDefaultRoomPlayerIdGenerator() {
        return this.playerIdGeneratorClass;
    }

    @Override
    public void setDefaultRoomPlayerIdGeneratorClass(Class customIdGeneratorClass) {
        this.playerIdGeneratorClass = customIdGeneratorClass;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addGroup(String groupId) {
        List list = this.groups;
        synchronized (list) {
            this.groups.add(groupId);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addRoom(Room room) {
        this.roomsById.put(room.getId(), room);
        this.roomsByName.put(room.getName(), room);
        List list = this.groups;
        synchronized (list) {
            if (!this.groups.contains(room.getGroupId())) {
                this.groups.add(room.getGroupId());
            }
        }
        this.addRoomToGroup(room);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsGroup(String groupId) {
        boolean flag = false;
        List list = this.groups;
        synchronized (list) {
            flag = this.groups.contains(groupId);
        }
        return flag;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List getGroups() {
        ArrayList groupsCopy = null;
        List list = this.groups;
        synchronized (list) {
            groupsCopy = new ArrayList(this.groups);
        }
        return groupsCopy;
    }

    @Override
    public Room getRoomById(int id) {
        return (Room)this.roomsById.get(id);
    }

    @Override
    public Room getRoomByName(String name) {
        return (Room)this.roomsByName.get(name);
    }

    public List getRoomList() {
        return new ArrayList(this.roomsById.values());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List getRoomListFromGroup(String groupId) {
        List roomList = (List)this.roomsByGroup.get(groupId);
        ArrayList copyOfRoomList = null;
        if (roomList != null) {
            List list = roomList;
            synchronized (list) {
                copyOfRoomList = new ArrayList(roomList);
            }
        } else {
            copyOfRoomList = new ArrayList();
        }
        return copyOfRoomList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeGroup(String groupId) {
        List list = this.groups;
        synchronized (list) {
            this.groups.remove(groupId);
        }
    }

    @Override
    public void removeRoom(int roomId) {
        Room room = (Room)this.roomsById.get(roomId);
        if (room == null) {
            this.logger.warn("Can't remove requested room. ID = " + roomId + ". Room was not found.");
        } else {
            this.removeRoom(room);
        }
    }

    @Override
    public void removeRoom(String name) {
        Room room = (Room)this.roomsByName.get(name);
        if (room == null) {
            this.logger.warn("Can't remove requested room. Name = " + name + ". Room was not found.");
        } else {
            this.removeRoom(room);
        }
    }

    @Override
    public void removeRoom(Room room) {
        room.setActive(false);
        this.roomsById.remove(room.getId());
        this.roomsByName.remove(room.getName());
        this.removeRoomFromGroup(room);
        this.logger.info("Removed: " + room);
    }

    @Override
    public boolean containsRoom(int id, String groupId) {
        Room room = (Room)this.roomsById.get(id);
        return this.isRoomContainedInGroup(room, groupId);
    }

    @Override
    public boolean containsRoom(int id) {
        return this.roomsById.containsKey(id);
    }

    @Override
    public boolean containsRoom(Room room, String groupId) {
        return this.isRoomContainedInGroup(room, groupId);
    }

    @Override
    public boolean containsRoom(Room room) {
        return this.roomsById.containsValue(room);
    }

    @Override
    public boolean containsRoom(String name, String groupId) {
        Room room = (Room)this.roomsByName.get(name);
        return this.isRoomContainedInGroup(room, groupId);
    }

    @Override
    public boolean containsRoom(String name) {
        return this.roomsByName.containsKey(name);
    }

    public boolean containsRoomId(int id) {
        return this.roomsById.containsKey(id);
    }

    @Override
    public Zone getOwnerZone() {
        return this.ownerZone;
    }

    @Override
    public void setOwnerZone(Zone zone) {
        this.ownerZone = zone;
    }

    @Override
    public void removeUser(User user) {
        if (user.getJoinedRoom() != null) {
            user.getJoinedRoom().removeUser(user);
        }
    }

    @Override
    public void removeUser(User user, Room room) {
        if (!room.containsUser(user)) {
            throw new BZRuntimeException("Can't remove user: " + user + ", from: " + room);
        }
        room.removeUser(user);
        this.logger.debug("User: " + user.getName() + " removed from Room: " + room.getName());
        this.handleAutoRemove(room);
    }

    @Override
    public void checkAndRemove(Room room) {
        this.handleAutoRemove(room);
    }

    @Override
    public void changeRoomName(Room room, String newName) throws BZRoomException {
        if (room == null) {
            throw new IllegalArgumentException("Can't change name. Room is Null!");
        }
        if (!this.containsRoom(room)) {
            throw new IllegalArgumentException(room + " is not managed by this Zone: " + this.ownerZone);
        }
        this.validateRoomName(newName);
        String oldName = room.getName();
        room.setName(newName);
        this.roomsByName.put(newName, room);
        this.roomsByName.remove(oldName);
    }

    @Override
    public void changeRoomPasswordState(Room room, String password) {
        if (room == null) {
            throw new IllegalArgumentException("Can't change password. Room is Null!");
        }
        if (!this.containsRoom(room)) {
            throw new IllegalArgumentException(room + " is not managed by this Zone: " + this.ownerZone);
        }
        room.setPassword(password);
    }

    @Override
    public void changeRoomCapacity(Room room, int newMaxUsers, int newMaxSpect) {
        if (room == null) {
            throw new IllegalArgumentException("Can't change password. Room is Null!");
        }
        if (!this.containsRoom(room)) {
            throw new IllegalArgumentException(room + " is not managed by this Zone: " + this.ownerZone);
        }
        if (newMaxUsers > 0) {
            room.setMaxUsers(newMaxUsers);
        }
        if (newMaxSpect >= 0) {
            room.setMaxSpectators(newMaxSpect);
        }
    }

    private void handleAutoRemove(Room room) {
        if (room.isEmpty() && room.isDynamic()) {
            switch (room.getAutoRemoveMode().ordinal() + 1) {
                default: {
                    break;
                }
                case 1: {
                    if (room.isGame()) {
                        this.removeWhenEmpty(room);
                        break;
                    }
                    this.removeWhenEmptyAndCreatorIsGone(room);
                    break;
                }
                case 2: {
                    this.removeWhenEmpty(room);
                    break;
                }
                case 3: {
                    this.removeWhenEmptyAndCreatorIsGone(room);
                }
            }
        }
    }

    private void removeWhenEmpty(Room room) {
        if (room.isEmpty()) {
            this.bz.getAPIManager().getBzApi().removeRoom(room);
        }
    }

    private void removeWhenEmptyAndCreatorIsGone(Room room) {
        User owner = room.getOwner();
        if (owner != null && !owner.isConnected()) {
            this.bz.getAPIManager().getBzApi().removeRoom(room);
        }
    }

    private boolean isRoomContainedInGroup(Room room, String groupId) {
        boolean flag = false;
        if (room != null && room.getGroupId().equals(groupId) && this.containsGroup(groupId)) {
            flag = true;
        }
        return flag;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addRoomToGroup(Room room) {
        String groupId = room.getGroupId();
        ArrayList<Room> roomList = (ArrayList<Room>)this.roomsByGroup.get(groupId);
        if (roomList == null) {
            roomList = new ArrayList<Room>();
            this.roomsByGroup.put(groupId, roomList);
        }
        ArrayList<Room> arrayList = roomList;
        synchronized (arrayList) {
            roomList.add(room);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void removeRoomFromGroup(Room room) {
        List roomList = (List)this.roomsByGroup.get(room.getGroupId());
        if (roomList != null) {
            List list = roomList;
            synchronized (list) {
                roomList.remove(room);
            }
        } else {
            this.logger.info("Cannot remove room: " + room.getName() + " from it's group: " + room.getGroupId() + ". The group was not found.");
        }
    }

    private void validateRoomName(String roomName) throws BZRoomException {
        if (roomName.length() > 0 && roomName.length() < 100) {
            return;
        }
    }

    private void populateTransientFields() {
        this.bz = BitZeroServer.getInstance();
        this.logger = LoggerFactory.getLogger(this.getClass());
    }
}

