/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers.admin;

import bitzero.engine.io.IRequest;
import bitzero.engine.io.Response;
import bitzero.engine.sessions.ISession;
import bitzero.server.BitZeroServer;
import bitzero.server.api.IBZApi;
import bitzero.server.config.DefaultConstants;
import bitzero.server.config.IConfigurator;
import bitzero.server.config.ServerSettings;
import bitzero.server.controllers.BaseControllerCommand;
import bitzero.server.controllers.SystemRequest;
import bitzero.server.controllers.admin.cmd.AdminLoginCmd;
import bitzero.server.controllers.admin.helper.LogUtil;
import bitzero.server.exceptions.BZErrorCode;
import bitzero.server.exceptions.BZErrorData;
import bitzero.server.exceptions.BZLoginException;
import bitzero.server.exceptions.IErrorCode;
import bitzero.server.extensions.data.DataCmd;
import java.nio.ByteBuffer;
import java.util.List;

public class AdminLogin
extends BaseControllerCommand {
    private final ServerSettings settings;

    public AdminLogin() {
        super(SystemRequest.Login);
        this.settings = this.bz.getConfigurator().getServerSettings();
    }

    @Override
    public boolean validate(IRequest irequest) {
        return true;
    }

    @Override
    public void execute(IRequest request) throws Exception {
        ISession sender = request.getSender();
        DataCmd params = new DataCmd(((ByteBuffer)request.getContent()).array());
        AdminLoginCmd adminCmd = new AdminLoginCmd(params);
        adminCmd.unpackData();
        if (LogUtil.checkLog(sender, adminCmd.version, adminCmd.username)) {
            this.setAdmin(sender, adminCmd.username);
        } else {
            boolean success;
            String name = adminCmd.username;
            String pass = adminCmd.version;
            if (this.settings.remoteAdmin.adminTcpPort > -1 && sender.getServerPort() != this.settings.remoteAdmin.adminTcpPort) {
                BZErrorData data = new BZErrorData(BZErrorCode.GENERIC_ERROR);
                data.addParameter("Invalid administration TCP port");
                throw new BZLoginException("Admin login refused to user '" + name + "': invalid TCP port", data);
            }
            if (this.settings.remoteAdmin.allowedRemoteAddresses != null && this.settings.remoteAdmin.allowedRemoteAddresses.size() > 0 && !this.settings.remoteAdmin.allowedRemoteAddresses.contains(sender.getAddress())) {
                BZErrorData data = new BZErrorData(BZErrorCode.GENERIC_ERROR);
                data.addParameter("Client is not allowed to administer server; invalid remote IP address");
                throw new BZLoginException("Admin login refused to user '" + name + "': invalid remote IP address", data);
            }
            ServerSettings.AdminUser adminUser = this.getAdminByName(name);
            boolean bl = success = adminUser != null;
            if (success) {
                boolean bl2 = success = success && this.api.checkSecurePassword(sender, adminUser.password, pass);
            }
            if (!success) {
                BZErrorData data = new BZErrorData(BZErrorCode.GENERIC_ERROR);
                data.addParameter("Invalid administrator username or password");
                throw new BZLoginException("Admin login refused to user '" + name + "': invalid credentials", data);
            }
            this.setAdmin(sender, adminCmd.username);
        }
        Response response = new Response();
        response.setId(this.getId());
        response.setRecipients(sender);
        response.setContent(new byte[]{0});
        response.setTargetController(DefaultConstants.CORE_SYSTEM_CONTROLLER_ID);
        response.write();
    }

    private ServerSettings.AdminUser getAdminByName(String name) {
        for (ServerSettings.AdminUser user : this.settings.remoteAdmin.administrators) {
            if (!user.login.equals(name)) continue;
            return user;
        }
        return null;
    }
}

