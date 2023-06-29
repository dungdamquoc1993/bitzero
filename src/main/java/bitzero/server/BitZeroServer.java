/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.PropertyConfigurator
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server;

import bitzero.engine.clustering.IClusterManager;
import bitzero.engine.config.ControllerConfig;
import bitzero.engine.config.EngineConfiguration;
import bitzero.engine.config.SocketConfig;
import bitzero.engine.core.BitZeroEngine;
import bitzero.engine.core.IEngineAcceptor;
import bitzero.engine.core.security.IConnectionFilter;
import bitzero.engine.data.BindableSocket;
import bitzero.engine.data.BufferType;
import bitzero.engine.data.TransportType;
import bitzero.engine.events.IEvent;
import bitzero.engine.events.IEventListener;
import bitzero.engine.service.IService;
import bitzero.engine.sessions.ISession;
import bitzero.engine.sessions.ISessionManager;
import bitzero.engine.websocket.WebSocketConfig;
import bitzero.server.api.APIManager;
import bitzero.server.api.IBZApi;
import bitzero.server.api.response.ResponseApi;
import bitzero.server.config.BZConfigurator;
import bitzero.server.config.CoreSettings;
import bitzero.server.config.DefaultConstants;
import bitzero.server.config.IConfigurator;
import bitzero.server.config.ServerSettings;
import bitzero.server.core.BZEvent;
import bitzero.server.core.BZEventManager;
import bitzero.server.core.BZEventParam;
import bitzero.server.core.BZEventType;
import bitzero.server.core.BZShutdownHook;
import bitzero.server.core.IBZEvent;
import bitzero.server.core.IBZEventManager;
import bitzero.server.core.IBZEventType;
import bitzero.server.core.ServerState;
import bitzero.server.entities.ExtensionManager;
import bitzero.server.entities.User;
import bitzero.server.entities.Zone;
import bitzero.server.entities.managers.BZBannedUserManager;
import bitzero.server.entities.managers.BZStatsManager;
import bitzero.server.entities.managers.BZUserManager;
import bitzero.server.entities.managers.BZZoneManager;
import bitzero.server.entities.managers.IBannedUserManager;
import bitzero.server.entities.managers.IExtensionManager;
import bitzero.server.entities.managers.IStatsManager;
import bitzero.server.entities.managers.IUserManager;
import bitzero.server.entities.managers.IZoneManager;
import bitzero.server.exceptions.BZException;
import bitzero.server.exceptions.BZRuntimeException;
import bitzero.server.exceptions.ExceptionMessageComposer;
import bitzero.server.util.BZRestart;
import bitzero.server.util.ClientDisconnectionReason;
import bitzero.server.util.DebugConsole;
import bitzero.server.util.FlashMasterSocketPolicyLoader;
import bitzero.server.util.IDisconnectionReason;
import bitzero.server.util.PacketCount;
import bitzero.server.util.ServerUptime;
import bitzero.server.util.StringHelper;
import bitzero.server.util.TaskScheduler;
import bitzero.server.util.deadlock.DefaultDeadlockListener;
import bitzero.server.util.deadlock.ThreadDeadlockDetector;
import bitzero.server.util.executor.SmartExecutorConfig;
import bitzero.server.util.executor.SmartThreadPoolExecutor;
import bitzero.server.util.monitor.GhostUserHunter;
import bitzero.server.util.monitor.IGhostUserHunter;
import bitzero.util.config.bean.ConstantMercury;
import bitzero.util.datacontroller.business.DataController;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import com.vinplay.vbee.common.config.VBeePath;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BitZeroServer {
    private final String version = "alpha";
    private static BitZeroServer _instance = null;
    private static AtomicInteger restartCounter = new AtomicInteger(0);
    private final BitZeroEngine bitZeroEngine = BitZeroEngine.getInstance();
    private final Logger log;
    private APIManager apiManager;
    private volatile ServerState state;
    private volatile boolean initialized;
    private volatile boolean started;
    private volatile long serverStartTime;
    private volatile boolean isRebooting;
    private volatile boolean isHalting;
    private final IConfigurator bzConfigurator;
    private IEventListener networkEventListener;
    private TaskScheduler taskScheduler;
    private IService adminToolService;
    private boolean clustered;
    private final IBZEventManager eventManager;
    private IGhostUserHunter ghostUserHunter;
    private IStatsManager statsManager;
    private IExtensionManager extensionManager;
    private IBannedUserManager bannedUserManger;
    private IZoneManager zoneManager;
    private IUserManager userManager;
    private DebugConsole debugConsole;
    private BZException zoneInitError;
    private PacketCount packetCount;
    private ThreadPoolExecutor sysmtemWorkerPool;

    public static BitZeroServer getInstance() {
        if (_instance == null) {
            _instance = new BitZeroServer();
        }
        return _instance;
    }
    public static BitZeroServer getInstance(Class cls) {
        if (_instance == null) {
            _instance = new BitZeroServer(cls);
        }
        return _instance;
    }

    private BitZeroServer() {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.bzConfigurator = new BZConfigurator();
        this.eventManager = new BZEventManager();
        this.packetCount = new PacketCount();
        this.state = ServerState.STARTING;
        this.initialized = false;
        this.started = false;
        this.isRebooting = false;
        this.isHalting = false;
        this.clustered = false;
        this.networkEventListener = new BitZeroEventListener(null);
        this.zoneManager = new BZZoneManager();
        if (this.userManager == null) {
            this.userManager = new BZUserManager();
        }
        this.statsManager = new BZStatsManager();
        this.taskScheduler = new TaskScheduler(1);
        this.bannedUserManger = new BZBannedUserManager();
        this.extensionManager = new ExtensionManager();
    }
    private BitZeroServer(Class loggerClass) {
        this.log = LoggerFactory.getLogger(loggerClass);
        this.bzConfigurator = new BZConfigurator();
        this.eventManager = new BZEventManager();
        this.packetCount = new PacketCount();
        this.state = ServerState.STARTING;
        this.initialized = false;
        this.started = false;
        this.isRebooting = false;
        this.isHalting = false;
        this.clustered = false;
        this.networkEventListener = new BitZeroEventListener(null);
        this.zoneManager = new BZZoneManager();
        if (this.userManager == null) {
            this.userManager = new BZUserManager();
        }
        this.statsManager = new BZStatsManager();
        this.taskScheduler = new TaskScheduler(1);
        this.bannedUserManger = new BZBannedUserManager();
        this.extensionManager = new ExtensionManager();
    }

    public static boolean isDebug() {
        return BitZeroServer._instance.bzConfigurator.getServerSettings().useDebugMode;
    }

    public String getVersion() {
        return "alpha";
    }

    public void start() {
        if (!this.initialized) {
            this.initialize();
        }
        try {
            this.bzConfigurator.loadConfiguration();
            this.initSystemWorkers();
            this.configureServer();
            this.configureBitZero();
            this.extensionManager.init();
            try {
                this.zoneManager.init(null);
                this.zoneManager.initializeZones();
            }
            catch (BZException err) {
                this.zoneInitError = err;
            }
            this.bitZeroEngine.start("BitZeroServer alpha");
            this.log.debug("START ENGINE SERVER ***   ");
            this.registerShutdown();
            this.deadLockDetector();
        }
        catch (FileNotFoundException e) {
            ExceptionMessageComposer msg = new ExceptionMessageComposer(e);
            msg.setDescription("There has been a problem loading the server configuration. The server cannot start.");
            msg.setPossibleCauses("Make sure that core.xml and server.xml files exist in your config/ folder.");
            this.log.error(msg.toString());
        }
        catch (BZException e) {
            ExceptionMessageComposer msg = new ExceptionMessageComposer(e);
            msg.setDescription("An error occurred during the Server boot, preventing it to start.");
            this.log.error(msg.toString());
        }
        catch (Exception e) {
            ExceptionMessageComposer msg = new ExceptionMessageComposer(e);
            msg.setDescription("Unexpected error during Server boot. The server cannot start.");
            msg.addInfo("Solution: Please email us the content of this error message, including the stack trace to toannobita@gmail..com");
            this.log.error(msg.toString());
        }
    }

    public boolean isClustered() {
        return this.clustered;
    }

    public void setClustered(boolean value) {
        if (this.initialized) {
            throw new IllegalStateException("Server already initialized, cannot change the cluster mode!");
        }
        this.clustered = value;
    }

    public void registerShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(){

            @Override
            public void run() {
                try {
                    List<BindableSocket> listBind = BitZeroServer.this.bitZeroEngine.getEngineAcceptor().getBoundSockets();
                    for (BindableSocket bindS : listBind) {
                        DataController.getController().delete("cache_binded_port__" + bindS.getAddress() + "_" + bindS.getPort());
                    }
                }
                catch (Exception listBind) {
                    // empty catch block
                }
            }
        });
    }

    public void deadLockDetector() {
        if (ConstantMercury.ENABLE_DEADLOCK_DETECTOR) {
            DefaultDeadlockListener listener = new DefaultDeadlockListener();
            ThreadDeadlockDetector detector = new ThreadDeadlockDetector(ConstantMercury.DEADLOCK_TIME);
            detector.addListener(listener);
        }
    }

    public int getRestartCount() {
        return restartCounter.get();
    }

    public synchronized void restart() {
        if (this.isRebooting) {
            return;
        }
        this.isRebooting = true;
        this.log.warn("*** SERVER RESTARTING ***");
        try {
            this.bitZeroEngine.shutDownSequence();
            this.started = false;
            BZRestart restarter = new BZRestart();
            restarter.start();
        }
        catch (Exception e) {
            this.log.error("Restart Failure: " + e);
        }
    }

    public void halt() {
        if (this.isHalting) {
            return;
        }
        this.isHalting = true;
        this.log.warn("*** SERVER HALTING ***");
        try {
            Thread stopper = new Thread(new Runnable(){
                int countDown;

                @Override
                public void run() {
                    while (this.countDown > 0) {
                        BitZeroServer.this.log.warn("Server Halt in " + this.countDown-- + " seconds...");
                        try {
                            Thread.sleep(1000);
                        }
                        catch (InterruptedException var1_1) {}
                    }
                    System.exit(0);
                }
            });
            stopper.start();
        }
        catch (Exception e) {
            this.log.error("Halt Failure: " + e);
        }
    }

    public boolean isStarted() {
        return this.started;
    }

    public TaskScheduler getTaskScheduler() {
        return this.taskScheduler;
    }

    public IBZEventManager getEventManager() {
        return this.eventManager;
    }

    public IExtensionManager getExtensionManager() {
        return this.extensionManager;
    }

    public IZoneManager getZoneManager() {
        return this.zoneManager;
    }

    public IBannedUserManager getBannedUserManager() {
        return this.bannedUserManger;
    }

    public ISessionManager getSessionManager() {
        return this.bitZeroEngine.getSessionManager();
    }

    public IUserManager getUserManager() {
        return this.userManager;
    }

    public ServerState getState() {
        return this.state;
    }

    public IConfigurator getConfigurator() {
        return this.bzConfigurator;
    }

    public int getMinClientApiVersion() {
        return 60;
    }

    public APIManager getAPIManager() {
        return this.apiManager;
    }

    public IStatsManager getStatsManager() {
        return this.statsManager;
    }

    public ServerUptime getUptime() {
        if (this.serverStartTime == 0) {
            throw new IllegalStateException("Server not ready yet, cannot provide uptime!");
        }
        return new ServerUptime(System.currentTimeMillis() - this.serverStartTime);
    }

    public void startDebugConsole() {
        if (this.debugConsole != null) {
            throw new IllegalStateException("A DebugConsole was already created.");
        }
        this.debugConsole = new DebugConsole();
    }

    private void initialize() {
        if (this.initialized) {
            throw new IllegalStateException("BitZeroServer engine already initialized!");
        }
        PropertyConfigurator.configure(VBeePath.basePath.concat("config/log4j.properties"));
        this.log.info("Boot sequence starts..." + (this.clustered ? " (cluster-mode)" : ""));
        String bootMessage = StringHelper.getAsciiMessage("boot");
        if (bootMessage != null) {
            this.log.info(bootMessage);
        }
        Runtime.getRuntime().addShutdownHook(new BZShutdownHook());
        this.apiManager = new APIManager();
        this.apiManager.init(null);
        this.ghostUserHunter = new GhostUserHunter();
        this.bitZeroEngine.addEventListener("serverStarted", this.networkEventListener);
        this.bitZeroEngine.addEventListener("sessionAdded", this.networkEventListener);
        this.bitZeroEngine.addEventListener("sessionLost", this.networkEventListener);
        this.bitZeroEngine.addEventListener("sessionIdle", this.networkEventListener);
        this.bitZeroEngine.addEventListener("sessionIdleCheckComplete", this.networkEventListener);
        this.bitZeroEngine.addEventListener("packetDropped", this.networkEventListener);
        this.bitZeroEngine.addEventListener("sessionReconnectionTry", this.networkEventListener);
        this.bitZeroEngine.addEventListener("sessionReconnectionSuccess", this.networkEventListener);
        this.bitZeroEngine.addEventListener("sessionReconnectionFailure", this.networkEventListener);
        this.initialized = true;
    }

    private void initLMService() {
        Object lsClazz = null;
        String target = "lib/Lib/bz2x-lms.jar";
    }

    private boolean isFrag(String who) {
        boolean res = false;
        int ZH = 1347093252;
        try {
            FileInputStream fis = new FileInputStream(who);
            byte[] header = new byte[4];
            fis.read(header);
            ByteBuffer bb = ByteBuffer.wrap(header);
            res = bb.getInt() != ZH;
        }
        catch (IOException fis) {
            // empty catch block
        }
        return res;
    }

    private void configureServer() {
        ServerSettings settings = this.bzConfigurator.getServerSettings();
        this.taskScheduler.resizeThreadPool(settings.schedulerThreadPoolSize);
        this.bannedUserManger.setAutoRemoveBan(settings.bannedUserManager.isAutoRemove);
        this.bannedUserManger.setName("BannedUserManager");
        this.bannedUserManger.setPersistent(settings.bannedUserManager.isPersistent);
        this.bannedUserManger.setPersistenceClass(settings.bannedUserManager.customPersistenceClass);
        this.bannedUserManger.init(null);
        ExceptionMessageComposer.globalPrintStackTrace = settings.useFriendlyLogging;
        ExceptionMessageComposer.useExtendedMessages = settings.useFriendlyExceptions;
    }

    private void configureBitZero() {
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        CoreSettings coreSettings = this.bzConfigurator.getCoreSettings();
        ServerSettings bzSettings = this.bzConfigurator.getServerSettings();
        for (ServerSettings.SocketAddress addr : bzSettings.socketAddresses) {
            engineConfiguration.addBindableAddress(new SocketConfig(addr.address, addr.port, TransportType.fromName(addr.type)));
        }
        engineConfiguration.addController(new ControllerConfig(coreSettings.systemControllerClass, DefaultConstants.CORE_SYSTEM_CONTROLLER_ID, bzSettings.systemControllerThreadPoolSize, bzSettings.systemControllerRequestQueueSize));
        engineConfiguration.addController(new ControllerConfig(coreSettings.extensionControllerClass, DefaultConstants.CORE_EXTENSIONS_CONTROLLER_ID, bzSettings.extensionControllerThreadPoolSize, bzSettings.extensionControllerRequestQueueSize));
        engineConfiguration.setDefaultMaxSessionIdleTime(bzSettings.sessionMaxIdleTime);
        try {
            engineConfiguration.setDefaultMaxLoggedInSessionIdleTime(bzSettings.userMaxIdleTime);
        }
        catch (IllegalArgumentException err) {
            engineConfiguration.setDefaultMaxLoggedInSessionIdleTime(bzSettings.sessionMaxIdleTime + 60);
            ExceptionMessageComposer msg = new ExceptionMessageComposer(err);
            msg.setDescription("Make sure that userMaxIdleTime > socketIdleTime");
            msg.addInfo("The problem was temporarily fixed by setting userMaxIdleTime as: " + engineConfiguration.getDefaultMaxLoggedInSessionIdleTime());
            msg.addInfo("Please review your server.xml file and fix the problem.");
            this.log.warn(msg.toString());
            engineConfiguration.setDefaultMaxLoggedInSessionIdleTime(bzSettings.sessionMaxIdleTime + 60);
        }
        engineConfiguration.setIoHandlerClass("bitzero.server.protocol.BZIoHandler");
        engineConfiguration.setReadBufferType(coreSettings.readBufferType.equalsIgnoreCase("direct") ? BufferType.DIRECT : BufferType.HEAP);
        engineConfiguration.setWriteBufferType(coreSettings.readBufferType.equalsIgnoreCase("direct") ? BufferType.DIRECT : BufferType.HEAP);
        engineConfiguration.setReadMaxBufferSize(coreSettings.maxReadBufferSize);
        engineConfiguration.setWriteMaxBufferSize(coreSettings.maxWriteBufferSize);
        engineConfiguration.setMaxIncomingRequestSize(coreSettings.maxIncomingRequestSize);
        engineConfiguration.setSessionPacketQueueMaxSize(coreSettings.sessionPacketQueueSize);
        engineConfiguration.setNagleAlgorithm(!coreSettings.tcpNoDelay);
        engineConfiguration.setOnlyRealTCP(coreSettings.onlyRealTCP);
        engineConfiguration.setAcceptorThreadPoolSize(coreSettings.engineAcceptorThreadPoolSize);
        engineConfiguration.setReaderThreadPoolSize(coreSettings.engineReaderThreadPoolSize);
        engineConfiguration.setWriterThreadPoolSize(coreSettings.engineWriterThreadPoolSize);
        engineConfiguration.setFlashCrossdomainPolicyEnabled(bzSettings.flashCrossdomainPolicy.useMasterSocketPolicy);
        if (engineConfiguration.isFlashCrossdomainPolicyEnabled()) {
            String crossdomainFile = VBeePath.basePath+ "config/" + bzSettings.flashCrossdomainPolicy.policyXmlFile;
            try {
                String crossdomainXml = new FlashMasterSocketPolicyLoader().loadPolicy(crossdomainFile);
                engineConfiguration.setFlashCrossdomainPolicyXml(crossdomainXml);
            }
            catch (IOException e) {
                ExceptionMessageComposer msg = new ExceptionMessageComposer(e);
                msg.setDescription("could not load the specified Flash crossdomain policy file: " + crossdomainFile);
                msg.setPossibleCauses("make sure to put the specified file in the expcted location");
                msg.addInfo("More infos: more details on Flash crossdomain files are found at http://www.adobe.com/devnet/flashplayer/articles/fplayer9_security_04.html");
                this.log.warn(msg.toString());
            }
        }
        engineConfiguration.setMaxConnectionsFromSameIp(99999);
        engineConfiguration.setClustered(this.clustered);
        WebSocketConfig wsc = new WebSocketConfig();
        if (bzSettings.webSocket != null) {
            wsc.setActive(bzSettings.webSocket.isActive);
            wsc.setHost(bzSettings.webSocket.bindAddress);
            wsc.setPort(bzSettings.webSocket.tcpPort);
            wsc.setSslPort(bzSettings.webSocket.sslPort);
            wsc.setSSL(bzSettings.webSocket.isSSL);
            wsc.setKeyStoreFile(bzSettings.webSocket.keyStoreFile);
            wsc.setKeyStorePassword(bzSettings.webSocket.keyStorePassword);
            wsc.setUsingFixThreadPool(bzSettings.webSocket.isUsingFixThreadPool);
            wsc.setBossThreadNum(bzSettings.webSocket.bossThreadNum);
            wsc.setWorkerThreadNum(bzSettings.webSocket.workerThreadNum);
            wsc.setAutoBahnTest(bzSettings.webSocket.isAutoBahnTest);
        }
        engineConfiguration.setWebSocketEngineConfig(wsc);
        this.bitZeroEngine.setConfiguration(engineConfiguration);
    }

    private void onSessionClosed(ISession session) {
        this.apiManager.getBzApi().disconnect(session);
    }

    private void onSocketEngineStart() {
        for (String blockedIp : this.bzConfigurator.getServerSettings().ipFilter.addressBlackList) {
            this.bitZeroEngine.getEngineAcceptor().getConnectionFilter().addBannedAddress(blockedIp);
        }
        for (String allowedIp : this.bzConfigurator.getServerSettings().ipFilter.addressWhiteList) {
            this.bitZeroEngine.getEngineAcceptor().getConnectionFilter().addWhiteListAddress(allowedIp);
        }
        this.bitZeroEngine.getEngineAcceptor().getConnectionFilter().setMaxConnectionsPerIp(this.bzConfigurator.getServerSettings().ipFilter.maxConnectionsPerAddress);
        List<BindableSocket> sockets = this.bitZeroEngine.getEngineAcceptor().getBoundSockets();
        String message = "Listening Sockets: ";
        for (BindableSocket socket : sockets) {
            message = String.valueOf(message) + socket.toString() + " ";
        }
        if (this.bzConfigurator.getServerSettings().webSocket != null && this.bzConfigurator.getServerSettings().webSocket.isActive) {
            message = message + "{ " + this.bzConfigurator.getServerSettings().webSocket.bindAddress + ":" + (this.bzConfigurator.getServerSettings().webSocket.isSSL ? new StringBuilder().append(this.bzConfigurator.getServerSettings().webSocket.sslPort).append(" (SSL)").toString() : Integer.valueOf(this.bzConfigurator.getServerSettings().webSocket.tcpPort)) + " (WebSocket) }";
        }
        this.log.info(message);
        String asciiArt_ServerReadyMessage = StringHelper.getAsciiMessage("ready");
        if (asciiArt_ServerReadyMessage != null) {
            Object[] arrobject = new Object[3];
            arrobject[0] = asciiArt_ServerReadyMessage;
            arrobject[1] = "alpha RC";
            arrobject[2] = this.clustered ? " - Node Id: " + this.bitZeroEngine.getClusterManager().getLocalNodeName() : "";
            String text = String.format("%s[ %s ] %s\n", arrobject);
            this.log.info(text);
        }
        this.statsManager.init(null);
        this.log.info("BitZeroServer Max (alpha RC-READY!");
        if (this.zoneInitError != null) {
            ExceptionMessageComposer composer = new ExceptionMessageComposer(this.zoneInitError);
            composer.setDescription("There were startup errors during the Zone Setup");
            composer.addInfo("Please connect via the AdminTool and correct the problem");
            this.log.warn(composer.toString());
        }
        this.serverStartTime = System.currentTimeMillis();
        this.started = true;
        this.eventManager.dispatchEvent(new BZEvent(BZEventType.SERVER_READY));
    }

    private void onSessionIdle(ISession idleSession) {
        User user = this.getUserManager().getUserBySession(idleSession);
        if (user == null) {
            throw new BZRuntimeException("IdleSession event ignored, cannot find any User for Session: " + idleSession);
        }
        this.apiManager.getBzApi().disconnectUser(user, ClientDisconnectionReason.IDLE);
    }

    private void onSessionReconnectionTry(ISession session) {
        User user = this.getUserManager().getUserBySession(session);
        if (user == null) {
            throw new BZRuntimeException("-Unexpected- Cannot find any User for Session: " + session);
        }
        user.setConnected(false);
        HashMap<BZEventParam, Object> evtParams = new HashMap<BZEventParam, Object>();
        evtParams.put(BZEventParam.ZONE, user.getZone());
        evtParams.put(BZEventParam.USER, user);
        this.eventManager.dispatchEvent(new BZEvent(BZEventType.USER_RECONNECTION_TRY, evtParams));
    }

    private void onSessionReconnectionSuccess(ISession session) {
        User user = this.getUserManager().getUserBySession(session);
        if (user == null) {
            throw new BZRuntimeException("-Unexpected- Cannot find any User for Session: " + session);
        }
        user.setConnected(true);
        HashMap<BZEventParam, Object> evtParams = new HashMap<BZEventParam, Object>();
        evtParams.put(BZEventParam.ZONE, user.getZone());
        evtParams.put(BZEventParam.USER, user);
        this.eventManager.dispatchEvent(new BZEvent(BZEventType.USER_RECONNECTION_SUCCESS, evtParams));
    }

    private void onSessionReconnectionFailure(ISession incomingSession) {
        this.apiManager.getResApi().notifyReconnectionFailure(incomingSession);
    }

    public PacketCount getPacketCount() {
        return this.packetCount;
    }

    public Executor getSystemThreadPool() {
        return this.sysmtemWorkerPool;
    }

    private void initSystemWorkers() {
        SmartExecutorConfig cfg = this.getConfigurator().getServerSettings().systemThreadPoolSettings;
        cfg.name = "Sys";
        this.sysmtemWorkerPool = new SmartThreadPoolExecutor(cfg);
    }

    private class BitZeroEventListener
    implements IEventListener {
        @Override
        public void handleEvent(IEvent event) {
            String evtName = event.getName();
            if (evtName.equals("serverStarted")) {
                BitZeroServer.this.onSocketEngineStart();
            } else if (evtName.equals("sessionLost")) {
                ISession session = (ISession)event.getParameter("session");
                if (session == null) {
                    throw new BZRuntimeException("UNEXPECTED: Session was lost, but session object is NULL!");
                }
                BitZeroServer.this.onSessionClosed(session);
            } else if (evtName.equals("sessionIdleCheckComplete") && BitZeroServer.this.getConfigurator().getServerSettings().ghostHunterEnabled) {
                BitZeroServer.this.ghostUserHunter.hunt();
            } else if (evtName.equals("sessionIdle")) {
                BitZeroServer.this.onSessionIdle((ISession)event.getParameter("session"));
            } else if (evtName.equals("sessionReconnectionTry")) {
                BitZeroServer.this.onSessionReconnectionTry((ISession)event.getParameter("session"));
            } else if (evtName.equals("sessionReconnectionSuccess")) {
                BitZeroServer.this.onSessionReconnectionSuccess((ISession)event.getParameter("session"));
            } else if (evtName.equals("sessionReconnectionFailure")) {
                BitZeroServer.this.onSessionReconnectionFailure((ISession)event.getParameter("session"));
            }
        }

        private BitZeroEventListener() {
        }

        BitZeroEventListener(BitZeroEventListener bitswarmeventlistener) {
            this();
        }
    }

}

