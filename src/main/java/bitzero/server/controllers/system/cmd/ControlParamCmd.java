/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers.system.cmd;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;
import java.nio.ByteBuffer;

public class ControlParamCmd
extends BaseCmd {
    public String command = "";
    public String[] param = new String[0];

    public ControlParamCmd(DataCmd dataCmd) {
        super(dataCmd);
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = this.makeBuffer();
        try {
            this.command = this.readString(bf);
            this.param = this.readStringArray(bf);
        }
        catch (Exception var2_2) {
            // empty catch block
        }
    }
}

