/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.entities;

import bitzero.engine.sessions.ISession;
import bitzero.server.BitZeroServer;
import bitzero.server.api.CreateRoomSettings;
import bitzero.server.entities.Room;
import bitzero.server.entities.User;
import bitzero.server.entities.managers.BZRoomManager;
import bitzero.server.entities.managers.BZUserManager;
import bitzero.server.entities.managers.IRoomManager;
import bitzero.server.entities.managers.IUserManager;
import bitzero.server.entities.managers.IZoneManager;
import bitzero.server.exceptions.BZCreateRoomException;
import bitzero.server.exceptions.BZErrorCode;
import bitzero.server.exceptions.BZErrorData;
import bitzero.server.exceptions.BZException;
import bitzero.server.exceptions.BZLoginException;
import bitzero.server.exceptions.BZRoomException;
import bitzero.server.exceptions.BZRuntimeException;
import bitzero.server.exceptions.BZTooManyRoomsException;
import bitzero.server.exceptions.IErrorCode;
import bitzero.server.extensions.IBZExtension;
import bitzero.server.security.PrivilegeManager;
import bitzero.server.util.DefaultPlayerIdGenerator;
import bitzero.server.util.IFloodFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Zone {
    private int id;
    private List disabledSystemEvents;
    private List publicGroups;
    private List defaultGroups;
    private IZoneManager zoneManager;
    private final IRoomManager roomManager = new BZRoomManager();
    private final IUserManager userManager = new BZUserManager();
    private IFloodFilter floodFilter;
    private PrivilegeManager privilegeManager;
    private volatile IBZExtension extension;
    private volatile boolean isActive = false;
    private boolean customLogin = false;
    private volatile int maxAllowedRooms;
    private volatile int maxAllowedUsers;
    private volatile int maxRoomsCreatedPerUser;
    private volatile int userCountChangeUpdateInterval = 0;
    private volatile int minRoomNameChars;
    private volatile int maxRoomNameChars;
    private volatile int userReconnectionSeconds = 0;
    private int maxUserIdleTime = 0;
    private final String name;
    private String defaultPlayerIdGeneratorClass;
    private ConcurrentMap properties;
    private Logger logger;
    private BitZeroServer bz;

    public Zone(String name, int id) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.bz = BitZeroServer.getInstance();
        this.roomManager.setOwnerZone(this);
        this.userManager.setOwnerZone(this);
        this.publicGroups = new ArrayList();
        this.properties = new ConcurrentHashMap();
        this.roomManager.addGroup("default");
        this.publicGroups.add("default");
        this.id = id;
    }

    public IUserManager getUserManager() {
        return this.userManager;
    }

    public int getId() {
        return this.id;
    }

    public boolean containsGroup(String groupId) {
        return this.roomManager.containsGroup(groupId);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean containsPublicGroup(String groupId) {
        boolean flag = false;
        List list = this.publicGroups;
        synchronized (list) {
            flag = this.publicGroups.contains(groupId);
        }
        return flag;
    }

    public Room createRoom(CreateRoomSettings params, User user) throws BZCreateRoomException {
        return this.roomManager.createRoom(params, user);
    }

    public Room createRoom(CreateRoomSettings params) throws BZCreateRoomException {
        return this.roomManager.createRoom(params);
    }

    public void changeRoomName(Room room, String newName) throws BZRoomException {
        this.roomManager.changeRoomName(room, newName);
    }

    public void changeRoomPasswordState(Room room, String password) {
        this.roomManager.changeRoomPasswordState(room, password);
    }

    public void changeRoomCapacity(Room room, int newMaxUsers, int newMaxSpect) {
        this.roomManager.changeRoomCapacity(room, newMaxUsers, newMaxSpect);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addDisabledSystemEvent(String eventID) {
        List list = this.disabledSystemEvents;
        synchronized (list) {
            this.disabledSystemEvents.add(eventID);
        }
    }

    public void addRoom(Room room) throws BZTooManyRoomsException {
        this.roomManager.addRoom(room);
    }

    public int getUserCount() {
        return this.userManager.getUserCount();
    }

    public int getMaxAllowedRooms() {
        return this.maxAllowedRooms;
    }

    public int getMaxAllowedUsers() {
        return this.maxAllowedUsers;
    }

    public int getMaxRoomsCreatedPerUserLimit() {
        return this.maxRoomsCreatedPerUser;
    }

    public String getName() {
        return this.name;
    }

    public Object getProperty(Object key) {
        return this.properties.get(key);
    }

    public void removeProperty(Object key) {
        this.properties.remove(key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List getPublicGroups() {
        ArrayList newList = null;
        List list = this.publicGroups;
        synchronized (list) {
            newList = new ArrayList(this.publicGroups);
        }
        return newList;
    }

    public List getGroups() {
        return this.roomManager.getGroups();
    }

    public List getDefaultGroups() {
        return new ArrayList(this.defaultGroups);
    }

    public Room getRoomById(int id) {
        return this.roomManager.getRoomById(id);
    }

    public Room getRoomByName(String name) {
        return this.roomManager.getRoomByName(name);
    }

    public List getRoomList() {
        return this.roomManager.getRoomList();
    }

    public List getRoomListFromGroup(String groupId) {
        return this.roomManager.getRoomListFromGroup(groupId);
    }

    public User getUserById(int id) {
        return this.userManager.getUserById(id);
    }

    public User getUserByName(String name) {
        return this.userManager.getUserByName(name);
    }

    public User getUserBySession(ISession session) {
        return this.userManager.getUserBySession(session);
    }

    public int getUserCountChangeUpdateInterval() {
        return this.userCountChangeUpdateInterval;
    }

    public String getDefaultPlayerIdGeneratorClassName() {
        return this.defaultPlayerIdGeneratorClass;
    }

    public void setDefaultPlayerIdGeneratorClassName(String className) {
        if (className == null || className.length() == 0) {
            className = "bitzero.server.util.DefaultPlayerIdGenerator";
        }
        this.defaultPlayerIdGeneratorClass = className;
        Class playerGeneratorClass = DefaultPlayerIdGenerator.class;
        try {
            playerGeneratorClass = Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            this.logger.warn("Was not able to instantiate PlayerIdGenerator Class: " + className + ", class is not found. Reverting to default implementation: " + playerGeneratorClass);
        }
        this.roomManager.setDefaultRoomPlayerIdGeneratorClass(playerGeneratorClass);
    }

    public IBZExtension getExtension() {
        return this.extension;
    }

    public void setExtension(IBZExtension extension) {
        this.extension = extension;
    }

    public int getUserReconnectionSeconds() {
        return this.userReconnectionSeconds;
    }

    public void setUserReconnectionSeconds(int seconds) {
        this.userReconnectionSeconds = seconds;
    }

    public int getMaxUserIdleTime() {
        return this.maxUserIdleTime;
    }

    public void setMaxUserIdleTime(int seconds) {
        this.maxUserIdleTime = seconds;
    }

    public Collection getUsersInGroup(String groupId) {
        HashSet<User> userList = new HashSet<User>();
        for (Room room : this.roomManager.getRoomListFromGroup(groupId)) {
            userList.addAll(room.getUserList());
        }
        return userList;
    }

    public Collection getSessionsInGroup(String groupId) {
        HashSet<ISession> sessionList = new HashSet<ISession>();
        for (Room room : this.roomManager.getRoomListFromGroup(groupId)) {
            sessionList.addAll(room.getSessionList());
        }
        return sessionList;
    }

    public List<ISession> getSessionList() {
        return this.userManager.getAllSessions();
    }

    public List<User> getUserList() {
        return this.userManager.getAllUsers();
    }

    public IZoneManager getZoneManager() {
        return this.zoneManager;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public boolean isCustomLogin() {
        return this.customLogin;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isSystemEventAllowed(String eventID) {
        boolean flag = false;
        List list = this.disabledSystemEvents;
        synchronized (list) {
            flag = !this.disabledSystemEvents.contains(eventID);
        }
        return flag;
    }

    public boolean containsProperty(Object key) {
        return this.properties.containsKey(key);
    }

    public void removeRoom(int roomId) {
        this.roomManager.removeRoom(roomId);
    }

    public void removeRoom(String name) {
        this.roomManager.removeRoom(name);
    }

    public void removeRoom(Room room) {
        this.roomManager.removeRoom(room);
    }

    public void checkAndRemove(Room room) {
        this.roomManager.checkAndRemove(room);
    }

    public void removeUserFromRoom(User user, Room room) {
        this.roomManager.removeUser(user, room);
    }

    public int getMinRoomNameChars() {
        return this.minRoomNameChars;
    }

    public void setMinRoomNameChars(int minRoomNameChars) {
        this.minRoomNameChars = minRoomNameChars;
    }

    public int getMaxRoomNameChars() {
        return this.maxRoomNameChars;
    }

    public void setMaxRoomNameChars(int maxRoomNameChars) {
        this.maxRoomNameChars = maxRoomNameChars;
    }

    public void setActive(boolean flag) {
        if (!flag && this.isActive) {
            this.removeAllUsers();
        }
        this.isActive = flag;
    }

    public void setCustomLogin(boolean flag) {
        this.customLogin = flag;
    }

    public void setMaxAllowedRooms(int max) {
        if (max < 0) {
            throw new BZRuntimeException("Negative values are not acceptable for Zone.maxAllowedRooms: " + max);
        }
        this.maxAllowedRooms = max;
    }

    public void setMaxAllowedUsers(int max) {
        if (max < 0) {
            throw new BZRuntimeException("Negative values are not acceptable for Zone.maxAllowedUsers: " + max);
        }
        this.maxAllowedUsers = max;
    }

    public void setMaxRoomsCreatedPerUserLimit(int max) {
        if (max < 0) {
            throw new BZRuntimeException("Negative values are not acceptable for Zone.maxRoomsCreatedPerUser: " + max);
        }
        this.maxRoomsCreatedPerUser = max;
    }

    public void setProperty(Object key, Object value) {
        this.properties.put(key, value);
    }

    public void setPublicGroups(List groupIDs) {
        this.publicGroups = groupIDs;
    }

    public void setDefaultGroups(List groupIDs) {
        this.defaultGroups = groupIDs;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setUserCountChangeUpdateInterval(int interval) {
        if (interval < 0) {
            throw new BZRuntimeException("Negative values are not acceptable for Zone.userCountChangeUpdateInterval: " + interval);
        }
        Zone zone = this;
        synchronized (zone) {
            this.userCountChangeUpdateInterval = interval;
        }
    }

    public void setZoneManager(IZoneManager manager) {
        this.zoneManager = manager;
    }

    public void validateUserName(String name) throws BZException {
        if (this.userManager.containsName(name)) {
            throw new BZException("User name is already taken: " + name);
        }
    }

    public IFloodFilter getFloodFilter() {
        return this.floodFilter;
    }

    public void removeAllUsers() {
        for (User user : this.userManager.getAllUsers()) {
            this.removeUser(user);
        }
    }

    public void removeUser(int userId) {
        User user = this.userManager.getUserById(userId);
        if (user == null) {
            this.logger.info("Can't remove user with Id: " + userId + ". User doesn't exist in Zone: " + this.name);
        } else {
            this.removeUser(user);
        }
    }

    public void removeUser(ISession session) {
        User user = this.userManager.getUserBySession(session);
        if (user == null) {
            this.logger.info("Can't remove user with Session: " + session + ". User doesn't exist in Zone: " + this.name);
        } else {
            this.removeUser(user);
        }
    }

    public void removeUser(String userName) {
        User user = this.userManager.getUserByName(userName);
        if (user == null) {
            this.logger.info("Can't remove user with Name: " + userName + ". User doesn't exist in Zone: " + this.name);
        } else {
            this.removeUser(user);
        }
    }

    public void removeUser(User user) {
        this.userManager.disconnectUser(user);
        this.roomManager.removeUser(user);
        this.logger.info("User: " + user.getName() + " was logout Zone " + this.getName());
    }

    public PrivilegeManager getPrivilegeManager() {
        return this.privilegeManager;
    }

    public void setPrivilegeManager(PrivilegeManager privilegeManager) {
        if (this.privilegeManager != null) {
            throw new BZRuntimeException("Cannot re-assign the PrivilegeManager in this Zone: " + this.name);
        }
        this.privilegeManager = privilegeManager;
    }

    private User login(ISession session, String userName, String password) throws BZLoginException {
        throw new BZLoginException("Login Custom ZOne: " + this.getName() + " is not supported this time!");
    }

    public User login(User user, boolean forceLogin) throws BZLoginException {
        boolean isEmptyName;
        if (forceLogin) {
            throw new UnsupportedOperationException("ForceLogin is not supported yet!");
        }
        boolean bl = isEmptyName = user.getName().length() == 0;
        if (!this.isActive()) {
            BZErrorData errorData = new BZErrorData(BZErrorCode.LOGIN_INACTIVE_ZONE);
            errorData.addParameter(this.getName());
            throw new BZLoginException("Zone: " + this.getName() + " is not active!", errorData);
        }
        if (this.getUserCount() >= this.maxAllowedUsers) {
            BZErrorData errorData = new BZErrorData(BZErrorCode.LOGIN_ZONE_FULL);
            errorData.addParameter(this.getName());
            throw new BZLoginException("The Zone is full, can't login user: " + user.getName(), errorData);
        }
        if (!isEmptyName && this.getUserByName(user.getName()) != null) {
            BZErrorData errorData = new BZErrorData(BZErrorCode.LOGIN_ALREADY_LOGGED);
            errorData.setParams(Arrays.asList(user.getName(), this.getName()));
            throw new BZLoginException("Another user is already logged with the same name: " + user.getName(), errorData);
        }
        user.setLastLoginTime(System.currentTimeMillis());
        this.manageNewUser(user);
        return user;
    }

    public boolean isFull() {
        return this.getUserCount() >= this.maxAllowedUsers;
    }

    public String toString() {
        return "{ Zone: " + this.name + " }";
    }

    public String getDump() {
        throw new UnsupportedOperationException("Sorry, not implemented yet!");
    }

    private User defaultLogin(ISession session, String userName, String password) {
        User user = new User(session);
        user.setName(userName);
        user.setZone(this);
        return user;
    }

    private void manageNewUser(User user) {
        this.userManager.addUser(user);
        this.logger.info("Add user Zoon " + user.getName() + ", " + user.toString());
        user.setZone(this);
    }

    private void populateTransientFields() {
        this.bz = BitZeroServer.getInstance();
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.floodFilter = null;
    }
}

