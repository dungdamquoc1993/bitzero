/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.config;

import bitzero.server.protocol.BZProtocolType;
import bitzero.server.util.IAdminHelper;
import bitzero.server.util.executor.SmartExecutorConfig;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ServerSettings {
    public transient IAdminHelper adminHelper;
    public volatile List<ServerSettings.SocketAddress> socketAddresses = new ArrayList();
    public volatile IpFilterSettings ipFilter = new IpFilterSettings();
    public volatile FlashCrossDomainPolicySettings flashCrossdomainPolicy = new FlashCrossDomainPolicySettings();
    public volatile int systemControllerThreadPoolSize = 1;
    public volatile int extensionControllerThreadPoolSize = 1;
    public volatile int systemControllerRequestQueueSize = 10000;
    public volatile int extensionControllerRequestQueueSize = 10000;
    public volatile int schedulerThreadPoolSize = 1;
    public volatile int protocolCompressionThreshold = 300;
    public BZProtocolType protocolMode;
    public RemoteAdminSettings remoteAdmin = new RemoteAdminSettings();
    public BannedUserManagerSettings bannedUserManager = new BannedUserManagerSettings();
    public volatile boolean extensionRemoteDebug = true;
    public volatile boolean useFriendlyExceptions = true;
    public MailerSettings mailer = new MailerSettings();
    public WebServerSettings webServer = new WebServerSettings();
    public boolean startExtensionFileMonitor = false;
    public volatile boolean useFriendlyLogging = true;
    public volatile boolean useDebugMode = false;
    public int sessionMaxIdleTime;
    public int userReconnectionSeconds = 10;
    public int userMaxIdleTime;
    public volatile boolean ghostHunterEnabled = true;
    public int mobileReconnectionSeconds = 30;
    public AdminUser admin = new AdminUser();
    public WebSocketEngineSettings webSocket = new WebSocketEngineSettings();
    public SmartExecutorConfig systemThreadPoolSettings = new SmartExecutorConfig();
    public SmartExecutorConfig extensionThreadPoolSettings = new SmartExecutorConfig();

    public static final class WebSocketEngineSettings
    implements Serializable {
        public boolean isActive = false;
        public String bindAddress = "127.0.0.1";
        public int tcpPort = 8888;
        public int sslPort = 8843;
        public List<String> validDomains = new ArrayList<String>();
        public boolean isSSL = false;
        public boolean isUsingFixThreadPool = true;
        public int bossThreadNum = 1;
        public int workerThreadNum = 5;
        public boolean isAutoBahnTest = false;
        public String keyStoreFile = "config/keystore.jks";
        public String keyStorePassword = "password";
    }

    public static final class WebServerSettings {
        public volatile boolean isActive = true;
        public volatile String cfgFile = "jetty/cfg/jetty.xml";
        public volatile int blueBoxPollingTimeout = 26;
        public volatile int blueBoxMsgQueueSize = 40;
    }

    public static final class SocketAddress {
        public static final String TYPE_UDP = "UDP";
        public static final String TYPE_TCP = "TCP";
        public volatile String address = "127.0.0.1";
        public volatile int port = 9339;
        public volatile String type = "TCP";
    }

    public static final class RemoteAdminSettings {
        public List<AdminUser> administrators = new ArrayList<AdminUser>();
        public List<String> allowedRemoteAddresses = new ArrayList<String>();
        public int adminTcpPort = 9933;
    }

    public static final class MailerSettings {
        public volatile boolean isActive = true;
        public volatile String mailHost = "";
        public volatile String mailUser = "";
        public volatile String mailPass = "";
        public volatile int smtpPort = 25;
        public volatile int workerThreads = 1;
    }

    public static final class IpFilterSettings {
        public List<String> addressBlackList = new ArrayList();
        public List<String> addressWhiteList = new ArrayList();
        public volatile int maxConnectionsPerAddress = 5;
    }

    public static final class FlashCrossDomainPolicySettings {
        public volatile boolean useMasterSocketPolicy = false;
        public volatile String policyXmlFile = "crossdomain.xml";
    }

    public static final class BannedUserManagerSettings {
        public boolean isAutoRemove = true;
        public boolean isPersistent = true;
        public String customPersistenceClass = null;
    }

    public static final class AdminUser {
        public volatile String login;
        public volatile String password;
    }

}

