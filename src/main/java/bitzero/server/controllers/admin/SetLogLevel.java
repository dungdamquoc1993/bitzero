/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Level
 *  org.apache.log4j.Logger
 *  org.slf4j.Logger
 */
package bitzero.server.controllers.admin;

import bitzero.engine.io.IRequest;
import bitzero.engine.sessions.ISession;
import bitzero.server.controllers.BaseControllerCommand;
import bitzero.server.controllers.SystemRequest;
import bitzero.server.controllers.admin.cmd.LogLevelCmd;
import bitzero.server.extensions.data.DataCmd;
import java.nio.ByteBuffer;
import org.apache.log4j.Level;
import org.slf4j.Logger;

public class SetLogLevel
extends BaseControllerCommand {
    public SetLogLevel() {
        super(SystemRequest.SetLogLevel);
    }

    @Override
    public boolean validate(IRequest request) {
        return this.checkSuperAdmin(request.getSender());
    }

    @Override
    public void execute(IRequest request) throws Exception {
        ISession sender = request.getSender();
        DataCmd params = new DataCmd(((ByteBuffer)request.getContent()).array());
        LogLevelCmd logCmd = new LogLevelCmd(params);
        logCmd.unpackData();
        try {
            org.apache.log4j.Logger.getLogger((String)logCmd.logName).setLevel(Level.toLevel((String)logCmd.level));
            this.logger.warn("Log Catalog : " + logCmd.logName + " ,changed to LogLevel :" + logCmd.level);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

