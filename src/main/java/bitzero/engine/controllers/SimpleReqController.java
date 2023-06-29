/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.engine.controllers;

import bitzero.engine.controllers.ControllerType;
import bitzero.engine.controllers.IController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SimpleReqController
implements IController {
    protected Object id;
    protected String name;
    protected volatile boolean isActive = false;
    protected final Logger bootLogger = LoggerFactory.getLogger((String)"bootLogger");
    protected final Logger logger;

    public SimpleReqController() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public void init(Object o) {
        if (this.isActive) {
            throw new IllegalArgumentException("Object is already initialized. Destroy it first!");
        }
        this.isActive = true;
        this.bootLogger.info(String.format("Controller started: %s ", this.getClass().getName()));
    }

    @Override
    public void destroy(Object o) {
        this.isActive = false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        if (this.name != null) {
            throw new IllegalStateException("Controller already has a name: " + this.name);
        }
        this.name = name;
    }

    @Override
    public Object getId() {
        return this.id;
    }

    @Override
    public void setId(Object id) {
        if (this.id != null) {
            throw new IllegalStateException("Controller already has an id: " + this.id);
        }
        this.id = id;
    }

    @Override
    public ControllerType getControllerType() {
        return ControllerType.WEBSOCKET;
    }
}

