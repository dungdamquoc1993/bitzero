/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.clustering;

import bitzero.engine.events.IEvent;
import bitzero.engine.service.IService;
import java.util.List;

public interface IClusterManager
extends IService {
    public String getLocalNodeName();

    public void registerLocalNode();

    public void removeNode(String var1);

    public void clearDataStore();

    public boolean isReadyForEvents();

    public void dispatchClusterEvent(IEvent var1, String var2);

    public void broadcastClusterEvent(IEvent var1, List var2);

    public void broadcastClusterEvent(IEvent var1);
}

