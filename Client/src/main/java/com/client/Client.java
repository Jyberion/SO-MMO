import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.client.auth.AuthServerMessage;
import com.client.auth.AuthServerMessageType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
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

import java.net.InetSocketAddress;

public class Client extends ApplicationAdapter {

    private Label statusLabel;
    private Stage stage;
    private Skin skin;
    private Channel channel;

    @Override
    public void create() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        statusLabel = new Label("Connecting...", skin);
        stage.addActor(statusLabel);

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
        // Update and draw the stage
        stage.act();
        stage.draw();
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
        // Example usage of enum constants
        AuthServerMessageType messageType = AuthServerMessageType.LOGIN_REQUEST;

        // Switch statement to handle different message types
        switch (messageType) {
            case LOGIN_REQUEST:
                System.out.println("Received LOGIN_REQUEST");
                break;
            case LOGIN_RESPONSE:
                System.out.println("Received LOGIN_RESPONSE");
                break;
            case LOGOUT_NOTIFICATION:
                System.out.println("Received LOGOUT_NOTIFICATION");
                break;
            case LOGIN_SUCCESS:
                System.out.println("Received LOGIN_SUCCESS");
                break;
            default:
                System.out.println("Unknown message type");
        }
    }
}
