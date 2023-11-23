import com.client.auth.AuthenticationHandler;
import com.client.auth.AuthServerMessageType;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.InetSocketAddress;
import javax.net.ssl.SSLContext;

public class Client {

public static void main(String[] args) throws Exception {
    // Create and configure SSLContext
    SslContext sslContext = SslContextBuilder.forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build();

    // Create AuthenticationHandler instance
            SSLContext javaSslContext = sslContext.newEngine(PooledByteBufAllocator.DEFAULT)
            .getSSLContext();

        AuthenticationHandler authenticationHandler = 
            new AuthenticationHandler(javaSslContext);

    // Configure the client
    EventLoopGroup group = new NioEventLoopGroup();
    try {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress("localhost", 8080))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(authenticationHandler);
                    }
                });

        // Start the client
        ChannelFuture channelFuture = bootstrap.connect().sync();
        Channel channel = channelFuture.channel();

        // Perform any additional tasks with the channel if needed

        // Wait for the channel to close
        channel.closeFuture().sync();
    } finally {
        // Shutdown the event loop group
        group.shutdownGracefully().sync();
    }
}
}
