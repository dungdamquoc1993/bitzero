/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers.admin.cmd;

import bitzero.server.controllers.SystemRequest;
import bitzero.server.extensions.data.BaseMsg;

public class AdminCmdMsg
extends BaseMsg {
    public String stringReturn = "";

    public AdminCmdMsg() {
        super((Short)SystemRequest.ExecuteCommand.getId());
    }
}

