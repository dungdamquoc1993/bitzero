/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.core;

import bitzero.engine.service.IService;
import bitzero.server.core.IBZEvent;
import bitzero.server.core.IBZEventDispatcher;
import java.util.concurrent.Executor;

public interface IBZEventManager
extends IBZEventDispatcher,
IService {
    public void setThreadPoolSize(int var1);

    public void dispatchImmediateEvent(IBZEvent var1);

    public Executor getThreadPool();
}

