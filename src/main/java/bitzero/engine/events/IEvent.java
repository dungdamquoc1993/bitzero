/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.events;

public interface IEvent {
    public Object getTarget();

    public void setTarget(Object var1);

    public String getName();

    public void setName(String var1);

    public Object getParameter(String var1);

    public void setParameter(String var1, Object var2);
}

