/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.extensions;

import bitzero.server.core.IBZEvent;
import bitzero.server.exceptions.BZException;
import bitzero.server.extensions.BZExtension;

public interface IServerEventHandler {
    public void handleServerEvent(IBZEvent var1) throws BZException;

    public void setParentExtension(BZExtension var1);

    public BZExtension getParentExtension();
}

