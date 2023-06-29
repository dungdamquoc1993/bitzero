/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.service;

import bitzero.engine.events.IEvent;
import bitzero.engine.events.IEventDispatcher;
import bitzero.engine.events.IEventListener;
import bitzero.engine.service.IService;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class BaseCoreService
implements IService,
IEventDispatcher {
    private String serviceName;
    private Map listenersByEvent = new ConcurrentHashMap();

    @Override
    public void init(Object obj) {
    }

    @Override
    public void destroy(Object o) {
        this.listenersByEvent.clear();
    }

    @Override
    public String getName() {
        return this.serviceName;
    }

    @Override
    public void setName(String name) {
        this.serviceName = name;
    }

    @Override
    public void handleMessage(Object obj) {
    }

    @Override
    public synchronized void addEventListener(String eventType, IEventListener listener) {
        CopyOnWriteArraySet<IEventListener> listeners = (CopyOnWriteArraySet<IEventListener>)this.listenersByEvent.get(eventType);
        if (listeners == null) {
            listeners = new CopyOnWriteArraySet<IEventListener>();
            this.listenersByEvent.put(eventType, listeners);
        }
        listeners.add(listener);
    }

    @Override
    public boolean hasEventListener(String eventType) {
        boolean found = false;
        Set listeners = (Set)this.listenersByEvent.get(eventType);
        if (listeners != null && listeners.size() > 0) {
            found = true;
        }
        return found;
    }

    @Override
    public void removeEventListener(String eventType, IEventListener listener) {
        Set listeners = (Set)this.listenersByEvent.get(eventType);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    @Override
    public void dispatchEvent(IEvent event) {
        CopyOnWriteArraySet<IEventListener> listeners = (CopyOnWriteArraySet<IEventListener>)this.listenersByEvent.get(event.getName());
        if (listeners != null && listeners.size() > 0) {
            for (IEventListener listenerObj : listeners) {
                listenerObj.handleEvent(event);
            }
        }
    }
}

