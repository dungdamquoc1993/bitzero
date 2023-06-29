/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.entities;

import bitzero.server.config.ConfigHandle;
import bitzero.server.entities.managers.IExtensionManager;
import bitzero.server.extensions.IBZExtension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtensionManager
implements IExtensionManager {
    Map<String, IBZExtension> extensions;
    IBZExtension extMain = null;

    @Override
    public IBZExtension getMainExtension() {
        return this.extMain;
    }

    @Override
    public IBZExtension getAdminExtension() {
        return null;
    }

    private void initExtensions() {
        this.extensions = new HashMap<String, IBZExtension>();
        String mainExtPath = ConfigHandle.instance().get("extension.main.path");
        try {
            this.extMain = (IBZExtension)Class.forName(mainExtPath).newInstance();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (this.extMain != null) {
            this.extMain.init();
            this.extensions.put("main", this.extMain);
        }
    }

    @Override
    public List getExtensions() {
        return null;
    }

    @Override
    public int getExtensionsCount() {
        return 0;
    }

    @Override
    public void init() {
        this.initExtensions();
    }

    @Override
    public void destroy() {
        this.getMainExtension().destroy();
    }

    @Override
    public void monitor() {
        this.getMainExtension().monitor();
    }

    @Override
    public void activateAllExtensions() {
    }

    @Override
    public void deactivateAllExtensions() {
    }

    @Override
    public boolean isExtensionMonitorActive() {
        return false;
    }

    @Override
    public void setExtensionMonitorActive(boolean flag) {
    }
}

