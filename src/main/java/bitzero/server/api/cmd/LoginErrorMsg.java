/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.api.cmd;

import bitzero.server.extensions.data.BaseMsg;
import java.nio.ByteBuffer;

public class LoginErrorMsg
extends BaseMsg {
    public String message = "";

    public LoginErrorMsg(short s, int i) {
        super(s, i);
    }

    public LoginErrorMsg(short s) {
        super(s);
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = this.makeBuffer();
        this.putStr(bf, this.message);
        return this.packBuffer(bf);
    }
}

