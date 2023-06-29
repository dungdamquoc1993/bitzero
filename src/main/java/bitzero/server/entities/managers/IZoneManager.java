/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.entities.managers;

import bitzero.server.config.ZoneSettings;
import bitzero.server.core.ICoreService;
import bitzero.server.entities.Zone;
import bitzero.server.exceptions.BZException;
import java.util.List;

public interface IZoneManager
extends ICoreService {
    public List<Zone> getZoneList();

    public Zone getZoneByName(String var1);

    public Zone getZoneById(int var1);

    public void initializeZones() throws BZException;

    public void addZone(Zone var1);

    public void toggleZone(String var1, boolean var2);

    public Zone createZone(ZoneSettings var1) throws BZException;
}

