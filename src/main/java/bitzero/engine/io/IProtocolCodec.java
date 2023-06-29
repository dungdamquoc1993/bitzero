/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.io;

import bitzero.engine.data.IPacket;
import bitzero.engine.io.IOHandler;
import bitzero.engine.io.IResponse;

public interface IProtocolCodec {
    public void onPacketRead(IPacket var1);

    public void onPacketWrite(IResponse var1);

    public IOHandler getIOHandler();

    public void setIOHandler(IOHandler var1);
}

