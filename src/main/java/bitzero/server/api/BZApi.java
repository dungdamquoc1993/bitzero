/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.api;

import bitzero.engine.io.Response;
import bitzero.engine.sessions.ISession;
import bitzero.engine.sessions.ISessionManager;
import bitzero.server.BitZeroServer;
import bitzero.server.api.APIManager;
import bitzero.server.api.CreateRoomSettings;
import bitzero.server.api.GenericMessageType;
import bitzero.server.api.IBZApi;
import bitzero.server.api.LoginErrorHandler;
import bitzero.server.api.cmd.GenericMessageMsg;
import bitzero.server.api.response.ResponseApi;
import bitzero.server.config.DefaultConstants;
import bitzero.server.config.IConfigurator;
import bitzero.server.config.ServerSettings;
import bitzero.server.controllers.SystemRequest;
import bitzero.server.core.BZEvent;
import bitzero.server.core.BZEventParam;
import bitzero.server.core.BZEventType;
import bitzero.server.core.IBZEvent;
import bitzero.server.core.IBZEventManager;
import bitzero.server.core.IBZEventType;
import bitzero.server.entities.Room;
import bitzero.server.entities.User;
import bitzero.server.entities.Zone;
import bitzero.server.entities.data.SFSObject;
import bitzero.server.entities.managers.BanMode;
import bitzero.server.entities.managers.IBannedUserManager;
import bitzero.server.entities.managers.IUserManager;
import bitzero.server.exceptions.BZCreateRoomException;
import bitzero.server.exceptions.BZErrorCode;
import bitzero.server.exceptions.BZErrorData;
import bitzero.server.exceptions.BZJoinRoomException;
import bitzero.server.exceptions.BZLoginException;
import bitzero.server.exceptions.BZRuntimeException;
import bitzero.server.exceptions.IErrorCode;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.server.util.ClientDisconnectionReason;
import bitzero.server.util.CryptoUtils;
import bitzero.server.util.IDisconnectionReason;
import bitzero.server.util.UsersUtil;
import bitzero.util.common.business.Debug;
import bitzero.util.config.bean.ConstantMercury;
import bitzero.util.datacontroller.business.DataController;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BZApi
implements IBZApi {
    protected final BitZeroServer bz;
    protected final Logger log;
    protected IUserManager globalUserManager;
    private final LoginErrorHandler loginErrorHandler;

    public BZApi(BitZeroServer bz) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.loginErrorHandler = new LoginErrorHandler();
        this.bz = bz;
        this.globalUserManager = bz.getUserManager();
    }

    @Override
    public User getUserById(int userId) {
        return this.globalUserManager.getUserById(userId);
    }

    @Override
    public User getUserByName(String name) {
        return this.globalUserManager.getUserByName(name);
    }

    @Override
    public User getUserBySession(ISession session) {
        return this.globalUserManager.getUserBySession(session);
    }

    @Override
    public void kickUser(User userToKick, User modUser, String kickMessage, int delaySeconds) {
        this.log.info("Kicking user: " + userToKick);
        this.bz.getBannedUserManager().kickUser(userToKick, modUser, kickMessage, delaySeconds);
    }

    @Override
    public void banUser(User userToBan, User modUser, String banMessage, BanMode mode, int durationMinutes, int delaySeconds) {
        this.bz.getBannedUserManager().banUser(userToBan, modUser, durationMinutes, mode, banMessage, banMessage, delaySeconds);
    }

    @Override
    public void disconnect(ISession session) {
        if (session == null) {
            throw new BZRuntimeException("Unexpected, cannot disconnect session. Session object is null.");
        }
        User lostUser = this.globalUserManager.getUserBySession(session);
        if (lostUser != null) {
            this.disconnectUser(lostUser);
        } else if (session.isConnected()) {
            try {
                session.close();
            }
            catch (IOException err) {
                throw new BZRuntimeException(err);
            }
        }
    }

    @Override
    public void disconnectUser(User user, IDisconnectionReason reason) {
        user.getSession().setSystemProperty("disconnectionReason", reason);
        this.bz.getAPIManager().getResApi().notifyClientSideDisconnection(user, reason);
        user.getSession().setMarkedForEviction();
    }

    @Override
    public void disconnectUser(User user) {
        this.disconnectUser(user, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void disconnectUser(User user, Boolean forceLogin) {
        if (user == null) {
            throw new BZRuntimeException("Cannot disconnect user, User object is null.");
        }
        Zone zone = user.getZone();
        Room room = user.getJoinedRoom();
        Integer playerId = user.getPlayerId();
        try {
            if (user.getSession().isConnected() && !forceLogin.booleanValue()) {
                user.getSession().close();
            }
            user.setConnected(false);
        }
        catch (Exception err) {
            err.printStackTrace();
        }
        finally {
            if (zone != null) {
                zone.removeUser(user);
            }
            this.globalUserManager.removeUser(user);
            user.setConnected(false);
            if (!forceLogin.booleanValue()) {
                this.removeUserServerLoginAddress(user.getId());
                this.removeUserServerAddress(user.getId());
            }
        }
        HashMap<BZEventParam, Object> evtParams = new HashMap<BZEventParam, Object>();
        evtParams.put(BZEventParam.USER, user);
        evtParams.put(BZEventParam.ZONE, zone);
        evtParams.put(BZEventParam.JOINED_ROOMS, room);
        evtParams.put(BZEventParam.PLAYER_ID, playerId);
        IDisconnectionReason disconnectionReason = (IDisconnectionReason)user.getSession().getSystemProperty("disconnectionReason");
        evtParams.put(BZEventParam.DISCONNECTION_REASON, disconnectionReason != null ? disconnectionReason : ClientDisconnectionReason.UNKNOWN);
        if (forceLogin.booleanValue()) {
            this.bz.getEventManager().dispatchImmediateEvent(new BZEvent(BZEventType.USER_DISCONNECT, evtParams));
        } else {
            this.bz.getEventManager().dispatchEvent(new BZEvent(BZEventType.USER_DISCONNECT, evtParams));
        }
        if (room != null) {
            zone.checkAndRemove(room);
        }
        this.log.info("User disconnected: " + user);
    }

    private void removeUserServerLoginAddress(int userId) {
        String key = ConstantMercury.PREFIX_SNSGAME_GENERAL + "server_address_login_cached" + "|" + userId;
        try {
            DataController.getController().deleteCache(key);
        }
        catch (Exception var3_3) {
            // empty catch block
        }
    }

    private void removeUserServerAddress(int userId) {
        String key = ConstantMercury.PREFIX_SNSGAME_GENERAL + "server_address_cached" + "|" + userId;
        try {
            DataController.getController().deleteCache(key);
        }
        catch (Exception var3_3) {
            // empty catch block
        }
    }

    @Override
    public void removeRoom(Room room) {
        this.removeRoom(room, true, true);
    }

    @Override
    public void removeRoom(Room room, boolean fireClientEvent, boolean fireServerEvent) {
        room.getZone().removeRoom(room);
        if (fireServerEvent) {
            HashMap<BZEventParam, Object> evtParams = new HashMap<BZEventParam, Object>();
            evtParams.put(BZEventParam.ZONE, room.getZone());
            evtParams.put(BZEventParam.ROOM, room);
            this.bz.getEventManager().dispatchEvent(new BZEvent(BZEventType.ROOM_REMOVED, evtParams));
        }
    }

    @Override
    public boolean checkSecurePassword(ISession session, String originalPass, String encryptedPass) {
        if (originalPass == null || originalPass.length() < 1) {
            return false;
        }
        if (encryptedPass == null || encryptedPass.length() < 1) {
            return false;
        }
        return encryptedPass.equals(CryptoUtils.getClientPassword(session, originalPass));
    }

    @Override
    public void login(ISession sender, byte loginOK, User user) {
        if (!this.bz.getSessionManager().containsSession(sender)) {
            this.log.warn("Login failed: " + user.getName() + " , session is already expired!");
            return;
        }
        Response response = new Response();
        response.setId(SystemRequest.Login.getId());
        response.setTargetController(DefaultConstants.CORE_EXTENSIONS_CONTROLLER_ID);
        response.setContent(new byte[]{loginOK});
        response.setRecipients(sender);
        if (loginOK == 0) {
            BZErrorData errorData;
            BZLoginException ex = null;
            if (this.bz.getBannedUserManager().isNameBanned(user.getName())) {
                errorData = new BZErrorData(BZErrorCode.LOGIN_BANNED_USER);
                errorData.addParameter(String.valueOf(this.bz.getBannedUserManager().getBanDuration(user.getName(), BanMode.BY_NAME)) + 's');
                ex = new BZLoginException("This user name is banned: " + user.getName(), errorData);
            }
            if (this.bz.getBannedUserManager().isIpBanned(sender.getAddress())) {
                errorData = new BZErrorData(BZErrorCode.LOGIN_BANNED_IP);
                errorData.addParameter(sender.getAddress());
                errorData.addParameter(String.valueOf(this.bz.getBannedUserManager().getBanDuration(sender.getAddress(), BanMode.BY_ADDRESS)));
                ex = new BZLoginException("This IP address is banned: " + sender.getAddress(), errorData);
            }
            if (ex != null) {
                this.loginErrorHandler.execute(sender, ex);
                return;
            }
            user.setReconnectionSeconds(this.bz.getConfigurator().getServerSettings().userReconnectionSeconds);
            user.setLastLoginTime(System.currentTimeMillis());
            user.getSession().setMaxLoggedInIdleTime(this.bz.getConfigurator().getServerSettings().userMaxIdleTime);
            user.setConnected(true);
            sender.setLoggedIn(true);
            this.globalUserManager.addUser(user);
            this.log.info("Add user Bitzero All 1 " + user.getName() + ", " + user.toString());
            this.log.info("Login in, " + user.getName() + ", " + user.toString());
            user.updateLastRequestTime();
            HashMap<BZEventParam, User> evtParams = new HashMap<BZEventParam, User>();
            evtParams.put(BZEventParam.USER, user);
            this.bz.getEventManager().dispatchEvent(new BZEvent(BZEventType.USER_LOGIN, evtParams));
            Debug.trace("Check request cmd successsssssssssssssssssssss !");
        } else {
            this.log.info("Login error: " + loginOK + ". Requested by: " + sender);
            response.write();
        }
    }

    @Override
    public void loginWebsocket(ISession sender, byte loginOK, User user) {
        if (!this.bz.getSessionManager().containsSession(sender)) {
            this.log.warn("Login failed: " + user.getName() + " , session is already expired!");
            return;
        }
        SFSObject resObj = SFSObject.newInstance();
        Response response = new Response();
        response.setId(SystemRequest.LoginWebsocket.getId());
        response.setTargetController(DefaultConstants.CORE_SYSTEM_CONTROLLER_ID);
        response.setContent(resObj);
        response.setRecipients(sender);
        if (loginOK == 0) {
            BZErrorData errorData;
            BZLoginException ex = null;
            if (this.bz.getBannedUserManager().isNameBanned(user.getName())) {
                errorData = new BZErrorData(BZErrorCode.LOGIN_BANNED_USER);
                errorData.addParameter(String.valueOf(this.bz.getBannedUserManager().getBanDuration(user.getName(), BanMode.BY_NAME)) + 's');
                ex = new BZLoginException("This user name is banned: " + user.getName(), errorData);
            }
            if (this.bz.getBannedUserManager().isIpBanned(sender.getAddress())) {
                errorData = new BZErrorData(BZErrorCode.LOGIN_BANNED_IP);
                errorData.addParameter(sender.getAddress());
                errorData.addParameter(String.valueOf(this.bz.getBannedUserManager().getBanDuration(sender.getAddress(), BanMode.BY_ADDRESS)));
                ex = new BZLoginException("This IP address is banned: " + sender.getAddress(), errorData);
            }
            if (ex != null) {
                this.loginErrorHandler.execute(sender, ex);
                return;
            }
            user.setReconnectionSeconds(this.bz.getConfigurator().getServerSettings().userReconnectionSeconds);
            user.setLastLoginTime(System.currentTimeMillis());
            user.getSession().setMaxLoggedInIdleTime(this.bz.getConfigurator().getServerSettings().userMaxIdleTime);
            user.setConnected(true);
            sender.setLoggedIn(true);
            this.globalUserManager.addUser(user);
            this.log.info("Add user Bitzero All 2 " + user.getName() + ", " + user.toString());
            this.log.info("Login in, " + user.getName() + ", " + user.toString() + user.getSession().getSystemProperty("ClientType"));
            user.updateLastRequestTime();
            HashMap<BZEventParam, User> evtParams = new HashMap<BZEventParam, User>();
            evtParams.put(BZEventParam.USER, user);
            this.bz.getEventManager().dispatchEvent(new BZEvent(BZEventType.USER_LOGIN, evtParams));
            resObj.putInt("id", user.getId());
            resObj.putUtfString("zn", "");
            resObj.putUtfString("un", user.getName());
            resObj.putShort("rs", (short)999);
            resObj.putShort("pi", user.getPrivilegeId());
            resObj.putNull("rl");
            response.write();
        } else {
            this.log.info("Login error: " + loginOK + ". Requested by: " + sender);
            response.write();
        }
    }

    @Override
    public void logout(User user) {
        if (user == null) {
            throw new BZRuntimeException("Cannot logout null user.");
        }
        this.disconnectUser(user);
        user.setConnected(false);
        this.globalUserManager.removeUser(user);
        HashMap<BZEventParam, User> evtParams = new HashMap<BZEventParam, User>();
        evtParams.put(BZEventParam.USER, user);
        this.bz.getEventManager().dispatchEvent(new BZEvent(BZEventType.USER_LOGOUT, evtParams));
        this.log.info("User logout: " + user);
    }

    @Override
    public Room createRoom(Zone zone, CreateRoomSettings params, User owner) throws BZCreateRoomException {
        return this.createRoom(zone, params, owner, true, null, false, true);
    }

    @Override
    public Room createRoom(Zone zone, CreateRoomSettings params, User owner, boolean joinIt, Room roomToLeave) throws BZCreateRoomException {
        return this.createRoom(zone, params, owner, joinIt, roomToLeave, true, true);
    }

    @Override
    public Room createRoom(Zone zone, CreateRoomSettings params, User owner, boolean joinIt, Room roomToLeave, boolean fireClientEvent, boolean fireServerEvent) throws BZCreateRoomException {
        Room theRoom = null;
        try {
            String groupId = params.getGroupId();
            if (groupId == null || groupId.length() == 0) {
                params.setGroupId("default");
            }
            theRoom = zone.createRoom(params, owner);
            if (owner != null) {
                owner.updateLastRequestTime();
            }
            this.log.info("Room created: " + theRoom);
            if (fireClientEvent) {
                throw new BZCreateRoomException("aaa");
            }
            if (fireServerEvent) {
                HashMap<BZEventParam, Object> eventParams = new HashMap<BZEventParam, Object>();
                eventParams.put(BZEventParam.ZONE, zone);
                eventParams.put(BZEventParam.ROOM, theRoom);
                this.bz.getEventManager().dispatchEvent(new BZEvent(BZEventType.ROOM_ADDED, eventParams));
            }
        }
        catch (BZCreateRoomException err) {
            String message = String.format("Room creation error. %s, %s, %s", err.getMessage(), zone, owner);
            throw new BZCreateRoomException(message);
        }
        if (theRoom != null && owner != null && joinIt) {
            try {
                this.joinRoom(owner, theRoom, theRoom.getPassword(), false, roomToLeave, true, fireClientEvent);
            }
            catch (BZJoinRoomException e) {
                this.log.warn("Unable to join the just created Room: " + theRoom + ", reason: " + e.getMessage());
            }
        }
        return theRoom;
    }

    @Override
    public void joinRoom(User user, Room roomToJoin, int nChair) throws BZJoinRoomException {
        this.joinRoom(user, roomToJoin, "", false, user.getJoinedRoom(), true, true, nChair, false);
    }

    @Override
    public void joinRoom(User user, Room room) throws BZJoinRoomException {
        this.joinRoom(user, room, "", false, user.getJoinedRoom());
    }

    @Override
    public void joinRoom(User user, Room roomToJoin, String password, boolean asSpectator, Room roomToLeave) throws BZJoinRoomException {
        this.joinRoom(user, roomToJoin, password, asSpectator, roomToLeave, true, true);
    }

    @Override
    public void joinRoom(User user, Room roomToJoin, String password, boolean asSpectator, Room roomToLeave, int nChair, boolean isHolding) throws BZJoinRoomException {
        this.joinRoom(user, roomToJoin, password, asSpectator, roomToLeave, true, true, nChair, isHolding);
    }

    @Override
    public void joinRoom(User user, Room roomToJoin, String password, boolean asSpectator, Room roomToLeave, int nChair) throws BZJoinRoomException {
        this.joinRoom(user, roomToJoin, password, asSpectator, roomToLeave, nChair, false);
    }

    @Override
    public void joinRoom(User user, Room roomToJoin, String password, boolean asSpectator, Room roomToLeave, boolean fireClientEvent, boolean fireServerEvent) throws BZJoinRoomException {
        try {
            if (user.isJoining()) {
                throw new BZRuntimeException("Join request discarded. User is already in a join transaction: " + user);
            }
            if (roomToJoin == null) {
                throw new BZJoinRoomException("Requested room doesn't exist", new BZErrorData(BZErrorCode.JOIN_BAD_ROOM));
            }
            if (!roomToJoin.isActive()) {
                String message = String.format("Room is currently locked, %s", roomToJoin);
                BZErrorData errData = new BZErrorData(BZErrorCode.JOIN_ROOM_LOCKED);
                errData.addParameter(roomToJoin.getName());
                throw new BZJoinRoomException(message, errData);
            }
            boolean doorIsOpen = true;
            if (roomToJoin.isPasswordProtected()) {
                doorIsOpen = roomToJoin.getPassword().equals(password);
            }
            if (!doorIsOpen) {
                String message = String.format("Room password is wrong, %s", roomToJoin);
                BZErrorData data = new BZErrorData(BZErrorCode.JOIN_BAD_PASSWORD);
                data.addParameter(roomToJoin.getName());
                throw new BZJoinRoomException(message, data);
            }
            user.setJoining(true);
            roomToJoin.addUser(user, asSpectator);
            user.updateLastRequestTime();
            if (fireClientEvent) {
                // empty if block
            }
            if (fireServerEvent) {
                HashMap<BZEventParam, Object> evtParams = new HashMap<BZEventParam, Object>();
                evtParams.put(BZEventParam.ROOM, roomToJoin);
                evtParams.put(BZEventParam.USER, user);
                this.bz.getEventManager().dispatchEvent(new BZEvent(BZEventType.USER_JOIN_ROOM, evtParams));
            }
            if (roomToLeave != null) {
                this.leaveRoom(user, roomToLeave);
            }
        }
        catch (BZJoinRoomException err) {
            String message = String.format("Join Error - %s", err.getMessage());
            throw new BZJoinRoomException(message, err.getErrorData());
        }
        finally {
            user.setJoining(false);
        }
    }

    public void joinRoom(User user, Room roomToJoin, String password, boolean asSpectator, Room roomToLeave, boolean fireClientEvent, boolean fireServerEvent, int nChair, boolean isHolding) throws BZJoinRoomException {
        try {
            this.log.debug("XU LI JOIN ROOM ******  ");
            if (user.isJoining()) {
                throw new BZRuntimeException("Join request discarded. User is already in a join transaction: " + user);
            }
            if (roomToJoin == null) {
                throw new BZJoinRoomException("Requested room doesn't exist", new BZErrorData(BZErrorCode.JOIN_BAD_ROOM));
            }
            if (!roomToJoin.isActive()) {
                String message = String.format("Room is currently locked, %s", roomToJoin);
                BZErrorData errData = new BZErrorData(BZErrorCode.JOIN_ROOM_LOCKED);
                errData.addParameter(roomToJoin.getName());
                throw new BZJoinRoomException(message, errData);
            }
            boolean doorIsOpen = true;
            if (roomToJoin.isPasswordProtected()) {
                doorIsOpen = roomToJoin.getPassword().equals(password);
            }
            if (!doorIsOpen) {
                String message = String.format("Room password is wrong, %s", roomToJoin);
                BZErrorData data = new BZErrorData(BZErrorCode.JOIN_BAD_PASSWORD);
                data.addParameter(roomToJoin.getName());
                throw new BZJoinRoomException(message, data);
            }
            user.setJoining(true);
            roomToJoin.addUser(user, asSpectator, nChair, isHolding);
            user.updateLastRequestTime();
            if (fireClientEvent) {
                // empty if block
            }
            if (fireServerEvent) {
                this.log.debug("JOIN ROOOM SUCCESS NE ******  ");
                HashMap<BZEventParam, Object> evtParams = new HashMap<BZEventParam, Object>();
                evtParams.put(BZEventParam.ROOM, roomToJoin);
                evtParams.put(BZEventParam.USER, user);
                this.bz.getEventManager().dispatchEvent(new BZEvent(BZEventType.USER_JOIN_ROOM, evtParams));
            }
            if (roomToLeave != null) {
                this.leaveRoom(user, roomToLeave);
            }
        }
        catch (BZJoinRoomException err) {
            String message = String.format("Join Error - %s", err.getMessage());
            throw new BZJoinRoomException(message, err.getErrorData());
        }
        finally {
            user.setJoining(false);
        }
    }

    public void joinRoom(User user, Room roomToJoin, String password, boolean asSpectator, Room roomToLeave, boolean fireClientEvent, boolean fireServerEvent, int nChair) throws BZJoinRoomException {
        this.joinRoom(user, roomToJoin, password, asSpectator, roomToLeave, fireClientEvent, fireServerEvent, nChair, false);
    }

    @Override
    public void leaveRoom(User user, Room room) {
        this.leaveRoom(user, room, true, true);
    }

    @Override
    public void leaveRoom(User user, Room room, boolean fireClientEvent, boolean fireServerEvent) {
        if (room == null && (room = user.getJoinedRoom()) == null) {
            throw new BZRuntimeException("LeaveRoom failed: user is not joined in any room. " + user);
        }
        int playerId = user.getPlayerId();
        if (user.getZone() != null) {
            user.getZone().removeUserFromRoom(user, room);
        }
        user.updateLastRequestTime();
        if (fireServerEvent) {
            HashMap<BZEventParam, Object> evtParams = new HashMap<BZEventParam, Object>();
            evtParams.put(BZEventParam.ZONE, user.getZone());
            evtParams.put(BZEventParam.ROOM, room);
            evtParams.put(BZEventParam.USER, user);
            evtParams.put(BZEventParam.PLAYER_ID, playerId);
            this.bz.getEventManager().dispatchEvent(new BZEvent(BZEventType.USER_LEAVE_ROOM, evtParams));
        } else {
            this.log.info("fireServerEvent fall");
        }
    }

    @Override
    public void sendPublicMessage(Room targetRoom, User sender, BaseMsg msg) {
        if (targetRoom == null) {
            throw new IllegalArgumentException("The target Room is null");
        }
        if (sender.getJoinedRoom().getId() != targetRoom.getId()) {
            throw new IllegalStateException("Sender " + sender + " is not joined the target room " + targetRoom);
        }
        if (msg == null) {
            this.log.info("Empty public message request (len == 0) discarded");
            return;
        }
        HashMap<BZEventParam, Object> evtParams = new HashMap<BZEventParam, Object>();
        evtParams.put(BZEventParam.ROOM, targetRoom);
        evtParams.put(BZEventParam.USER, sender);
        evtParams.put(BZEventParam.MESSAGE, msg);
        this.bz.getEventManager().dispatchEvent(new BZEvent(BZEventType.PUBLIC_MESSAGE, evtParams));
    }

    @Override
    public void sendPrivateMessage(User sender, User recipient, BaseMsg msg) {
        if (sender == null) {
            throw new IllegalArgumentException("PM sender is null.");
        }
        if (recipient == null) {
            throw new IllegalArgumentException("PM recipient is null");
        }
        if (sender == recipient) {
            throw new IllegalStateException("PM sender and receiver are the same. Why?");
        }
        if (msg == null) {
            this.log.info("Empty private message request (len == 0) discarded");
            return;
        }
        HashMap<BZEventParam, Object> evtParams = new HashMap<BZEventParam, Object>();
        evtParams.put(BZEventParam.USER, sender);
        evtParams.put(BZEventParam.RECIPIENT, recipient);
        evtParams.put(BZEventParam.MESSAGE, msg);
        this.bz.getEventManager().dispatchEvent(new BZEvent(BZEventType.PRIVATE_MESSAGE, evtParams));
    }

    @Override
    public void sendModeratorMessage(User sender, String message, String[] msg, Collection recipients) {
        this.sendSuperUserMessage(GenericMessageType.MODERATOR_MSG, sender, message, msg, recipients);
    }

    @Override
    public void sendAdminMessage(User sender, String message, String[] msg, Collection recipients) {
        this.sendSuperUserMessage(GenericMessageType.ADMING_MSG, sender, message, msg, recipients);
    }

    private void sendSuperUserMessage(GenericMessageType type, User sender, String message, String[] msg, Collection recipients) {
        if (recipients.size() == 0) {
            throw new IllegalStateException("Mod Message discarded. No recipients");
        }
        if (message.length() == 0) {
            throw new IllegalStateException("Mod Message discarded. Empty message");
        }
        if (sender == null) {
            switch (type.getId()) {
                case 3: {
                    sender = UsersUtil.getServerAdmin();
                    break;
                }
                default: {
                    sender = UsersUtil.getServerModerator();
                }
            }
        }
        this.sendGenericMessage(type, sender, -1, message, msg, recipients);
    }

    private void sendGenericMessage(GenericMessageType type, User sender, int targetRoomId, String message, String[] params, Collection recipients) {
        if (sender != null) {
            sender.updateLastRequestTime();
        }
        GenericMessageMsg msg = new GenericMessageMsg((short)type.getId());
        msg.sender = sender.getName();
        msg.message = message;
        if (params != null) {
            msg.params = params;
        }
        Response response = new Response();
        response.setId(SystemRequest.GenericMessage.getId());
        response.setTargetController(DefaultConstants.CORE_SYSTEM_CONTROLLER_ID);
        response.setContent(msg.createData());
        response.setRecipients(recipients);
        response.write();
    }
}

