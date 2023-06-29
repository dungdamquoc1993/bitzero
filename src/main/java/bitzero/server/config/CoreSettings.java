/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.config;

public final class CoreSettings {
    public String systemControllerClass = "bitzero.server.controllers.SystemController";
    public String extensionControllerClass = "bitzero.server.controllers.ExtensionController";
    public String ioHandlerClass = "bitzero.server.protocol.BZIoHandler";
    public String sessionManagerClass = "bitzero.engine.sessions.DefaultSessionManager";
    public String packetQueuePolicyClass = "bitzero.engine.sessions.DefaultPacketQueuePolicy";
    public String readBufferType = "HEAP";
    public String writeBufferType = "HEAP";
    public int maxIncomingRequestSize = 4096;
    public int maxReadBufferSize = 1024;
    public int maxWriteBufferSize = 32768;
    public int maxPacketBufferSize = 5012;
    public boolean tcpNoDelay = false;
    public int engineAcceptorThreadPoolSize = 1;
    public int engineReaderThreadPoolSize = 1;
    public int engineWriterThreadPoolSize = 1;
    public int sessionPacketQueueSize = 120;
    public boolean onlyRealTCP = false;
}

