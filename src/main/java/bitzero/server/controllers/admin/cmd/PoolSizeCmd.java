/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers.admin.cmd;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

public class PoolSizeCmd
extends BaseCmd {
    public int poolId = 0;
    public int size = 0;

    public PoolSizeCmd(DataCmd dataCmd) {
        super(dataCmd);
    }
}

