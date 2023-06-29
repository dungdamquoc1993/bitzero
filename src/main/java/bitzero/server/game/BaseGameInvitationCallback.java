/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.game;

import bitzero.server.entities.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseGameInvitationCallback {
    private Room game;
    private boolean leaveLastJoinedRoom;
    protected final Logger log;

    public BaseGameInvitationCallback(Room game, boolean leaveLastJoinedRoom) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.game = game;
        this.leaveLastJoinedRoom = leaveLastJoinedRoom;
    }

    protected Room getGame() {
        return this.game;
    }

    protected boolean isLeaveLastJoindRoom() {
        return this.leaveLastJoinedRoom;
    }
}

