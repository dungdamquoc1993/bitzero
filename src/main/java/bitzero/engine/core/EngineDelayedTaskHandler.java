/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.core;

import bitzero.engine.core.AbstractMethodDispatcher;
import bitzero.engine.core.BitZeroEngine;
import bitzero.engine.io.IResponse;
import bitzero.engine.util.scheduling.ITaskHandler;
import bitzero.engine.util.scheduling.Task;
import java.util.Map;

public final class EngineDelayedTaskHandler
extends AbstractMethodDispatcher
implements ITaskHandler {
    public EngineDelayedTaskHandler() {
        this.registerTasks();
    }

    private void registerTasks() {
        this.registerMethod("delayedSocketWrite", "onDelayedSocketWrite");
        this.registerMethod("RESTART", "onRestart");
    }

    @Override
    public void doTask(Task task) throws Exception {
        try {
            this.callMethod((String)task.getId(), new Object[]{task});
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onRestart(Object o) {
        BitZeroEngine.getInstance().restart();
    }

    public void onDelayedSocketWrite(Object o) {
        Task task = (Task)o;
        IResponse response = (IResponse)task.getParameters().get("response");
        if (response != null) {
            response.write();
        }
    }
}

