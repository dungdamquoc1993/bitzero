/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.extensions;

public interface IHandlerFactory {
    public void addHandler(short var1, Class var2);

    public void removeHandler(short var1);

    public Object findHandler(short var1) throws InstantiationException, IllegalAccessException;

    public void addHandler(String var1, Class var2);

    public void removeHandler(String var1);

    public Object findHandler(String var1) throws InstantiationException, IllegalAccessException;

    public void clearAll();
}

