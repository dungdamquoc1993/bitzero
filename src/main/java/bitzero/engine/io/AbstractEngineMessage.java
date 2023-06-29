/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.io;

import bitzero.engine.io.IEngineMessage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractEngineMessage
implements IEngineMessage {
    protected Object id;
    protected Object content;
    protected Map attributes;

    @Override
    public Object getId() {
        return this.id;
    }

    @Override
    public void setId(Object id) {
        this.id = id;
    }

    @Override
    public Object getContent() {
        return this.content;
    }

    @Override
    public void setContent(Object content) {
        this.content = content;
    }

    @Override
    public Object getAttribute(String key) {
        Object attr = null;
        if (this.attributes != null) {
            attr = this.attributes.get(key);
        }
        return attr;
    }

    @Override
    public void setAttribute(String key, Object attribute) {
        if (this.attributes == null) {
            this.attributes = new ConcurrentHashMap();
        }
        this.attributes.put(key, attribute);
    }
}

