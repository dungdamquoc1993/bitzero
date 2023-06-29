/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.core;

public interface IMethodDispatcher {
    public void registerMethod(String var1, String var2);

    public void unregisterKey(String var1);

    public void callMethod(String var1, Object[] var2) throws Exception;
}

