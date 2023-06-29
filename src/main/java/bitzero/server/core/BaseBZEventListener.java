/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.core;

import bitzero.server.core.IBZEvent;

public class BaseBZEventListener {
    private Object parentObject;

    public BaseBZEventListener() {
        this.parentObject = null;
    }

    public BaseBZEventListener(Object parentObject) {
        this.parentObject = parentObject;
    }

    public Object getParentObject() {
        return this.parentObject;
    }

    public void handleServerEvent(IBZEvent ibzevent) {
    }

    public String toString() {
        return this.parentObject != null ? this.parentObject.toString() : "{ Anonymous listener }";
    }
}

