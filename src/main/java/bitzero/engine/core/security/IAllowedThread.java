/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.core.security;

import bitzero.engine.core.security.ThreadComparisonType;

public interface IAllowedThread {
    public String getName();

    public ThreadComparisonType getComparisonType();
}

