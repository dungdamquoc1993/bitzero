/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.entities;

public class RoomSize {
    private int userCount;
    private int spectatorCount;

    public RoomSize(int userCount, int spectatorCount) {
        this.userCount = userCount;
        this.spectatorCount = spectatorCount;
    }

    public int getUserCount() {
        return this.userCount;
    }

    public int getSpectatorCount() {
        return this.spectatorCount;
    }

    public int getTotalUsers() {
        return this.getUserCount() + this.getSpectatorCount();
    }

    public String toString() {
        return String.format("{ u: %s, s: %s }", this.userCount, this.spectatorCount);
    }
}

