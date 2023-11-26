package com.client;

import com.client.provider.UIManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.client.auth.AuthServerMessage;
import com.client.auth.AuthServerMessageType;
import io.netty.channel.Channel;
import java.net.InetSocketAddress;

public class Client extends ApplicationAdapter {

    private UIManager uiManager;
    private Channel channel;

    @Override
    public void create() {
        uiManager = new UIManager();
        uiManager.setLoginClickListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                uiManager.sendAuthenticationMessage(channel);
            }
        });

        // Start the Netty client in a separate thread
        new Thread(() -> {
            try {
                startNettyClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void render() {
        uiManager.draw();
    }

    @Override
    public void dispose() {
        uiManager.dispose();
    }

    private void startNettyClient() throws Exception {
        SslContext sslContext = SslContext.newClientContext(SslProvider.JDK, null, InsecureTrustManagerFactory.INSTANCE);
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new SslHandler(sslContext.newEngine(ch.alloc())));
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                    // Handle authentication response
                                    AuthServerMessage response = (AuthServerMessage) msg;
                                    updateStatus(response.getMessage());
                                }
                            });
                        }
                    });

            channel = bootstrap.connect(new InetSocketAddress("localhost", 8080)).sync().channel();

        } finally {
            group.shutdownGracefully().sync();
        }
    }

    private void updateStatus(String status) {
        // Update UI on the LibGDX Application Thread
        Gdx.app.postRunnable(() -> statusLabel.setText(status));
    }

    public void sendAuthenticationMessage(String username, String password) {
        // Send authentication message
        AuthServerMessage message = new AuthServerMessage(AuthServerMessageType.LOGIN_REQUEST, username + ":" + password);
        channel.writeAndFlush(message);
    }

    // Main entry point
    public static void main(String[] args) {
        // Launch LibGDX application with your Client class
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        new Lwjgl3Application(new Client(), config);
    }

}
