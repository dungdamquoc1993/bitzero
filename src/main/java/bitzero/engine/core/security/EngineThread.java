/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.core.security;

import bitzero.engine.core.security.IAllowedThread;
import bitzero.engine.core.security.ThreadComparisonType;

public final class EngineThread
implements IAllowedThread {
    private String name;
    private ThreadComparisonType comparisonType;

    public EngineThread(String name, ThreadComparisonType comparisonType) {
        this.name = name;
        this.comparisonType = comparisonType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ThreadComparisonType getComparisonType() {
        return this.comparisonType;
    }
}

