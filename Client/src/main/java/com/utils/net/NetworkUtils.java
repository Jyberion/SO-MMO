package com.utils.net;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

public class NetworkUtils {

  public static String[][] getWorldServers() {
    // Hardcoded list of world server details: server name, server IP, and server
    // port
    return new String[][] {
        { "WorldServer1", "192.168.1.1", "9001" },
        { "WorldServer2", "192.168.1.2", "9002" },
        { "WorldServer3", "192.168.1.3", "9003" },
        // Additional servers can be added here
    };
  }

  public static Map<String, Boolean> getWorldStatusFromLoginServer() {
    // Create a new HashMap to keep the status of the world servers
    Map<String, Boolean> worldStatus = new HashMap<>();

    // Hardcoded world server details from the getWorldServers method
    String[][] worldServers = getWorldServers();

    // Simulate checking the status of each server
    // In reality, you would implement the actual check with the login server here
    for (String[] serverDetails : worldServers) {
      String serverName = serverDetails[0];
      // Let's say for now all servers are online for the sake of this example
      worldStatus.put(serverName, true);
    }

    return worldStatus;
  }

  public static ChannelFuture connectToServer(String serverHost, int serverPort) {
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    Bootstrap b = new Bootstrap();
    b.group(workerGroup)
        .channel(NioSocketChannel.class)
        .remoteAddress(new InetSocketAddress(serverHost, serverPort))
        .handler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) {
            ChannelPipeline p = ch.pipeline();
            p.addLast(new ByteArrayDecoder());
            p.addLast(new ByteArrayEncoder());
            // Add other necessary channel handlers
          }
        });

    // Connect to the server
    return b.connect();
  }

  public static boolean verifyFileWithServer(String fileID, String fileChecksum) {
    // Verification logic to communicate with the server
    // Currently a placeholder; should be implemented as per the application's
    // protocol
    return true;
  }

  // Other methods like connectToServer and verifyFileWithServer remain unchanged
}
