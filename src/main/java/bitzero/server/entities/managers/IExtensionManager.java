/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.entities.managers;

import bitzero.server.extensions.IBZExtension;
import java.util.List;

public interface IExtensionManager {
    public int getExtensionsCount();

    public List getExtensions();

    public IBZExtension getMainExtension();

    public IBZExtension getAdminExtension();

    public void init();

    public void destroy();

    public void monitor();

    public void activateAllExtensions();

    public void deactivateAllExtensions();

    public boolean isExtensionMonitorActive();

    public void setExtensionMonitorActive(boolean var1);
}

