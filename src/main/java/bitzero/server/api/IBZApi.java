/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.api;

import bitzero.engine.sessions.ISession;
import bitzero.server.api.CreateRoomSettings;
import bitzero.server.entities.Room;
import bitzero.server.entities.User;
import bitzero.server.entities.Zone;
import bitzero.server.entities.managers.BanMode;
import bitzero.server.exceptions.BZCreateRoomException;
import bitzero.server.exceptions.BZJoinRoomException;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.server.util.IDisconnectionReason;
import java.util.Collection;

public interface IBZApi {
    public boolean checkSecurePassword(ISession var1, String var2, String var3);

    public void login(ISession var1, byte var2, User var3);

    public void loginWebsocket(ISession var1, byte var2, User var3);

    public void logout(User var1);

    public void kickUser(User var1, User var2, String var3, int var4);

    public void banUser(User var1, User var2, String var3, BanMode var4, int var5, int var6);

    public void disconnectUser(User var1);

    public void disconnectUser(User var1, Boolean var2);

    public void disconnectUser(User var1, IDisconnectionReason var2);

    public void disconnect(ISession var1);

    public Room createRoom(Zone var1, CreateRoomSettings var2, User var3) throws BZCreateRoomException;

    public Room createRoom(Zone var1, CreateRoomSettings var2, User var3, boolean var4, Room var5) throws BZCreateRoomException;

    public Room createRoom(Zone var1, CreateRoomSettings var2, User var3, boolean var4, Room var5, boolean var6, boolean var7) throws BZCreateRoomException;

    public User getUserById(int var1);

    public User getUserByName(String var1);

    public User getUserBySession(ISession var1);

    public void joinRoom(User var1, Room var2, int var3) throws BZJoinRoomException;

    public void joinRoom(User var1, Room var2) throws BZJoinRoomException;

    public void joinRoom(User var1, Room var2, String var3, boolean var4, Room var5) throws BZJoinRoomException;

    public void joinRoom(User var1, Room var2, String var3, boolean var4, Room var5, int var6, boolean var7) throws BZJoinRoomException;

    public void joinRoom(User var1, Room var2, String var3, boolean var4, Room var5, int var6) throws BZJoinRoomException;

    public void joinRoom(User var1, Room var2, String var3, boolean var4, Room var5, boolean var6, boolean var7) throws BZJoinRoomException;

    public void leaveRoom(User var1, Room var2);

    public void leaveRoom(User var1, Room var2, boolean var3, boolean var4);

    public void removeRoom(Room var1);

    public void removeRoom(Room var1, boolean var2, boolean var3);

    public void sendPublicMessage(Room var1, User var2, BaseMsg var3);

    public void sendPrivateMessage(User var1, User var2, BaseMsg var3);

    public void sendModeratorMessage(User var1, String var2, String[] var3, Collection var4);

    public void sendAdminMessage(User var1, String var2, String[] var3, Collection var4);
}

