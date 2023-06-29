/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers.admin.cmd;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

public class AdminLoginCmd
extends BaseCmd {
    public String username = "";
    public String version = "";

    public AdminLoginCmd(DataCmd dataCmd) {
        super(dataCmd);
    }
}

