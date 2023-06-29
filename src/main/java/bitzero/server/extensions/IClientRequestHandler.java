/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.extensions;

import bitzero.server.entities.User;
import bitzero.server.extensions.BZExtension;
import bitzero.server.extensions.data.DataCmd;

public interface IClientRequestHandler {
    public void handleClientRequest(User var1, DataCmd var2);

    public void setParentExtension(BZExtension var1);

    public BZExtension getParentExtension();

    public void init();
}

