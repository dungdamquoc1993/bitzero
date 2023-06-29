/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.util;

import bitzero.server.entities.Room;

public interface IPlayerIdGenerator {
    public void init();

    public int getPlayerSlot();

    public void freePlayerSlot(int var1);

    public boolean takeSlot(int var1);

    public void onRoomResize();

    public void setParentRoom(Room var1);

    public Room getParentRoom();
}

