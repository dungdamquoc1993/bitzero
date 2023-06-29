/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.config;

import bitzero.engine.config.ControllerConfig;
import bitzero.engine.config.EngineConstants;
import bitzero.engine.config.SocketConfig;
import bitzero.engine.data.BufferType;
import bitzero.engine.websocket.WebSocketConfig;
import java.util.ArrayList;
import java.util.List;

public final class EngineConfiguration {
    private String localNodeName = "local";
    private int defaultMaxSessionIdleTime = 60;
    private int defaultMaxLoggedInSessionIdleTime = 120;
    private int connectionCleanerInterval = 60;
    private int acceptorThreadPoolSize = 1;
    private int readerThreadPoolSize = 1;
    private int writerThreadPoolSize = 1;
    private int sessionPacketQueueMaxSize = 200;
    private boolean clustered = false;
    private boolean nagleAlgorithm = true;
    private String sessionManagerClass = "bitzero.engine.sessions.DefaultSessionManager";
    private String ioHandlerClass = "bitzero.engine.io.protocols.text.TextIOHandler";
    private String packetQueuePolicyClass = "bitzero.engine.sessions.DefaultPacketQueuePolicy";
    private BufferType readBufferType = EngineConstants.DEFAULT_READ_BUFFER_TYPE;
    private BufferType writeBufferType = EngineConstants.DEFAULT_WRITE_BUFFER_TYPE;
    private int readMaxBufferSize = 8192;
    private int writeMaxBufferSize = 32768;
    private int maxIncomingRequestSize = 4096;
    private List bindableAddresses = new ArrayList();
    private List controllerConfigs = new ArrayList();
    private String clusterUniqueIdGeneratorClass;
    private boolean flashCrossdomainPolicyEnabled;
    private String flashCrossdomainPolicyXml;
    private int globalReconnectionSeconds = 0;
    private int maxConnectionsFromSameIp = 3;
    private boolean onlyRealTCP = false;
    private WebSocketConfig webSocketEngineConfig = new WebSocketConfig();

    public void addController(ControllerConfig cfg) {
        this.controllerConfigs.add(cfg);
    }

    public List getControllerConfigs() {
        return this.controllerConfigs;
    }

    public void addBindableAddress(SocketConfig socket) {
        this.bindableAddresses.add(socket);
    }

    public List getBindableSockets() {
        return this.bindableAddresses;
    }

    public String getSessionManagerClass() {
        return this.sessionManagerClass;
    }

    public void setSessionManagerClass(String sessionManagerClass) {
        this.sessionManagerClass = sessionManagerClass;
    }

    public boolean isNagleAlgorithm() {
        return this.nagleAlgorithm;
    }

    public void setNagleAlgorithm(boolean nagleAlgorithm) {
        this.nagleAlgorithm = nagleAlgorithm;
    }

    public String getLocalNodeName() {
        return this.localNodeName;
    }

    public void setLocalNodeName(String localNodeName) {
        this.localNodeName = localNodeName;
    }

    public boolean isClustered() {
        return this.clustered;
    }

    public int getDefaultMaxSessionIdleTime() {
        return this.defaultMaxSessionIdleTime;
    }

    public void setDefaultMaxSessionIdleTime(int defaultMaxSessionIdleTime) {
        this.defaultMaxSessionIdleTime = defaultMaxSessionIdleTime;
    }

    public int getDefaultMaxLoggedInSessionIdleTime() {
        return this.defaultMaxLoggedInSessionIdleTime;
    }

    public void setDefaultMaxLoggedInSessionIdleTime(int defaultMaxLoggedInSessionIdleTime) throws IllegalArgumentException {
        if (defaultMaxLoggedInSessionIdleTime < this.defaultMaxSessionIdleTime) {
            String errorMsg = String.format("userMaxIdleTime (%s) cannot be smaller than sessionMaxIdleTime (%s)", defaultMaxLoggedInSessionIdleTime, this.defaultMaxSessionIdleTime);
            throw new IllegalArgumentException(errorMsg);
        }
        this.defaultMaxLoggedInSessionIdleTime = defaultMaxLoggedInSessionIdleTime;
    }

    public int getConnectionCleanerInterval() {
        return this.connectionCleanerInterval;
    }

    public void setConnectionCleanerInterval(int connectionCleanerInterval) {
        this.connectionCleanerInterval = connectionCleanerInterval;
    }

    public int getAcceptorThreadPoolSize() {
        return this.acceptorThreadPoolSize;
    }

    public void setAcceptorThreadPoolSize(int acceptorThreadPoolSize) {
        this.acceptorThreadPoolSize = acceptorThreadPoolSize;
    }

