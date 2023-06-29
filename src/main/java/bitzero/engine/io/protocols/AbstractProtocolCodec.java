/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.engine.io.protocols;

import bitzero.engine.controllers.IController;
import bitzero.engine.controllers.IControllerManager;
import bitzero.engine.core.BitZeroEngine;
import bitzero.engine.exceptions.RequestQueueFullException;
import bitzero.engine.io.IOHandler;
import bitzero.engine.io.IProtocolCodec;
import bitzero.engine.io.IRequest;
import bitzero.engine.util.Logging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractProtocolCodec
implements IProtocolCodec {
    protected final IControllerManager controllerManager;
    protected final BitZeroEngine engine = BitZeroEngine.getInstance();
    protected final Logger logger;
    protected IOHandler ioHandler;

    public AbstractProtocolCodec() {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.controllerManager = this.engine.getControllerManager();
    }

    protected void dispatchRequestToController(IRequest request, Object controllerId) {
        if (controllerId == null) {
            throw new IllegalStateException("Invalid Request: missing controllerId -> " + request);
        }
        IController controller = this.controllerManager.getControllerById(controllerId);
        try {
            controller.enqueueRequest(request);
        }
        catch (RequestQueueFullException err) {
            this.logger.error(String.format("RequestQueue is full (%s/%s). Dropping incoming request: ", controller.getQueueSize(), controller.getMaxQueueSize(), request.toString()));
        }
        catch (NullPointerException err) {
            this.logger.warn("Can't handle this request! The related controller is not found: " + controllerId + ", Request: " + request);
            Logging.logStackTrace(this.logger, err);
        }
    }

    @Override
    public IOHandler getIOHandler() {
        return this.ioHandler;
    }

    @Override
    public void setIOHandler(IOHandler handler) {
        this.ioHandler = handler;
    }
}

