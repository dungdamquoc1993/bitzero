/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.io;

import bitzero.engine.io.IOHandler;
import bitzero.engine.io.IProtocolCodec;
import bitzero.engine.io.filter.IFilterChain;

public abstract class AbstractIOHandler
implements IOHandler {
    protected IProtocolCodec codec;
    protected volatile long readPackets;
    protected IFilterChain preFilterChain;
    protected IFilterChain postFilterChain;

    @Override
    public IProtocolCodec getCodec() {
        return this.codec;
    }

    @Override
    public void setCodec(IProtocolCodec codec) {
        this.codec = codec;
    }

    @Override
    public long getReadPackets() {
        return this.readPackets;
    }

    @Override
    public IFilterChain getPreFilterChain() {
        return this.preFilterChain;
    }

    public void setPreFilterChain(IFilterChain preFilterChain) {
        this.preFilterChain = preFilterChain;
    }

    @Override
    public IFilterChain getPostFilterChain() {
        return this.postFilterChain;
    }

    public void setPostFilterChain(IFilterChain postFilterChain) {
        this.postFilterChain = postFilterChain;
    }
}

