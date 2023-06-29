/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.controllers;

import bitzero.engine.controllers.IController;
import bitzero.engine.service.IService;

public interface IControllerManager
extends IService {
    public IController getControllerById(Object var1);

    public void addController(Object var1, IController var2);

    public void removeController(Object var1);
}

