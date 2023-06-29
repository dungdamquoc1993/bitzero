/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.engine.websocket.netty;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WebSocketSslServerSslContext {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketSslServerSslContext.class);
    private static final String PROTOCOL = "TLS";
    private final SSLContext _serverContext;

    public static WebSocketSslServerSslContext getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private WebSocketSslServerSslContext() {
        SSLContext serverContext;
        serverContext = null;
        try {
            String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
            if (algorithm == null) {
                algorithm = "SunX509";
            }
            try {
                String keyStoreFilePath = System.getProperty("keystore.file.path");
                String keyStoreFilePassword = System.getProperty("keystore.file.password");
                KeyStore ks = KeyStore.getInstance("JKS");
                FileInputStream fin = new FileInputStream(keyStoreFilePath);
                ks.load(fin, keyStoreFilePassword.toCharArray());
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
                kmf.init(ks, keyStoreFilePassword.toCharArray());
                serverContext = SSLContext.getInstance("TLS");
                serverContext.init(kmf.getKeyManagers(), null, null);
            }
            catch (Exception e) {
                throw new Error("Failed to initialize the server-side SSLContext", e);
            }
        }
        catch (Exception ex) {
            ex = ex;
            if (logger.isErrorEnabled()) {
                logger.error("Error initializing SslContextManager. " + ex.getMessage(), (Throwable)ex);
            }
            this._serverContext = serverContext;
            return;
        }
        this._serverContext = serverContext;
    }

    public SSLContext getServerContext() {
        return this._serverContext;
    }

    private static interface SingletonHolder {
        public static final WebSocketSslServerSslContext INSTANCE = new WebSocketSslServerSslContext();
    }

}

