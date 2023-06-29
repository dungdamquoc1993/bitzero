/*
 * Decompiled with CFR 0_116.
 */
package bitzero.util.socialcontroller.business.zm;

public class ZM_Config {
    public String apiURL = "";
    public String apiKey = "";
    public String secret = "";
    public long timeout = 30;

    public ZM_Config() {
    }

    public ZM_Config(String apiURL, String apiKey, String secret, long timeout) {
        this.apiURL = apiURL;
        this.apiKey = apiKey;
        this.secret = secret;
        this.timeout = timeout;
    }
}

