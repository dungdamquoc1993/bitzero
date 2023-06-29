/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.core;

import bitzero.engine.io.IOHandler;
import java.nio.channels.Selector;

public interface IEngineReader {
    public Selector getSelector();

    public IOHandler getIOHandler();

    public void setIoHandler(IOHandler var1);

    public long getReadBytes();

    public long getReadPackets();
}

