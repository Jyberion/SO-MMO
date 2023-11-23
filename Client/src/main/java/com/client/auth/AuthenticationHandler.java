package com.client.auth;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslHandler;

import java.net.InetSocketAddress;
import java.util.Scanner;

import javax.net.ssl.SSLContext;

public class AuthenticationHandler extends ChannelHandlerAdapter {

    private String username;
    private String password;
    private SSLContext sslContext;
    private boolean isAuthenticated;

    public AuthenticationHandler(SSLContext sslContext) {
        this.sslContext = sslContext;
        this.isAuthenticated = false;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            // Prompt the user for username and password
            System.out.print("Enter username: ");
            setUsername(scanner.nextLine());

            System.out.print("Enter password: ");
            setPassword(scanner.nextLine());
        }

        // Establish SSL connection with the Authentication Server
        SslHandler sslHandler = new SslHandler(sslContext.createSSLEngine());
        ctx.pipeline().addFirst(sslHandler);
        sslHandler.handshakeFuture().sync();

        // Create and send authentication request to the Authentication Server
        AuthServerMessage authRequest = new AuthServerMessage(AuthServerMessageType.LOGIN_REQUEST);
        authRequest.setUsername(username);
        authRequest.setPassword(password);
        ctx.writeAndFlush(authRequest);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] responseBytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(responseBytes);

            // Process the received data
            // ...

            // Release the ByteBuf
            byteBuf.release();
        } else {
            System.out.println("Received unexpected message type: " + msg.getClass().getName());
        }

        // Pass the message to the next handler in the pipeline
        ctx.fireChannelRead(msg);
    }

    public void handleLoginResponse(AuthServerMessage authResponse) {
        // Process the login response received from the Authentication Server
        if (authResponse.getType() == AuthServerMessageType.LOGIN_SUCCESS) {
            isAuthenticated = true;
            System.out.println("Login successful.");
        } else {
            System.out.println("Login failed.");
        }
    }

public void handleConnection(ChannelHandlerContext ctx) throws Exception {
    SocketChannel socketChannel = (SocketChannel) ctx.channel();

    try {
        socketChannel.connect(new InetSocketAddress("localhost", 8080)).sync();

        // Handle communication with the Authentication Server
        InetSocketAddress address = (InetSocketAddress) socketChannel.remoteAddress();
        System.out.println("Connected to Authentication Server at: " + address.getHostName() + ":" + address.getPort());

        // Prepare and send authentication request message
        AuthServerMessage authRequest = new AuthServerMessage(AuthServerMessageType.LOGIN_REQUEST);
        authRequest.setUsername(username);
        authRequest.setPassword(password);
        socketChannel.writeAndFlush(authRequest);

        // Receive and process authentication response message
        ByteBuf responseBuf = ctx.alloc().buffer(); // Use ByteBuf from ctx
        // NOTE: Here we are using the ctx channel, not the socketChannel directly
        ctx.channel().read(); // Trigger the read operation

        // You can now handle the received data in the channelRead method

    } finally {
        // Close the socket channel
        socketChannel.close();
    }

    // Perform the intended operation if authentication is successful
    if (isAuthenticated) {
        System.out.println("Sending data to the protected resource...");

        // Prepare and send data to the protected resource
        String data = "This is the data to be sent to the protected resource.";
        socketChannel.writeAndFlush(data.getBytes());
    }
}

}
