/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.controllers.v2;

import bitzero.engine.controllers.SimpleReqController;
import bitzero.engine.exceptions.RequestQueueFullException;
import bitzero.engine.io.IRequest;
import bitzero.engine.sessions.ISession;
import bitzero.engine.util.Logging;
import bitzero.server.BitZeroServer;
import bitzero.server.config.IConfigurator;
import bitzero.server.config.ServerSettings;
import bitzero.server.entities.User;
import bitzero.server.entities.data.ISFSObject;
import bitzero.server.entities.managers.IExtensionManager;
import bitzero.server.entities.managers.IUserManager;
import bitzero.server.exceptions.ExceptionMessageComposer;
import bitzero.server.exceptions.SFSExtensionException;
import bitzero.server.extensions.IBZExtension;
import bitzero.server.util.executor.SmartExecutorConfig;
import bitzero.server.util.executor.SmartThreadPoolExecutor;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionReqController
extends SimpleReqController {
    public static final String KEY_EXT_CMD = "c";
    public static final String KEY_EXT_PARAMS = "p";
    public static final String KEY_ROOMID = "r";
    private final Logger logger;
    private final BitZeroServer sfs;
    private ThreadPoolExecutor threadPool;
    private int qSize;

    public ExtensionReqController() {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.sfs = BitZeroServer.getInstance();
    }

    @Override
    public void init(Object o) {
        super.init(o);
        SmartExecutorConfig cfg = BitZeroServer.getInstance().getConfigurator().getServerSettings().extensionThreadPoolSettings;
        cfg.name = "Ext";
        this.threadPool = new SmartThreadPoolExecutor(cfg);
        this.logger.info(this.name + " initalized");
    }

    @Override
    public void enqueueRequest(final IRequest request) throws RequestQueueFullException {
        this.threadPool.execute(new Runnable(){

            @Override
            public void run() {
                if (ExtensionReqController.this.isActive) {
                    try {
                        ExtensionReqController.this.processRequest(request);
                    }
                    catch (Throwable t) {
                        Logging.logStackTrace(ExtensionReqController.this.logger, t);
                    }
                }
            }
        });
    }

    protected void processRequest(IRequest request) throws Exception {
        String cmd;
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(request.toString());
        }
        long t1 = System.nanoTime();
        User sender = this.sfs.getUserManager().getUserBySession(request.getSender());
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
        IBZExtension extension = this.sfs.getExtensionManager().getMainExtension();
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

    @Override
    public int getQueueSize() {
        return this.threadPool.getQueue().size();
    }

    @Override
    public int getMaxQueueSize() {
        return this.qSize;
    }

    @Override
    public void setMaxQueueSize(int size) {
        this.qSize = size;
    }

    @Override
    public int getThreadPoolSize() {
        return this.threadPool.getPoolSize();
    }

    @Override
    public void setThreadPoolSize(int size) {
    }

    @Override
    public void handleMessage(Object message) {
    }

}

