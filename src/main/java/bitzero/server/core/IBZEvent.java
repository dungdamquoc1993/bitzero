/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.core;

import bitzero.server.core.IBZEventParam;
import bitzero.server.core.IBZEventType;

public interface IBZEvent {
    public IBZEventType getType();

    public Object getParameter(IBZEventParam var1);
}

