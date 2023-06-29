/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.extensions;

import bitzero.engine.sessions.ISession;
import bitzero.server.api.IBZApi;
import bitzero.server.core.IBZEvent;
import bitzero.server.core.IBZEventListener;
import bitzero.server.entities.User;
import bitzero.server.extensions.BZExtension;
import bitzero.server.extensions.ExtensionLogLevel;
import bitzero.server.extensions.IClientRequestHandler;
import bitzero.server.extensions.data.BaseMsg;
import java.util.Collection;
import java.util.List;

public abstract class BaseClientRequestHandler
implements IClientRequestHandler,
IBZEventListener {
    private BZExtension parentExtension;

    @Override
    public void init() {
        this.parentExtension.trace(this.getClass().getName(), "initilazion");
    }

    @Override
    public void handleServerEvent(IBZEvent ibzevent) throws Exception {
    }

    @Override
    public BZExtension getParentExtension() {
        return this.parentExtension;
    }

    @Override
    public void setParentExtension(BZExtension ext) {
        this.parentExtension = ext;
    }

    protected IBZApi getApi() {
        return this.parentExtension.bzApi;
    }

    protected void send(BaseMsg msg, User recipient) {
        this.parentExtension.send(msg, recipient);
    }

    protected void send(BaseMsg msg, List recipients) {
        this.parentExtension.send(msg, recipients);
    }

    protected /* varargs */ void trace(Object ... args) {
        this.parentExtension.trace(args);
    }

    protected /* varargs */ void trace(ExtensionLogLevel level, Object ... args) {
        this.parentExtension.trace(level, args);
    }
}

