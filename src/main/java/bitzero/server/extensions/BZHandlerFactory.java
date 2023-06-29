/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.extensions;

import bitzero.server.extensions.BZExtension;
import bitzero.server.extensions.IClientRequestHandler;
import bitzero.server.extensions.IHandlerFactory;
import bitzero.server.extensions.IServerEventHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BZHandlerFactory
implements IHandlerFactory {
    private static final short NUM_SEPARATOR = 1000;
    private static final String DOT_SEPARATOR = ".";
    private static final Boolean CACHE_HANDLER = true;
    private final Map handlers = new ConcurrentHashMap();
    private final Map cachedHandlers = new ConcurrentHashMap();
    private final BZExtension parentExtension;

    public BZHandlerFactory(BZExtension parentExtension) {
        this.parentExtension = parentExtension;
    }

    private short makeId(short handlerKey) {
        if (handlerKey < 1000) {
            return handlerKey;
        }
        short id = (short)(handlerKey / 1000);
        id = (short)(id * 1000);
        return id;
    }

    @Override
    public void addHandler(short handlerKey, Class handlerClass) {
        this.handlers.put(this.makeId(handlerKey), handlerClass);
        try {
            this.findHandler(handlerKey);
        }
        catch (InstantiationException var3_3) {
        }
        catch (IllegalAccessException var3_4) {
            // empty catch block
        }
    }

    @Override
    public synchronized void clearAll() {
        this.handlers.clear();
        this.cachedHandlers.clear();
    }

    @Override
    public synchronized void removeHandler(short handlerKey) {
        short id = this.makeId(handlerKey);
        this.handlers.remove(id);
        if (this.cachedHandlers.containsKey(id)) {
            this.cachedHandlers.remove(id);
        }
    }

    @Override
    public Object findHandler(short key) throws InstantiationException, IllegalAccessException {
        Object handler = this.getHandlerInstance(this.makeId(key));
        return handler;
    }

    private Object getHandlerInstance(short key) throws InstantiationException, IllegalAccessException {
        Object handler = this.cachedHandlers.get(key);
        if (handler != null) {
            return handler;
        }
        Class handlerClass = (Class)this.handlers.get(key);
        if (handlerClass == null) {
            return null;
        }
        handler = handlerClass.newInstance();
        if (handler instanceof IClientRequestHandler) {
            ((IClientRequestHandler)handler).setParentExtension(this.parentExtension);
            ((IClientRequestHandler)handler).init();
        } else if (handler instanceof IServerEventHandler) {
            ((IServerEventHandler)handler).setParentExtension(this.parentExtension);
        }
        if (CACHE_HANDLER.booleanValue()) {
            this.cachedHandlers.put(key, handler);
        }
        return handler;
    }

    @Override
    public void addHandler(String handlerKey, Class handlerClass) {
        this.handlers.put(handlerKey, handlerClass);
    }

    @Override
    public synchronized void removeHandler(String handlerKey) {
        this.handlers.remove(handlerKey);
        if (this.cachedHandlers.containsKey(handlerKey)) {
            this.cachedHandlers.remove(handlerKey);
        }
    }

    @Override
    public Object findHandler(String key) throws InstantiationException, IllegalAccessException {
        Object handler = this.getHandlerInstance(key);
        if (handler == null) {
            int lastDotPos = key.lastIndexOf(".");
            if (lastDotPos > 0) {
                key = key.substring(0, lastDotPos);
            }
            handler = this.getHandlerInstance(key);
        }
        return handler;
    }

    private Object getHandlerInstance(String key) throws InstantiationException, IllegalAccessException {
        Object handler = this.cachedHandlers.get(key);
        if (handler != null) {
            return handler;
        }
        Class handlerClass = (Class)this.handlers.get(key);
        if (handlerClass == null) {
            return null;
        }
        handler = handlerClass.newInstance();
        if (handler instanceof IClientRequestHandler) {
            ((IClientRequestHandler)handler).setParentExtension(this.parentExtension);
        } else if (handler instanceof IServerEventHandler) {
            ((IServerEventHandler)handler).setParentExtension(this.parentExtension);
        }
        if (CACHE_HANDLER.booleanValue()) {
            this.cachedHandlers.put(key, handler);
        }
        return handler;
    }
}

