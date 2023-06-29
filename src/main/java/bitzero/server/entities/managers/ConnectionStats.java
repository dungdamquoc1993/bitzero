/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.entities.managers;

public class ConnectionStats {
    private final int socketSessionCount;
    private final int npcSessionCount;
    private final int tunnelledSessionCount;
    private final int websocketSessionCount;

    public ConnectionStats(int socketSessionCount, int npcSessionCount, int tunnelledSessionCount, int websocketSessionCount) {
        this.socketSessionCount = socketSessionCount;
        this.npcSessionCount = npcSessionCount;
        this.tunnelledSessionCount = tunnelledSessionCount;
        this.websocketSessionCount = websocketSessionCount;
    }

    public int getSocketCount() {
        return this.socketSessionCount;
    }

    public int getNpcCount() {
        return this.npcSessionCount;
    }

    public int getTunnelledCount() {
        return this.tunnelledSessionCount;
    }

    public int getWebsocketSessionCount() {
        return this.websocketSessionCount;
    }
}

