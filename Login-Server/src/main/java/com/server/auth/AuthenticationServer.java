    package com.server.auth;

    import io.netty.bootstrap.ServerBootstrap;
    import io.netty.channel.EventLoopGroup;
    import io.netty.channel.nio.NioEventLoopGroup;
    import io.netty.channel.socket.nio.NioServerSocketChannel;
    import io.netty.channel.ChannelInitializer;
    import io.netty.channel.socket.SocketChannel;
    import io.netty.channel.ChannelPipeline;
    import io.netty.handler.codec.bytes.ByteArrayDecoder;
    import io.netty.handler.codec.bytes.ByteArrayEncoder;
    import io.netty.channel.ChannelFuture;

    public class AuthenticationServer {
        private final int port;

        public AuthenticationServer(int port) {
            this.port = port;
        }

        public void start() throws InterruptedException {
            EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
            EventLoopGroup workerGroup = new NioEventLoopGroup(); // (2)
            try {
                ServerBootstrap b = new ServerBootstrap(); // (3)
                b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (4)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (5)
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new ByteArrayDecoder());
                            p.addLast(new ByteArrayEncoder());
                            p.addLast(new AuthenticationHandler()); // (6)
                        }
                    });

                ChannelFuture f = b.bind(port).sync(); // (7)
                System.out.println("AuthenticationServer started and listening for connections on " + f.channel().localAddress());

                f.channel().closeFuture().sync(); // (8)
            } finally {
                workerGroup.shutdownGracefully(); // (9)
                bossGroup.shutdownGracefully(); // (10)
            }
        }

        public static void main(String[] args) {
            int port = 12345; // Assign a port number for the authentication server
            try {
                new AuthenticationServer(port).start(); // Start the server
            } catch (InterruptedException e) {
                System.out.println("Server was interrupted or failed to start.");
                Thread.currentThread().interrupt();
            }
        }
    }
