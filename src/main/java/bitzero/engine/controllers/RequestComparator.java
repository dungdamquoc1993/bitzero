/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.controllers;

import bitzero.engine.data.MessagePriority;
import bitzero.engine.io.IRequest;
import java.util.Comparator;

public class RequestComparator
implements Comparator {
    public int compare(IRequest r1, IRequest r2) {
        int res = 0;
        res = r1.getPriority().getValue() < r2.getPriority().getValue() ? -1 : (r1.getPriority() == r2.getPriority() ? (r1.getTimeStamp() < r2.getTimeStamp() ? -1 : (r1.getTimeStamp() > r2.getTimeStamp() ? 1 : 0)) : 1);
        return res;
    }

    public int compare(Object obj, Object obj1) {
        return this.compare((IRequest)obj, (IRequest)obj1);
    }
}

