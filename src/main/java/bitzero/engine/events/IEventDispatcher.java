/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.events;

import bitzero.engine.events.IEvent;
import bitzero.engine.events.IEventListener;

public interface IEventDispatcher {
    public void addEventListener(String var1, IEventListener var2);

    public boolean hasEventListener(String var1);

    public void removeEventListener(String var1, IEventListener var2);

    public void dispatchEvent(IEvent var1);
}

