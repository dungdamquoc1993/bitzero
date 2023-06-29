/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.io;

import bitzero.engine.data.IPacket;
import bitzero.engine.io.IFilterSupport;
import bitzero.engine.io.IProtocolCodec;
import bitzero.engine.sessions.ISession;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

public interface IOHandler
extends IFilterSupport {
    public void onDataRead(ISession var1, byte[] var2);

    public void onDataRead(DatagramChannel var1, SocketAddress var2, byte[] var3);

    public void onDataWrite(IPacket var1);

    public IProtocolCodec getCodec();

    public void setCodec(IProtocolCodec var1);

    public long getReadPackets();

    public long getIncomingDroppedPackets();
}

