/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.io;

import bitzero.engine.core.BitZeroEngine;
import bitzero.engine.data.TransportType;
import bitzero.engine.io.AbstractEngineMessage;
import bitzero.engine.io.IResponse;
import bitzero.engine.service.IService;
import bitzero.engine.sessions.ISession;
import bitzero.engine.util.scheduling.ITaskHandler;
import bitzero.engine.util.scheduling.Scheduler;
import bitzero.engine.util.scheduling.Task;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class Response
extends AbstractEngineMessage
implements IResponse {
    private Collection recipients;
    private TransportType type = TransportType.TCP;
    private Object targetController;

    @Override
    public Collection getRecipients() {
        return this.recipients;
    }

    @Override
    public TransportType getTransportType() {
        return this.type;
    }

    @Override
    public boolean isTCP() {
        return this.type == TransportType.TCP;
    }

    @Override
    public boolean isUDP() {
        return this.type == TransportType.UDP;
    }

    @Override
    public void setRecipients(Collection recipents) {
        this.recipients = recipents;
    }

    @Override
    public void setRecipients(ISession session) {
        ArrayList<ISession> recipients = new ArrayList<ISession>();
        recipients.add(session);
        this.setRecipients(recipients);
    }

    @Override
    public void setTransportType(TransportType type) {
        this.type = type;
    }

    @Override
    public void write() {
        BitZeroEngine.getInstance().write(this);
    }

    @Override
    public void write(int delay) {
        Scheduler scheduler = (Scheduler)BitZeroEngine.getInstance().getServiceByName("scheduler");
        Task delayedSocketWriteTask = new Task("delayedSocketWrite");
        delayedSocketWriteTask.getParameters().put("response", this);
        scheduler.addScheduledTask(delayedSocketWriteTask, delay, false, BitZeroEngine.getInstance().getEngineDelayedTaskHandler());
    }

    @Override
    public Object getTargetController() {
        return this.targetController;
    }

    @Override
    public void setTargetController(Object o) {
        this.targetController = o;
    }

    public static IResponse clone(IResponse original) {
        Response newResponse = new Response();
        newResponse.setContent(original.getContent());
        newResponse.setTargetController(original.getTargetController());
        newResponse.setId(original.getId());
        newResponse.setTransportType(original.getTransportType());
        return newResponse;
    }
}

