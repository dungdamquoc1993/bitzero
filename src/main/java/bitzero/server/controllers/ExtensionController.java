/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.controllers;

import bitzero.engine.controllers.AbstractController;
import bitzero.engine.io.IRequest;
import bitzero.engine.sessions.ISession;
import bitzero.server.BitZeroServer;
import bitzero.server.entities.User;
import bitzero.server.entities.data.ISFSObject;
import bitzero.server.entities.managers.IExtensionManager;
import bitzero.server.entities.managers.IUserManager;
import bitzero.server.exceptions.ExceptionMessageComposer;
import bitzero.server.exceptions.SFSExtensionException;
import bitzero.server.extensions.IBZExtension;
import bitzero.server.extensions.data.DataCmd;
import bitzero.server.util.PacketCount;
import bitzero.server.util.PacketType;
import java.nio.ByteBuffer;

import bitzero.util.common.business.CommonHandle;
import bitzero.util.common.business.Debug;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionController
extends AbstractController {
    public static final String KEY_EXT_CMD = "c";
    public static final String KEY_EXT_PARAMS = "p";
    public static final String KEY_ROOMID = "r";
    private final Logger logger;
    private final BitZeroServer bz;
    private IExtensionManager extensionManager;

    public ExtensionController() {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.bz = BitZeroServer.getInstance();
    }

    @Override
    public void init(Object o) {
        super.init(o);
        this.extensionManager = this.bz.getExtensionManager();
    }

    @Override
    public void destroy(Object o) {
        super.destroy(o);
        this.extensionManager = null;
    }

    @Override
    public void processRequest(IRequest request) throws Exception {
        this.processRegularRequest(request);
    }

    public void processRegularRequest(IRequest request) throws Exception {
        this.logger.debug(request.toString());
        CommonHandle.writeErrLog(request.toString());
        Debug.trace("Check request cmd 1: " +  request.getId());
       
        long t1 = System.currentTimeMillis();
		short cmd = 0;
		if (request.getId() != null) {
			if (request.getId() instanceof Integer) {
				Integer idInt = (Integer) request.getId();
				cmd = idInt.shortValue();
			} else {
				cmd = (Short) request.getId();
			}
		}
        DataCmd params = new DataCmd(((ByteBuffer)request.getContent()).array());
        User sender = this.bz.getUserManager().getUserBySession(request.getSender());
        IBZExtension extension = this.bz.getExtensionManager().getMainExtension();
        if (sender != null) {
            sender.updateLastRequestTime();
        }
//        String logSender = sender == null ? request.getSender().toString() : sender.getName();
        Debug.trace("Check request cmd cmdId: " + cmd + new StringBuilder().append(" - sender : ").append(sender).toString());
        try {
            BitZeroServer.getInstance().getPacketCount().addPacket(PacketType.INCOMING, cmd);
            if (sender == null) {
                extension.doLogin(cmd, request.getSender(), params);
                Debug.trace("Check request cmd 4: " +  request.getId());
            } else if (cmd != 1) {
                Debug.trace("Check request cmd 3: " +  request.getId());
                extension.handleClientRequest(cmd, sender, params);
            }
        }
        catch (Exception e) {
            ExceptionMessageComposer composer = new ExceptionMessageComposer(e);
            composer.setDescription("Error while handling client request in extension: " + extension.getName());
            composer.addInfo("Extension Cmd: " + request.getId());
            this.logger.error(composer.toString());
        }
        long t2 = System.currentTimeMillis();
        long delta = (t2 - t1) / 1000;
        Debug.trace("Check request cmd Extension call executed in: " + delta);
        if (delta > 1000.0) {
        	Debug.trace("Check request cmd Slow  Extension call executed : " + delta + new StringBuilder().append("in Cmd ").append(cmd).toString());
        }
    }

    protected void processWebsocketRequest(IRequest request) throws Exception {
        String cmd;
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(request.toString());
        }
        long t1 = System.nanoTime();
        User sender = this.bz.getUserManager().getUserBySession(request.getSender());
        if (sender != null) {
            sender.updateLastRequestTime();
        }
        ISFSObject reqObj = (ISFSObject)request.getContent();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(reqObj.getDump());
        }
        if ((cmd = reqObj.getUtfString("c")) == null || cmd.length() == 0) {
            throw new SFSExtensionException("Extension Request refused. Missing CMD. " + sender);
        }
        ISFSObject params = reqObj.getSFSObject("p");
        IBZExtension extension = this.bz.getExtensionManager().getMainExtension();
        String logSender = sender == null ? request.getSender().toString() : sender.getName();
        //LoggerFactory.getLogger((String)"request").debug("Extension call cmdId: " + cmd + new StringBuilder().append(" - from : ").append(logSender).toString());
        try {
            extension.handleClientRequest(cmd, sender, params);
        }
        catch (Exception e) {
            ExceptionMessageComposer composer = new ExceptionMessageComposer(e);
            composer.setDescription("Error while handling client request in extension: " + extension.toString());
            composer.addInfo("Extension Cmd: " + cmd);
            this.logger.error(composer.toString());
        }
        long t2 = System.nanoTime();
        if (this.logger.isDebugEnabled()) {
            //this.logger.debug("Extension call executed in: " + (double)(t2 - t1) / 1000000.0);
        }
    }
}

