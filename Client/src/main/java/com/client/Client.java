import com.client.auth.AuthenticationHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.InetSocketAddress;

public class Client extends Application {

    private Label statusLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        statusLabel = new Label("Connecting...");

        StackPane root = new StackPane();
        root.getChildren().add(statusLabel);

        primaryStage.setTitle("Netty Client");
        primaryStage.setScene(new Scene(root, 300, 200));
        primaryStage.show();

        // Start the Netty client in a separate thread
        new Thread(() -> {
            try {
                startNettyClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startNettyClient() throws Exception {
        SslContext sslContext = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        AuthenticationHandler authenticationHandler = new AuthenticationHandler(sslContext, this::updateStatus);

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(sslContext.newHandler(ch.alloc()))
                                    .addLast(authenticationHandler);
                        }
                    });

            Channel channel = bootstrap.connect(new InetSocketAddress("localhost", 8080)).sync().channel();

            channel.closeFuture().sync();

        } finally {
            group.shutdownGracefully().sync();
        }
    }

    private void updateStatus(String status) {
        // Update UI on the JavaFX Application Thread
        Platform.runLater(() -> statusLabel.setText(status));
    }
}
