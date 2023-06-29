/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers.system.cmd;

import bitzero.server.controllers.SystemRequest;
import bitzero.server.extensions.data.BaseMsg;
import java.nio.ByteBuffer;

public class SendHandsake
extends BaseMsg {
    public String sessionToken = "";
    public int reconnectTime = 5;

    public SendHandsake() {
        super((Short)SystemRequest.Handshake.getId());
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        this.putStr(bf, this.sessionToken);
        bf.putInt(this.reconnectTime);
        return this.packBuffer(bf);
    }
}

