/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.core;

import bitzero.engine.core.BitZeroEngine;
import bitzero.engine.events.IEvent;
import bitzero.engine.events.IEventListener;

public class EngineIOEventHandler
implements IEventListener {
    private final BitZeroEngine engine = BitZeroEngine.getInstance();

    @Override
    public void handleEvent(IEvent event) {
        this.engine.dispatchEvent(event);
    }
}

