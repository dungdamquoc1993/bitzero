/*
 * Decompiled with CFR 0_116.
 */
package bitzero.util.logcontroller.business;

public interface ILogController {
    public void writeLog(LogMode var1, String var2);

    public void writeLog(String var1, String var2);

    public void writeLog(LogMode var1, String var2, String var3);

    public static enum LogMode {
        ERROR("error"),
        ACTION("action"),
        PAYMENT("payment"),
        INFO("info");
        
        private final String code;

        private LogMode(String code) {
            this.code = code;
        }

        public String value() {
            return this.code;
        }
    }

}

