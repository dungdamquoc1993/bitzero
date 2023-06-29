/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.config;

import bitzero.engine.data.BufferType;

public class EngineConstants {
    public static final String BOOT_LOGGER_FILEHANDLER = "";
    public static final String BOOT_LOGGER_CFG_FILE = "config/bootlog.properties";
    public static final String BOOT_LOGGER_NAME = "bootLogger";
    public static final String DEFAULT_SESSION_MANAGER = "bitzero.engine.sessions.DefaultSessionManager";
    public static final String DEFAULT_IOHANDLER = "bitzero.engine.io.protocols.text.TextIOHandler";
    public static final String DEFAULT_QUEUE_POLICY = "bitzero.engine.sessions.DefaultPacketQueuePolicy";
    public static final BufferType DEFAULT_READ_BUFFER_TYPE = BufferType.DIRECT;
    public static final BufferType DEFAULT_WRITE_BUFFER_TYPE = BufferType.HEAP;
    public static final String SERVICE_SOCKET_ACCEPTOR = "socketAcceptor";
    public static final String SERVICE_SOCKET_READER = "socketReader";
    public static final String SERVICE_SOCKET_WRITER = "socketWriter";
    public static final String SERVICE_SCHEDULER = "scheduler";
    public static final String SERVICE_SESSION_MANAGER = "sessionManager";
    public static final String SERVICE_CONTROLLER_MANAGER = "controllerManager";
    public static final String SERVICE_SECURITY_MANAGER = "securityManager";
    public static final String SERVICE_CLUSTER_MANAGER = "clusterManager";
    public static final String SERVICE_WEBSOCKET_ENGINE = "webSocketEngine";
    public static final int DEFAULT_SESSION_QUEUE_SIZE = 200;
    public static final int DEFAULT_READ_MAX_BUFFER_SIZE = 8192;
    public static final int DEFAULT_WRITE_MAX_BUFFER_SIZE = 32768;
    public static final int DEFAULT_MAX_INCOMING_REQUEST_SIZE = 4096;
    public static final String TASK_DELAYED_SOCKET_WRITE = "delayedSocketWrite";
    public static final String SESSION_SELECTION_KEY = "SessionSelectionKey";
    public static final int DEFAULT_CONNECTION_FILTER_MAX_IP = 3;
}

