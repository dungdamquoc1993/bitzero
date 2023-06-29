/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.io;

import bitzero.engine.io.filter.IFilterChain;

public interface IFilterSupport {
    public IFilterChain getPreFilterChain();

    public IFilterChain getPostFilterChain();
}

