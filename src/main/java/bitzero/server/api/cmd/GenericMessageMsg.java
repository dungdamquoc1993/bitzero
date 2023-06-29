/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.api.cmd;

import bitzero.server.extensions.data.BaseMsg;
import java.nio.ByteBuffer;

public class GenericMessageMsg
extends BaseMsg {
    public String sender = "";
    public String message = "";
    public String[] params = new String[0];

    public GenericMessageMsg(short s) {
        super(s);
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        this.putStr(bf, this.sender);
        this.putStr(bf, this.message);
        this.putStringArray(bf, this.params);
        return this.packBuffer(bf);
    }
}

