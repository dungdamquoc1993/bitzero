/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.util;

import java.util.Arrays;

public class IndexGenerator {
    private volatile Boolean[] playerSlots;

    public void init(int Size) {
        this.playerSlots = new Boolean[Size + 1];
        Arrays.fill(this.playerSlots, Boolean.FALSE);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
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
        return playerId;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
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
}