    public int getReaderThreadPoolSize() {
        return this.readerThreadPoolSize;
    }

    public void setReaderThreadPoolSize(int readerThreadPoolSize) {
        this.readerThreadPoolSize = readerThreadPoolSize;
    }

    public int getWriterThreadPoolSize() {
        return this.writerThreadPoolSize;
    }

    public void setWriterThreadPoolSize(int writerThreadPoolSize) {
        this.writerThreadPoolSize = writerThreadPoolSize;
    }

    public String getIoHandlerClass() {
        return this.ioHandlerClass;
    }

    public void setIoHandlerClass(String ioHandlerClass) {
        this.ioHandlerClass = ioHandlerClass;
    }

    public BufferType getReadBufferType() {
        return this.readBufferType;
    }

    public void setReadBufferType(BufferType readBufferType) {
        this.readBufferType = readBufferType;
    }

    public BufferType getWriteBufferType() {
        return this.writeBufferType;
    }

    public void setWriteBufferType(BufferType writeBufferType) {
        this.writeBufferType = writeBufferType;
    }

    public int getReadMaxBufferSize() {
        return this.readMaxBufferSize;
    }

    public void setReadMaxBufferSize(int readMaxBufferSize) {
        this.readMaxBufferSize = readMaxBufferSize;
    }

    public void setClustered(boolean clustered) {
        this.clustered = clustered;
    }

    public List getBindableAddresses() {
        return this.bindableAddresses;
    }

    public void setBindableAddresses(List bindableAddresses) {
        this.bindableAddresses = bindableAddresses;
    }

    public int getSessionPacketQueueMaxSize() {
        return this.sessionPacketQueueMaxSize;
    }

    public void setSessionPacketQueueMaxSize(int sessionPacketQueueMaxSize) {
        this.sessionPacketQueueMaxSize = sessionPacketQueueMaxSize;
    }

    public String getPacketQueuePolicyClass() {
        return this.packetQueuePolicyClass;
    }

    public void setPacketQueuePolicyClass(String packetQueuePolicyClass) {
        this.packetQueuePolicyClass = packetQueuePolicyClass;
    }

    public int getWriteMaxBufferSize() {
        return this.writeMaxBufferSize;
    }

    public void setWriteMaxBufferSize(int writeMaxBufferSize) {
        this.writeMaxBufferSize = writeMaxBufferSize;
    }

    public String getClusterUniqueIdGeneratorClass() {
        return this.clusterUniqueIdGeneratorClass;
    }

    public void setClusterUniqueIdGeneratorClass(String clusterUniqueIdGeneratorClass) {
        this.clusterUniqueIdGeneratorClass = clusterUniqueIdGeneratorClass;
    }

    public int getMaxIncomingRequestSize() {
        return this.maxIncomingRequestSize;
    }

    public void setMaxIncomingRequestSize(int maxIncomingRequestSize) {
        this.maxIncomingRequestSize = maxIncomingRequestSize;
    }

    public boolean isFlashCrossdomainPolicyEnabled() {
        return this.flashCrossdomainPolicyEnabled;
    }

    public void setFlashCrossdomainPolicyEnabled(boolean flashCrossdomainPolicyEnabled) {
        this.flashCrossdomainPolicyEnabled = flashCrossdomainPolicyEnabled;
    }

    public String getFlashCrossdomainPolicyXml() {
        return this.flashCrossdomainPolicyXml;
    }

    public void setFlashCrossdomainPolicyXml(String flashCrossdomainPolicyXml) {
        this.flashCrossdomainPolicyXml = flashCrossdomainPolicyXml;
    }

    public int getGlobalReconnectionSeconds() {
        return this.globalReconnectionSeconds;
    }

    public void setGlobalReconnectionSeconds(int globalReconnectionSeconds) {
        this.globalReconnectionSeconds = globalReconnectionSeconds;
    }

    public int getMaxConnectionsFromSameIp() {
        return this.maxConnectionsFromSameIp;
    }

    public void setMaxConnectionsFromSameIp(int maxConnectionsFromSameIp) {
        this.maxConnectionsFromSameIp = maxConnectionsFromSameIp;
    }

    public boolean isOnlyRealTCP() {
        return this.onlyRealTCP;
    }

    public void setOnlyRealTCP(boolean onlyRealTCP) {
        this.onlyRealTCP = onlyRealTCP;
    }

    public WebSocketConfig getWebSocketEngineConfig() {
        return this.webSocketEngineConfig;
    }

    public void setWebSocketEngineConfig(WebSocketConfig webSocketEngineConfig) {
        this.webSocketEngineConfig = webSocketEngineConfig;
    }
}

