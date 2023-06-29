/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.core;

import bitzero.engine.data.IPacket;
import bitzero.engine.io.IOHandler;
import bitzero.engine.sessions.ISession;

public interface IEngineWriter {
    public IOHandler getIOHandler();

    public void setIOHandler(IOHandler var1);

    public void continueWriteOp(ISession var1);

    public void enqueuePacket(IPacket var1);

    public long getDroppedPacketsCount();

    public long getWrittenBytes();

    public long getWrittenPackets();

    public int getQueueSize();

    public int getThreadPoolSize();
}

