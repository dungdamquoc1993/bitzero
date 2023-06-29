/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.annotations;

import java.lang.annotation.Annotation;

public interface Instantiation
extends Annotation {
    public InstantiationMode value();

    public static enum InstantiationMode {
        NEW_INSTANCE,
        SINGLE_INSTANCE;
        

        private InstantiationMode() {
        }
    }

}

