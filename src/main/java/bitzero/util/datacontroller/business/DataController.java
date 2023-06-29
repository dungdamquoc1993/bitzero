/*
 * Decompiled with CFR 0_116.
 */
package bitzero.util.datacontroller.business;

import bitzero.server.config.ConfigHandle;
import bitzero.util.common.business.CommonHandle;
import bitzero.util.datacontroller.business.IDataController;

public class DataController {
    private static IDataController _instance = null;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static IDataController getController() {
        Class<DataController> class_ = DataController.class;
        synchronized (DataController.class) {
            if (_instance == null) {
                try {
                    _instance = (IDataController)Class.forName(ConfigHandle.instance().get("dataProvider")).newInstance();
                }
                catch (Exception e) {
                    CommonHandle.writeErrLog(e);
                }
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return _instance;
        }
    }
}

