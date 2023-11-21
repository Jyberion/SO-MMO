import com.client.NetworkClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import java.io.IOException;

public class Client {

  private String host;
  private int port;

  public Client(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public void sendMoveMessage(int x, int y) throws IOException {
    sendMessage(Message.TYPE_PLAYER_MOVE, new byte[]{(byte) x, (byte) y});
  }

  public void sendAttackMessage(int targetId) throws IOException {
    sendMessage(Message.TYPE_PLAYER_ATTACK, new byte[]{(byte) targetId});
  }

  public void sendChatMessage(String message) throws IOException {
    sendMessage(Message.TYPE_CHAT_MESSAGE, message.getBytes());
  }

  private void sendMessage(byte type, byte[] payload) throws IOException {
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(workerGroup)
          .channel(NioSocketChannel.class)
          .handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
              ch.pipeline()
                  .addLast(new LengthFieldBasedFrameDecoder(Byte.MAX_VALUE, 0, 1, 0, 0))
                  .addLast(new ByteArrayEncoder())
                  .addLast(new NetworkClientHandler());
            }
          });

      ChannelFuture future = bootstrap.connect(host, port).sync();
      Channel channel = future.channel();

      Message message = new Message(type, payload);
      message.send(channel);

      channel.closeFuture().sync();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      workerGroup.shutdownGracefully();
    }
  }

  public static void main(String[] args) throws IOException {
    Client client = new Client("localhost", 8080);

    client.sendMoveMessage(100, 100);
    client.sendAttackMessage(2);
    client.sendChatMessage("Hello, world!");
  }

  private class Message {

    private byte type;
    private byte[] payload;

    public Message(byte type, byte[] payload) {
      this.type = type;
      this.payload = payload;
    }

    public byte getType() {
      return type;
    }

    public byte[] getPayload() {
      return payload;
    }

    public void send(Channel channel) {
      channel.writeAndFlush(type);
      channel.writeAndFlush(payload.length);
      channel.writeAndFlush(payload);
    }
  }

}
