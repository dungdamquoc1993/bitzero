/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.util;

import bitzero.server.entities.Room;
import bitzero.server.util.IPlayerIdGenerator;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPlayerIdGenerator
implements IPlayerIdGenerator {
    private Room parentRoom;
    private volatile Boolean[] playerSlots;
    private final Logger logger;

    public DefaultPlayerIdGenerator() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public void init() {
        this.playerSlots = new Boolean[this.parentRoom.getMaxUsers() + 1];
        Arrays.fill(this.playerSlots, Boolean.FALSE);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getPlayerSlot() {
        int playerId = 0;
        Boolean[] arrboolean = this.playerSlots;
        synchronized (arrboolean) {
            for (int ii = 1; ii < this.playerSlots.length; ++ii) {
                if (this.playerSlots[ii].booleanValue()) continue;
                playerId = ii;
                this.playerSlots[ii] = Boolean.TRUE;
                break;
            }
        }
        if (playerId < 1) {
            this.logger.warn("No player slot found in " + this.parentRoom);
        }
        return playerId;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void freePlayerSlot(int playerId) {
        if (playerId == -1) {
            return;
        }
        if (playerId >= this.playerSlots.length) {
            return;
        }
        Boolean[] arrboolean = this.playerSlots;
        synchronized (arrboolean) {
            this.playerSlots[playerId] = Boolean.FALSE;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onRoomResize() {
        Boolean[] newPlayerSlots = new Boolean[this.parentRoom.getMaxUsers() + 1];
        Boolean[] arrboolean = this.playerSlots;
        synchronized (arrboolean) {
            for (int i = 1; i < newPlayerSlots.length; ++i) {
                newPlayerSlots[i] = i < this.playerSlots.length ? this.playerSlots[i] : Boolean.FALSE;
            }
        }
        this.playerSlots = newPlayerSlots;
    }

    @Override
    public Room getParentRoom() {
        return this.parentRoom;
    }

    @Override
    public void setParentRoom(Room room) {
        this.parentRoom = room;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean takeSlot(int playerId) {
        if (playerId == -1) {
            return false;
        }
        if (playerId >= this.playerSlots.length) {
            return false;
        }
        Boolean[] arrboolean = this.playerSlots;
        synchronized (arrboolean) {
            if (this.playerSlots[playerId].booleanValue()) {
                return false;
            }
            this.playerSlots[playerId] = Boolean.TRUE;
        }
        return true;
    }
}

