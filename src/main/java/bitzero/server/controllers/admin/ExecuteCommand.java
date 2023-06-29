/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 */
package bitzero.server.controllers.admin;

import bitzero.engine.io.IRequest;
import bitzero.engine.io.Response;
import bitzero.engine.sessions.ISession;
import bitzero.server.config.DefaultConstants;
import bitzero.server.controllers.BaseControllerCommand;
import bitzero.server.controllers.SystemRequest;
import bitzero.server.controllers.admin.cmd.AdminCmd;
import bitzero.server.controllers.admin.cmd.AdminCmdMsg;
import bitzero.server.controllers.admin.helper.AdminCmdLog;
import bitzero.server.extensions.data.DataCmd;
import bitzero.server.util.system.ILogDevice;
import bitzero.server.util.system.SysCommandExecutor;
import java.nio.ByteBuffer;
import org.slf4j.Logger;

public class ExecuteCommand
extends BaseControllerCommand {
    private static ISession admin = null;
    private static SysCommandExecutor sysCmdExecutor = null;

    public ExecuteCommand() {
        super(SystemRequest.ExecuteCommand);
    }

    @Override
    public boolean validate(IRequest request) {
        return this.checkSuperAdmin(request.getSender());
    }

    @Override
    public void execute(IRequest request) throws Exception {
        ISession sender = request.getSender();
        DataCmd params = new DataCmd(((ByteBuffer)request.getContent()).array());
        AdminCmd adminCmd = new AdminCmd(params);
        adminCmd.unpackData();
        String result = "";
        try {
            if (sysCmdExecutor == null) {
                sysCmdExecutor = new SysCommandExecutor();
            } else {
                sysCmdExecutor.destroy();
            }
            sysCmdExecutor.setOutputLogDevice(new AdminCmdLog(sender));
            sysCmdExecutor.setErrorLogDevice(new AdminCmdLog(sender));
            int exitStatus = sysCmdExecutor.runCommand(adminCmd.stringCmd);
            this.logger.info("Cmd: ", (Object)adminCmd.stringCmd);
            this.logger.info("Result: ", (Object)exitStatus);
            result = "exitStatus: " + exitStatus;
        }
        catch (Exception ex) {
            this.logger.warn("Execute Command Error ", (Object)adminCmd.stringCmd);
            result = ex.getMessage();
        }
        AdminCmdMsg msg = new AdminCmdMsg();
        msg.stringReturn = result;
        Response response = new Response();
        response.setId(this.getId());
        response.setRecipients(sender);
        response.setContent(msg.createData());
        response.setTargetController(DefaultConstants.CORE_SYSTEM_CONTROLLER_ID);
        response.write();
    }
}

