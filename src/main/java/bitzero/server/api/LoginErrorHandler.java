/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.api;

import bitzero.engine.io.Response;
import bitzero.engine.sessions.ISession;
import bitzero.server.api.cmd.LoginErrorMsg;
import bitzero.server.config.DefaultConstants;
import bitzero.server.controllers.SystemRequest;
import bitzero.server.exceptions.BZErrorCode;
import bitzero.server.exceptions.BZErrorData;
import bitzero.server.exceptions.BZLoginException;
import bitzero.server.exceptions.IErrorCode;
import java.util.Arrays;
import java.util.List;

public final class LoginErrorHandler {
    public void execute(ISession sender, BZLoginException err) {
        if (err.getErrorData() == null) {
            BZErrorData errData = new BZErrorData(BZErrorCode.GENERIC_ERROR);
            errData.addParameter("Cant log in !");
            err = new BZLoginException(err.getMessage(), errData);
        }
        LoginErrorMsg msg = new LoginErrorMsg(err.getErrorData().getCode().getId());
        msg.message = err.getMessage() + " - " + Arrays.toString(err.getErrorData().getParams().toArray());
        Response response = new Response();
        response.setId(SystemRequest.Login.getId());
        response.setTargetController(DefaultConstants.CORE_SYSTEM_CONTROLLER_ID);
        response.setContent(msg.createData());
        response.setRecipients(sender);
        response.write();
    }
}

