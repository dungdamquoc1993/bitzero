/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.controllers;

import bitzero.engine.controllers.ControllerType;
import bitzero.engine.exceptions.RequestQueueFullException;
import bitzero.engine.io.IRequest;
import bitzero.engine.service.IService;

public interface IController
extends IService {
    public Object getId();

    public void setId(Object var1);

    public void enqueueRequest(IRequest var1) throws RequestQueueFullException;

    public int getQueueSize();

    public int getMaxQueueSize();

    public void setMaxQueueSize(int var1);

    public int getThreadPoolSize();

    public void setThreadPoolSize(int var1);

    public ControllerType getControllerType();
}

