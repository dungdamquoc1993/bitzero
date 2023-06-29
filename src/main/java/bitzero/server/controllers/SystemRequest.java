/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers;

public enum SystemRequest {
    Handshake(0),
    Login(1),
    LoginWebsocket(9002),
    Logout(2),
    GetRoomList(3),
    JoinRoom(4),
    AutoJoin(5),
    CreateRoom(6),
    GenericMessage(7),
    ChangeRoomName(8),
    ChangeRoomPassword(9),
    ObjectMessage(10),
    SetRoomVariables(11),
    SetUserVariables(12),
    CallExtension(13),
    LeaveRoom(14),
    SubscribeRoomGroup(15),
    UnsubscribeRoomGroup(16),
    SpectatorToPlayer(17),
    PlayerToSpectator(18),
    ChangeRoomCapacity(19),
    PublicMessage(20),
    PrivateMessage(21),
    ModeratorMessage(22),
    AdminMessage(23),
    KickUser(24),
    BanUser(25),
    ManualDisconnection(26),
    GoOnline(27),
    InviteUser(28),
    InvitationReply(29),
    CreateBZGame(30),
    QuickJoinGame(31),
    OnEnterRoom(32),
    OnRoomCountChange(33),
    OnUserLost(34),
    OnRoomLost(35),
    OnUserExitRoom(36),
    OnClientDisconnection(37),
    BanUserChat(38),
    PingPong(50),
    CheckOnline(51),
    SystemStats(1000),
    SetPoolSize(1001),
    SetLogLevel(1002),
    CrossCommand(200),
    ServiceNotify(201),
    DashBoard(9001),
    IpFilterCommand(8000),
    CrossExtCommand(300),
    ExecuteCommand(9000);
    
    private Object id;

    public static SystemRequest fromId(Object id) {
        SystemRequest req = null;
        for (SystemRequest item : SystemRequest.values()) {
            if (!item.getId().equals(id)) continue;
            req = item;
            break;
        }
        if (req == null) {
            req = CallExtension;
        }
        return req;
    }

    private SystemRequest(Object id) {
        this.id = id;
    }

    public Object getId() {
        return this.id;
    }
}

