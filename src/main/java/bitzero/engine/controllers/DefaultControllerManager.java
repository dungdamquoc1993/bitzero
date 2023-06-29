/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.controllers;

import bitzero.engine.controllers.IController;
import bitzero.engine.controllers.IControllerManager;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultControllerManager
implements IControllerManager {
    protected String name;
    protected ConcurrentMap<Object,IController> controllers = new ConcurrentHashMap();

    @Override
    public void init(Object o) {
        this.startAllControllers();
    }

    @Override
    public void destroy(Object o) {
        this.shutDownAllControllers();
        this.controllers = null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void handleMessage(Object obj) {
    }

    @Override
    public void addController(Object id, IController controller) {
        this.controllers.putIfAbsent(id, controller);
    }

    @Override
    public IController getControllerById(Object id) {
        return (IController)this.controllers.get(id);
    }

    @Override
    public void removeController(Object id) {
        this.controllers.remove(id);
    }

    private synchronized void shutDownAllControllers() {
        for (IController controller : this.controllers.values()) {
            controller.destroy(null);
        }
    }

    private synchronized void startAllControllers() {
        for (IController controller : this.controllers.values()) {
            controller.init(null);
        }
    }
}

