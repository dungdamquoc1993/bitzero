/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.extensions;

import bitzero.server.BitZeroServer;
import bitzero.server.core.IBZEvent;
import bitzero.server.core.IBZEventListener;
import bitzero.server.core.IBZEventManager;
import bitzero.server.core.IBZEventType;
import bitzero.server.entities.User;
import bitzero.server.entities.data.ISFSObject;
import bitzero.server.exceptions.BZException;
import bitzero.server.exceptions.BZRuntimeException;
import bitzero.server.extensions.BZHandlerFactory;
import bitzero.server.extensions.BaseBZExtension;
import bitzero.server.extensions.ExtensionLogLevel;
import bitzero.server.extensions.IClientRequestHandler;
import bitzero.server.extensions.IHandlerFactory;
import bitzero.server.extensions.IServerEventHandler;
import bitzero.server.extensions.data.DataCmd;

public abstract class BZExtension
extends BaseBZExtension {
    public static final String MULTIHANDLER_REQUEST_ID = "__[[REQUEST_ID]]__";
    public final IHandlerFactory handlerFactory;

    public BZExtension() {
        this.handlerFactory = new BZHandlerFactory(this);
    }

    @Override
    public void destroy() {
        this.handlerFactory.clearAll();
        this.removeEventsForListener(this);
    }

    protected void addRequestHandler(short requestId, Class theClass) {
        if (!IClientRequestHandler.class.isAssignableFrom(theClass)) {
            throw new BZRuntimeException(String.format("Provided Request Handler does not implement IClientRequestHandler: %s, Cmd: %s", theClass, requestId));
        }
        this.handlerFactory.addHandler(requestId, theClass);
    }

    protected void addEventHandler(IBZEventType eventType, Class theClass) {
        if (!IServerEventHandler.class.isAssignableFrom(theClass)) {
            throw new BZRuntimeException(String.format("Provided Event Handler does not implement IServerEventHandler: %s, Cmd: %s", theClass, eventType.toString()));
        }
        this.addEventListener(eventType, this);
        this.handlerFactory.addHandler(eventType.toString(), theClass);
    }

    protected void removeRequestHandler(String requestId) {
        this.handlerFactory.removeHandler(requestId);
    }

    protected void removeEventHandler(IBZEventType eventType) {
        this.bz.getEventManager().removeEventListener(eventType, this);
        this.removeEventListener(eventType, this);
        this.handlerFactory.removeHandler(eventType.toString());
    }

    protected void clearAllHandlers() {
        this.handlerFactory.clearAll();
    }

    @Override
    public void handleClientRequest(short requestId, User sender, DataCmd dataCmd) {
        try {
            IClientRequestHandler handler = (IClientRequestHandler)this.handlerFactory.findHandler(requestId);
            if (handler == null) {
                throw new BZRuntimeException("Request handler not found: '" + requestId + "'. Make sure the handler is registered in your extension using addRequestHandler()");
            }
            dataCmd.setId(requestId);
            handler.handleClientRequest(sender, dataCmd);
        }
        catch (InstantiationException err) {
            this.trace(ExtensionLogLevel.WARN, "Cannot instantiate handler class: " + err);
        }
        catch (IllegalAccessException err) {
            this.trace(ExtensionLogLevel.WARN, "Illegal access for handler class: " + err);
        }
    }

    @Override
    public void handleClientRequest(String cmdId, User user, ISFSObject objData) throws BZException {
    }

    @Override
    public void handleServerEvent(IBZEvent event) throws Exception {
        String handlerId = event.getType().toString();
        try {
            IServerEventHandler handler = (IServerEventHandler)this.handlerFactory.findHandler(handlerId);
            if (handler == null) {
                throw new BZRuntimeException("Event handler not found: '" + handlerId + "'. Make sure the handler is registered in your extension using addEventHandler()");
            }
            handler.handleServerEvent(event);
        }
        catch (IllegalAccessException err) {
            this.trace(ExtensionLogLevel.WARN, "Illegal access for handler class: " + err);
        }
        catch (InstantiationException err) {
            this.trace(ExtensionLogLevel.WARN, "Cannot instantiate handler class: " + err);
        }
    }
}

