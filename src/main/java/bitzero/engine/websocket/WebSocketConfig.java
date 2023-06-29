/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.websocket;

import java.util.List;

public class WebSocketConfig {
    public static final String LOCALHOST = "127.0.0.1";
    private int port = 8888;
    private int sslPort = 8889;
    private String host = "127.0.0.1";
    private List<String> validDomains;
    private boolean isActive = false;
    private boolean isSSL = true;
    private boolean isUsingFixThreadPool = true;
    private int bossThreadNum = 1;
    private int workerThreadNum = 5;
    private boolean isAutoBahnTest = false;
    private String keyStoreFile = "config/keystore.jks";
    private String keyStorePassword = "password";

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getSslPort() {
        return this.sslPort;
    }

    public void setSslPort(int sslPort) {
        this.sslPort = sslPort;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<String> getValidDomains() {
        return this.validDomains;
    }

    public void setValidDomains(List<String> validDomains) {
        this.validDomains = validDomains;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getKeyStoreFile() {
        return this.keyStoreFile;
    }

    public void setKeyStoreFile(String keyStorePath) {
        this.keyStoreFile = keyStorePath;
    }

    public String getKeyStorePassword() {
        return this.keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public boolean isSSL() {
        return this.isSSL;
    }

    public void setSSL(boolean isSSL) {
        this.isSSL = isSSL;
    }

    public boolean isUsingFixThreadPool() {
        return this.isUsingFixThreadPool;
    }

    public void setUsingFixThreadPool(boolean isUsingFixThreadPool) {
        this.isUsingFixThreadPool = isUsingFixThreadPool;
    }

    public void setBossThreadNum(int bossNum) {
        this.bossThreadNum = bossNum;
    }

    public int getBossThreadNum() {
        return this.bossThreadNum;
    }

    public void setWorkerThreadNum(int workerNum) {
        this.workerThreadNum = workerNum;
    }

    public int getWorkerThreadNum() {
        return this.workerThreadNum;
    }

    public void setAutoBahnTest(boolean isTest) {
        this.isAutoBahnTest = isTest;
    }

    public boolean isAutoBahnTest() {
        return this.isAutoBahnTest;
    }
}

