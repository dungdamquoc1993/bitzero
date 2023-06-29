/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.core;

import bitzero.engine.core.AbstractMethodDispatcher;
import bitzero.engine.core.BitZeroEngine;
import bitzero.engine.events.Event;
import bitzero.engine.events.IEvent;
import bitzero.engine.events.IEventListener;

public class EngineEventHandler
extends AbstractMethodDispatcher
implements IEventListener {
    BitZeroEngine engine = BitZeroEngine.getInstance();

    public EngineEventHandler() {
        this.registerMethods();
    }

    private void registerMethods() {
        this.registerMethod("sessionAdded", "onSessionAdded");
        this.registerMethod("sessionLost", "onSessionLost");
        this.registerMethod("packetDropped", "onPacketDropped");
    }

    @Override
    public void handleEvent(IEvent event) {
        try {
            this.callMethod(event.getName(), new Object[]{event});
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSessionLost(Object o) {
        this.engine.dispatchEvent((Event)o);
    }

    public void onSessionAdded(Object o) {
        this.engine.dispatchEvent((Event)o);
    }

    public void onPacketDropped(Object o) {
        this.engine.dispatchEvent((Event)o);
    }
}

