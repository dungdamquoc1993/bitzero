/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.core;

import bitzero.server.core.IBZEvent;
import bitzero.server.core.IBZEventListener;
import bitzero.server.core.IBZEventType;

public interface IBZEventDispatcher {
    public void addEventListener(IBZEventType var1, IBZEventListener var2);

    public boolean hasEventListener(IBZEventType var1);

    public void removeEventListener(IBZEventType var1, IBZEventListener var2);

    public void dispatchEvent(IBZEvent var1);
}

