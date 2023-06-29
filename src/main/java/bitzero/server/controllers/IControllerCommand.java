/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers;

import bitzero.engine.io.IRequest;

public interface IControllerCommand {
    public boolean validate(IRequest var1) throws Exception;

    public Object preProcess(IRequest var1) throws Exception;

    public void execute(IRequest var1) throws Exception;

    public void executeWebsocket(IRequest var1) throws Exception;
}

