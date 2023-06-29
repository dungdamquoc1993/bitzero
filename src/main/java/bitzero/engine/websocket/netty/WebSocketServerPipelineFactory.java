/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.jboss.netty.channel.ChannelHandler
 *  org.jboss.netty.channel.ChannelPipeline
 *  org.jboss.netty.channel.ChannelPipelineFactory
 *  org.jboss.netty.channel.Channels
 *  org.jboss.netty.handler.codec.http.HttpChunkAggregator
 *  org.jboss.netty.handler.codec.http.HttpRequestDecoder
 *  org.jboss.netty.handler.codec.http.HttpResponseEncoder
 *  org.jboss.netty.handler.ssl.SslHandler
 */
package bitzero.engine.websocket.netty;

import bitzero.engine.io.IProtocolCodec;
import bitzero.engine.websocket.netty.WebSocketServerHandler;
import bitzero.engine.websocket.netty.WebSocketSslServerSslContext;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.ssl.SslHandler;

public class WebSocketServerPipelineFactory
implements ChannelPipelineFactory {
    private final IProtocolCodec codec;
    private boolean isSSL = false;
    private boolean isAutoBahnTest = false;

    public WebSocketServerPipelineFactory(IProtocolCodec codec, boolean isSSL, boolean isAutoBahnTest) {
        this.isSSL = isSSL;
        this.codec = codec;
        this.isAutoBahnTest = isAutoBahnTest;
    }

    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        if (this.isSSL) {
            SSLEngine engine = WebSocketSslServerSslContext.getInstance().getServerContext().createSSLEngine();
            engine.setUseClientMode(false);
            pipeline.addLast("ssl", (ChannelHandler)new SslHandler(engine));
        }
        pipeline.addLast("decoder", (ChannelHandler)new HttpRequestDecoder());
        pipeline.addLast("aggregator", (ChannelHandler)new HttpChunkAggregator(65536));
        pipeline.addLast("encoder", (ChannelHandler)new HttpResponseEncoder());
        pipeline.addLast("handler", (ChannelHandler)new WebSocketServerHandler(this.codec, this.isSSL, this.isAutoBahnTest));
        return pipeline;
    }
}

