/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.config;

import bitzero.server.config.CoreSettings;
import bitzero.server.config.ServerSettings;
import bitzero.server.config.ZoneSettings;
import bitzero.server.exceptions.BZException;
import java.io.IOException;
import java.util.List;

public interface IConfigurator {
    public void loadConfiguration() throws Exception;

    public List loadZonesConfiguration() throws BZException;

    public void saveServerSettings(boolean var1) throws IOException;

    public void saveZoneSettings(ZoneSettings var1, boolean var2) throws IOException;

    public void saveZoneSettings(ZoneSettings var1, boolean var2, String var3) throws IOException;

    public CoreSettings getCoreSettings();

    public ServerSettings getServerSettings();

    public List getZoneSettings();

    public ZoneSettings getZoneSetting(String var1);

    public ZoneSettings getZoneSetting(int var1);

    public void saveNewZoneSettings(ZoneSettings var1) throws IOException;

    public void removeZoneSetting(String var1) throws IOException;
}

